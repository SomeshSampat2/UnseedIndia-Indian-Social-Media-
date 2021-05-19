package com.example.unseenindia.Adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unseenindia.Models.User;
import com.example.unseenindia.ProfileActivity;
import com.example.unseenindia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<User> mUsers;
    private Context mContext;

    public SearchAdapter(List<User> mUsers, Context mContext) {
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_user_item,parent,false);

        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final User user = mUsers.get(position);

        holder.quote.setText(user.getQuote());
        holder.quote.setTextColor(Color.parseColor("#380036"));
        holder.username.setText(user.getUsername());
        Picasso.get().load(user.getProfileImage()).placeholder(R.drawable.ic_person_blue_24dp).into(holder.profileImage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {

                if(!user.getUserid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                    Intent intent = new Intent(mContext, ProfileActivity.class);

                    intent.putExtra("userId", user.getUserid());

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)mContext,
                            Pair.<View, String>create(holder.username,"user_trans"),
                            Pair.<View, String>create(holder.followBtn,"but_trans"),
                            Pair.<View, String>create(holder.profileImage,"profile_trans"),
                            Pair.<View, String>create(holder.quote,"status_trans"));

                    mContext.startActivity(intent,options.toBundle());
                }
            }
        });


        isfollowing(user.getUserid(),holder.followBtn);

        if(user.getUserid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
        {
            holder.followBtn.setVisibility(View.GONE);
        }

        holder.followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.followBtn.getText().equals("Follow")) {

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Following").child(user.getUserid()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUserid()).child("Followers")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(mContext, "Following", Toast.LENGTH_SHORT).show();

                                holder.followBtn.setText("Following");
                                holder.followBtn.setBackgroundResource(R.drawable.btn_background_following);
                                holder.followBtn.setTextColor(Color.BLACK);
                            }

                        }
                    });
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Following").child(user.getUserid()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUserid()).child("Followers")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                Toast.makeText(mContext, "Unfollowed", Toast.LENGTH_SHORT).show();

                                holder.followBtn.setText("Follow");
                                holder.followBtn.setBackgroundResource(R.drawable.btn_background_follow);
                                holder.followBtn.setTextColor(Color.WHITE);
                            }

                        }
                    });


                }


            }
        });

    }

    private void isfollowing(final String userid, final Button followBtn) {

        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild(userid))
                {
                    followBtn.setText("Following");
                    followBtn.setBackgroundResource(R.drawable.btn_background_following);
                    followBtn.setTextColor(Color.BLACK);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }




    @Override
    public int getItemCount() {
        return mUsers.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView profileImage;
        public TextView username;
        public TextView quote;
        public Button followBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.username);
            quote = itemView.findViewById(R.id.quote);
            followBtn = itemView.findViewById(R.id.btn_follow);



        }
    }
}
