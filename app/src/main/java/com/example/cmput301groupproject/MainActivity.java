package com.example.cmput301groupproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AlertDialog;

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

public class MainActivity extends AppCompatActivity implements ItemFragment.OnFragmentInteractionListener, SortFragment.SortListener {
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
            }
        });

        tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to tags. Probably source inspiration from "Kendrick" branch
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

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement Nandini's stuff
            }
        });

    }
    // the SortListener method in MainActivity to receive the sorted list
    @Override
    public void onSortDataList(ArrayList<HouseholdItem> sortedList) {
        // Handle the sorted list received from SortFragment
        // Update data list and refresh the adapter
        dataList = sortedList;
        itemAdapter.notifyDataSetChanged();
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
    // this method to receive the sorted list from the SortFragment


}