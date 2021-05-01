package com.edutec.deportes.deportes.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.edutec.deportes.R;
import com.edutec.deportes.deportes.adapters.DeportesAdapter;
import com.edutec.deportes.deportes.models.Deporte;
import com.edutec.deportes.session.activity.SignUpActivity;
import com.edutec.deportes.user.activity.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DeportesActivity extends AppCompatActivity {

    private RecyclerView recyclerGames;
    private ArrayList<Deporte> deportes;
    private FirebaseFirestore db;
    private DeportesAdapter deportesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deportes);
        recyclerGames = findViewById(R.id.recyclerGames);
        db = FirebaseFirestore.getInstance();
        deportes = new ArrayList<>();
        getAllRealtime();
        deportesAdapter = new DeportesAdapter(deportes);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);

        recyclerGames.setLayoutManager(manager);
        recyclerGames.setAdapter(deportesAdapter);
        recyclerGames.setItemAnimator(new DefaultItemAnimator());
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        getAll();
    }*/

    private void getAllRealtime() {
        db.collection("juegos")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error al recuperar los documentos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    deportes.clear();
                    for (QueryDocumentSnapshot document : snapshots) {
                        Deporte deporte = new Deporte();
                        deporte.setTeam1(document.getString("equipo1"));
                        deporte.setTeam2(document.getString("equipo2"));
                        deporte.setScore1(document.getLong("resultado1").intValue());
                        deporte.setScore2(document.getLong("resultado2").intValue());
                        deporte.setWinner(document.getString("winner"));

                        deportes.add(deporte);
                    }
                    deportesAdapter.notifyDataSetChanged();
                });
    }

    private void getAll() {
        db.collection("juegos")
                .get()
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       deportes.clear();
                       for (QueryDocumentSnapshot document : task.getResult()) {
                           Deporte deporte = new Deporte();
                           deporte.setTeam1(document.getString("equipo1"));
                           deporte.setTeam2(document.getString("equipo2"));
                           deporte.setScore1(document.getLong("resultado1").intValue());
                           deporte.setScore2(document.getLong("resultado2").intValue());
                           deporte.setWinner(document.getString("winner"));

                           deportes.add(deporte);
                       }
                       deportesAdapter.notifyDataSetChanged();
                   } else {
                       Toast.makeText(this, "Error al recuperar los documentos", Toast.LENGTH_SHORT).show();
                   }
                });
    }

    public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    public void launchProfile(View view) {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    public void launchAddSport(View view) {
        startActivity(new Intent(this, AddDeporteActivity.class));
    }
}