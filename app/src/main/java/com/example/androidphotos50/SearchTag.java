package com.example.androidphotos50;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class SearchTag extends AppCompatActivity {

    public static final String TAG_TYPE = "tagType";
    public static final String TAG_VALUE = "tagValue";

    private EditText tagType;
    private EditText tagValue;


    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_tag);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // activates the up arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get the fields
        tagType = findViewById(R.id.tag_name);
        tagValue = findViewById(R.id.tag_value);
    }

    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * Commits the search
     * @param view
     */
    public void save(View view) {
        // gather all data from text fields
        String type = tagType.getText().toString().replaceAll("\n", "");
        String value = tagValue.getText().toString().replaceAll("\n", "");

        // pop up dialog if errors in input, and return
        // name and year are mandatory
        if (type == null || type.length() == 0 || (!type.equals("person") && !type.equals("location"))
                || value == null || value.length() == 0) {
            Bundle bundle = new Bundle();
            bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                    "Valid entries required for type and value to search");
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