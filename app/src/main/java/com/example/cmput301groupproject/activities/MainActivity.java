package com.example.cmput301groupproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AlertDialog;

import com.example.cmput301groupproject.fragments.FiltersFragment;
import com.example.cmput301groupproject.utility.HouseholdItem;
import com.example.cmput301groupproject.fragments.ItemFragment;
import com.example.cmput301groupproject.R;
import com.example.cmput301groupproject.fragments.SortFragment;
import com.example.cmput301groupproject.adapters.CustomItemList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The MainActivity class represents the main activity of the application
 */
public class MainActivity extends AppCompatActivity implements ItemFragment.OnFragmentInteractionListener, SortFragment.SortListener, FiltersFragment.FiltersFragmentListener {
    private Button selectButton;
    private Button tagButton;
    private Button sortButton;
    private Button filterButton;
    private ListView itemList;
    private FloatingActionButton addItemButton;

    private ArrayList<HouseholdItem> dataList;
    private ArrayAdapter<HouseholdItem> itemAdapter;
    private FirebaseFirestore db;
    private CollectionReference itemsRef;
    private ArrayList<HouseholdItem> unfilteredList;
    private int filterCount = 0;

    /**
     * Initializes the main activity, sets the layout, and defines interactions
     *
     * @param savedInstanceState The saved instance state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        // Retrieve the collection path from the intent if it exists
        String userCollectionPath = getIntent().getStringExtra("userDoc");

        // Only update the collection path if it's not already set
        if (userCollectionPath != null && itemsRef == null) {
            // Use the retrieved collection path to set up the Firestore collection reference
            itemsRef = db.collection(userCollectionPath);
        }

        dataList = new ArrayList<>();

        itemAdapter = new CustomItemList(this, dataList);

        itemList = findViewById(R.id.item_list);
        itemList.setAdapter(itemAdapter);

        // Allows editing or removal of clicked expenses
        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HouseholdItem selectedItem = dataList.get(i);

                ItemFragment.newInstance(selectedItem).show(getSupportFragmentManager(), "EDIT_ITEM");
            }
        });

        itemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });

        addItemButton = findViewById(R.id.add_item_b);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ItemFragment().show(getSupportFragmentManager(), "ADD_ITEM");
            }
        });

            itemsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot querySnapshots,
                                    @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e("Firestore", error.toString());
                        return;
                    }
                    if (querySnapshots != null) {
                        dataList.clear();
                        for (QueryDocumentSnapshot doc: querySnapshots) {
                            String firestoreId = doc.getId(); // Retrieve the auto-generated Firestore ID
                            String description = doc.getString("Description");
                            String dateOfPurchaseString = doc.getString("Purchase Date");
                            String make = doc.getString("Make");
                            String model = doc.getString("Model");
                            String serialNumber = doc.getString("Serial Number");
                            String estimatedValue = doc.getString("Estimated Value");
                            String comment = doc.getString("Comment");

                            Log.d("Firestore", String.format("Item(%s, %s, %s, %s, %s, %s, %s) fetched",
                                    dateOfPurchaseString, description, make, model, serialNumber, estimatedValue, comment));
                            HouseholdItem savedItem = new HouseholdItem(dateOfPurchaseString, description, make, model, serialNumber, estimatedValue, comment);
                            savedItem.setFirestoreId(firestoreId);
                            dataList.add(savedItem);
                        }
                        itemAdapter.notifyDataSetChanged();

                        // Calculate the total estimated value of the items and set it to the TextView
                        setTotalEstimatedValue(dataList);
                    }
                }
            });

        selectButton = findViewById(R.id.selectButton);
        tagButton = findViewById(R.id.tagButton);
        sortButton = findViewById(R.id.sortButton);
        filterButton = findViewById(R.id.filterButton);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send dataList to ListActivity
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("dataList", dataList);
                startActivity(intent);
            }
        });


        tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send userCollectionPath to ViewTagsActivity
                Intent viewTagsIntent = new Intent(MainActivity.this, ViewTagsActivity.class);
                viewTagsIntent.putExtra("userID", userCollectionPath);
                startActivity(viewTagsIntent);
            }
        });



        final int[] lastCheckedItem = {-1}; // Initialize to -1 (no item selected initially)

        // handle the button to open the alert dialog with the single item selection
        sortButton.setOnClickListener(v -> {
            // AlertDialog builder instance to build the alert dialog
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Sort by:");
            // form of list so that user can select the item from
            final String[] listItems = new String[]{"Date","Description", "Make","Estimated Value"};

            final View customLayout = getLayoutInflater().inflate(R.layout.dialog_custom_sort, null);


            alertDialog.setSingleChoiceItems(listItems, lastCheckedItem[0], (dialog, which) -> {
                if (lastCheckedItem[0] == which) {
                    // Clicking the same item again deselects it
                    ((AlertDialog) dialog).getListView().setItemChecked(which, false);
                    lastCheckedItem[0] = -1; // Reset the last checked item index
                }
                else {
                    lastCheckedItem[0] = which; // Update the last checked item index
                }

            });
            alertDialog.setView(customLayout);
            AlertDialog customAlertDialog = alertDialog.create();

            // Handle button clicks for Ascending and Descending within the AlertDialog
            Button btnAscending = customLayout.findViewById(R.id.btnAscending);
            Button btnDescending = customLayout.findViewById(R.id.btnDescending);
            btnAscending.setOnClickListener(view -> {
                // Handle Ascending button click

                if (lastCheckedItem[0] != -1) {
                    int selectedSortCriteria = lastCheckedItem[0];

                    SortFragment sortFragment = new SortFragment();
                    sortFragment.setSortListener(sortedList -> {
                        dataList = sortedList;
                        itemAdapter.notifyDataSetChanged();
                    });
                    // Pass the selected data list and sorting criteria
                    sortFragment.receiveDataList(dataList, selectedSortCriteria, SortFragment.ASCENDING);
                }
                customAlertDialog.dismiss();
            });
            btnDescending.setOnClickListener(view -> {
                // Handle Descending button click
                if (lastCheckedItem[0] != -1) {
                    int selectedSortCriteria = lastCheckedItem[0];

                    SortFragment sortFragment = new SortFragment();
                    sortFragment.setSortListener(sortedList -> {
                        dataList = sortedList;
                        itemAdapter.notifyDataSetChanged();
                    });
                    // Pass the selected data list and sorting criteria
                    sortFragment.receiveDataList(dataList, selectedSortCriteria, SortFragment.DESCENDING);
                }
                customAlertDialog.dismiss();
            });

            customAlertDialog.show();

        });


        filterButton.setOnClickListener(new View.OnClickListener() {    //filter items button listener
            @Override
            public void onClick(View v) {
                filterCount++;
                //creates a new instance of the filters fragment.
                FiltersFragment.newInstance(dataList).show(getSupportFragmentManager(), "FILTER_ITEMS");
            }
        });

        // New code for receiving intents from ListActivity
        handleIntentsFromExternalActivity(getIntent());
    }
    // the SortListener method in MainActivity to receive the sorted list
    @Override
    public void onSortDataList(ArrayList<HouseholdItem> sortedList) {
        // Handle the sorted list received from SortFragment
        // Update data list and refresh the adapter
        dataList = sortedList;
        itemAdapter.notifyDataSetChanged();
    }


    /**
     * Sets the total estimated value of household items
     *
     * @param items The list of HouseholdItem objects
     */
    private void setTotalEstimatedValue(ArrayList<HouseholdItem> items) {
        double totalValue = calculateTotalEstimatedValue(items);
        TextView totalEstimatedValue = findViewById(R.id.total_item_value);
        totalEstimatedValue.setText("Total Estimated Value: $" + totalValue);
    }

