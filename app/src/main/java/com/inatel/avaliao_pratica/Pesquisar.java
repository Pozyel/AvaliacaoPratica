package com.inatel.avaliao_pratica;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Pesquisar extends AppCompatActivity {
private RecyclerView recyclerView;
private ArrayList<Candidato> cand;
private CandidatoAdapter candidatoAdapter;
DatabaseReference dRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisar);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cand = new ArrayList<Candidato>();
        dRef = FirebaseDatabase.getInstance().getReference().child("Candidato");
        dRef.addListenerForSingleValueEvent(valueEventListener);
    }

   ValueEventListener valueEventListener = new ValueEventListener() {
       @Override
       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
               Candidato c = dataSnapshot1.getValue(Candidato.class);
               cand.add(c);
           }
           candidatoAdapter = new CandidatoAdapter(Pesquisar.this,cand);
           recyclerView.setAdapter(candidatoAdapter);
       }

       @Override
       public void onCancelled(@NonNull DatabaseError databaseError) {

       }
   };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filtro,menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                candidatoAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}
