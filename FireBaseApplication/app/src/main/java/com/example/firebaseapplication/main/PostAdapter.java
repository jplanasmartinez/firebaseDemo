package com.example.firebaseapplication.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebaseapplication.R;
import com.example.firebaseapplication.models.Post;
import com.example.firebaseapplication.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final List<Post> posts;
    public Context context;
    public Fragment currentFragment;

    public PostAdapter(List<Post> posts, Context context, Fragment currentFragment) {
        this.posts = posts;
        this.context = context;
        this.currentFragment = currentFragment;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_holder, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        Glide.with(context)
                .load(post.postImage)
                .centerCrop()
                .into(holder.imagePost);
        holder.textPost.setText(post.postText);
        FirebaseFirestore.getInstance().collection("users")
                .document(post.userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                   User user = documentSnapshot.toObject(User.class);
                   Glide.with(context)
                            .load(user.profileImageURL)
                            .centerCrop()
                            .into(holder.imageUser);
                   holder.textUser.setText(user.username);
                }
        });
        holder.postLayout.setOnClickListener(view -> {
            setFragment(new PostFragment(post.postId));
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
    private void setFragment(Fragment fragment) {
        if (currentFragment.getFragmentManager() != null) {
            currentFragment.getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout_main, fragment)
                    .addToBackStack(HomeFragment.class.getSimpleName())
                    .commit();
        }
    }
    public static class PostViewHolder extends RecyclerView.ViewHolder {

        public ImageView imagePost;
        public ImageView imageUser;
        public TextView textPost;
        public TextView textUser;
        public RelativeLayout postLayout;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePost = itemView.findViewById(R.id.imagePost);
            imageUser = itemView.findViewById(R.id.imageUser);
            textPost = itemView.findViewById(R.id.textPost);
            textUser = itemView.findViewById(R.id.textUser);
            postLayout = itemView.findViewById(R.id.postLayout);
        }
    }
}
