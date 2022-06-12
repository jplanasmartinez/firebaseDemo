package com.example.firebaseapplication.init;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebaseapplication.AppFragment;
import com.example.firebaseapplication.R;
import com.example.firebaseapplication.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends AppFragment {

    private static String profilePlaceholder = "https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png?20150327203541";

    private Uri uriProfilePic;
    private TextInputEditText mailRegisterText;
    private TextInputEditText userRegisterText;
    private TextInputEditText passwordRegisterText;
    private TextInputEditText passwordRegisterTextConfirmation;
    private Button registerButton2;
    private TextView uploadText;
    private ImageView uploadImage;
    private CardView uploadCard;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        return new RegisterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hook(view);
        final ActivityResultLauncher<String> phoneGallery = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                uriProfilePic = uri;
                Glide.with(requireContext())
                        .load(uri)
                        .centerCrop()
                        .into(uploadImage);
                appViewModel.setUriImagenSeleccionada(uri);
            } else {
                uriProfilePic = null;
                Glide.with(uploadImage).load(R.drawable.placeholder);
            }
        });
        uploadCard.setOnClickListener(v -> {
            phoneGallery.launch("image/*");
        });
        registerButton2.setOnClickListener(v -> {
            String mail = mailRegisterText.getText().toString();
            String username = userRegisterText.getText().toString();
            String password = passwordRegisterText.getText().toString();
            String password2 = passwordRegisterTextConfirmation.getText().toString();
            if (checkMail(mail) && checkPassword(password, password2)){
                registerNewUser(username, mail, password, uriProfilePic);
            }
        });
    }

    private void hook(View view) {
        registerButton2 = view.findViewById(R.id.registerButton2);
        mailRegisterText = view.findViewById(R.id.mailRegisterText);
        userRegisterText = view.findViewById(R.id.userRegisterText);
        passwordRegisterText = view.findViewById(R.id.passwordRegisterText);
        passwordRegisterTextConfirmation = view.findViewById(R.id.passwordRegisterTextConfirmation);
        uploadImage = view.findViewById(R.id.uploadImage);
        uploadText = view.findViewById(R.id.uploadText);
        uploadCard = view.findViewById(R.id.uploadCard);
    }

    public boolean containsUpperCaseLetter(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.isUpperCase(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }
    public boolean containsNumber(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (Character.isDigit(string.charAt(i))) {
                return true;
            }
        }
        return false;
    }
    private boolean checkMail(String mail) {
        if (mail.length() < 1 || mail.isEmpty()){
            Toast.makeText(requireContext(), "El correo no es valido", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean checkPassword(String password, String password2) {
        if (password.length() < 1 || password.isEmpty()){
            Toast.makeText(requireContext(), "La contraseña no es valida", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 8){
            Toast.makeText(requireContext(), "La contraseña debe ser de 8 carácteres", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!containsUpperCaseLetter(password)){
            Toast.makeText(requireContext(), "La contraseña debe tener una mayúscula", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!containsNumber(password)){
            Toast.makeText(requireContext(), "La contraseña debe tener un número", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.equals(password2)){
            Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void setFragment(Fragment fragment) {
        if (getFragmentManager() != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout_init, fragment)
                    .commit();
        }
    }
    private void registerNewUser(String username, String mail, String password, Uri uriProfilePic) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (uriProfilePic != null) {
                    FirebaseStorage.getInstance().getReference("/profileimgs/" + UUID.randomUUID() + ".jpg")
                            .putFile(uriProfilePic)
                            .continueWithTask(task2 -> task2.getResult().getStorage().getDownloadUrl())
                            .addOnSuccessListener(imageUrl -> saveUser(Objects
                                            .requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), username, mail, password, imageUrl));
                } else {
                    saveUser(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), username, mail, password, null);
                }
                Toast.makeText(requireContext(), "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();
                setFragment(new LoginFragment());
            } else {
                Toast.makeText(requireContext(), Objects.requireNonNull(task.getException()).getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUser(String userid, String usernameValue, String emailValue, String passwordValue, Uri imageUri) {

        String imageUrl;

        if (imageUri == null || imageUri.toString().equals("")) {
            imageUrl = profilePlaceholder;
        } else {
            imageUrl = imageUri.toString();
        }

        User userToAdd = new User(userid, usernameValue, emailValue, passwordValue, imageUrl);

        db.collection("users")
                .document(userid)
                .set(userToAdd);
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(Objects.requireNonNull(userRegisterText.getText()).toString())
                .setPhotoUri(Uri.parse(imageUrl))
                .build();
        auth.getCurrentUser().updateProfile(profileUpdates);
        appViewModel.userLogged = userToAdd;
    }
}