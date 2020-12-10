package com.example.androidphotos50;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class AddTag extends AppCompatActivity {

    public static final String ALBUM_INDEX = "albumIndex";
    public static final String ALBUM_NAME = "albumName";
    public static final String PHOTO_POS = "photoPos";
    public static final String TAG_TYPE = "tagType";
    public static final String TAG_VALUE = "tagValue";

    private EditText tagType;
    private EditText tagValue;
    private String albumName;
    private int photo_pos;
    private Album album;
    private Photo photo;
    private ArrayList<Album> albums;


    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tag);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // activates the up arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get the fields
        tagType = findViewById(R.id.tag_name);
        tagValue = findViewById(R.id.tag_value);

        // see if info was passed in to populate fields
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            albumName = bundle.getString(ALBUM_NAME);
            photo_pos = bundle.getInt(PHOTO_POS);
        }
        albums = AlbumManager.loadAllAlbums(this);
        for (int i = 0; i < albums.size(); i++) {
            if (albums.get(i).getName().equals(albumName)){
                album = albums.get(i);
                break;
            }
        }
        if (album == null)
            album = albums.get(0);

        photo = album.getPhoto(photo_pos);
    }

    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void save(View view) {
        // gather all data from text fields
        String type = tagType.getText().toString().replaceAll("\n", "");
        String value = tagValue.getText().toString().replaceAll("\n", "");
        ArrayList<Tag> tags = photo.getTagList();
        boolean exists = false;
        for (Tag t: tags){
            if (t.equals(type, value))
                exists = true;
        }

        // pop up dialog if errors in input, and return
        // name and year are mandatory
        if (type == null || type.length() == 0 || (!type.equals("person") && !type.equals("location"))
        || value == null || value.length() == 0) {
            Bundle bundle = new Bundle();
            bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                    "Valid entries required for type and value");
            DialogFragment newFragment = new AlbumDialogFragment();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(), "badfields");
            return; // does not quit activity, just returns from method
        } else if (exists){
            //Do not allow duplicate tag (case insensitive)
            Bundle bundle = new Bundle();
            bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                    "Provided tag and value already exist.");
            DialogFragment newFragment = new AlbumDialogFragment();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(), "badfields");
            return; // does not quit activity, just returns from method
        }

        // make Bundle
        Bundle bundle = new Bundle();
        bundle.putString(TAG_TYPE, type);
        bundle.putString(TAG_VALUE, value);

        // send back to caller
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish(); // pops activity from the call stack, returns to parent

    }
}