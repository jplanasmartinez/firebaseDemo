package com.example.firebaseapplication.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebaseapplication.ProfileFragment;
import com.example.firebaseapplication.R;
import com.example.firebaseapplication.init.InitActivity;
import com.example.firebaseapplication.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{
    public static final String FRAGMENT_TAG = "profile";
    public static final String PREF_FILE_NAME = "LogData";
    public Toolbar toolbar;
    DrawerLayout drawer_layout;
    public NavigationView navigationView;
    ImageView profileImage;
    TextView profileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hook();

        drawerToolSet();

        setFragment(new HomeFragment(), "a");
    }

    private void hook() {
        toolbar = findViewById(R.id.toolbar);
        drawer_layout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
    }

    public void drawerToolSet() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer_layout,
                toolbar,
                R.string.open,
                R.string.close);
        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();


        // User data
        View headerView = navigationView.getHeaderView(0);
        profileImage = headerView.findViewById(R.id.profileImage);
        profileName = headerView.findViewById(R.id.profileName);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    profileName.setText(user.username);
                    Glide.with(this).load(user.profileImageURL).circleCrop().into(profileImage);

                }

            }
        });
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setFragment (Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout_main, fragment, tag)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG) != null) {
            setFragment(new HomeFragment(), "a");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer_layout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()){
            case R.id.changeProfile:
                SharedPreferences sharedPreferences = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
                if (sharedPreferences.getString("mail","").equals("") || sharedPreferences.getString("mail","").isEmpty()){
                    Toast.makeText(this, R.string.google_parameters, Toast.LENGTH_SHORT).show();
                } else {
                    navigationView.getMenu().getItem(0).setChecked(false);
                    setFragment(new EditProfileFragment(), "e");
                }
                break;
            case R.id.profile:
                setFragment(new ProfileFragment(FirebaseAuth.getInstance().getCurrentUser().getUid()), FRAGMENT_TAG);
                break;
            case R.id.logOut:
                navigationView.getMenu().getItem(1).setChecked(false);
                logOut();
                break;
        }

        return true;
    }
    public void logOut(){
        FirebaseAuth.getInstance().signOut();
        GoogleSignInClient googleSignInAccount = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build());
        googleSignInAccount.signOut();
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("mail", "");
        editor.putString("pass", "");
        editor.commit();
        Intent intent = new Intent(this, InitActivity.class);
        finish();
        startActivity(intent);
    }
}