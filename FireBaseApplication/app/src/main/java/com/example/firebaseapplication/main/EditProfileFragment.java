package com.example.firebaseapplication.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebaseapplication.AppFragment;
import com.example.firebaseapplication.R;
import com.example.firebaseapplication.init.InitActivity;
import com.example.firebaseapplication.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;
import java.util.UUID;

public class EditProfileFragment extends AppFragment {
    ImageView uploadImage;
    TextInputEditText userEditText;
    CardView uploadCard;
    Button saveChanges;

    boolean photoClicked = false;
    private MutableLiveData<Uri> uriProfilePic = new MutableLiveData<>();
    private static String profilePlaceholder = "https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png?20150327203541";

    public EditProfileFragment() {
        // Required empty public constructor
    }
    public static EditProfileFragment newInstance(String param1, String param2) {
        return new EditProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hook(view);

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity)getActivity()).toolbar.setNavigationIcon(R.drawable.icon_log_out);
        ((MainActivity)getActivity()).toolbar.setNavigationOnClickListener(view1 -> {
            setFragment(new HomeFragment());
        });
        Glide.with(view).load(profilePlaceholder).centerCrop().into(uploadImage);
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    if (photoClicked){
                        Glide.with(view).load(user.profileImageURL).centerCrop().into(uploadImage);
                    }
                    userEditText.setHint(user.username);
                }
            }
        });
        final ActivityResultLauncher<String> phoneGallery = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                uriProfilePic.postValue(uri);
                Glide.with(requireContext())
                        .load(uri)
                        .centerCrop()
                        .into(uploadImage);
                appViewModel.setUriImagenSeleccionada(uri);
            }
        });

        uploadCard.setOnClickListener(v -> {
            photoClicked = true;
            phoneGallery.launch("image/*");
        });

        saveChanges.setOnClickListener(view1 -> {
            changeUser();
        });

    }



    private void hook(View view) {
        uploadImage = view.findViewById(R.id.uploadImage);
        userEditText = view.findViewById(R.id.userEditText);
        uploadCard = view.findViewById(R.id.uploadCard);
        saveChanges = view.findViewById(R.id.saveChanges);
    }
    private void changeUser() {
        String newName = userEditText.getText().toString();
        boolean somethingChanged = false;
        if (uriProfilePic != null){
            somethingChanged = changeProfileImage();
        }
        if (newName.length() > 0){
            changeProfileText(newName);
            somethingChanged = true;
        }
        if (somethingChanged){
            Toast.makeText(getContext(), R.string.user_updated, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), InitActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), R.string.user_not_updated, Toast.LENGTH_SHORT).show();
            setFragment(new HomeFragment());
        }


    }
    private boolean changeProfileImage(){
        if (!photoClicked){
            return false;
        }
        uriProfilePic.observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                FirebaseStorage.getInstance().getReference("/profileimgs/" + UUID.randomUUID() + ".jpg")
                        .putFile(uri)
                        .continueWithTask(task2 -> task2.getResult().getStorage().getDownloadUrl())
                        .addOnSuccessListener(this::updateUserPic);
            }
        });
        return true;
    }
    private void updateUserPic(Uri imageUri) {
        String imageUrl = "";

        if (imageUri != null && !imageUri.toString().equals("")) {
            imageUrl = imageUri.toString();
        }

        db.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).update("profileImageURL", imageUrl);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(imageUrl))
                .build();

        auth.getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(task -> {
            DocumentReference userDoc = db.collection("users").document(auth.getCurrentUser().getUid());
        });
    }
    private void changeProfileText(String newName){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();
        Objects.requireNonNull(auth.getCurrentUser()).updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentReference userDoc = db.collection("users").document(auth.getCurrentUser().getUid());
                        userDoc.update("username", newName);
                    }
                });
    }
    private void setFragment(Fragment fragment) {
        if (getFragmentManager() != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout_main, fragment)
                    .commit();
        }
    }

}