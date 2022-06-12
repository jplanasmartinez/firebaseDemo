package com.example.firebaseapplication.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firebaseapplication.R;
import com.example.firebaseapplication.models.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerPost;
    private FloatingActionButton plusButton;
    private List<Post> posts = new ArrayList<>();

    public HomeFragment() {
    }
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toggleTopBar();
        hook(view);
        getPosts();
        plusButton.setOnClickListener(view1 -> {
            setFragment(new PostUploadFragment());
        });
    }



    private void toggleTopBar() {
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).toolbar.setNavigationIcon(R.drawable.icon_menu);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(requireActivity(),
                ((MainActivity) getActivity()).drawer_layout,
                ((MainActivity) getActivity()).toolbar,
                R.string.open,
                R.string.close);
        ((MainActivity) getActivity()).drawer_layout.addDrawerListener(toggle);
        toggle.syncState();
    }
    private void hook(View view) {
        recyclerPost = view.findViewById(R.id.recyclerPost);
        plusButton = view.findViewById(R.id.plusButton);
    }
    private void getPosts() {
        FirebaseFirestore.getInstance()
            .collection("posts")
            .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            posts.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Post post = document.toObject(Post.class);
                                posts.add(post);
                            }
                            setRecycler();
                        }
                    }
                });
    }
    private void setRecycler() {
        if (posts != null){

            recyclerPost.setAdapter(new PostAdapter(posts, requireContext(), HomeFragment.this));
            recyclerPost.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        }
    }
    private void setFragment(Fragment fragment) {
        if (getFragmentManager() != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.homeFragmentLayout, fragment)
                    .commit();
        }
    }
}