package com.edutec.deportes.deportes.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.edutec.deportes.R;
import com.edutec.deportes.deportes.models.Deporte;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddDeporteActivity extends AppCompatActivity {

    private TextInputEditText edtEquipo1, edtEquipo2, edtResultado1, edtResultado2;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deporte);
        connectToView();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = auth.getCurrentUser();
    }

    public void addSport(View view) {
        Deporte deporte = new Deporte();
        deporte.setTeam1(edtEquipo1.getText().toString());
        deporte.setTeam2(edtEquipo2.getText().toString());
        deporte.setScore1(Integer.parseInt(edtResultado1.getText().toString()));
        deporte.setScore2(Integer.parseInt(edtResultado2.getText().toString()));
        String winner = "Empate";
        if (deporte.getScore1() > deporte.getScore2())
            winner = deporte.getTeam1();

        if (deporte.getScore2() > deporte.getScore1())
            winner = deporte.getTeam2();

        deporte.setWinner(winner);
        saveSport(deporte);

    }

    private void saveSport(Deporte deporte) {
        Map<String, Object> data = new HashMap<>();
        data.put("equipo1", deporte.getTeam1());
        data.put("equipo2", deporte.getTeam2());
        data.put("resultado1", deporte.getScore1());
        data.put("resultado2", deporte.getScore2());
        data.put("winner", deporte.getWinner());
        data.put("uid", user.getUid());

        db.collection("juegos")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Deporte agregado exitosamente", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error al guardar el resultado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void connectToView() {
        edtEquipo1 = findViewById(R.id.edtEquipo1);
        edtEquipo2 = findViewById(R.id.edtEquipo2);
        edtResultado1 = findViewById(R.id.edtResultado1);
        edtResultado2 = findViewById(R.id.edtResultado2);
    }
}