package com.example.n5wntl;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class Family {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public String nev;
    public int szuletett;
    public int meghallt;
    public boolean ferfi;
    public String userId;
    public Family() {}

    public Family(int meghallt, String nev, int szuletett, boolean ferfi, String hazastarsid) {
        this.meghallt = meghallt;
        this.nev = nev;
        this.szuletett = szuletett;
        this.ferfi = ferfi;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            this.userId = "no";
        } else {
            this.userId = currentUser.getUid();
        }
    }
    public void addtodb() {
        db.collection("Family").add(this)
                .addOnSuccessListener(documentReference -> {
                    System.out.println("DocumentSnapshot added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error adding document: " + e);
                });
    }
}
