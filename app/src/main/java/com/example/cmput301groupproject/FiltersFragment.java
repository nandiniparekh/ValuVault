package com.example.cmput301groupproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;


/**
 * This class represents the Filters fragment that pops up when the Filter button is clicked on Main Screen
 */
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
    private Button filterByTags;
    private ListView listViewTags;
    private TextView selectedTagsTV;

    static ArrayList<HouseholdItem> unfilteredItems = new ArrayList<HouseholdItem>();
    private final ArrayList<HouseholdItem> filteredItems = new ArrayList<HouseholdItem>();
    private ArrayList<String> tagsList = new ArrayList<String>();
    private ArrayList<String> selectedTags = new ArrayList<String>();

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

        //finding and assigning buttons from the layout
        makeFilterText = view.findViewById(R.id.makeFilter_text);
        descriptionKeyword = view.findViewById(R.id.description_keyword);
        startDateText = view.findViewById(R.id.editText);
        endDateText = view.findViewById(R.id.endDate_filter_text);
        filterByMakeButton = view.findViewById(R.id.filterbymake_button);
        filterByDescButton = view.findViewById(R.id.filterByDesc_button);
        filterByDateRangeButton = view.findViewById(R.id.dateFilter_button);
        removeFilters = view.findViewById(R.id.removeFilter_button);
        filterByTags = view.findViewById(R.id.filter_tags_button);
        listViewTags = view.findViewById(R.id.list_filter_tags);

        SharedPreferences preferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String userCollectionPath = preferences.getString("userCollectionPath", null);
        TagsManager tagsManager = new TagsManager(userCollectionPath);

        TagsAdapter tagsAdapter = new TagsAdapter(getContext(), tagsList);
        listViewTags.setAdapter(tagsAdapter);

        tagsManager.getTags(new TagsManager.CallbackHandler<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> result) {
                tagsList.clear();
                tagsList.addAll(result);
                tagsAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(String e) {
                Log.e("Filter TAGS", "Error fetching tags: " + e);
            }
        });

        //creating FilterItems object and setting the unfiltered list
        FilterItems filterItems = new FilterItems();
        filterItems.setItemsList(unfilteredItems);


        // Set up checkbox click listener for selecting and deselecting items
        tagsAdapter.onTagCheckedChangeListener((position, isChecked) -> {
            selectedTags.clear();
            String selectedTag = tagsList.get(position);
            if (isChecked && !selectedTags.contains(selectedTag)) {
                selectedTags.add(selectedTag);
            }
        });


        filterByTags.setOnClickListener(v -> {
            if (selectedTags.isEmpty())
                Toast.makeText(getContext(), "You have not selected any tags. Please select one or more tags.", Toast.LENGTH_SHORT).show();
            else
                filterItems.filterByTags(selectedTags);
            listener.onFilterList(filterItems.getFilteredItems());
            dismiss();
        });

        filterByMakeButton.setOnClickListener(v -> {
            String make = makeFilterText.getText().toString();
            //calls the filterByMake function in FilterItems class if this button is clicked
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
            //calls the filterByKeyword function in the FilterItems class if this button is clicked
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
            //calls the filterByDate function in the FilterItems class if this button is clicked
            if (!start.isEmpty() && !end.isEmpty()) {
                filterItems.filterByDate(start, end);
                dismiss();
            } else {
                Toast.makeText(getContext(), "Please enter start and end dates.", Toast.LENGTH_SHORT).show();
            }
            ArrayList<HouseholdItem> items = filterItems.getFilteredItems();
            if (items == null || items.isEmpty()) {
                Toast.makeText(getContext(), "The entered dates are not valid. Please enter in expected format.", Toast.LENGTH_SHORT).show();
            }
            listener.onFilterList(items);
        });

        removeFilters.setOnClickListener(v -> {
            //removes the filters on the list of items when this button is called.
            listener.onRemoveFilters(true);
            dismiss();
        });

        return view;
    }

    /**
     * Creates a new instance of FiltersFragment with a given list of items.
     *
     * @param items An ArrayList of HouseholdItem objects (unfiltered) to be passed to the fragment.
     * @return A new instance of FiltersFragment with the provided items as its arguments.
     */
    public static FiltersFragment newInstance(ArrayList<HouseholdItem> items) {
        FiltersFragment fragment = new FiltersFragment();

        Bundle args = new Bundle();
        args.putSerializable("items", items);

        unfilteredItems = items;

        fragment.setArguments(args);
        return fragment;
    }

}


