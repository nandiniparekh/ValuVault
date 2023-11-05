package com.example.cmput301groupproject;

import android.util.Log;

import androidx.fragment.app.Fragment;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortFragment extends Fragment {
    public interface SortListener {
        void onSortDataList(List<HouseholdItem> sortedList);
    }
    private SortListener sortListener;

    public SortFragment() {
        // Required empty public constructor
    }

    public void setSortListener(SortListener listener) {
        this.sortListener = listener;
    }

    public void receiveDataList(List<HouseholdItem> dataList) {
        Log.d("SortFragment", "Received dataList for sorting: " + dataList.size() + " items");

        if (dataList != null && !dataList.isEmpty()) {
            // If dataList is not empty, sort the dataList
            sortDataListComment(dataList);
            if (sortListener != null) {
                sortListener.onSortDataList(dataList);
            }

        } else {
            // Log a warning if dataList is empty or null
            Log.w("SortFragment", "DataList is empty or null. Unable to perform sorting.");

        }
    }
    private void sortDataListComment(List<HouseholdItem> dataList) {
        Collections.sort(dataList, new Comparator<HouseholdItem>() {
            @Override
            public int compare(HouseholdItem item1, HouseholdItem item2) {
                // Compare items based on comments in alphabetical order (case-insensitive)
                return item1.getComment().compareToIgnoreCase(item2.getComment());
            }
        });
    }

}
