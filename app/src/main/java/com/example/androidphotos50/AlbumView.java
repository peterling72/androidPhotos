package com.example.androidphotos50;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class AlbumView extends AppCompatActivity {

    public static final String ALBUM_NAME = "albumName";
    private static final int PICK_IMAGE = 1;
    public static final int RESULT_MOVED = 2;
    public static final int RESULT_DISPLAY = 3;

    private ListView listView;
    private Album album;
    private ArrayList<Album> albumList;
    private ArrayList<Photo> photos;
    private Button add_button;
    private Button remove_button;
    private Button display_button;
    private Button move_button;

    private String selected_photo;
    private String albumName;
    private int selected_photo_pos;

    public static final int ADD_PHOTO_CODE = 1;

    @Override
    public boolean onSupportNavigateUp(){
        //Take us back to the previous activity
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_list);

        // see if info was passed in to populate fields
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            albumName = bundle.getString(ALBUM_NAME);

        // activates the up arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        albumList = AlbumManager.loadAllAlbums(this);
        for (int i = 0; i < albumList.size(); i++) {
            if (albumList.get(i).getName().equals(albumName)){
                album = albumList.get(i);
                break;
            }
        }
        if (album == null)
            album = albumList.get(0);

        //Set selected status to 'nothing'
        updateSelectedPhoto(null);
        selected_photo = "";

        //Load photos
        photos = album.getPhotos();

        listView = findViewById(R.id.photo_list);
        listView.setAdapter(
                new ArrayAdapter<Photo>(this, R.layout.photo, photos));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){
                Photo p = (Photo)adapter.getItemAtPosition(position);
                updateSelectedPhoto(p);
                selected_photo_pos = position;
            }
        });

        display_button = findViewById(R.id.display_button);
        add_button = findViewById(R.id.add_button);
        remove_button = findViewById(R.id.remove_button);
        move_button = findViewById(R.id.move_button);

        //Add button logic
        add_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //Code here executes on main thread after user presses button
                getSupportActionBar().setTitle("you pressed the add button!");
                addPhoto();
                //addAlbum();
            }
        });

        //Remove button logic
        remove_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //Code here executes on main thread after user presses button
                getSupportActionBar().setTitle("you pressed the remove button!");
                removePhoto(selected_photo_pos);
            }
        });

        //Move button logic
        move_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //Code here executes on main thread after user presses button
                getSupportActionBar().setTitle("you pressed the move button!");
                movePhoto(selected_photo_pos);
            }
        });

        //Display button logic
        display_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //Code here executes on main thread after user presses button
                getSupportActionBar().setTitle("you pressed the open button!");
                openPhoto(selected_photo_pos);
            }
        });
    }

    /**
     * Updates the action bar with the selected photo's name,
     * updates the global variable "selected_photo" with the photo name as a string
     * @param p - the photo selected
     */
    private void updateSelectedPhoto(Photo p){
        if (p == null){
            getSupportActionBar().setTitle("No photo currently selected.");
            selected_photo = "";
            selected_photo_pos = -1;
            return;
        }
        getSupportActionBar().setTitle("Current photo: " + p.getCaption());
        selected_photo = p.getCaption();
    }
/*
    private ArrayList<String> getPhotoNames(){
        ArrayList<String> photoNames = new ArrayList<String>();
        for (Photo p : photos){
            photoNames.add(p.getCaption());
        }
        return photoNames;
    }
*/

    private void openPhoto(int pos){
        if (pos == -1 || pos >= photos.size())
            return;

        Bundle bundle = new Bundle();
        bundle.putString(PhotoDisplay.ALBUM_NAME, album.getName());
        bundle.putInt(PhotoDisplay.PHOTO_POS, pos);
        Intent intent = new Intent(this, PhotoDisplay.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, RESULT_DISPLAY);
    }
    private void addPhoto(){
        Intent intent = new Intent();
        intent.setType("image/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void movePhoto(int pos){
        if (pos == -1 || pos >= photos.size())
            return;

        Bundle bundle = new Bundle();
        bundle.putString(MovePhoto.ALBUM_NAME, album.getName());
        bundle.putInt(MovePhoto.PHOTO_POS, pos);
        Intent intent = new Intent(this, MovePhoto.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, RESULT_MOVED);


    }
    private void removePhoto(int pos){
        if (pos == -1 || pos >= photos.size())
            return;
        Photo p = photos.remove(pos);
        album.remove(p);

        listView.setAdapter(
                new ArrayAdapter<Photo>(this, R.layout.photo, photos));

        updateSelectedPhoto(null);
        selected_photo_pos = -1;

        //Save changes
        AlbumManager.writeAlbums(albumList, this);

        //Redo the adapter to reflect changes
        listView.setAdapter(
                new ArrayAdapter<Photo>(this, R.layout.photo, photos));
    }

    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        Log.i("Photos", "requestCode from AlbumView: " + requestCode);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == PICK_IMAGE){
            Uri uri = intent.getData();

            Cursor returnCursor =
                    getContentResolver().query(uri, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String name = returnCursor.getString(nameIndex);
            returnCursor.close();

            Log.i("Photos", "URI path: " + uri.getPath());
            //Add photo to album
            album.add(new Photo(uri, name));
            Log.i("Photos", "Output: " + name);

            AlbumManager.writeAlbums(albumList, this);

            //Redo the adapter to reflect changes
            listView.setAdapter(
                    new ArrayAdapter<Photo>(this, R.layout.photo, photos));

            return;
        } else if (requestCode == RESULT_MOVED || requestCode == RESULT_DISPLAY){

            //Refresh photo list
            albumList = AlbumManager.loadAllAlbums(this);
            for (int i = 0; i < albumList.size(); i++) {
                if (albumList.get(i).getName().equals(albumName)){
                    album = albumList.get(i);
                    break;
                }
            }
            if (album == null)
                album = albumList.get(0);
            photos = album.getPhotos();

            //Redo the adapter to reflect changes
            listView.setAdapter(
                    new ArrayAdapter<Photo>(this, R.layout.photo, photos));
        }
        /*
        Bundle bundle = intent.getExtras();
        if (bundle == null)
            return;

        //gather all info passed back by launched activity

        String name = bundle.getString(AddEditAlbum.ALBUM_NAME);
        int index = bundle.getInt(AddEditAlbum.ALBUM_INDEX);

        //Add photo to the album and refresh our photo list
        album.addToAlbum(name);
        photos = album.getPhotos();
        Log.i("Photos", "done!");

        //Redo the adapter to reflect changes
        listView.setAdapter(
                new ArrayAdapter<Photo>(this, R.layout.photo, photos));

        //Save changes
        AlbumManager.writeAlbums(albumList, this);
         */
    }

}

