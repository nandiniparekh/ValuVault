package com.example.cmput301groupproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemFragment.OnFragmentInteractionListener,SortFragment.SortListener {
    private ArrayList<HouseholdItem> dataList;
    private ListView itemList;
    private ArrayAdapter<HouseholdItem> itemAdapter;
    private FirebaseFirestore db;
    private CollectionReference itemsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        itemsRef = db.collection("items");
        dataList = new ArrayList<>();
        // Other code omitted

        itemAdapter = new CustomItemList(this, dataList);

        itemList = findViewById(R.id.item_list);
        itemList.setAdapter(itemAdapter);

        final FloatingActionButton editItemsButton = findViewById(R.id.edit_items_b);
        editItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ItemFragment().show(getSupportFragmentManager(), "EDIT_ITEM");
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
                        String dateOfPurchaseString = doc.getString("PurchaseDate");
                        String description = doc.getString("Description");
                        String make = doc.getString("Make");
                        String model = doc.getString("Model");
                        String serialNumber = doc.getString("SerialNumber");
                        String estimatedValue = doc.getString("EstimatedValue");
                        String comment = doc.getString("Comment");

                        Log.d("Firestore", String.format("Item(%s, %s, %s, %s, %s, %s, %s) fetched",
                                dateOfPurchaseString, description, make, model, serialNumber, estimatedValue, comment));
                        dataList.add(new HouseholdItem(dateOfPurchaseString, description, make, model, serialNumber, estimatedValue, comment));
                    }
                    itemAdapter.notifyDataSetChanged();

                    // *********** this part below works ************
                    //SortFragment SortFragment = new SortFragment();
                    //SortFragment.receiveDataList(dataList);

                }
            }
        });

        // Find the button
        Button btnSort = findViewById(R.id.btn_sort);

        // Set OnClickListener for the sort button
        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Button", "Sort Button Clicked");

                // *************** but this part doesn't ******************

                // Call the SortFragment to sort the list when the button is clicked
                SortFragment sortFragment = new SortFragment();
                sortFragment.setSortListener(MainActivity.this); // Setting the listener to the activity
                sortFragment.receiveDataList(dataList);
            }
        });




    }



    @Override
    public void onOKPressed(HouseholdItem item) {
        HashMap<String, String> data = new HashMap<>();
        data.put("Make", item.getMake());
        data.put("Model", item.getModel());
        data.put("Estimated Value", item.getEstimatedValue());
        data.put("Comment", item.getComment());
        data.put("Serial Number", item.getSerialNumber());
        itemsRef.document(item.getDescription()).set(data);

        itemsRef
                .document(item.getDescription())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                });

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

}