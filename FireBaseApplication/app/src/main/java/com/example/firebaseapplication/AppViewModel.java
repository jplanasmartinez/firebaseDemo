package com.example.firebaseapplication;


import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.firebaseapplication.models.User;

public class AppViewModel extends ViewModel {
    public static User userLogged;
    public MutableLiveData<Uri> uriImagenSeleccionada = new MutableLiveData<>();

    public void setUriImagenSeleccionada(Uri uri) {
        uriImagenSeleccionada.setValue(uri);
    }
}
