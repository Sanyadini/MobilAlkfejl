package com.example.n5wntl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditFamily extends AppCompatActivity {
//todo refakt
    EditText nevval, szuletettval, meghaltval;
    Switch nemval;
    TextView titleval;
    Button modifyButton;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_family);
        nevval = findViewById(R.id.input_field);
        szuletettval = findViewById(R.id.szuletett);
        meghaltval = findViewById(R.id.meghalt);
        nemval = findViewById(R.id.nem);
        titleval = findViewById(R.id.Nameof);
        modifyButton = findViewById(R.id.addfam);
        Intent intent = getIntent();
        id = intent.getStringExtra("docId");
        String nev = intent.getStringExtra("nev");
        int szuletett = intent.getIntExtra("szuletett", 0);
        int meghalt = intent.getIntExtra("meghalt", 0);
        boolean ferfi = intent.getBooleanExtra("ferfi", true);
        titleval.setText(nev);
        nevval.setText(nev);
        szuletettval.setText(String.valueOf(szuletett));
        meghaltval.setText(String.valueOf(meghalt));
        nemval.setChecked(ferfi);
        modifyButton.setOnClickListener(v -> {
            String ujNev = nevval.getText().toString().trim();
            int ujSzuletett;
            int ujMeghalt;

            try {
                ujSzuletett = Integer.parseInt(szuletettval.getText().toString().trim());
                ujMeghalt = Integer.parseInt(meghaltval.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Hibás évszám formátum!", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean ujFerfi = nemval.isChecked();
            updateInDb(ujNev, ujSzuletett, ujMeghalt, ujFerfi);
        });
    }

    private void updateInDb(String nev, int szuletett, int meghalt, boolean ferfi) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Family")
                .document(id)
                .update(
                        "nev", nev,
                        "szuletett", szuletett,
                        "meghallt", meghalt,
                        "ferfi", ferfi
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Családtag módosítva", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("UPDATE", "Hiba: ", e);
                    Toast.makeText(this, "Hiba a módosítás közben: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
