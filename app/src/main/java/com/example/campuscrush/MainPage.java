package com.example.campuscrush;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campuscrush.adapter.MyAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainPage extends AppCompatActivity {
    ImageButton mainPage, chatPage, accountPage;
    RecyclerView recyclerView;
    //StorageReference storageReference;
    MyAdapter myAdapter;
    ArrayList<MarketUsers> list;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userDataRef = db.collection("UserData");

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);


        mainPage = findViewById(R.id.market);
        chatPage = findViewById(R.id.chat);
        accountPage = findViewById(R.id.home);


        recyclerView = findViewById(R.id.userList);
        //storageReference = FirebaseStorage.getInstance().getReference("UserData");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        myAdapter = new MyAdapter(this, list);
        recyclerView.setAdapter(myAdapter);

        //storageReference.addSnapshotListener()
        userDataRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<MarketUsers> userList = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    // Parse Firestore data into MarketUsers object
                    MarketUsers marketUser = documentSnapshot.toObject(MarketUsers.class);
                    if (marketUser.getName() != null && !marketUser.getName().isEmpty()) { //hides no name records
                        userList.add(marketUser);
                    }
                }

                // Set up RecyclerView with the userList
                RecyclerView recyclerView = findViewById(R.id.userList);
                RecyclerView.Adapter<MyAdapter.MyViewHolder> adapter = new MyAdapter(getApplicationContext(), userList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure XD

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




    }
}
