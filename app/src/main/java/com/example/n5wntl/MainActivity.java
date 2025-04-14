package com.example.n5wntl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Firebase db = Firebase.INSTANCE;
    Button logout, add, list;
    TextView myemailtext;
    EditText inputNev, inputSzuletett, inputMeghalt;
    Switch switchFerfi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        logout = findViewById(R.id.logout);
        add = findViewById(R.id.addfam);
        list = findViewById(R.id.listfam);
        myemailtext = findViewById(R.id.myemail);

        inputNev = findViewById(R.id.input_field);
        inputSzuletett = findViewById(R.id.szuletett);
        inputMeghalt = findViewById(R.id.meghalt);
        switchFerfi = findViewById(R.id.nem);

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            myemailtext.setText(currentUser.getEmail());
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
            return;
        }

        list.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FamilyTree.class);
            startActivity(intent);
            finish();
        });

        add.setOnClickListener(v -> {
            String nev = inputNev.getText().toString();
            String szuletett = inputSzuletett.getText().toString();
            String meghalt = inputMeghalt.getText().toString();
            boolean ferfi = switchFerfi.isChecked();
            Family member = new Family(Integer.parseInt(meghalt), nev, Integer.parseInt(szuletett), ferfi, currentUser.getUid());
            member.addtodb();
            inputNev.setText("");
            inputSzuletett.setText("");
            inputMeghalt.setText("");
            switchFerfi.setChecked(false);
        });

        logout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
            Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

}


