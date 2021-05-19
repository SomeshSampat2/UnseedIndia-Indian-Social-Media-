package com.example.unseenindia.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.unseenindia.Adapters.PostAdapter;
import com.example.unseenindia.Models.Post;
import com.example.unseenindia.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FollowersPostsFragment extends Fragment {


    private RecyclerView recyclerViewFollowersPosts;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private List<String> followingsList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("People You Follow");
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        View view = inflater.inflate(R.layout.fragment_followers_posts, container, false);

        recyclerViewFollowersPosts = view.findViewById(R.id.followers_post_recycler_view);

        recyclerViewFollowersPosts.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);

        recyclerViewFollowersPosts.setLayoutManager(linearLayoutManager);

        postList = new ArrayList<>();

        postAdapter = new PostAdapter(getContext(),postList);



        recyclerViewFollowersPosts.setAdapter(postAdapter);

        followingsList = new ArrayList<>();

        getFollowingsList();




        return view;
    }

    private void getFollowingsList() {

        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                followingsList.clear();

                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    followingsList.add(dataSnapshot.getKey());
                }

                getPosts();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void getPosts() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postList.clear();

                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                      Post post = dataSnapshot.getValue(Post.class);

                      for (String id:followingsList)
                      {
                          if (post.getPublisher().equals(id))
                          {
                              postList.add(post);
                          }
                      }

                }
                postAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}