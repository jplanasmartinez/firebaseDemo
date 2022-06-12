package com.example.firebaseapplication.init;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.firebaseapplication.AppFragment;
import com.example.firebaseapplication.main.MainActivity;
import com.example.firebaseapplication.R;
import com.example.firebaseapplication.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends AppFragment {
    public static final String PREF_FILE_NAME = "LogData";
    private TextInputEditText mailText;
    private TextInputEditText passText;
    private Button logInButton;
    private Button registerButton;
    private Button googleButton;

    public ImageView image2;

    // TODO: Rename and change types of parameters


    public LoginFragment() {
        // Required empty public constructor
    }
    public static LoginFragment newInstance(String param1, String param2) {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleSignInClient googleSignInAccount = GoogleSignIn.getClient(requireContext(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build());
        hook(view);

        logInButton.setOnClickListener(v -> {

            String mail = mailText.getText().toString();
            String password = passText.getText().toString();

            if ((!mail.isEmpty() && !password.isEmpty())){
                logInMail(mail, password);
            } else{
                Toast.makeText(requireContext(), "Introduce valores", Toast.LENGTH_SHORT).show();            }

        });

        registerButton.setOnClickListener(v -> {
            setFragment(new RegisterFragment());
        });

        googleButton.setOnClickListener(v -> {
            signInClient.launch(googleSignInAccount.getSignInIntent());
        });

    }

    // Methods

    private void hook(View view) {
        mailText = view.findViewById(R.id.mailText);
        passText = view.findViewById(R.id.passText);
        logInButton = view.findViewById(R.id.logInButton);
        registerButton = view.findViewById(R.id.registerButton);
        googleButton = view.findViewById(R.id.googleButton);
    }
    private void setFragment(Fragment fragment) {
        if (getFragmentManager() != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout_init, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void logInMail(String mail, String password) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(
                        mail,
                        password
                ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                db.collection("users").document(auth.getCurrentUser().getUid()).addSnapshotListener((documentSnapshot, e) -> {
                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("mail", mail);
                    editor.putString("pass", password);
                    editor.commit();
                    access();
                });
            } else {
                Toast.makeText(requireContext(), task.getException().getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        if(account == null) return;

        FirebaseAuth.getInstance().signInWithCredential(GoogleAuthProvider.getCredential(account.getIdToken(), null))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        createGoogleAccount();
                    }
                });
    }
    private void createGoogleAccount(){
        db.collection("users")
                .document(auth.getUid())
                .set(new User(auth.getCurrentUser().getUid(),
                    auth.getCurrentUser().getDisplayName(),
                    auth.getCurrentUser().getEmail(),
                    null,
                    auth.getCurrentUser().getPhotoUrl().toString()))
                .addOnCompleteListener(task -> {
                    access();
                });
    }
    private void access(){
        Intent intent = new Intent();
        intent.setClass(getActivity(), MainActivity.class);
        getActivity().finish();
        getActivity().startActivity(intent);

    }
    ActivityResultLauncher<Intent> signInClient = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    try {
                        firebaseAuthWithGoogle(GoogleSignIn.getSignedInAccountFromIntent(result.getData()).getResult(ApiException.class));
                    } catch (ApiException e) {

                    }
                }
            });
}