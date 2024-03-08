package com.example.campuscrush;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campuscrush.adapter.ChatRecyclerAdapter;
import com.example.campuscrush.adapter.MyAdapter;
import com.example.campuscrush.model.ChatMessageModel;
import com.example.campuscrush.model.ChatroomModel;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    TextView userNameAndSurname;
    ImageView miniProfilePic;
    ImageButton backBtn, sendBtn;
    EditText messageInput;
    RecyclerView recyclerView;
    String chatroomId;
    ChatroomModel chatroomModel;
    Intent intent;
    ChatRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userNameAndSurname = findViewById(R.id.tvNameAndSurname);
        miniProfilePic = findViewById(R.id.miniProfilePic);
        backBtn = findViewById(R.id.backbtn);
        sendBtn = findViewById(R.id.sendbtn);
        messageInput = findViewById(R.id.chatBox);
        recyclerView = findViewById(R.id.chatRecyclerView);


        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        intent = getIntent();
        if (intent != null) {
            String userName = intent.getStringExtra("userName");
            String userSurname = intent.getStringExtra("userSurname");
            String userEmail = intent.getStringExtra("userEmail");

            MyAdapter.loadProfilePicture(userEmail, miniProfilePic);


            if (userName != null && userSurname != null) {
                String fullName = userName + " " + userSurname;
                // Now you have the full name, you can set it to a TextView
                userNameAndSurname.setText(fullName);
            } else {
                // Handle case when userName or userSurname is null
                userNameAndSurname.setText("no name or surname");
            }
        } else {
            // Handle case when intent is null
            userNameAndSurname.setText("intent was null");
        }

        chatroomId = FirebaseUtil.getChatroomId(FirebaseAuth.getInstance().getCurrentUser().getEmail(), intent.getStringExtra("userEmail"));

        sendBtn.setOnClickListener((v ->{
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMesssageToUser(message);
        }));

        getOrCreateChatroomModel();
        setupChatRecyclerView();
    }

    void setupChatRecyclerView(){
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendMesssageToUser(String message){


        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);


        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseAuth.getInstance().getCurrentUser().getEmail(), Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            messageInput.setText("");
                        }
                    }
                });

    }
    void getOrCreateChatroomModel(){

        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if(chatroomModel == null){

                    //first time chat (new chat)
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseAuth.getInstance().getCurrentUser().getEmail(), intent.getStringExtra("userEmail")),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });


    }
}
