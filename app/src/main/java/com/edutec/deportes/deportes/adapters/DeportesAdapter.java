package com.edutec.deportes.deportes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edutec.deportes.R;
import com.edutec.deportes.deportes.models.Deporte;

import java.util.ArrayList;

public class DeportesAdapter extends RecyclerView.Adapter<DeportesAdapter.ViewHolder> {

    private ArrayList<Deporte> deporteArrayList;

    public DeportesAdapter(ArrayList<Deporte> deporteArrayList) {
        this.deporteArrayList = deporteArrayList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtTeam1, txtTeam2, txtResultado1, txtResultado2, txtWinner;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTeam1 = itemView.findViewById(R.id.txtTeam1);
            txtTeam2 = itemView.findViewById(R.id.txtTeam2);
            txtResultado1 = itemView.findViewById(R.id.txtScore1);
            txtResultado2 = itemView.findViewById(R.id.txtScore2);
            txtWinner = itemView.findViewById(R.id.txtWinner);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_match, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Deporte deporte = deporteArrayList.get(position);

        holder.txtTeam1.setText(deporte.getTeam1());
        holder.txtTeam2.setText(deporte.getTeam2());
        holder.txtResultado1.setText(deporte.getScore1() + "");
        holder.txtResultado2.setText(deporte.getScore2() + "");
        holder.txtWinner.setText(deporte.getWinner());
    }

    @Override
    public int getItemCount() {
        return deporteArrayList.size();
    }
}
