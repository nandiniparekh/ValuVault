package com.example.cmput301groupproject;


import static org.junit.Assert.assertEquals;

import com.example.cmput301groupproject.fragments.SortFragment;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class SortFragmentTest {

    private SortFragment sortFragment;
    private ArrayList<HouseholdItem> testItems;

    @Before
    public void setUp() {
        sortFragment = new SortFragment();
        testItems = new ArrayList<>();

        // Populate testItems with sample HouseholdItem data for testing
        testItems.add(new HouseholdItem("2023/01/15", "Item1", "MakeX", "100", "26", "11", "beepboop"));
        testItems.add(new HouseholdItem("2023/11/26", "Item2", "MakeY", "150", "669", "100", "beepboopblah"));
        testItems.add(new HouseholdItem("2023/08/01", "Item3", "Dasom", "korean","16","500","beepboopblahblah?"));
    }

        @Test
    public void testSortByDateAscending() {
        sortFragment.receiveDataList(testItems, SortFragment.SORT_BY_DATE, SortFragment.ASCENDING);
        ArrayList<HouseholdItem> sortedList = getSortedList();
        //System.out.print(sortedList.get(0).getDateOfPurchase());
        assertEquals("2023/01/15", sortedList.get(0).getDateOfPurchase());
        assertEquals("2023/08/01", sortedList.get(1).getDateOfPurchase());
        assertEquals("2023/11/26", sortedList.get(2).getDateOfPurchase());
    }

    @Test
    public void testSortByDateDescending() {
        sortFragment.receiveDataList(testItems, SortFragment.SORT_BY_DATE, SortFragment.DESCENDING);
        ArrayList<HouseholdItem> sortedList = getSortedList();
        assertEquals("2023/11/26", sortedList.get(0).getDateOfPurchase());
        assertEquals("2023/08/01", sortedList.get(1).getDateOfPurchase());
        assertEquals("2023/01/15", sortedList.get(2).getDateOfPurchase());


    }

    @Test
    public void testSortByDescriptionAscending() {
        sortFragment.receiveDataList(testItems, SortFragment.SORT_BY_DESCRIPTION, SortFragment.ASCENDING);
        ArrayList<HouseholdItem> sortedList = getSortedList();
        // Add assertions for description sorting in ascending order
        assertEquals("Item1", sortedList.get(0).getDescription());
        assertEquals("Item2", sortedList.get(1).getDescription());
        assertEquals("Item3", sortedList.get(2).getDescription());
    }

    @Test
    public void testSortByDescriptionDescending() {
        sortFragment.receiveDataList(testItems, SortFragment.SORT_BY_DESCRIPTION, SortFragment.DESCENDING);
        ArrayList<HouseholdItem> sortedList = getSortedList();
        // Add assertions for description sorting in descending order
        assertEquals("Item3", sortedList.get(0).getDescription());
        assertEquals("Item2", sortedList.get(1).getDescription());
        assertEquals("Item1", sortedList.get(2).getDescription());
    }

    @Test
    public void testSortByMakeAscending() {
        sortFragment.receiveDataList(testItems, SortFragment.SORT_BY_MAKE, SortFragment.ASCENDING);
        ArrayList<HouseholdItem> sortedList = getSortedList();
        // Add assertions for make sorting in ascending order
        assertEquals("Dasom", sortedList.get(0).getMake());
        assertEquals("MakeX", sortedList.get(1).getMake());
        assertEquals("MakeY", sortedList.get(2).getMake());



    }

    @Test
    public void testSortByMakeDescending() {
        sortFragment.receiveDataList(testItems, SortFragment.SORT_BY_MAKE, SortFragment.DESCENDING);
        ArrayList<HouseholdItem> sortedList = getSortedList();
        // Add assertions for make sorting in descending order

        assertEquals("MakeY", sortedList.get(0).getMake());
        assertEquals("MakeX", sortedList.get(1).getMake());
        assertEquals("Dasom", sortedList.get(2).getMake());
    }

    @Test
    public void testSortByValueAscending() {
        sortFragment.receiveDataList(testItems, SortFragment.SORT_BY_VAL, SortFragment.ASCENDING);
        ArrayList<HouseholdItem> sortedList = getSortedList();
        // Add assertions for value sorting in ascending order
        assertEquals("11", sortedList.get(0).getEstimatedValue());
        assertEquals("100", sortedList.get(1).getEstimatedValue());
        assertEquals("500", sortedList.get(2).getEstimatedValue());
    }

    @Test
    public void testSortByValueDescending() {
        sortFragment.receiveDataList(testItems, SortFragment.SORT_BY_VAL, SortFragment.DESCENDING);
        ArrayList<HouseholdItem> sortedList = getSortedList();
        // Add assertions for value sorting in descending order

        assertEquals("500", sortedList.get(0).getEstimatedValue());
        assertEquals("100", sortedList.get(1).getEstimatedValue());
        assertEquals("11", sortedList.get(2).getEstimatedValue());
    }

    private ArrayList<HouseholdItem> getSortedList() {
        // Helper method to retrieve the sorted list after sorting is applied
        // Adjust this method to fit your implementation.
        return testItems; // For a basic example
    }
}
