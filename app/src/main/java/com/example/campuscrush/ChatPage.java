package com.example.campuscrush;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ChatPage extends AppCompatActivity {
    ImageButton mainPage, chatPage, accountPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatpage);

        mainPage = findViewById(R.id.market);
        chatPage = findViewById(R.id.chat);
        accountPage = findViewById(R.id.home);

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
