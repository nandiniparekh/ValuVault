package com.example.cmput301groupproject;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class FilterFragmentTest {
    private ArrayList<HouseholdItem> testItemsList;
    FilterItems filterItems;

    @Before
    public void setUp() {
        filterItems = new FilterItems();
        testItemsList = new ArrayList<>();
        // Populate testItemsList with sample data
        // Item 1
        HouseholdItem item1 = new HouseholdItem("2022/03/18",
                "Description1",
                "Make1",
                "Model1",
                "SerialNumber1",
                "50",
                "Comment1");
        item1.addTag("ok");
        testItemsList.add(item1);

        // Item 2
        HouseholdItem item2 = new HouseholdItem("2022/03/19",
                "Description2",
                "Make2",
                "Model2",
                "SerialNumber2",
                "75",
                "Comment2");
        item2.addTag("hello");
        testItemsList.add(item2);

        HouseholdItem item3 = new HouseholdItem("2023/02/22",
                "Description3",
                "Make3",
                "Model3",
                "SerialNumber3",
                "72",
                "Comment3");
        item3.addTag("tag3");
        item3.addTag("ok");
        testItemsList.add(item3);

        filterItems.setItemsList(testItemsList);
    }

    @Test
    public void testFilterByDate() {
        filterItems.filterByDate("2022/02/01", "2022/05/01");
        ArrayList<HouseholdItem> filteredList = filterItems.getFilteredItems();
        assertEquals(2, filteredList.size());
        assertEquals("Description1", filteredList.get(0).getDescription());
        assertEquals("Description2", filteredList.get(1).getDescription());
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

    @Test
    public void testFilterByTags() {
        ArrayList<String> tagsFilter = new ArrayList<String>();
        tagsFilter.add("ok");
        filterItems.filterByTags(tagsFilter);
        ArrayList<HouseholdItem> filteredList = filterItems.getFilteredItems();
        assertEquals(2, filteredList.size());
        assertEquals("Description1", filteredList.get(0).getDescription());
        assertEquals("Description3", filteredList.get(1).getDescription());
    }
}
