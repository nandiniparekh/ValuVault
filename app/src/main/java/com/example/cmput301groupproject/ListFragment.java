package com.example.cmput301groupproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ListFragment extends Fragment {
    private Button backSelectButton;
    private Button applyTagsButton;
    private Button deleteSelectedItemsButton;
    private ListView itemList;
    private ArrayList<HouseholdItem> passedItemList;
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

        // Find the buttons in the layout
        Button applyTagsButton = view.findViewById(R.id.applyTagsButton);
        Button deleteSelectedItemsButton = view.findViewById(R.id.deleteSelectedItemsButton);

        // Set click listeners for the buttons
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



    public static ItemFragment newInstance(ArrayList<HouseholdItem> items) {
        ItemFragment fragment = new ItemFragment();

        Bundle args = new Bundle();
        args.putSerializable("items", items);

        fragment.setArguments(args);
        return fragment;
    }
}

