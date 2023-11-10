package com.example.cmput301groupproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class FiltersFragment extends DialogFragment {

    public interface FiltersFragmentListener {
        void onFilterList(ArrayList<HouseholdItem> filteredDataList);
        void onRemoveFilters(boolean isUnfiltered);
    }

    private FiltersFragmentListener listener;
    private EditText makeFilterText;
    private EditText descriptionKeyword;
    private EditText startDateText;
    private EditText endDateText;
    private Button filterByMakeButton;
    private Button filterByDescButton;
    private Button filterByDateRangeButton;
    private Button removeFilters;

    static ArrayList<HouseholdItem> unfilteredItems = new ArrayList<HouseholdItem>();

    private ArrayList<HouseholdItem> filteredItems = new ArrayList<HouseholdItem>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (FiltersFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement FiltersFragmentListener");
        }
    }

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
        removeFilters = view.findViewById(R.id.removeFilter_button);

        FilterItems filterItems = new FilterItems();
        filterItems.setItemsList(unfilteredItems);

        filterByMakeButton.setOnClickListener(v -> {
            String make = makeFilterText.getText().toString();
            if (!make.isEmpty()) {
                filterItems.filterByMake(make);
                dismiss();
            } else {
                Toast.makeText(getContext(), "The text you entered is invalid", Toast.LENGTH_SHORT).show();
            }
            listener.onFilterList(filterItems.getFilteredItems());
        });

        filterByDescButton.setOnClickListener(v -> {
            String keyword = descriptionKeyword.getText().toString();
            if (!keyword.isEmpty()) {
                filterItems.filterByKeyword(keyword);
                dismiss();
            } else {
                Toast.makeText(getContext(), "The text you entered is invalid", Toast.LENGTH_SHORT).show();
            }
            listener.onFilterList(filterItems.getFilteredItems());
        });

        filterByDateRangeButton.setOnClickListener(v -> {
            String start = startDateText.getText().toString();
            String end = endDateText.getText().toString();

            if (!start.isEmpty() && !end.isEmpty()) {
                filterItems.filterByDate(start, end);
                dismiss();
            } else {
                Toast.makeText(getContext(), "Please enter start and end dates.", Toast.LENGTH_SHORT).show();
            }
            ArrayList<HouseholdItem> items = filterItems.getFilteredItems();
            if (items == null || !items.isEmpty()) {
                Toast.makeText(getContext(), "The entered dates are not valid. Please enter in expected format.", Toast.LENGTH_SHORT).show();
            }
            listener.onFilterList(items);
        });

        removeFilters.setOnClickListener(v -> {
            listener.onRemoveFilters(true);
            dismiss();
        });

        return view;
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


