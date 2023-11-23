package com.example.cmput301groupproject;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;

public class FilterFragmentTest {
    private ArrayList<HouseholdItem> testItemsList;
    FilterItems filterItems;
    public void setUp() {
        FilterItems filterItems = new FilterItems();
        testItemsList = new ArrayList<>();
        // Populate testItemsList with sample data
        // Item 1
        testItemsList.add(new HouseholdItem(
                "2022/03/18",
                "Description1",
                "Make1",
                "Model1",
                "SerialNumber1",
                "50",
                "Comment1"
        ));

        // Item 2
        testItemsList.add(new HouseholdItem(
                "2022/03/19",
                "Description2",
                "Make2",
                "Model2",
                "SerialNumber2",
                "75",
                "Comment2"
        ));

        testItemsList.add(new HouseholdItem(
                "2022/03/20",
                "Description3",
                "Make3",
                "Model3",
                "SerialNumber3",
                "100",
                "Comment3"
        ));

        filterItems.setItemsList(testItemsList);
    }

    @Test
    public void testFilterByDate() {
        filterItems.filterByDate("2022/02/01", "2022/03/01");
        ArrayList<HouseholdItem> filteredList = filterItems.getFilteredItems();
        assertEquals(1, filteredList.size());
        assertEquals("Description2", filteredList.get(0).getDescription());
    }

    @Test
    public void testFilterByMake() {
        filterItems.filterByMake("Make2");
        ArrayList<HouseholdItem> filteredList = filterItems.getFilteredItems();
        assertEquals(1, filteredList.size());
        assertEquals("Description2", filteredList.get(0).getDescription());
    }

    @Test
    public void testFilterByKeyword() {
        filterItems.filterByKeyword("Description");
        ArrayList<HouseholdItem> filteredList = filterItems.getFilteredItems();
        assertEquals(3, filteredList.size());
        assertEquals("Description1", filteredList.get(0).getDescription());
        assertEquals("Description2", filteredList.get(1).getDescription());
        assertEquals("Description3", filteredList.get(2).getDescription());
    }
}
