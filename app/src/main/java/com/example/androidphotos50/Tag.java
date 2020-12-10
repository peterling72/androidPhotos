package com.example.androidphotos50;
import java.io.Serializable;

/**
 * Tag object. Contains the following information about a tag:
 * <p> 1. The type of tag (location, person, etc.).
 * <p> 2. The value pertaining to the type of tag (France, George, etc.).
 * <p> 3. Whether or not a tag is unique. If a tag is unique, only one instance of that type can be in a photo.
 * @author jrn84, pl466
 *
 */
public class Tag implements Serializable {

    /**
     * The type of tag (location, person, etc.)
     */
    private String type;

    /**
     * The value pertaining to the type of tag (France, George, etc.)
     */
    private String value;

    /**
     * If a tag is unique, only one instance of that type can be in a photo
     */
    boolean unique;

    /**
     * Tag constructor.
     * @param type - The type of tag (location, person, etc.)
     * @param value - The value pertaining to the type of tag (France, George, etc.)
     * @param unique - If a tag is unique, only one instance of that type can be in a photo
     */
    public Tag(String type, String value, boolean unique){
        this.type = type;
        this.value = value;
        this.unique = unique;
    }

    /**
     * Gets the type of Tag.
     * @return - The type of tag (location, person, etc.)
     */
    public String getType() {
        return type;
    }

    /**
     * gets the value of this Tag.
     * @return - The value pertaining to the type of tag (France, George, etc.)
     */
    public String getValue() {
        return value;
    }

    /**
     * gets the boolean value of whether or not this Tag is unique.
     * @return - Whether or not this tag is unique
     */
    public boolean isUnique() {
        return unique;
    }

    /**
     * Gets a String representation of this Tag.
     * @return - A string that's a tuple of the form (TAG TYPE, TAG VALUE)
     */
    public String toString() {
        return "(" + type + ", " + value + ")";
    }

    /**
     * If two tags are equal, then they have the same type and value. This method captures that specification
     * through an input type and input value
     * @param type - The type of tag (location, person, etc.)
     * @param value - The value pertaining to the type of tag (France, George, etc.)
     * @return - true if the tag has the same type and value as the parameters
     */
    public boolean equals(String type, String value) {
        if (type.equals(this.type) && value.equals(this.value))
            return true;
        return false;
    }
}
