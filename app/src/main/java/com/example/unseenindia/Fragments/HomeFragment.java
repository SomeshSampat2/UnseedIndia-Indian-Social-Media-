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
import com.example.unseenindia.MainActivity;
import com.example.unseenindia.Models.Post;
import com.example.unseenindia.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private List<Post> postList;
    private PostAdapter postAdapter;
    private RecyclerView recyclerViewPosts;
    private MainActivity mainActivity;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        recyclerViewPosts = view.findViewById(R.id.recycler_view_posts);

        postList = new ArrayList<>();

        postAdapter = new PostAdapter(getContext(),postList);

        recyclerViewPosts.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);


        recyclerViewPosts.setLayoutManager(linearLayoutManager);

        recyclerViewPosts.setAdapter(postAdapter);

        loadPosts();





        return view;
    }

    private void loadPosts()
    {
         FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();

                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                     Post post = dataSnapshot.getValue(Post.class);

                     postList.add(post);
                }

                postAdapter.notifyDataSetChanged();

             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
    }


}
