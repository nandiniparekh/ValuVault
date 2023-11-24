package com.example.cmput301groupproject.utility;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TagsManager {

    private static final String TAG = "TagsManager";
    private static final String USERS_COLLECTION = "users";
    private static final String TAGS_FIELD = "tags";
    private static final String TAG_LIST_FIELD = "tagList";

    private FirebaseFirestore db;
    private String userId;
    private CollectionReference userRef;
    private DocumentReference tagsRef;

    public TagsManager(String userId) {
        this.userId = userId;
        db = FirebaseFirestore.getInstance();
        userRef = db.collection(userId);
        tagsRef = userRef.document("tags");

        tagsRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle the error
                    Log.e(TAG, "Error listening for tags: " + e.getMessage());
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Document exists, handle the tags data
                    Map<String, Object> tagsData = documentSnapshot.getData();

                    if (tagsData != null && tagsData.containsKey(TAG_LIST_FIELD)) {
                        // Tags field exists, get the list of tags
                        ArrayList<String> tags = (ArrayList<String>) tagsData.get(TAG_LIST_FIELD);

                        // Now, you can use the 'tags' list as needed
                        Log.d(TAG, "Current tags: " + tags);

                        // Call a method or update UI with the tags
                        //updateTags(tags);
                    }
                } else {
                    // Document does not exist (it might not have been created yet)
                    Log.d(TAG, "Tags document does not exist");

                    // Initialize the tags document with an empty list
                    Map<String, Object> initialTagsData = new HashMap<>();
                    initialTagsData.put(TAG_LIST_FIELD, new ArrayList<String>());

                    // Create the tags document
                    tagsRef.set(initialTagsData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Tags document created successfully");

                                    // Now you can handle the case where the tags document is created
                                    // and it has an empty list of tags
                                    //updateTags(new ArrayList<String>());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Error creating tags document: " + e.getMessage());
                                }
                            });
                }
            }
        });
    }

    public void getTags(final CallbackHandler callback) {
        tagsRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> tagsData = documentSnapshot.getData();
                        if (tagsData != null && tagsData.containsKey(TAG_LIST_FIELD)) {
                            ArrayList<String> tags = (ArrayList<String>) tagsData.get(TAG_LIST_FIELD);
                            callback.onSuccess(tags);
                            return;
                        }
                    }

                    // If the document or tags field is missing, return an empty list
                    callback.onSuccess(new ArrayList<>());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching tags: " + e.getMessage());
                    callback.onFailure(e.getMessage());
                });
    }

    public void addTag(String tag, final CallbackHandler callback) {
        tagsRef.update(TAG_LIST_FIELD, FieldValue.arrayUnion(tag))
                .addOnSuccessListener(aVoid -> callback.onSuccess(tag))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding tag: " + e.getMessage());
                    callback.onFailure(e.getMessage());
                });
    }

    public void deleteTags(ArrayList<String> tagsToDelete, final CallbackHandler callback) {
        tagsRef.update(TAG_LIST_FIELD, FieldValue.arrayRemove(tagsToDelete.toArray()))
                .addOnSuccessListener(aVoid -> callback.onSuccess(tagsToDelete))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting tags: " + e.getMessage());
                    callback.onFailure(e.getMessage());
                });
    }

    public interface CallbackHandler<T> {
        void onSuccess(T result);

        void onFailure(String e);
    }
}


