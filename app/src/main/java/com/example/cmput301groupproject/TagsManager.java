package com.example.cmput301groupproject;

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

/**
 * The `TagsManager` class handles interactions with Firebase Firestore for managing tags.
 * It provides methods for retrieving, adding, and deleting tags associated with a user.
 */
public class TagsManager {

    private static final String TAG = "TagsManager";
    private static final String USERS_COLLECTION = "users";
    private static final String TAGS_FIELD = "tags";
    private static final String TAG_LIST_FIELD = "tagList";

    private FirebaseFirestore db;
    private String userId;
    private CollectionReference userRef;
    private DocumentReference tagsRef;

    /**
     * Constructs a new `TagsManager` instance for the specified user.
     *
     * @param userId The unique identifier of the user.
     */
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

                        // Log available tags
                        Log.d(TAG, "Current tags: " + tags);
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

    /**
     * Fetches the list of tags associated with the user from Firestore.
     *
     * @param callback The callback to handle the result or failure.
     */
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

    /**
     * Adds a new tag to the user's list of tags in Firestore.
     *
     * @param tag      The tag to be added.
     * @param callback The callback to handle the result or failure.
     */
    public void addTag(String tag, final CallbackHandler callback) {
        tagsRef.update(TAG_LIST_FIELD, FieldValue.arrayUnion(tag))
                .addOnSuccessListener(aVoid -> callback.onSuccess(tag))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding tag: " + e.getMessage());
                    callback.onFailure(e.getMessage());
                });
    }

    /**
     * Deletes specified tags from the user's list of tags in Firestore.
     *
     * @param tagsToDelete The list of tags to be deleted.
     * @param callback     The callback to handle the result or failure.
     */
    public void deleteTags(ArrayList<String> tagsToDelete, final CallbackHandler callback) {
        tagsRef.update(TAG_LIST_FIELD, FieldValue.arrayRemove(tagsToDelete.toArray()))
                .addOnSuccessListener(aVoid -> callback.onSuccess(tagsToDelete))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting tags: " + e.getMessage());
                    callback.onFailure(e.getMessage());
                });
    }

    /**
     * Callback interface for handling asynchronous results or failures.
     *
     * @param <T> The type of the result.
     */
    public interface CallbackHandler<T> {
        /**
         * Called when the operation is successful.
         *
         * @param result The result of the operation.
         */
        void onSuccess(T result);

        /**
         * Called when the operation encounters a failure.
         *
         * @param e The error message.
         */
        void onFailure(String e);
    }
}


