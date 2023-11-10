package com.example.cmput301groupproject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the class that represents an item in the household inventory
 */
public class HouseholdItem implements Serializable {
    private String dateOfPurchase;
    private String description;
    private String make;
    private String model;
    private String serialNumber;
    private String estimatedValue;
    private String comment;
    private String firestoreId;

    private List<String> tags;

    /**
     * Constructs a HouseholdItem object with the provided attributes
     *
     * @param dateOfPurchase The date of purchase for the item
     * @param description    The description of the item
     * @param make           The make of the item
     * @param model          The model of the item
     * @param serialNumber   The serial number of the item
     * @param estimatedValue The estimated value of the item
     * @param comment        Any additional comments or notes about the item
     */
    public HouseholdItem(String dateOfPurchase, String description, String make, String model, String serialNumber, String estimatedValue, String comment) {
        this.dateOfPurchase = dateOfPurchase;
        this.description = description;
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.estimatedValue = estimatedValue;
        this.comment = comment;
        this.tags = new ArrayList<>();
    }

    // Getters and Setters
    /**
     * Returns the date of purchase of the item
     *
     * @return The date of purchase
     */
    public String getDateOfPurchase() {
        return dateOfPurchase;
    }

    /**
     * Sets the date of purchase of the item
     *
     * @param dateOfPurchase The date of purchase to be set
     */
    public void setDateOfPurchase(String dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }

    /**
     * Returns the description of the item
     *
     * @return The description of the item
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the item
     *
     * @param description The description to be set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the make of the item
     *
     * @return The make of the item
     */
    public String getMake() {
        return make;
    }

    /**
     * Sets the make of the item
     *
     * @param make The make to be set
     */
    public void setMake(String make) {
        this.make = make;
    }

    /**
     * Returns the model of the item
     *
     * @return The model of the item
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the model of the item
     *
     * @param model The model to be set
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Returns the serial number of the item
     *
     * @return The serial number of the item
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number of the item
     *
     * @param serialNumber The serial number to be set
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Returns the estimated value of the item
     *
     * @return The estimated value of the item
     */
    public String getEstimatedValue() {
        return estimatedValue;
    }

    /**
     * Sets the estimated value of the item
     *
     * @param estimatedValue The estimated value to be set
     */
    public void setEstimatedValue(String estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    /**
     * Returns the comment of the item
     *
     * @return The comment of the item
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the comment of the item
     *
     * @param comment The comment to be set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Returns the firestore ID of the item
     *
     * @return The firestore ID of the item
     */
    public String getFirestoreId() {
        return firestoreId;
    }

    /**
     * Sets the firestore ID of the item
     *
     * @param firestoreId The firestore ID to be set
     */
    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    /**
     * Returns the tags of the item
     *
     * @return The tags of the item
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Adds a tag to the item
     *
     * @param tag The tag to be added
     */
    public void addTag(String tag) {
        tags.add(tag);
    }

    /**
     * Sets the tags of the item
     *
     * @param tags The tags to be set
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * Provides a better string representation of the HouseholdItem object
     *
     * @return A string representation of the HouseholdItem object
     */
    @Override
    public String toString() {
        return "Item{" +
                "dateOfPurchase=" + dateOfPurchase +
                ", description='" + description + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", estimatedValue=" + estimatedValue +
                ", comment='" + comment + '\'' +
                ", tags=" + tags +
                '}';
    }
}