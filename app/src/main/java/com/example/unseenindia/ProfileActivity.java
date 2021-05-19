package com.example.unseenindia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unseenindia.Adapters.PhotoAdapter;
import com.example.unseenindia.Adapters.PostAdapter;
import com.example.unseenindia.Models.Post;
import com.example.unseenindia.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView noOfPosts,noOfFollowers,noOfFollowings,username,quote;
    private ImageButton userPosts,favouritePosts;


    private Button follow;
    private String userId;

    private RecyclerView recyclerViewPosts;
    private PhotoAdapter photoAdapter;
    private List<Post> postList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.profile_image);
        noOfPosts = findViewById(R.id.no_of_posts);
        noOfFollowers = findViewById(R.id.no_of_followers);
        noOfFollowings = findViewById(R.id.no_of_followings);
        username = findViewById(R.id.username);
        quote = findViewById(R.id.quote);
        userPosts = findViewById(R.id.user_posts);
        favouritePosts = findViewById(R.id.favourite_posts);
        follow = findViewById(R.id.follow_btn);

        userId = getIntent().getStringExtra("userId");


        recyclerViewPosts = findViewById(R.id.recycler_view_user_posts);
        recyclerViewPosts.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerViewPosts.setLayoutManager(gridLayoutManager);

        postList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getApplicationContext(),postList);

        recyclerViewPosts.setAdapter(photoAdapter);


        loadUserInfo();
        loadNoOfPosts();
        loadPosts();
        loadNoOfFollowers();
        loadNoOfFollowings();
        isFollowing();


        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followUser();
            }
        });


    }

    private void loadNoOfFollowings() {

        FirebaseDatabase.getInstance().getReference().child("Follow").child(userId)
                .child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    noOfFollowings.setText(String.valueOf(snapshot.getChildrenCount()));
                }
                else
                {
                    noOfFollowings.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadNoOfFollowers() {

        FirebaseDatabase.getInstance().getReference().child("Follow").child(userId)
                .child("Followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    noOfFollowers.setText(String.valueOf(snapshot.getChildrenCount()));
                }
                else
                {
                    noOfFollowers.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void isFollowing() {

        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild(userId))
                {
                    follow.setText("Following");
                    follow.setBackgroundResource(R.drawable.btn_background_following);
                    follow.setTextColor(Color.BLACK);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void followUser() {

        if(follow.getText().equals("Follow")) {

            FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("Following").child(userId).setValue(true);

            FirebaseDatabase.getInstance().getReference().child("Follow").child(userId).child("Followers")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Following", Toast.LENGTH_SHORT).show();

                        follow.setText("Following");
                        follow.setBackgroundResource(R.drawable.btn_background_following);
                        follow.setTextColor(Color.BLACK);
                    }

                }
            });
        }
        else
        {
            FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("Following").child(userId).removeValue();

            FirebaseDatabase.getInstance().getReference().child("Follow").child(userId).child("Followers")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful())
                    {
                        Toast.makeText(ProfileActivity.this, "Unfollowed", Toast.LENGTH_SHORT).show();

                        follow.setText("Follow");
                        follow.setBackgroundResource(R.drawable.btn_background_follow);
                        follow.setTextColor(Color.WHITE);
                    }

                }
            });


        }

    }

    private void loadPosts() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postList.clear();

                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Post post = dataSnapshot.getValue(Post.class);

                    if(userId.equals(post.getPublisher()))
                    {
                        postList.add(post);
                    }

                }

                photoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadNoOfPosts() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int counter = 0;

                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Post post = dataSnapshot.getValue(Post.class);

                    if(userId.equals(post.getPublisher()))
                    {
                        counter++;
                    }

                }

                noOfPosts.setText(String.valueOf(counter));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadUserInfo() {

        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                Picasso.get().load(user.getProfileImage()).into(profileImage);
                username.setText(user.getUsername());
                quote.setText(user.getQuote());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}