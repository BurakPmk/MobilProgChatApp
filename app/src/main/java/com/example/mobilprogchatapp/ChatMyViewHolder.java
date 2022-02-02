package com.example.mobilprogchatapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMyViewHolder extends RecyclerView.ViewHolder {

    CircleImageView firstUserProfile,secondUserProfile;
    TextView firstUserMessage,secondUserMassage;
    public ChatMyViewHolder(@NonNull View itemView) {
        super(itemView);
        firstUserMessage = itemView.findViewById(R.id.firstUserMessage);
        secondUserMassage = itemView.findViewById(R.id.secondUserMessage);
        firstUserProfile = itemView.findViewById(R.id.firstUserProfile);
        secondUserProfile = itemView.findViewById(R.id.secondUserProfile);
    }
}
