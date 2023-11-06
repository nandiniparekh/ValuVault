package com.example.cmput301groupproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity implements ItemFragment.OnFragmentInteractionListener {
    private ListView itemList;
    private FloatingActionButton addItemButton;
    private TextView totalEstimatedValue;
    private ArrayList<HouseholdItem> dataList;
    private ArrayAdapter<HouseholdItem> itemAdapter;
    private FirebaseFirestore db;
    private CollectionReference itemsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        itemsRef = db.collection("Kendrick_items");
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

        final FloatingActionButton addItemButton = findViewById(R.id.add_item_b);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ItemFragment().show(getSupportFragmentManager(), "ADD_ITEM");
            }
        });

        totalEstimatedValue = findViewById(R.id.total_item_value);

        // Your logic for populating the ListView goes here

        // Calculate the total estimated value of the items and set it to the TextView
        double totalValue = calculateTotalEstimatedValue(dataList); // Define your own method
        totalEstimatedValue.setText("Total Estimated Value: $" + totalValue);

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
                        String description = doc.getId();
                        String dateOfPurchaseString = doc.getString("PurchaseDate");
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
                }
            }
        });

    }

    // Calculate the total estimated value of all items in the list
    private double calculateTotalEstimatedValue(ArrayList<HouseholdItem> items) {
        double totalValue = 0.0;

        for (HouseholdItem item : items) {
            String estimatedValueString = item.getEstimatedValue(); // Assuming estimatedValue is a string
            try {
                double estimatedValue = Double.parseDouble(estimatedValueString);
                totalValue += estimatedValue;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return totalValue;
    }

    @Override
    public void onHouseholdItemAdded(HouseholdItem item) {
        HashMap<String, String> data = new HashMap<>();
        data.put("Make", item.getMake());
        data.put("Model", item.getModel());
        data.put("Estimated Value", item.getEstimatedValue());
        data.put("Comment", item.getComment());
        data.put("Serial Number", item.getSerialNumber());
        data.put("Purchase Date", item.getDateOfPurchase());
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

    public void onHouseholdItemEdited(HouseholdItem editedItem) {

    }

    public void onHouseholdItemRemoved(HouseholdItem removedItem) {

    }
}