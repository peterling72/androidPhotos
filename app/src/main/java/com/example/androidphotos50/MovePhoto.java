package com.example.androidphotos50;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MovePhoto extends AppCompatActivity {

    public static final String PHOTO_POS = "photoPos";
    public static final String ALBUM_NAME = "albumName";

    private ListView listView;
    private ArrayList<Album> albums;
    private Album album;
    private Photo photo;
    private Button move_button;

    private String selected_album;
    private String albumName;
    private int selected_album_pos;
    private int photo_pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.move_list);

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
        if (albums == null) {
            //albums.dat file wasn't found
            String[] albumsList = getResources().getStringArray(R.array.albums_array);
            albums = new ArrayList<>(albumsList.length);
            for (int i = 0; i < albumsList.length; i++) {
                albums.add(new Album(albumsList[i]));
            }
        }

        //Get the album this photo is from
        for (int i = 0; i < albums.size(); i++) {
            if (albums.get(i).getName().equals(albumName)){
                album = albums.get(i);
                break;
            }
        }
        if (album == null)
            album = albums.get(0);

        //Get our photo
        photo = album.getPhoto(photo_pos);

        getSupportActionBar().setTitle("Moving " + photo.getCaption());

        //Set selected status to 'nothing'
        updateSelectedAlbum(null);
        selected_album = "";



        listView = findViewById(R.id.album_list);
        listView.setAdapter(
                new ArrayAdapter<Album>(this, R.layout.album, albums));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){
                Album a = (Album)adapter.getItemAtPosition(position);
                updateSelectedAlbum(a);
                selected_album_pos = position;
            }
        });

        move_button = findViewById(R.id.move_button);

        //move button logic
        move_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //Code here executes on main thread after user presses button
                getSupportActionBar().setTitle("you pressed the move button!");
                movePhoto(selected_album_pos);
            }
        });
    }

    private void movePhoto(int pos){
        if (pos < 0 || pos >= albums.size())
            return;
        Album a = albums.get(pos);
        //Add photo to new album and remove it from original album
        a.add(photo);
        album.remove(photo);

        AlbumManager.writeAlbums(albums, this);
        setResult(AlbumView.RESULT_OK);
        //Return to parent
        finish();
    }

    /**
     * Updates the action bar with the selected album's name,
     * updates the global variable "selected_album" with the album name as a string
     * @param a
     */
    private void updateSelectedAlbum(Album a){
        if (a == null){
            getSupportActionBar().setTitle("No album currently selected.");
            selected_album = "";
            selected_album_pos = -1;
            return;
        }
        getSupportActionBar().setTitle("Current album: " + a.getName());
        selected_album = a.getName();
    }

    /*
    private ArrayList<String> getAlbumNames(){
        ArrayList<String> albumNames = new ArrayList<String>();
        for (Album a : albums){
            albumNames.add(a.getName().toLowerCase());
        }
        return albumNames;
    }
    */


}

