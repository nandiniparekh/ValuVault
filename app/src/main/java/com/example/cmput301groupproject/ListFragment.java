package com.example.cmput301groupproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class ListFragment extends DialogFragment {
    private Button backSelectButton;
    private Button applyTagsButton;
    private Button deleteSelectedItemsButton;
    private ListView selectItemList;
    private ArrayList<HouseholdItem> passedDataList;
    private ArrayAdapter<HouseholdItem> listAdapter;
    private ArrayList<HouseholdItem> selectedItems;
    private ArrayList<String> selectedTags;
    private OnFragmentInteractionListener listListener;

    public interface OnFragmentInteractionListener {
        void onTagsApplied(ArrayList<HouseholdItem> taggedItems, ArrayList<String> tags);
        void onListItemsRemoved(ArrayList<HouseholdItem> removedItems);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context + "OnFragmentInteractionListener is not implemented");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.edit_list_fragment, container, false);

        selectItemList = view.findViewById(R.id.select_item_list);
        passedDataList = new ArrayList<>();

        Bundle args = getArguments();
        if (args != null) {
            passedDataList = (ArrayList<HouseholdItem>) args.getSerializable("items");
        }

        // Find the buttons in the layout
        backSelectButton = view.findViewById((R.id.backSelectButton));
        applyTagsButton = view.findViewById(R.id.applyTagsButton);
        deleteSelectedItemsButton = view.findViewById(R.id.deleteSelectedItemsButton);

        // Set click listeners for the buttons
        backSelectButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Exit fragment
            }
        }));
        applyTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listListener.onTagsApplied(selectedItems, selectedTags);
            }
        });

        deleteSelectedItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listListener.onListItemsRemoved(selectedItems);
            }
        });

        return view;
    }



    public static ListFragment newInstance(ArrayList<HouseholdItem> items) {
        ListFragment fragment = new ListFragment();

        Bundle args = new Bundle();
        args.putSerializable("items", items);

        fragment.setArguments(args);
        return fragment;
    }
}

