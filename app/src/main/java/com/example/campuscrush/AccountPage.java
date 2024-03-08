

package com.example.campuscrush;

        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.net.Uri;
        import android.os.Bundle;
        import android.provider.MediaStore;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.Switch;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.firestore.DocumentReference;
        import com.google.firebase.firestore.DocumentSnapshot;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.storage.FileDownloadTask;
        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageReference;
        import com.google.firebase.storage.UploadTask;

        import java.io.File;
        import java.io.IOException;
        import java.util.HashMap;
        import java.util.Map;


public class AccountPage extends AppCompatActivity {

    private FirebaseFirestore db;
    FirebaseUser user;
    FirebaseAuth auth;
    Button logout, saveAccountData;
    ImageButton mainPage, chatPage, accountPage;
    EditText emailText, nameText, surnameText, dateText;
    Switch switchGender;
    ImageView profilePic;

    StorageReference storageReference;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountpage);




        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        logout = findViewById(R.id.logout);
        mainPage = findViewById(R.id.market);
        chatPage = findViewById(R.id.chat);
        accountPage = findViewById(R.id.home);
        emailText = findViewById(R.id.emailEditText);
        nameText = findViewById(R.id.nameEditText);
        surnameText = findViewById(R.id.surnameEditText);
        dateText = findViewById(R.id.birthEditDate);
        saveAccountData = findViewById(R.id.saveAccountData);
        switchGender = findViewById(R.id.switchGender);
        profilePic = findViewById(R.id.profilePic);

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        mainPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                startActivity(intent);
                finish();
            }
        });

        chatPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChatPage.class);
                startActivity(intent);
                finish();
            }
        });

        accountPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AccountPage.class);
                startActivity(intent);
                finish();
            }
        });

            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            if (user == null) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
            else {
                emailText.setText(user.getEmail());
                setAccountData();
                loadProfilePicture();

                //nameText = user.getEmail();


                //nickname.setText(userName);
                //loadProfilePicture();

            }

            saveAccountData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateAccountData();
                }
            });

            profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open the image picker when the ImageButton is clicked
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                }
            });

    }

    void loadProfilePicture(){

        storageReference = FirebaseStorage.getInstance().getReference("images/"+ emailText.getText() +"/profilePicture.jpg");

        try {
            File localfile = File.createTempFile("tempfile", ".jpg");
            storageReference.getFile(localfile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {


                            Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                            profilePic.setImageBitmap(bitmap);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                            Toast.makeText(AccountPage.this, "Failed to retrieve profile picture", Toast.LENGTH_SHORT).show();

                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the selected image URI
            Uri selectedImageUri = data.getData();

            // Upload the selected image to Firebase Storage
            uploadImageToFirebase(selectedImageUri);
        }
    }
    private void uploadImageToFirebase(Uri imageUri) {
        // Assuming userId is the unique identifier for the user
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + emailText.getText() + "/profilePicture.jpg");

        // Upload the image to Firebase Storage
        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image uploaded successfully
                        // Now, you can get the download URL and store it in Firestore or perform other actions
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle unsuccessful upload
                    }
                });


    }
    private void setAccountData(){ //do stuff

        db.collection("UserData").document(user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot != null && documentSnapshot.exists()){
                        nameText.setText(documentSnapshot.getString("name"));
                        surnameText.setText(documentSnapshot.getString("surname"));
                        dateText.setText(documentSnapshot.getString("dateOfBirth"));
                        switchGender.setChecked(documentSnapshot.getBoolean("sex"));

                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AccountPage.this, "Err: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private static final int PICK_IMAGE_REQUEST = 1;

    private void updateAccountData(){ //updates Account Data (like name, surname etc.)

                DocumentReference documentReference = db.collection("UserData").document(user.getEmail());

                Map<String, Object> updates = new HashMap<>();
                updates.put("name", nameText.getText().toString());
                updates.put("surname", surnameText.getText().toString());
                updates.put("dateOfBirth", dateText.getText().toString());
                updates.put("sex", switchGender.isChecked());




            documentReference.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Toast.makeText(AccountPage.this, "data changed.", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AccountPage.this, "Err", Toast.LENGTH_SHORT).show();
                    }
                });

            }

    }




