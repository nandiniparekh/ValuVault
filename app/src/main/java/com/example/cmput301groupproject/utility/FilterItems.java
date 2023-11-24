package com.example.cmput301groupproject.utility;

import com.example.cmput301groupproject.utility.HouseholdItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
This class contains the functions for filtering list of items based on a few different parameters.
 */
public class FilterItems {

    private ArrayList<HouseholdItem> itemsList;
    private ArrayList<HouseholdItem> filteredItems;

    //Class constructor
    public FilterItems() {
        itemsList = new ArrayList<HouseholdItem>();
        filteredItems = new ArrayList<HouseholdItem>();
    }

    /**
     * Filters the list of items after adding date range constraints.
     *
     * @param start     string that represents the start date for the date range
     * @param end       string that represents the end date for the date range
     */
    public void filterByDate(String start, String end) {
        filteredItems.clear();
        //setting a custom formatter to parse dates all in the same format for comparison
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        //parsing dates
        LocalDate startDate = LocalDate.parse(start, formatter);
        LocalDate endDate = LocalDate.parse(end, formatter);
        //matching items' dates with the date range entered by user to obtain filtered list
        for (HouseholdItem item : itemsList) {
            try {
                LocalDate purchaseDate = LocalDate.parse(item.getDateOfPurchase(), formatter);
                if (purchaseDate.compareTo(startDate) >= 0 && purchaseDate.compareTo(endDate) <= 0) {
                    filteredItems.add(item);
                }
            }
            catch (Exception e) {
               return;
            }
        }
    }

    /**
     * Filters the list of items based on their make
     *
     * @param make string that contains the make for filtering the list of items
     */
    public void filterByMake(String make) {
        filteredItems.clear();
        //matching user input for make against the make of all item records to obtain filtered list
        for (HouseholdItem item : itemsList) {
            if (item.getMake().equalsIgnoreCase(make)) {
                filteredItems.add(item);
            }
        }
    }

    /**
     * Filters the list of items based on if a keyword exists in the description
     *
     * @param word string that contains the keyword to check in items' descriptions
     */
    public void filterByKeyword(String word) {
        String keyword = word.toLowerCase();
        filteredItems.clear();
        //checking if the keyword is contained by any description of the items to obtain filtered list.
        for (HouseholdItem item : itemsList){
            String description = item.getDescription().toLowerCase();
            if (description.contains(keyword)) {
                filteredItems.add(item);
            }
        }
    }

    /**
     * Setter function for the unifiltered items list.
     *
     * @param itemsList ArrayList of the type HouseholdItem which is the original items list to be filtered
     */
    public void setItemsList(ArrayList<HouseholdItem> itemsList) {
        this.itemsList = itemsList;
    }

    /**
     * Returns the filtered items list after adding requested constraints
     *
     * @return filteredItems     ArrayList of the type HouseholdItem which is the filtered items list.
     */
    public ArrayList<HouseholdItem> getFilteredItems() {
        return filteredItems;
    }

}
