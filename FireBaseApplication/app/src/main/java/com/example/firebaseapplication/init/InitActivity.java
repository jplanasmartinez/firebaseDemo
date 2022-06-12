package com.example.firebaseapplication.init;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.firebaseapplication.R;
import com.example.firebaseapplication.main.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;

public class InitActivity extends AppCompatActivity {
    public static final String PREF_FILE_NAME = "LogData";
    public Boolean isLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        setFragment(new PrevFragment());

        Handler handler = new Handler();
        Runnable run = () -> {
            logWithGoogle(GoogleSignIn.getLastSignedInAccount(this));
            logWithMail();
        };
        handler.postDelayed(run, 1200);
    }

    private void setFragment (Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout_init, fragment);
        fragmentTransaction.commit();
    }
    private void logWithGoogle(GoogleSignInAccount lastSignedInAccount) {
        if (lastSignedInAccount != null) {
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            this.finish();
            startActivity(intent);
            isLogin = false;
        }

    }
    private void logWithMail() {
        if (isLogin){
            SharedPreferences sharedPreferences = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
            String mail = sharedPreferences.getString("mail", "");
            String pass = sharedPreferences.getString("pass", "");
            if (mail.length() > 0 && pass.length() > 0){
                FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(
                                mail,
                                pass
                        ).addOnCompleteListener(task -> {
                    Intent intent = new Intent();
                    intent.setClass(this, MainActivity.class);
                    this.finish();
                    startActivity(intent);
                });
            } else {
                setFragment(new LoginFragment());
            }
        }
    }
}