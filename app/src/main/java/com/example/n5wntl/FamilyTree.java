package com.example.n5wntl;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
public class FamilyTree extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseUser user;
    private LinearLayout familyListLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_tree);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        familyListLayout = findViewById(R.id.family_list_layout);

        TextView extremesText = findViewById(R.id.szelsosegek);
        Button extremesButton = findViewById(R.id.szelso);

        extremesButton.setOnClickListener(v -> listExtremes(extremesText));

        fetchFamilyMembers();
        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(FamilyTree.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        Button refreshButton = findViewById(R.id.refresh);
        refreshButton.setOnClickListener(v -> fetchFamilyMembers());

    }

    private void fetchFamilyMembers() {
        db.collection("Family")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    familyListLayout.removeAllViews();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Family member = doc.toObject(Family.class);
                        String docId = doc.getId();
                        TextView textView = new TextView(this);
                        textView.setText(member.nev + " (" + member.szuletett + "-" + member.meghallt + ")");
                        textView.setPadding(8, 8, 8, 8);
                        Button editBtn = new Button(this);
                        editBtn.setText("Szerkesztés");
                        editBtn.setOnClickListener(v -> {
                            Intent i = new Intent(FamilyTree.this, EditFamily.class);
                            i.putExtra("docId", docId);
                            i.putExtra("nev", member.nev);
                            i.putExtra("szuletett", member.szuletett);
                            i.putExtra("meghalt", member.meghallt);
                            i.putExtra("ferfi", member.ferfi);
                            startActivity(i);
                        });
                        Button deleteBtn = new Button(this);
                        deleteBtn.setText("Törlés");
                        deleteBtn.setOnClickListener(v -> confirmDelete(docId));
                        LinearLayout entryLayout = new LinearLayout(this);
                        entryLayout.setOrientation(LinearLayout.VERTICAL);
                        entryLayout.addView(textView);
                        entryLayout.addView(editBtn);
                        entryLayout.addView(deleteBtn);
                        entryLayout.setPadding(16, 16, 16, 16);
                        familyListLayout.addView(entryLayout);
                    }
                    if (queryDocumentSnapshots.isEmpty()) {
                        TextView empty = new TextView(this);
                        empty.setText("Jelenleg nincs családtag rögzítve.");
                        familyListLayout.addView(empty);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Hiba történt: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void listExtremes(TextView output) {
        String userId = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Family")
                .whereEqualTo("userId", userId)
                .orderBy("szuletett", Query.Direction.ASCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(oldestSnapshots -> {
                    if (!oldestSnapshots.isEmpty()) {
                        DocumentSnapshot oldest = oldestSnapshots.getDocuments().get(0);
                        String oldestname = oldest.getString("nev");
                        Long oldestage = oldest.getLong("szuletett");
                        db.collection("Family")
                                .whereEqualTo("userId", userId)
                                .orderBy("szuletett", Query.Direction.DESCENDING)
                                .limit(1)
                                .get()
                                .addOnSuccessListener(youngestSnapshots -> {
                                    if (!youngestSnapshots.isEmpty()) {
                                        DocumentSnapshot youngest = youngestSnapshots.getDocuments().get(0);
                                        String youngestname = youngest.getString("nev");
                                        Long youngestage = youngest.getLong("szuletett");
                                        output.setText("Legidősebb: " + oldestname + " (" + oldestage + ")\n" + "Legfiatalabb: " + youngestname + " (" + youngestage + ")");
                                    }
                                })
                                .addOnFailureListener(e -> output.setText("Hiba:" + e.getMessage()));}
                })
                .addOnFailureListener(e -> output.setText("Hiba: " + e.getMessage()));
    }
    private void confirmDelete(String docId) {
        new AlertDialog.Builder(this)
                .setTitle("Törlés megerősítése")
                .setMessage("Biztosan törölni szeretnéd ezt a családtagot?")
                .setPositiveButton("Igen", (dialog, which) -> deleteMember(docId))
                .setNegativeButton("Mégse", null)
                .show();
    }private void deleteMember(String docId) {
        db.collection("Family").document(docId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Törölve", Toast.LENGTH_SHORT).show();
                    fetchFamilyMembers();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Hiba törlés közben: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



}
