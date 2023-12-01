package com.example.cmput301groupproject;

import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/**
 * This is a class that sorts the items in ascending or descending order by date, description, make, and value
 */

public class SortFragment extends Fragment {

    public static final int SORT_BY_DATE = 0;
    public static final int SORT_BY_DESCRIPTION = 1;
    public static final int SORT_BY_MAKE = 2;
    public static final int SORT_BY_VAL = 3;
    public static final int SORT_BY_TAG = 4;
    public static final int ASCENDING = 0;
    public static final int DESCENDING = 1;
    private int sortCriteria;
    private int sortOrder;
    public interface SortListener {
        void onSortDataList(ArrayList<HouseholdItem> sortedList);
    }
    private SortListener sortListener;

    public SortFragment() {
        // Required empty public constructor
    }


    public void setSortListener(SortListener listener) {
        this.sortListener = listener;
    }

    /**
     * This receives dataList from the MainActivity and implement the sort functionality
     * @param dataList
     * This is to sort dataList by sortCriteria and sortOrder
     */
    public void receiveDataList(ArrayList<HouseholdItem> dataList, int sortCriteria,int sortOrder) {
        // Set the sorting criteria and order
        this.sortCriteria = sortCriteria;
        this.sortOrder = sortOrder;

        // Apply sorting based on the selected criteria
        switch (sortCriteria) {
            case SORT_BY_DATE:
                sortDataListDate(dataList);
                break;
            case SORT_BY_DESCRIPTION:
                sortDataListDescription(dataList);
                break;
            case SORT_BY_MAKE:
                sortDataListMake(dataList);
                break;
            case SORT_BY_VAL:
                sortDataListVal(dataList);
                break;
            case SORT_BY_TAG:
                sortDataListTags(dataList);
                break;
        }

        if (sortListener != null) {
            sortListener.onSortDataList(dataList);
        }

    }

    /**
     * This returns a list sorted by Description
     * @return dataList
     * Return the sorted list
     */
    private void sortDataListDescription(ArrayList<HouseholdItem> dataList) {
        Collections.sort(dataList, new Comparator<HouseholdItem>() {
            @Override
            public int compare(HouseholdItem item1, HouseholdItem item2) {
                // Compare items based on Description in alphabetical order (case-insensitive)

                return item1.getDescription().compareToIgnoreCase(item2.getDescription());
            }

        });
        if (sortOrder == DESCENDING) {
            Collections.reverse(dataList);
        }
    }
    /**
     * This returns a list sorted by Date
     * @return dataList
     * Return the sorted list
     */
    private void sortDataListDate(ArrayList<HouseholdItem> dataList) {
        Collections.sort(dataList, new Comparator<HouseholdItem>() {
            @Override
            public int compare(HouseholdItem item1, HouseholdItem item2) {
                // Parse the date strings to Date objects for comparison
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                try {
                    Date date1 = dateFormat.parse(item1.getDateOfPurchase());
                    Date date2 = dateFormat.parse(item2.getDateOfPurchase());


                    if (date1 != null && date2 != null) {
                        // sort by ascending order, so oldest date first
                        return date1.compareTo(date2);
                        // For descending order, use date2.compareTo(date1)
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return 0;

            }
        });
        if (sortOrder == DESCENDING) {
            Collections.reverse(dataList);
        }
    }
    /**
     * This returns a list sorted by Make
     * @return dataList
     * Return the sorted list
     */
    private void sortDataListMake(ArrayList<HouseholdItem> dataList) {
        Collections.sort(dataList, new Comparator<HouseholdItem>() {
            @Override
            public int compare(HouseholdItem item1, HouseholdItem item2) {
                // Compare items based on make and sort in ascending order
                return item1.getMake().compareToIgnoreCase(item2.getMake());
            }
        });
        if (sortOrder == DESCENDING) {
            Collections.reverse(dataList);
        }
    }
    /**
     * This returns a list sorted by Estimated Value
     * @return dataList
     * Return the sorted list
     */
    private void sortDataListVal(ArrayList<HouseholdItem> dataList) {
        Collections.sort(dataList, new Comparator<HouseholdItem>() {
            @Override
            public int compare(HouseholdItem item1, HouseholdItem item2) {
                // Convert estimated values from string to double for comparison
                double value1 = Double.parseDouble(item1.getEstimatedValue());
                double value2 = Double.parseDouble(item2.getEstimatedValue());

                // Compare the parsed values for sorting in ascending order
                return Double.compare(value1, value2);
            }
        });
        if (sortOrder == DESCENDING) {
            Collections.reverse(dataList);
        }
    }
    private void sortDataListTags(ArrayList<HouseholdItem> dataList) {
        Collections.sort(dataList, new Comparator<HouseholdItem>() {
            @Override
            public int compare(HouseholdItem item1, HouseholdItem item2) {
                // Ensure the tags lists are sorted alphabetically for each item
                Collections.sort(item1.getTags(), String.CASE_INSENSITIVE_ORDER);
                Collections.sort(item2.getTags(), String.CASE_INSENSITIVE_ORDER);

                List<String> tags1 = item1.getTags();
                List<String> tags2 = item2.getTags();

                // Compare tags lexicographically
                int minSize = Math.min(tags1.size(), tags2.size());
                for (int i = 0; i < minSize; i++) {
                    int tagComparison = tags1.get(i).compareToIgnoreCase(tags2.get(i));
                    if (tagComparison != 0) {
                        return tagComparison;
                    }
                }

                // If one list is a prefix of the other, or if they're identical,
                // the shorter list should come first
                return tags1.size() - tags2.size();
            }
        });

        if (sortOrder == DESCENDING) {
            Collections.reverse(dataList);
        }
    }


}