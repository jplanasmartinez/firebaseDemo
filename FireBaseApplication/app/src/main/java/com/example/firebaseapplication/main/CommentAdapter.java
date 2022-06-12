package com.example.firebaseapplication.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebaseapplication.R;
import com.example.firebaseapplication.models.Comment;
import com.example.firebaseapplication.models.Post;
import com.example.firebaseapplication.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private final List<Comment> comments;
    public Context context;
    public Fragment currentFragment;

    public CommentAdapter(List<Comment> comments, Context context, Fragment currentFragment) {
        this.comments = comments;
        this.context = context;
        this.currentFragment = currentFragment;
    }
    @NonNull
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.comment_holder, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        FirebaseFirestore.getInstance().collection("users")
                .document(comment.userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        holder.textUserComment.setText(user.username);
                        Glide.with(context)
                                .load(user.profileImageURL)
                                .centerCrop()
                                .into(holder.imageUserComment);
                    }
                });
        holder.textComment.setText(comment.commentText);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        CardView cardComment;
        TextView textUserComment;
        TextView textComment;
        ImageView imageUserComment;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            cardComment = itemView.findViewById(R.id.cardComment);
            textUserComment = itemView.findViewById(R.id.textUserComment);
            textComment = itemView.findViewById(R.id.textComment);
            imageUserComment = itemView.findViewById(R.id.imageUserComment);
        }
    }
}
