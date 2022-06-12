package com.example.firebaseapplication.main;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.firebaseapplication.R;
import com.example.firebaseapplication.models.Comment;
import com.example.firebaseapplication.models.Post;
import com.example.firebaseapplication.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostFragment extends Fragment {

    String postId;
    private List<Comment> comments = new ArrayList<>();
    ImageView imagePostDetail;
    ImageView imageUserPostDetail;
    TextView textPostDetail;
    TextView textUserPostDetail;
    RecyclerView commentsRecyclerPostDetail;
    FloatingActionButton newCommentPostDetail;
    LinearLayout userPostDetail;



    public PostFragment(String postId) {
        this.postId = postId;
    }
    // TODO: Rename and change types and number of parameters
    public static PostFragment newInstance(String postId) {
        return new PostFragment(postId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hook(view);
        loadPostData();
        loadPostComments();
        newCommentPostDetail.setOnClickListener(v -> {
            setFragment(new CommentPostFragment(postId));
        });
    }

    private void loadPostData() {
        FirebaseFirestore.getInstance().collection("posts")
                .document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Post post = documentSnapshot.toObject(Post.class);
                        putData(post);
                    }
                });
    }
    private void putData(Post post){
        Glide.with(requireContext())
                .load(post.postImage)
                .centerCrop()
                .into(imagePostDetail);
        textPostDetail.setText(post.postText);
        loadUserData(post.userId);
    }
    private void loadUserData(String postString) {
        FirebaseFirestore.getInstance().collection("users")
                .document(postString)
                .get()
                .addOnSuccessListener(documentSnapshot2 -> {
                    if (documentSnapshot2.exists()) {
                        User user = documentSnapshot2.toObject(User.class);
                        Glide.with(requireContext())
                                .load(user.profileImageURL)
                                .centerCrop()
                                .into(imageUserPostDetail);
                        textUserPostDetail.setText(user.username);
                    }
                });
    }
    private void loadPostComments() {

        FirebaseFirestore.getInstance()
                .collection("comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Comment comment = document.toObject(Comment.class);
                                if (comment.postId.equals(postId)){
                                    comments.add(comment);
                                }
                            }
                            setRecycler();
                        }
                    }
                });
    }

    private void setRecycler() {
        if (comments != null){
            commentsRecyclerPostDetail.setAdapter(new CommentAdapter(comments, requireContext(), PostFragment.this));
            commentsRecyclerPostDetail.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        }
    }

    private void setFragment(Fragment fragment) {
        if (getFragmentManager() != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.postFrameLayout, fragment)
                    .commit();
        }
    }

    private void hook(View view) {
        imagePostDetail = view.findViewById(R.id.imagePostDetail);
        imageUserPostDetail = view.findViewById(R.id.imageUserPostDetail);
        textPostDetail = view.findViewById(R.id.textPostDetail);
        textUserPostDetail = view.findViewById(R.id.textUserPostDetail);
        commentsRecyclerPostDetail = view.findViewById(R.id.commentsRecyclerPostDetail);
        newCommentPostDetail = view.findViewById(R.id.newCommentPostDetail);
        userPostDetail = view.findViewById(R.id.userPostDetail);
    }
}