    /**
     * Calculates the total estimated value of all items in the list
     *
     * @param items The list of HouseholdItem objects
     * @return The total estimated value as a double
     */
    private double calculateTotalEstimatedValue(ArrayList<HouseholdItem> items) {
        double totalValue = 0.0;

        for (HouseholdItem item : items) {
            String estimatedValueString = item.getEstimatedValue(); // Assuming estimatedValue is a string
            if (estimatedValueString != null && !estimatedValueString.trim().isEmpty()) {
                try {
                    double estimatedValue = Double.parseDouble(estimatedValueString);
                    totalValue += estimatedValue;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        return totalValue;
    }

    /**
     * Adds a new HouseholdItem to the database
     *
     * @param item The HouseholdItem object to be added
     */
    @Override
    public void onHouseholdItemAdded(HouseholdItem item) {
        HashMap<String, String> data = new HashMap<>();
        data.put("Description", item.getDescription());
        data.put("Make", item.getMake());
        data.put("Model", item.getModel());
        data.put("Estimated Value", item.getEstimatedValue());
        data.put("Comment", item.getComment());
        data.put("Serial Number", item.getSerialNumber());
        data.put("Purchase Date", item.getDateOfPurchase());
        itemsRef.add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String documentId = documentReference.getId(); // Store this auto-generated ID for future use
                        Log.d("Firestore", "DocumentSnapshot written with ID: " + documentId);
                    }
                });
    }

