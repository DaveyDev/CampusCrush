package com.example.campuscrush.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campuscrush.ChatActivity;
import com.example.campuscrush.MarketUsers;
import com.example.campuscrush.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;

    ArrayList<MarketUsers> userList;

    public MyAdapter(Context context, ArrayList<MarketUsers> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_market, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        MarketUsers marketUsers = userList.get(position);



            Log.d("MarketUsersAdapter", "name: " + marketUsers.toString()); // Check if the name is retrieved correctly


            holder.userName.setText(marketUsers.getName());
            holder.userSurname.setText(marketUsers.getSurname());
            holder.userAge.setText(marketUsers.getDateOfBirth());
            if (marketUsers.isSex()) {
                holder.userSex.setText("male");
            } else {
                holder.userSex.setText("female");
            }


        String email = marketUsers.getEmail();
        loadProfilePicture(email, holder.userPhoto);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("userName", marketUsers.getName());
            intent.putExtra("userSurname", marketUsers.getSurname());
            intent.putExtra("userEmail", marketUsers.getEmail());
            //intent.putExtra()
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        }

    public static void loadProfilePicture(String profilePictureUrl, ImageView imageView) {
        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/"+ profilePictureUrl+ "/profilePicture.jpg");

            try {
                File localFile = File.createTempFile("tempfile", ".jpg");
                storageReference.getFile(localFile)
                        .addOnSuccessListener(taskSnapshot -> {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            imageView.setImageBitmap(bitmap);
                        }).addOnFailureListener(e -> {
                            //Toast.makeText(context, "Failed to retrieve profile picture", Toast.LENGTH_SHORT).show();
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Handle the case where there is no profile picture URL
            // You can set a default image or hide the ImageView
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{


        TextView userName, userSurname, userAge, userSex;
        ImageView userPhoto;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.tvName);
            userSurname = itemView.findViewById(R.id.tvSurname);
            userAge = itemView.findViewById(R.id.tvAge);
            userSex = itemView.findViewById(R.id.tvSex);
            userPhoto = itemView.findViewById(R.id.tvPhoto);


        }
    }
}
