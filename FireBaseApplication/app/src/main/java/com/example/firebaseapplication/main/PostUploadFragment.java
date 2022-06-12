package com.example.firebaseapplication.main;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebaseapplication.AppFragment;
import com.example.firebaseapplication.R;
import com.example.firebaseapplication.models.Post;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;

import java.util.UUID;

public class PostUploadFragment extends AppFragment {

    private static String postPlaceHolder = "https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png?20150327203541";
    private Uri uriPostPic;
    private TextInputEditText uploadPostText;
    private Button uploadPostButton;
    private ImageView uploadPostImage;
    private CardView uploadPostCard;

    public PostUploadFragment() {
        // Required empty public constructor
    }

    public static PostUploadFragment newInstance(String param1, String param2) {
        return new PostUploadFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_upload, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hook(view);
        final ActivityResultLauncher<String> phoneGallery = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                uriPostPic = uri;
                Glide.with(requireContext())
                        .load(uri)
                        .centerCrop()
                        .into(uploadPostImage);
                appViewModel.setUriImagenSeleccionada(uri);
            } else {
                uriPostPic = null;
                Glide.with(uploadPostImage).load(R.drawable.placeholder);
            }
        });
        uploadPostCard.setOnClickListener(v -> {
            phoneGallery.launch("image/*");
        });
        uploadPostButton.setOnClickListener(v -> {
            if (uriPostPic != null && !uploadPostText.getText().toString().equals("")){
                newPost();
            }
        });
    }

    private void hook(View view) {
        uploadPostText = view.findViewById(R.id.uploadPostText);
        uploadPostButton = view.findViewById(R.id.uploadPostButton);
        uploadPostImage = view.findViewById(R.id.uploadPostImage);
        uploadPostCard = view.findViewById(R.id.uploadPostCard);
    }
    private void newPost() {
        Post post = new Post(auth.getCurrentUser().getUid(), postPlaceHolder, uploadPostText.getText().toString());
        FirebaseStorage.getInstance().getReference("/profileimgs/" + UUID.randomUUID() + ".jpg")
            .putFile(uriPostPic)
            .continueWithTask(task2 -> task2.getResult().getStorage().getDownloadUrl())
            .addOnSuccessListener(imageUrl -> {

                post.postImage = imageUrl.toString();
                db.collection("posts").document(post.postId).set(post).addOnCompleteListener(task -> {
                    Toast.makeText(requireContext(), R.string.upload_post_success, Toast.LENGTH_SHORT).show();
                    setFragment(new HomeFragment());
                });
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