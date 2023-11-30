package com.example.cmput301groupproject;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class SortFragmentTest {

    private SortFragment sortFragment;
    private ArrayList<HouseholdItem> testItems;

    @Before
    public void setUp() {
        sortFragment = new SortFragment();
        testItems = new ArrayList<>();

        // Populate testItems with sample HouseholdItem data for testing
        testItems.add(new HouseholdItem("2023/01/15", "Item1", "MakeX", "100", "26", "11", "beepboop"));
        //item1.setTags(new ArrayList<>(Arrays.asList("end", "apple", "zebra")));
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
    @Test
    public void testSortByTagsAlphabetically() {
        // Setup the test environment
        ArrayList<HouseholdItem> testItemsWithTags = new ArrayList<>();

        HouseholdItem item1 = new HouseholdItem("2023/01/01", "Item1", "Make1", "Model1", "Serial1", "100", "Comment1");
        item1.setTags(new ArrayList<>(Arrays.asList("banana", "apple", "cherry")));
        HouseholdItem item2 = new HouseholdItem("2023/01/02", "Item2", "Make2", "Model2", "Serial2", "200", "Comment2");
        item2.setTags(new ArrayList<>(Arrays.asList("pineapple", "banana")));
        HouseholdItem item3 = new HouseholdItem("2023/01/03", "Item3", "Make3", "Model3", "Serial3", "300", "Comment3");
        item3.setTags(new ArrayList<>(Arrays.asList("banana", "cherry")));

        testItemsWithTags.add(item1);
        testItemsWithTags.add(item2);
        testItemsWithTags.add(item3);

        // Call the method to sort by tags
        sortFragment.receiveDataList(testItemsWithTags,SortFragment.SORT_BY_TAG,SortFragment.ASCENDING);

        // Assertions
        // Check if the items are sorted in the correct order based on their tags
        assertEquals("Item1", testItemsWithTags.get(0).getDescription()); // 'apple' comes first alphabetically
        assertEquals("Item3", testItemsWithTags.get(1).getDescription()); // 'banana' is next
        assertEquals("Item2", testItemsWithTags.get(2).getDescription()); // 'cherry' is last
    }


    private ArrayList<HouseholdItem> getSortedList() {
        // Helper method to retrieve the sorted list after sorting is applied
        return testItems;
    }
}
