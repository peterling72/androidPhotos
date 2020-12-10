package com.example.androidphotos50;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Album object. Contains the following information about an album:
 * <p> 1. An ArrayList of Photos
 * <p> 2. The name of the album
 * <p> 3. The owner (username) of the album
 * @author jrn84, pl466
 *
 */
public class Album implements Serializable {

    /**
     * The photos contained in the album
     */
    private ArrayList<Photo> photos;

    /**
     * The name of the album
     */
    private String name;

    /**
     * Constructor for an unnamed album.
     */
    public Album(){
        photos = new ArrayList<Photo>();
        name = "Untitled";
    }

    /**
     * Constructor for a named album.
     * @param name - The desired name for the album.
     */
    public Album(String name){
        photos = new ArrayList<Photo>();
        this.name = name;
    }

    /**
     * Renames the album to the name specified
     * @param name - Desired new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Adds a new Photo to the album (by generating a new Photo using a filepath)
     * @param filepath - The filepath of the desired Photo to be added
     */
    public void addToAlbum(String filepath) {
        //No checking if this is valid for now
        photos.add(new Photo(filepath));
    }

    /**
     * Adds an existing photo to the album
     * @param p - The Photo to be added
     */
    public void add(Photo p) {
        photos.add(p);
    }

    /**
     * Removes the specified photo from the album
     * @param photo - Photo to be removed
     */
    public void remove(Photo photo) {
        int i = photos.indexOf(photo);

        if (i >= 0) {
            photos.remove(i);
        }
    }

    /**
     * Removes the photo at the specified index.
     * @param index - index of the Photo to be removed
     */
    public void removeByIndex(int index) {
        photos.remove(index);
    }

    /**
     * Returns the name of the album.
     * @return - The name of the album.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the array of Photos.
     * @return - An ArrayList containing all the Photos in the album.
     */
    public ArrayList<Photo> getPhotos(){
        return photos;
    }

    /**
     * Gets the photo at the specified index
     * @param i - The specified index
     * @return - The Photo at the specified index in the album's Photo ArrayList.
     */
    public Photo getPhoto(int i) {
        return photos.get(i);
    }

    /**
     * Since no two albums under the same user can have the same name, this equals method
     * merely allows us to use .indexOf and .equals methods based off of whether or not
     * the current album and the compared album have the same user and name fields.
     * This doesn't actually check if the photo arraylists contained are the same.
     * @return - True if this object and the comparison object have equal 'owner' and 'name' fields.
     */
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof Album))
            return false;

        Album a = (Album)o;
        if (a.getName().equals(name))
            return true;
        return false;
    }

    /**
     * Gets the size of this album (how many photos are in it)
     * @return - The size of the Photo ArrayList
     */
    public int size() {
        return photos.size();
    }

    public String toString(){
        return name;
    }
}
