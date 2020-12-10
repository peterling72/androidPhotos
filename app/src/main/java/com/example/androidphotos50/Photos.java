package com.example.androidphotos50;

import androidx.appcompat.app.AppCompatActivity;

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

public class Photos extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Album> albums;
    private Button add_button;
    private Button remove_button;
    private Button edit_button;
    private Button open_button;

    private String selected_album;
    private int selected_album_pos;

    public static final int EDIT_ALBUM_CODE = 1;
    public static final int ADD_ALBUM_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_list);

        //Set selected status to 'nothing'
        updateSelectedAlbum(null);
        selected_album = "";

        //Load albums
        try {
            FileInputStream fis = openFileInput("albums.dat");
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(fis));
            String albumInfo = null;
            albums = new ArrayList<Album>();
            while ((albumInfo = br.readLine()) != null) {
                String[] tokens = albumInfo.split("\\|");
                if (tokens.length > 0) {
                    albums.add(new Album(tokens[0]));
                }
            }
        } catch (IOException e) {
            //albums.dat file wasn't found
            String[] albumsList = getResources().getStringArray(R.array.albums_array);
            albums = new ArrayList<>(albumsList.length);
            for (int i = 0; i < albumsList.length; i++) {
                albums.add(new Album(albumsList[i]));
            }
        }

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

        open_button = findViewById(R.id.open_button);
        add_button = findViewById(R.id.add_button);
        edit_button = findViewById(R.id.edit_button);
        remove_button = findViewById(R.id.remove_button);

        //Add button logic
        add_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //Code here executes on main thread after user presses button
                getSupportActionBar().setTitle("you pressed the add button!");
                addAlbum();
            }
        });

        //Remove button logic
        remove_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //Code here executes on main thread after user presses button
                getSupportActionBar().setTitle("you pressed the remove button!");
                removeAlbum(selected_album_pos);
            }
        });

        //Edit button logic
        edit_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //Code here executes on main thread after user presses button
                getSupportActionBar().setTitle("you pressed the edit button!");
                editAlbum(selected_album_pos);
            }
        });

        //Open button logic
        open_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //Code here executes on main thread after user presses button
                getSupportActionBar().setTitle("you pressed the open button!");
            }
        });
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

    private ArrayList<String> getAlbumNames(){
        ArrayList<String> albumNames = new ArrayList<String>();
        for (Album a : albums){
            albumNames.add(a.getName().toLowerCase());
        }
        return albumNames;
    }

    private void editAlbum(int pos){
        if (pos == -1 || pos >= albums.size())
            return;

        Bundle bundle = new Bundle();
        Album album = albums.get(pos);
        bundle.putInt(AddEditAlbum.ALBUM_INDEX, pos);
        bundle.putString(AddEditAlbum.ALBUM_NAME, album.getName());
        bundle.putStringArrayList(AddEditAlbum.EXISTING_ALBUMS, getAlbumNames());
        Intent intent = new Intent(this, AddEditAlbum.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, EDIT_ALBUM_CODE);
    }
    private void addAlbum(){
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(AddEditAlbum.EXISTING_ALBUMS, getAlbumNames());
        Intent intent = new Intent(this, AddEditAlbum.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, ADD_ALBUM_CODE);
    }

    private void removeAlbum(int pos){
        if (pos == -1 || pos >= albums.size())
            return;
        albums.remove(pos);

        listView.setAdapter(
                new ArrayAdapter<Album>(this, R.layout.album, albums));

        updateSelectedAlbum(null);
        selected_album_pos = -1;

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

        String name = bundle.getString(AddEditAlbum.ALBUM_NAME);
        int index = bundle.getInt(AddEditAlbum.ALBUM_INDEX);

        if (requestCode == EDIT_ALBUM_CODE){
            Album album = albums.get(index);
            album.setName(name);
        } else{
            albums.add(new Album(name));
            Log.i("Photos", "done!");
        }

        //Redo the adapter to reflect changes
        listView.setAdapter(
                new ArrayAdapter<Album>(this, R.layout.album, albums));
    }

}