    /**
     * Edits a HouseholdItem in the database
     *
     * @param editedItem The edited HouseholdItem object
     */
    public void onHouseholdItemEdited(HouseholdItem editedItem) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("Description", editedItem.getDescription());
        data.put("Make", editedItem.getMake());
        data.put("Model", editedItem.getModel());
        data.put("Estimated Value", editedItem.getEstimatedValue());
        data.put("Comment", editedItem.getComment());
        data.put("Serial Number", editedItem.getSerialNumber());
        data.put("Purchase Date", editedItem.getDateOfPurchase());
        itemsRef.document(editedItem.getFirestoreId())
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error updating document", e);
                    }
                });
    }

    /**
     * Removes a HouseholdItem from the database
     *
     * @param removedItem The HouseholdItem object to be removed
     */
    public void onHouseholdItemRemoved(HouseholdItem removedItem) {
        itemAdapter.remove(removedItem);
        itemsRef.document(removedItem.getFirestoreId()).delete();
        itemAdapter.notifyDataSetChanged();
    }

    /**
     * Adds tags to selected items
     *
     * @param taggedItems The list of HouseholdItem objects to have tags applied
     * @param tags        The list of tags to be applied
     */
    // Method to handle adding tags and performing an action
    public void onTagsApplied(ArrayList<HouseholdItem> taggedItems, ArrayList<String> tags) {
        // Apply tags

    }

    /**
     * Deletes selected items from database
     *
     * @param removedItems The list of HouseholdItem objects to be removed
     */
    public void onListItemsRemoved(ArrayList<HouseholdItem> removedItems) {
        // Delete selected items
        for(HouseholdItem removedItem :removedItems){
            onHouseholdItemRemoved(removedItem);
        }
    }

    /**
     * Handles incoming intents from other activities
     *
     * @param intent The incoming intent
     */
    private void handleIntentsFromExternalActivity(Intent intent) {
        if (intent != null) {
            String command = intent.getStringExtra("command");
            if (command != null) {
                switch (command) {
                    case "applyTags":
                        ArrayList<HouseholdItem> selectedItems = (ArrayList<HouseholdItem>) intent.getSerializableExtra("selectedItems");
                        ArrayList<String> selectedTags = (ArrayList<String>) intent.getSerializableExtra("selectedTags");
                        if (selectedItems != null && selectedTags != null) {
                            // Handle selected items and tags
                            onTagsApplied(selectedItems, selectedTags);
                        }
                        break;
                    case "deleteItems":
                        ArrayList<HouseholdItem> removedItems = (ArrayList<HouseholdItem>) intent.getSerializableExtra("selectedItems");
                        if (removedItems != null) {
                            // Handle removed items
                            onListItemsRemoved(removedItems);
                        }
                        break;
                    default:
                        // Handle unknown command
                        break;
                }
            }
        }
    }

    @Override
    public void onFilterList(ArrayList<HouseholdItem> filteredDataList) {
        if (filterCount < 2) {
            unfilteredList = (ArrayList<HouseholdItem>) dataList.clone();
        }
        if (filteredDataList.isEmpty()) {
            Toast.makeText(MainActivity.this, "No records found with the selected filters", Toast.LENGTH_SHORT).show();
        }
        if (filteredDataList != null && !filteredDataList.isEmpty()) {
            dataList.clear();
            dataList.addAll(filteredDataList);
            itemAdapter.notifyDataSetChanged();
            setTotalEstimatedValue(dataList);
            Log.d("Filtering", "Filtered list displayed");
        } else {
            Log.e("Filtering", "Problem with the list");
        }
    }

    @Override
    public void onRemoveFilters(boolean isUnfiltered) {
        if (isUnfiltered && !unfilteredList.equals(dataList)) {
            dataList.clear();
            dataList.addAll(unfilteredList);
            itemAdapter.notifyDataSetChanged();
            setTotalEstimatedValue(dataList);
        } else {
            Log.e("Removing filters", "Problem with removing filters");
        }
    }
}