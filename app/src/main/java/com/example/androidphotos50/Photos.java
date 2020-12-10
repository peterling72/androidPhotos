package com.example.androidphotos50;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    private String selected_album;

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
            return;
        }
        getSupportActionBar().setTitle("Current album: " + a.getName());
        selected_album = a.getName();
    }
}

