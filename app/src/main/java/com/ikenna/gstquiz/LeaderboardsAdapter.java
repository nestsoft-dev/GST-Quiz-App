package com.ikenna.gstquiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class LeaderboardsAdapter extends RecyclerView.Adapter<LeaderboardsAdapter.LeaderboardViewHolder>{
    Context context;
    ArrayList<UserModel> users;

    public LeaderboardsAdapter(Context context, ArrayList<UserModel> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leaderboard_rows, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        UserModel user = users.get(position);

        holder.name.setText(user.getName());
        holder.points.setText(String.valueOf(user.getPoints()));
      holder.index.setText(String.format("#%d", position+1));
//
//        Glide.with(context)
//                .load(user.getProfile())
//                .into(holder.binding.imageView7);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView name,points,index;


        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            points = itemView.findViewById(R.id.point);
            index = itemView.findViewById(R.id.index);

        }
    }
}
