package com.example.cmput301groupproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class FiltersFragment extends Fragment {
    private EditText makeFilterText;
    private EditText descriptionKeyword;
    private EditText startDateText;
    private EditText endDateText;
    private Button filterByMakeButton;
    private Button filterByDescButton;
    private Button filterByDateRangeButton;

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



        filterByMakeButton.setOnClickListener(v -> {
            String make = makeFilterText.getText().toString();
            FilterItems fi = new FilterItems();
            if (!make.isEmpty()) {

            } else {
                // Handle empty input
            }
        });

        filterByDescButton.setOnClickListener(v -> {
            String keyword = descriptionKeyword.getText().toString();
            if (!keyword.isEmpty()) {
                // Call filterByKeyword method here
            } else {
                // Handle empty input
            }
        });

        filterByDateRangeButton.setOnClickListener(v -> {
            String start = startDateText.getText().toString();
            String end = endDateText.getText().toString();
            if (isValidDate(start) && isValidDate(end)) {
                // Call filterByDate method here
            } else {
                // Handle invalid date input
            }
        });

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
}

