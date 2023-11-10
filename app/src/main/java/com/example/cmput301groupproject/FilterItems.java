package com.example.cmput301groupproject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class FilterItems {

    private ArrayList<HouseholdItem> itemsList;
    private ArrayList<HouseholdItem> filteredItems;

    public FilterItems() {
        itemsList = new ArrayList<HouseholdItem>();
        filteredItems = new ArrayList<HouseholdItem>();
    }

    public void filterByDate(String start, String end) {
        filteredItems.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate startDate = LocalDate.parse(start, formatter);
        LocalDate endDate = LocalDate.parse(end, formatter);
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

    public void filterByMake(String make) {
        filteredItems.clear();
        for (HouseholdItem item : itemsList) {
            if (item.getMake().equalsIgnoreCase(make)) {
                filteredItems.add(item);
            }
        }
    }

    public void filterByKeyword(String word) {
        String keyword = word.toLowerCase();
        filteredItems.clear();
        for (HouseholdItem item : itemsList){
            String description = item.getDescription().toLowerCase();
            if (description.contains(keyword)) {
                filteredItems.add(item);
            }
        }
    }

    public void setItemsList(ArrayList<HouseholdItem> itemsList) {
        this.itemsList = itemsList;
    }

    public ArrayList<HouseholdItem> getFilteredItems() {
        return filteredItems;
    }

}
