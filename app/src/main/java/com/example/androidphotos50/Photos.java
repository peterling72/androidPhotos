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
    private Button search_button;

    private String selected_album;
    private int selected_album_pos;

    public static final int EDIT_ALBUM_CODE = 1;
    public static final int ADD_ALBUM_CODE = 2;
    public static final int SEARCH_TAG_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_list);

        AlbumManager.verifyStoragePermissions(this);

        //Set selected status to 'nothing'
        updateSelectedAlbum(null);
        selected_album = "";

        //Load albums
        albums = AlbumManager.loadAllAlbums(this);
        if (albums == null) {
            //albums.dat file wasn't found
            String[] albumsList = getResources().getStringArray(R.array.albums_array);
            albums = new ArrayList<>(albumsList.length);
            for (int i = 0; i < albumsList.length; i++) {
                albums.add(new Album(albumsList[i]));
            }
            AlbumManager.writeAlbums(albums, this);
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
        search_button = findViewById(R.id.search_button);

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
                //getSupportActionBar().setTitle("you pressed the open button!");
                openAlbum(selected_album_pos);
            }
        });

        //Search button logic
        search_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //Code here executes on main thread after user presses button
                getSupportActionBar().setTitle("you pressed the search button!");
                searchTags();
            }
        });
    }

    /**
     * Searches for the specified tags by pulling up the SearchTag activity
     */
    private void searchTags(){
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, SearchTag.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, SEARCH_TAG_CODE);
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

    private void openAlbum(int pos){
        if (pos == -1 || pos >= albums.size())
            return;

        Bundle bundle = new Bundle();
        Album album = albums.get(pos);
        bundle.putString(AlbumView.ALBUM_NAME, album.getName());
        Intent intent = new Intent(this, AlbumView.class);
        intent.putExtras(bundle);
        startActivity(intent);
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
        Album a = albums.remove(pos);
        getSupportActionBar().setTitle("Removed " + a.getName());

        listView.setAdapter(
                new ArrayAdapter<Album>(this, R.layout.album, albums));

        updateSelectedAlbum(null);
        selected_album_pos = -1;

        //Save changes
        AlbumManager.writeAlbums(albums, this);
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

        if (requestCode == SEARCH_TAG_CODE){
            //Refresh the local list of albums to ensure we search well.
            albums = AlbumManager.loadAllAlbums(this);

            String type = bundle.getString(SearchTag.TAG_TYPE);
            String value = bundle.getString(SearchTag.TAG_VALUE);
            Log.i("Photos", "type: " + type + ", value: " + value);
            Album new_album = new Album("Search " + type + ": " + value);
            for (Album a : albums){
                for (Photo p: a.getPhotos()){
                    ArrayList<Tag> tags = p.getTagList();
                    for (Tag t : tags){
                        if (t.getType().toLowerCase().equals(type.toLowerCase()) &&
                        t.getValue().toLowerCase().contains(value.toLowerCase()))
                            new_album.add(p);
                    }
                }
            }
            if (new_album.size() > 0){
                //Add our search result album to the list
                albums.add(new_album);

                //Redo the adapter to reflect changes
                listView.setAdapter(
                        new ArrayAdapter<Album>(this, R.layout.album, albums));

                //Save changes
                AlbumManager.writeAlbums(albums, this);

                getSupportActionBar().setTitle("Stored search in new album!");
            } else{
                getSupportActionBar().setTitle("No results found from search");
            }
            return;
        }
        //gather all info passed back by launched activity

        String name = bundle.getString(AddEditAlbum.ALBUM_NAME);
        int index = bundle.getInt(AddEditAlbum.ALBUM_INDEX);

        if (requestCode == EDIT_ALBUM_CODE){
            Album album = albums.get(index);
            album.setName(name);
            getSupportActionBar().setTitle("Edited album name");
        } else{
            albums.add(new Album(name));
            Log.i("Photos", "done!");
            getSupportActionBar().setTitle("Created album " + name);
        }

        //Redo the adapter to reflect changes
        listView.setAdapter(
                new ArrayAdapter<Album>(this, R.layout.album, albums));

        //Save changes
        AlbumManager.writeAlbums(albums, this);
    }

}

