package com.edutec.deportes.session.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.edutec.deportes.R;
import com.edutec.deportes.deportes.activity.DeportesActivity;
import com.edutec.deportes.services.MessagingToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private FloatingActionButton btnLogin;
    private ConstraintLayout mainContainer;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        connectToView();
        auth = FirebaseAuth.getInstance();
        btnLogin.setOnClickListener(view -> signIn());

    }

    private void signIn() {
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        signInWithEmailAndPassword(email, password);
    }

    private void signInWithEmailAndPassword(String email, String password) {
        /* auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                    }
                }); */
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        new MessagingToken(this);
                        Intent intent = new Intent(this, DeportesActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Snackbar.make(mainContainer, "Cuenta no existe", Snackbar.LENGTH_LONG)
                                .setAction("Crear Cuenta", view -> {
                                    Intent intent = new Intent(this, SignUpActivity.class);
                                    startActivity(intent);
                                    finish();
                                }).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    private void connectToView() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        mainContainer = findViewById(R.id.mainContainer);
    }
}