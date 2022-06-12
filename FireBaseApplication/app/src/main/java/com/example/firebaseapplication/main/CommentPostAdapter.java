package com.example.firebaseapplication.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebaseapplication.R;
import com.example.firebaseapplication.models.Comment;
import com.example.firebaseapplication.models.Post;
import com.example.firebaseapplication.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CommentPostAdapter extends RecyclerView.Adapter<CommentPostAdapter.CommentPostViewHolder>{
    private final List<Comment> comments;
    public Context context;
    public Fragment currentFragment;

    public CommentPostAdapter(List<Comment> comments, Context context, Fragment currentFragment) {
        this.comments = comments;
        this.context = context;
        this.currentFragment = currentFragment;
    }


    @NonNull
    @Override
    public CommentPostAdapter.CommentPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_and_comment_holder, parent, false);
        return new CommentPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentPostAdapter.CommentPostViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.profileCommentText.setText(comment.commentText);
        FirebaseFirestore.getInstance().collection("posts")
                .document(comment.postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Post post = documentSnapshot.toObject(Post.class);
                        Glide.with(context)
                                .load(post.postImage)
                                .centerCrop()
                                .into(holder.profileCommentImage);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentPostViewHolder extends RecyclerView.ViewHolder {
        TextView profileCommentText;
        ImageView profileCommentImage;

        public CommentPostViewHolder(@NonNull View itemView) {
            super(itemView);
            profileCommentText = itemView.findViewById(R.id.profileCommentText);
            profileCommentImage = itemView.findViewById(R.id.profileCommentImage);

        }

    }
}
