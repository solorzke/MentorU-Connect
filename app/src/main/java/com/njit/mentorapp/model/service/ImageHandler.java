package com.njit.mentorapp.model.service;

import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ImageHandler
{
    public static void uploadFile(byte [] byteArray, String name, final FireBaseCallback callback)
    {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        String path = "images/" + name + ".jpg";
        final StorageReference riversRef = mStorageRef.child(path);
        riversRef.putBytes(byteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               callback.onCallback(riversRef);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DEBUG_OUTPUT", e.getMessage());
            }
        });
    }
}
