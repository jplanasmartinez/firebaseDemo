package com.example.firebaseapplication.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.firebaseapplication.AppFragment;
import com.example.firebaseapplication.R;
import com.example.firebaseapplication.models.Comment;
import com.google.android.material.textfield.TextInputEditText;

public class CommentPostFragment extends AppFragment {
    private String postId;
    private TextInputEditText commentText;
    private Button commentButton;
    public CommentPostFragment(String postId) {
        this.postId = postId;
    }

    public static CommentPostFragment newInstance(String a1) {
        return new CommentPostFragment(a1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hook(view);
        commentButton.setOnClickListener(v -> {
            if (!commentText.getText().toString().equals("")){
                postComment();

            }
        });
    }

    private void postComment() {
        String text = commentText.getText().toString();
        Comment comment = new Comment(
                postId,
                text,
                auth.getCurrentUser().getUid()
        );
        db.collection("comments").document(comment.commentId).set(comment).addOnCompleteListener(task -> {
            Toast.makeText(requireContext(), R.string.upload_post_success, Toast.LENGTH_SHORT).show();
            setFragment(new PostFragment(postId));
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
    private void hook(View view) {
        commentText = view.findViewById(R.id.commentText);
        commentButton = view.findViewById(R.id.commentButton);
    }
}