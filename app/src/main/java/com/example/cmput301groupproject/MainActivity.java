package com.example.cmput301groupproject;

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
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemFragment.OnFragmentInteractionListener, ListFragment.OnFragmentInteractionListener, SortFragment.SortListener, FiltersFragment.FiltersFragmentListener{
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        itemsRef = db.collection("ID_items");
        dataList = new ArrayList<>();
        // Other code omitted

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
                // Maybe switch ListView or just spawn ListFragment
                ListFragment.newInstance(dataList).show(getSupportFragmentManager(), "SELECT_ITEMS");
            }
        });

        tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to tags. Probably source inspiration from "Kendrick" branch
            }
        });

        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement Marzia's stuff
            }
        });


        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterCount++;
                FiltersFragment.newInstance(dataList).show(getSupportFragmentManager(), "FILTER_ITEMS");
            }
        });

    }

    // Method to set the total estimated value
    private void setTotalEstimatedValue(ArrayList<HouseholdItem> items) {
        double totalValue = calculateTotalEstimatedValue(items);
        TextView totalEstimatedValue = findViewById(R.id.total_item_value);
        totalEstimatedValue.setText("Total Estimated Value: $" + totalValue);
    }

    // Calculate the total estimated value of all items in the list
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

    public void onHouseholdItemRemoved(HouseholdItem removedItem) {
        itemAdapter.remove(removedItem);
        itemsRef.document(removedItem.getDescription()).delete();
        itemAdapter.notifyDataSetChanged();
    }

    // Method to handle adding tags and performing an action
    public void onTagsApplied(ArrayList<HouseholdItem> taggedItems, ArrayList<String> tags) {
        // Apply tags

    }

    // Method to handle deleting selected items and performing an action
    public void onListItemsRemoved(ArrayList<HouseholdItem> removedItems) {
        // Delete selected items
        for(HouseholdItem removedItem :removedItems){
            onHouseholdItemRemoved(removedItem);
        }
    }
    // this method to receive the sorted list from the SortFragment
    @Override
    public void onSortDataList(List<HouseholdItem> sortedList) {
        if (sortedList != null && !sortedList.isEmpty()) {
            dataList.clear();
            dataList.addAll(sortedList);
            itemAdapter.notifyDataSetChanged();
            Log.d("Doggy", "Sorted dataList: " + dataList.size() + " items");


        } else {
            Log.e("MainActivity", "Received empty or null sorted list from SortFragment.");
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
        } else {
            Log.e("Removing filters", "Problem with removing filters");
        }
    }
}