package com.example.campuscrush;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    EditText registerTextMail, registerTextPassword;
    Button buttonReg;
    FirebaseAuth mAuth;
    TextView registration;
    private FirebaseFirestore db;
    String newNickname;
    String registerTextMailtmp;



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        registerTextMail = findViewById(R.id.registerMail);
        registerTextPassword = findViewById(R.id.registerPassword);
        buttonReg = findViewById(R.id.registerbutton);
        registration = findViewById(R.id.gotoRegister);

        db = FirebaseFirestore.getInstance();
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;
                email = String.valueOf(registerTextMail.getText());
                password = String.valueOf(registerTextPassword.getText());

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this,"no email",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, "no password", Toast.LENGTH_SHORT).show();
                    return;
                }

                String userName = registerTextMail.getText().toString();

                int atIndex = userName.indexOf('@');
                if (atIndex != -1) {
                    userName = userName.substring(0, atIndex);

                } else {
                    System.out.println("Invalid email address");
                }
                newNickname = userName;
                registerTextMailtmp = registerTextMail.getText().toString();

                createOrUpdateNickname(registerTextMailtmp);

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information

                                    Toast.makeText(Register.this, "The account has been successfully created.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Register.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }


        });




    };
    private void createOrUpdateNickname(String registerTextMail) {
        DocumentReference documentReference = db.collection("UserData").document(registerTextMail);
        documentReference.set(new HashMap<String, Object>() {{
                    put("email", registerTextMail);
                    put("dateOfBirth", "");
                    put("name", "");
                    put("surname", "");
                    put("sex", true);
                }}, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Toast.makeText(Register.this, "Nickname created.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "Err: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}