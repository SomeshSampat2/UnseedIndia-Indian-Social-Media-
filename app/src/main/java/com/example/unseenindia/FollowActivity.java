package com.example.unseenindia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.unseenindia.Adapters.SearchAdapter;
import com.example.unseenindia.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowActivity extends AppCompatActivity {


    private TextView follow;
    private RecyclerView recyclerViewFollow;
    private SearchAdapter searchAdapter;
    private List<User> userList;
    private List<String> idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        String followVal = getIntent().getStringExtra("followVal");

        follow = findViewById(R.id.follow);
        recyclerViewFollow = findViewById(R.id.recycler_view_follow);

        recyclerViewFollow.setHasFixedSize(true);
        recyclerViewFollow.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        idList = new ArrayList<>();

        searchAdapter = new SearchAdapter(userList,getApplicationContext());

        recyclerViewFollow.setAdapter(searchAdapter);


        if (followVal.equals("followers")){

            follow.setText("Followers");

            getFollowers();

        }

        if (followVal.equals("followings")){

            follow.setText("Followings");

            getFollowings();

        }



    }

    private void getFollowings() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();

                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    idList.add(dataSnapshot.getKey());
                }

                displayFollow();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getFollowers() {

        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();

                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    idList.add(dataSnapshot.getKey());
                }

                displayFollow();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void displayFollow() {

        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();

                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    User user = dataSnapshot.getValue(User.class);

                    for (String id:idList)
                    {

                        if (id.equals(user.getUserid()))
                        {
                            userList.add(user);
                        }


                    }

                }

                searchAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.static_anim,R.anim.static_anim);

    }
}