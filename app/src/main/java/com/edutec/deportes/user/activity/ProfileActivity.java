package com.edutec.deportes.user.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.edutec.deportes.R;
import com.edutec.deportes.user.models.User;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private TextView txtName, txtEmail, txtUsername;
    private User usuario;
    private ImageView imgProfile;
    private final int REQUEST_CODE_IMAGE_PICKER = 5050;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        connectToView();
        usuario = new User();

        imgProfile.setOnClickListener(view -> {
            Options options = Options.init()
                    .setRequestCode(REQUEST_CODE_IMAGE_PICKER)
                    .setCount(1)
                    .setFrontfacing(false)
                    .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
                    .setPath("/sports/images/profile-pictures");

            Pix.start(this, options);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PICKER) {
            ArrayList<String> pictures = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            File imgFile = new File(pictures.get(0));

            Uri uri = Uri.fromFile(imgFile);
            StorageReference profileReference = storageRef.child("profile-pictures/").child(uri.getLastPathSegment());
            UploadTask uploadTask = profileReference.putFile(uri);

            uploadTask.addOnFailureListener(failure ->
                    Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
            ).addOnSuccessListener(onSuccess -> {
                Toast.makeText(this, "Cargada", Toast.LENGTH_SHORT).show();
                Map<String, Object> dataF = new HashMap<>();
                dataF.put("profile-image", "profile-pictures/" + uri.getLastPathSegment());
                db.collection("user")
                        .document(user.getUid())
                        .update(dataF)
                        .addOnSuccessListener(avoid -> {
                            loadImage(uri.getLastPathSegment());
                        })
                        .addOnFailureListener(onFailed -> {
                            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
                        });
            });
        }
    }

    private void loadImage(String uri) {
        storageRef.child("profile-pictures/"+uri).getDownloadUrl()
                .addOnSuccessListener(url -> {
                    Glide.with(this).load(url).into(imgProfile);
                }).addOnFailureListener(failure -> {
            Toast.makeText(this, "Error al descargar la imagen", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        user = auth.getCurrentUser();

        DocumentReference reference = db.collection("user").document(user.getUid());
        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    usuario.setNombre(documentSnapshot.getString("nombre"));
                    usuario.setEmail(documentSnapshot.getString("correo"));
                    usuario.setBirthday(documentSnapshot.getString("fecha_de_nacimiento"));
                    usuario.setUsername(documentSnapshot.getString("nombre_de_usuario"));

                    txtUsername.setText(usuario.getUsername());
                    txtEmail.setText(usuario.getEmail());
                    txtName.setText(usuario.getNombre());

                }
            } else {
                Toast.makeText(this, "No se han podido recuperar los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void connectToView() {
        txtName = findViewById(R.id.txtNombre);
        txtEmail = findViewById(R.id.txtCorreo);
        txtUsername = findViewById(R.id.txtUsername);
        imgProfile = findViewById(R.id.imgProfile);
    }
}