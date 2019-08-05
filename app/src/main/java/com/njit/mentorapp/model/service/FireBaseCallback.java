package com.njit.mentorapp.model.service;

import com.google.firebase.storage.StorageReference;

public interface FireBaseCallback
{
    void onCallback(String value);

    void onCallback(StorageReference storageReference);
}
