package com.example.n5wntl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    Vector<Family> familyList = new Vector<>();

    FirebaseAuth auth;
    Button logout;
    TextView myemailtext;
    Button add;
    Button list;
    Date szuletett;
    Date meghallt;
    String nev;
    boolean ferfi;

    int listlen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); TODO: use it
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        logout = findViewById(R.id.logout);
        myemailtext = findViewById(R.id.myemail);
        add = findViewById(R.id.addfam);
        nev = ((EditText) findViewById(R.id.input_field)).getText().toString();
        list = findViewById(R.id.listfam);
        listlen=0;
        //szuletett = (findViewById(R.id.szuletett)).getText().toString(); TODO: make it a date
        //meghallt = (findViewById(R.id.meghalt)).getText().toString(); TODO: make it a date
        ferfi = ((Switch) findViewById(R.id.nem)).isChecked();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            myemailtext.setText(email);
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
            return;
        }
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: pass the list of family members to FamilyTree class
                Intent intent = new Intent(MainActivity.this, FamilyTree.class);
                startActivity(intent);
                finish();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                familyList.add(new Family(nev, ferfi));
                listlen++;
                Toast.makeText(MainActivity.this, "Family member added " + listlen, Toast.LENGTH_SHORT).show();
            }

        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
                Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
class Family{
    public String nev;
    //public Date szuletett;
    //public Date meghallt;
    public boolean ferfi;

    public Family(String nev, boolean ferfi) {
        this.nev = nev;
        //this.szuletett = szuletett;
        //this.meghallt = meghallt;
        this.ferfi = ferfi;
    }
}