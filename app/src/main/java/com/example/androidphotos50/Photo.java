package com.example.androidphotos50;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
/**
 * Photo object. Contains the following information about a photo:
 * <p> 1. The filepath (on the user's PC) of the image. Can be relative or absolute path.
 * <p> 2. The caption for this photo.
 * <p> 3. The creation date of the photo.
 * <p> 4. An ArrayList of Tags about this photo.
 * @author jrn84, pl466
 *
 */
public class Photo{
    /**
     *
     */
    private static final long serialVersionUID = 6945705504174401506L;

    /**
     * The file path that points to our image file.
     */
    private String filepath;

    /**
     * The caption for the Photo. By default, this is the filename of the image.
     */
    private String caption;

    /**
     * The creation date of the image file specified by filepath.
     */
    private Date creation_date;

    /**
     * The list of Tag objects associated with this Photo.
     */
    private ArrayList<Tag> tags;

    /**
     * Given a filepath string, creates a Photo
     * @param filepath - A string that is a filepath to a valid image
     */
    public Photo(String filepath){
        this.filepath = filepath;
        int index = filepath.lastIndexOf('\\');
        caption = filepath.substring(index+1);

        //Get current time and strip milliseconds
		/*
		creation_date = Calendar.getInstance();
		creation_date.set(Calendar.MILLISECOND, 0);
		*/
        File file = new File(filepath);
        Date lastmodified = new Date(1000*(file.lastModified()/1000));
        creation_date = lastmodified;

        tags = new ArrayList<Tag>();
    }

    /**
     * Given a File object, creates a Photo
     * @param file - A File object that hopefully points to a valid image
     */
    public Photo(File file){

        this.filepath = file.toString();
        int index = filepath.lastIndexOf('\\');
        caption = filepath.substring(index+1);

        Date lastmodified = new Date(1000*(file.lastModified()/1000));
        creation_date = lastmodified;

        tags = new ArrayList<Tag>();
    }

    /**
     * Constructor that clones another photo
     * @param p - Photo to be cloned
     */
    @SuppressWarnings("unchecked")
    public Photo(Photo p) {
        filepath = p.getPath();
        caption = p.getCaption();
        creation_date = p.getDate();
        tags = (ArrayList<Tag>)p.getTagList().clone();
    }

    /**
     * Adds a new tag to the photo. Violation checking is done in the ImageController method that uses this.
     * @param type - Type of tag (location, person, etc.)
     * @param value - Value of tag (France, George, etc.)
     * @param isUnique - Whether or not this tag should be the only tag of its type in this Photo
     */
    public void addTag(String type, String value, boolean isUnique) {
        tags.add(new Tag(type, value, isUnique));
    }

    /**
     * Removes the tag with the specified type and value from the Photo.
     * @param type - The type of tag
     * @param value - The value of the type of tag
     */
    public void removeTag(String type, String value) {
        for (int i = 0; i < tags.size(); i++) {
            if (tags.get(i).equals(type, value)) {
                tags.remove(i);
                i--;
            }
        }
    }

    /**
     * Gets a String containing all the Tags for this Photo.
     * @return - a String containing all of the Photo's tags.
     */
    public String getTags() {
        String str = "";

        for (Tag t : tags) {
            str += t.toString() + ", ";
        }
        str = str.substring(0, str.length()-2);

        return str;
    }

    /**
     * Returns the first index of a Tag with the specified tag type
     * @param type - Type of tag you're looking for
     * @return - the index in the tag list that the tag is located at. -1 if it doesn't exist.
     */
    public int indexOfTagType(String type) {
        for (int i = 0; i < tags.size(); i++) {
            Tag t = tags.get(i);
            if (t.getType().equals(type))
                return i;
        }
        return -1;
    }

    /**
     * Returns true if the Photo contains the specified tag and value. Used by the search function
     * @param type - tag type
     * @param value - tag value
     * @return - true if the Photo contains the specified tag and value
     */
    public boolean containsTag(String type, String value) {
        for (Tag t : tags) {
            if (t.getType().toLowerCase().equals(type.toLowerCase()) &&
                    t.getValue().toLowerCase().equals(value.toLowerCase()))
                return true;
        }
        return false;
    }

    /**
     * gets the Tag ArrayList for this Photo.
     * @return - The ArrayList of tags for this Photo.
     */
    public ArrayList<Tag> getTagList(){
        return tags;
    }

    /**
     * Gets the Date object for this Photo.
     * @return - A Date object containing the creation date of this Photo.
     */
    public Date getDate() {
        return creation_date;
    }

    /**
     * Gets the filepath String for this Photo.
     * @return - A string containing the filepath of this Photo.
     */
    public String getPath() {
        return filepath;
    }

    /**
     * Gets the caption for this Photo.
     * @return - A string containing the caption for this photo.
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the caption for this photo.
     * @param s - The desired caption
     */
    public void setCaption(String s) {
        caption = s;
    }

}
