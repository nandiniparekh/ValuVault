package com.example.cmput301groupproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class FiltersFragment extends DialogFragment {
    private EditText makeFilterText;
    private EditText descriptionKeyword;
    private EditText startDateText;
    private EditText endDateText;
    private Button filterByMakeButton;
    private Button filterByDescButton;
    private Button filterByDateRangeButton;


    static ArrayList<HouseholdItem> unfilteredItems = new ArrayList<HouseholdItem>();

    private ArrayList<HouseholdItem> filteredItems = new ArrayList<HouseholdItem>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_items_fragment, container, false);

        makeFilterText = view.findViewById(R.id.makeFilter_text);
        descriptionKeyword = view.findViewById(R.id.description_keyword);
        startDateText = view.findViewById(R.id.editText);
        endDateText = view.findViewById(R.id.endDate_filter_text);
        filterByMakeButton = view.findViewById(R.id.filterbymake_button);
        filterByDescButton = view.findViewById(R.id.filterByDesc_button);
        filterByDateRangeButton = view.findViewById(R.id.dateFilter_button);

        FilterItems filterItems = new FilterItems();
        filterItems.setItemsList(unfilteredItems);

        filterByMakeButton.setOnClickListener(v -> {
            String make = makeFilterText.getText().toString();
            if (!make.isEmpty()) {
                filterItems.filterByMake(make);
            } else {
            }
            dismiss();
        });

        filterByDescButton.setOnClickListener(v -> {
            String keyword = descriptionKeyword.getText().toString();
            if (!keyword.isEmpty()) {
                filterItems.filterByKeyword(keyword);
            } else {
            }
            dismiss();
        });

        filterByDateRangeButton.setOnClickListener(v -> {
            String start = startDateText.getText().toString();
            String end = endDateText.getText().toString();
            if (isValidDate(start) && isValidDate(end)) {
                filterItems.filterByDate(start, end);
            } else {
            }
            dismiss();
        });

        filteredItems = filterItems.getFilteredItems();

        return view;
    }


    private boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static FiltersFragment newInstance(ArrayList<HouseholdItem> items) {
        FiltersFragment fragment = new FiltersFragment();

        Bundle args = new Bundle();
        args.putSerializable("items", items);

        unfilteredItems = items;

        fragment.setArguments(args);
        return fragment;
    }

}


