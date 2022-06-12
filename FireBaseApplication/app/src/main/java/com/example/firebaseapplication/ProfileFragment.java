package com.example.firebaseapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.firebaseapplication.main.CommentPostAdapter;
import com.example.firebaseapplication.main.HomeFragment;
import com.example.firebaseapplication.main.MainActivity;
import com.example.firebaseapplication.main.PostAdapter;
import com.example.firebaseapplication.models.Comment;
import com.example.firebaseapplication.models.Post;
import com.example.firebaseapplication.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends AppFragment {
    String userId;
    List<Comment> comments = new ArrayList<>();
    ImageView profilePhoto;
    TextView profileName;
    RecyclerView commentHolder;


    public ProfileFragment(String userId) {
        this.userId = userId;
    }

    public static ProfileFragment newInstance(String arg1) {
        ProfileFragment fragment = new ProfileFragment(arg1);
        
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
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
        putUserData();
    }

    private void putUserData() {
        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        profileName.setText(user.username);
                        Glide.with(requireContext())
                                .load(auth.getCurrentUser().getPhotoUrl())
                                .centerCrop()
                                .into(profilePhoto);
                        setComments(user.userid);
                    }
                });

    }





    private void setComments(String userid) {
        FirebaseFirestore.getInstance()
                .collection("comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            comments.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Comment comment = document.toObject(Comment.class);
                                if (comment.userId.equals(userid)) comments.add(comment);
                            }
                            setRecycler();
                        }
                    }
                });
    }
    private void setRecycler() {
        if (comments != null){
            commentHolder.setAdapter(new CommentPostAdapter(comments, requireContext(), ProfileFragment.this));
            commentHolder.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        }
    }
    private void setFragment(Fragment fragment) {
        if (getFragmentManager() != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout_main, fragment)
                    .commit();
        }
    }

    private void hook(View view) {
        profilePhoto = view.findViewById(R.id.profilePhoto);
        profileName = view.findViewById(R.id.profileName);
        commentHolder = view.findViewById(R.id.commentHolder);
    }
}