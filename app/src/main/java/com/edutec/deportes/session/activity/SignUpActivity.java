package com.edutec.deportes.session.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.edutec.deportes.R;
import com.edutec.deportes.deportes.activity.DeportesActivity;
import com.edutec.deportes.services.MessagingToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText edtEmail, edtPassword;
    private EditText edtDate, edtName, edtUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        connectToView();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        createNotificationChannel();

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Default";
            String description = "Default channel for notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Default", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(this, DeportesActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void processSignUpData(View view) {

        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        signUpWithEmailAndPassword(email, password);

    }

    private void signUpWithEmailAndPassword(String email, String password) {
        /*auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(getApplicationContext(), "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();
                    }
                });*/

        db.collection("user")
                .whereEqualTo("nombre_de_usuario", edtUsername.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    System.out.println("RESULTADO TASK: " + task.isSuccessful());
                    if (task.isSuccessful() && task.getResult().size() == 0) {

                        auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this, internalTask -> {
                                    if (internalTask.isSuccessful()) {
                                        new MessagingToken(this);
                                        FirebaseUser user = auth.getCurrentUser();
                                        saveUser(user.getUid());
                                    } else {
                                        Toast.makeText(this, "Error en la creación de cuenta", Toast.LENGTH_SHORT).show();
                                        System.out.println(internalTask.getException());
                                    }
                                });

                    } else {
                        Toast.makeText(this, "NOMBRE DE USUARIO YA EXISTE", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    // region SAVE USER
    private void saveUser(String uid) {

        Map<String, Object> data = new HashMap<>();
        data.put("correo", edtEmail.getText().toString());
        data.put("fecha_de_nacimiento", edtDate.getText().toString());
        data.put("nombre", edtName.getText().toString());
        data.put("nombre_de_usuario", edtUsername.getText().toString());

        db.collection("user")
                .document(uid)
                .set(data)
                .addOnSuccessListener(success -> {
                    Toast.makeText(this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, DeportesActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(failure -> {
                    Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT).show();
                    System.out.println(failure.getCause() + " : " + failure.getMessage());
                    FirebaseAuth.getInstance().signOut();
                });
    }
    // endregion

    // region DATE PICKER REGION
    public void clickDate(View view) {
        showDatePickerDialog();
    }

    private void showDatePickerDialog() {
        /*DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

            }
        });*/

        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {
            final String dateSelected = day + "/" + (month + 1) + "/" + year;
            edtDate.setText(dateSelected);
        });

        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment {

        private DatePickerDialog.OnDateSetListener listener;

        public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener) {
            DatePickerFragment fragment = new DatePickerFragment();
            fragment.setListener(listener);
            return fragment;
        }

        public void setListener(DatePickerDialog.OnDateSetListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), listener, year, month, day);
        }
    }
    // endregion

    public void goToLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void connectToView() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtDate = findViewById(R.id.edtDate);
        edtName = findViewById(R.id.edtName);
        edtUsername = findViewById(R.id.edtUsername);
    }
}