package com.example.androidphotos50;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class PhotoDisplay extends AppCompatActivity {

    public static final String PHOTO_POS = "photoPos";
    public static final String ALBUM_NAME = "albumName";

    private ImageView imageView;
    private ListView listView;
    private ArrayList<Album> albums;
    private ArrayList<Tag> tags;
    private int photo_pos;
    private String albumName;
    private Album album;
    private Photo photo;
    private Button next_button;
    private Button previous_button;
    private Button add_button;
    private Button remove_button;
    private int selected_tag_pos;

    public static final int ADD_TAG_CODE = 2;


    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view);

        //Grab ImageView from xml
        imageView = findViewById(R.id.imageView);

        //Grab listView
        listView = findViewById(R.id.tag_list);

        // activates the up arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // see if info was passed in to populate fields
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            albumName = bundle.getString(ALBUM_NAME);
            photo_pos = bundle.getInt(PHOTO_POS);
        }

        //Load albums
        albums = AlbumManager.loadAllAlbums(this);
        for (int i = 0; i < albums.size(); i++) {
            if (albums.get(i).getName().equals(albumName)){
                album = albums.get(i);
                break;
            }
        }
        if (album == null)
            album = albums.get(0);

        //Display image
        photo = album.getPhoto(photo_pos);
        imageView.setImageURI(Uri.parse(photo.getPath()));

        tags = photo.getTagList();

        //Initialize listView
        listView.setAdapter(
                new ArrayAdapter<Tag>(this, R.layout.tag, tags));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){
                selected_tag_pos = position;
            }
        });

        next_button = findViewById(R.id.next_button);
        add_button = findViewById(R.id.add_button);
        remove_button = findViewById(R.id.remove_button);
        previous_button = findViewById(R.id.previous_button);

        //Add button logic
        add_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //Code here executes on main thread after user presses button

                addTag();
            }
        });

        //Remove button logic
        remove_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //Code here executes on main thread after user presses button
                removeTag(selected_tag_pos);
            }
        });

        //Move button logic
        previous_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //Code here executes on main thread after user presses button
                nextPhoto();
            }
        });

        //Display button logic
        next_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //Code here executes on main thread after user presses button
                previousPhoto();
            }
        });
    }

    private void addTag(){
        Bundle bundle = new Bundle();
        bundle.putInt(AddTag.PHOTO_POS, photo_pos);
        bundle.putString(AddTag.ALBUM_NAME, album.getName());
        Intent intent = new Intent(this, AddTag.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, ADD_TAG_CODE);
    }

    private void removeTag(int pos){
        if (pos < 0 || pos >= tags.size())
            return;
        Tag tag = tags.get(pos);
        String type = tag.getType();
        String value = tag.getValue();
        //Remove tag
        photo.removeTag(type, value);
        //Reset selection to nothing
        selected_tag_pos = -1;

        //Update local tags list
        tags = photo.getTagList();

        //Redo the adapter to reflect changes
        listView.setAdapter(
                new ArrayAdapter<Tag>(this, R.layout.tag, tags));

        //Save changes
        AlbumManager.writeAlbums(albums, this);

    }

    private void nextPhoto(){
        //Move to the next photo in the album
        if (photo_pos == album.size()-1){
            photo_pos = 0;
        } else photo_pos++;

        photo = album.getPhoto(photo_pos);
        imageView.setImageURI(Uri.parse(photo.getPath()));

        //Update tag listView
        //...
        tags = photo.getTagList();
        //Redo the adapter to reflect changes
        listView.setAdapter(
                new ArrayAdapter<Tag>(this, R.layout.tag, tags));


    }

    private void previousPhoto(){
        //Move to the previous photo in the album
        if (photo_pos == 0){
            photo_pos = album.size()-1;
        } else photo_pos--;

        photo = album.getPhoto(photo_pos);
        imageView.setImageURI(Uri.parse(photo.getPath()));

        //Update tag listView
        //...
        tags = photo.getTagList();
        //Redo the adapter to reflect changes
        listView.setAdapter(
                new ArrayAdapter<Tag>(this, R.layout.tag, tags));


    }

    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_OK) {
            return;
        }

        Bundle bundle = intent.getExtras();
        if (bundle == null)
            return;

        //gather all info passed back by launched activity

        String type = bundle.getString(AddTag.TAG_TYPE);
        String value = bundle.getString(AddTag.TAG_VALUE);

        if (requestCode == ADD_TAG_CODE){
            photo.addTag(type, value, false);
            //Update local tags list
            tags = photo.getTagList();
        }

        //Redo the adapter to reflect changes
        listView.setAdapter(
                new ArrayAdapter<Tag>(this, R.layout.tag, tags));

        //Save changes
        AlbumManager.writeAlbums(albums, this);
    }
}