package com.example.androidphotos50;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.*;
import java.util.ArrayList;

/**
 * Abstract class that contains methods for handling file I/O on the master list of albums.
 * @author jrn84, pl466
 *
 */
public abstract class AlbumManager {

    /**
     * Folder we're saving in
     */
    public static final String storeDir = "data";
    /**
     * Filename of the albums save file
     */
    public static final String storeFile = "albums.dat";

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    /**
     * Loads ALL albums into an ArrayList of Albums. Used by the AlbumManager to write Albums to file.
     * @return ArrayList of all Album that are saved in albums.dat.
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Album> loadAllAlbums(Context context) {
        try {
            ObjectInputStream ois;
            ois = new ObjectInputStream(context.openFileInput(storeFile));
            ArrayList<Album> temp = (ArrayList<Album>)ois.readObject();
            ois.close();
            return temp;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.i("Photos", "Failed to read albums from the path specified");
            return null;
        }
    }

    /**
     * Writes the albums for a specific user to file. First removes all of the user's albums from the master file,
     * then adds these new ones in to effectively 'refresh' the user's entries.
     * @param albums - The ArrayList of albums for the user
     */
    public static void writeAlbums(ArrayList<Album> albums, Context context) {
        try {
            //Write the list to file
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(context.openFileOutput(storeFile, context.MODE_PRIVATE));
            oos.writeObject(albums);
            oos.close();
        } catch (IOException e) {
            Log.i("Photos", "Failed to write albums to path specified");
            e.printStackTrace();
        }
    }
}
