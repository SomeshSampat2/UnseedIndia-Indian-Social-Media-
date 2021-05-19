package com.example.unseenindia.Fragments;

import android.app.Activity;
import android.app.DownloadManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.unseenindia.Adapters.SearchAdapter;
import com.example.unseenindia.MainActivity;
import com.example.unseenindia.Models.User;
import com.example.unseenindia.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private List<User> userList;
    private SearchAdapter searchAdapter;
    private RecyclerView recyclerViewSearchedusers;
    private EditText searchText;
    private MainActivity mainActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchText = view.findViewById(R.id.search_bar);
        recyclerViewSearchedusers = view.findViewById(R.id.searched_list);
        userList = new ArrayList<>();
        searchAdapter = new SearchAdapter(userList,getContext());

        recyclerViewSearchedusers.setHasFixedSize(true);
        recyclerViewSearchedusers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewSearchedusers.setAdapter(searchAdapter);

        displayAllUsers();

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                searchUsers(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });






        return view;
    }

    private void displayAllUsers() {

        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userList.clear();

                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    User user = dataSnapshot.getValue(User.class);

                    userList.add(user);
                }

                searchAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void searchUsers(String searchText)
    {
        Query query = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("username").startAt(searchText).endAt(searchText+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userList.clear();

                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {

                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }

                searchAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
