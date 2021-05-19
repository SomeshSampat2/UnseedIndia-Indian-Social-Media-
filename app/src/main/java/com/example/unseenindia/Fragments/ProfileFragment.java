package com.example.unseenindia.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.unseenindia.Adapters.PhotoAdapter;
import com.example.unseenindia.Adapters.PostAdapter;
import com.example.unseenindia.FollowActivity;
import com.example.unseenindia.MainActivity;
import com.example.unseenindia.Models.Post;
import com.example.unseenindia.Models.User;
import com.example.unseenindia.ProfileSettingsActivity;
import com.example.unseenindia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {


    private ImageView profileImage;
    private TextView noOfPosts,noOfFollowers,noOfFollowings,username,quote;
    private ImageButton myPosts,favouritePosts;
    private List<Post> postList;
    private RecyclerView recyclerViewPosts;
    private Button editProfile;
    private PhotoAdapter photoAdapter;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Profile");
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();


        profileImage = view.findViewById(R.id.profile_image);
        noOfPosts = view.findViewById(R.id.no_of_posts);
        noOfFollowers = view.findViewById(R.id.no_of_followers);
        noOfFollowings = view.findViewById(R.id.no_of_followings);
        username = view.findViewById(R.id.username);
        quote = view.findViewById(R.id.quote);
        myPosts = view.findViewById(R.id.my_posts);
        favouritePosts = view.findViewById(R.id.favourite_posts);
        editProfile = view.findViewById(R.id.edit_profile_btn);
        recyclerViewPosts = view.findViewById(R.id.recycler_view_my_posts);


        postList = new ArrayList<>();

        photoAdapter = new PhotoAdapter(getContext(),postList);

        recyclerViewPosts.setHasFixedSize(true);
        recyclerViewPosts.setLayoutManager(new GridLayoutManager(getContext(),3));
        recyclerViewPosts.setAdapter(photoAdapter);


        getUserInfo();
        getPostCount();
        getUserPosts();
        loadNoOfFollowers();
        loadNoOfFollowings();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Activity activity = getActivity();
                Intent intent = new Intent(getContext(), ProfileSettingsActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.static_anim,R.anim.static_anim);

            }
        });



        noOfFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FollowActivity.class);
                intent.putExtra("followVal","followers");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.static_anim,R.anim.static_anim);
            }
        });

        noOfFollowings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FollowActivity.class);
                intent.putExtra("followVal","followings");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.static_anim,R.anim.static_anim);
            }
        });


        return view;
    }

    private void loadNoOfFollowings() {

        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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
        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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

    private void getUserPosts()
    {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();

                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Post post = dataSnapshot.getValue(Post.class);

                    if(post.getPublisher().equals(firebaseUser.getUid()))
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

    private void getPostCount()
    {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int counter = 0;

                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Post post = dataSnapshot.getValue(Post.class);

                    if(post.getPublisher().equals(firebaseUser.getUid()))
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

    private void getUserInfo()
    {
        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
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
