package com.example.unseenindia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unseenindia.Adapters.CommentAdapter;
import com.example.unseenindia.Models.Comment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {


    private ImageView profileImage;
    private EditText comment;
    private TextView post;
    DatabaseReference commentsRef;

    private RecyclerView recyclerViewComments;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        commentsRef = FirebaseDatabase.getInstance().getReference().child("Comments");

       postId = getIntent().getStringExtra("postId");

        profileImage = findViewById(R.id.profile_image);
        comment = findViewById(R.id.comment);
        post = findViewById(R.id.post);

        recyclerViewComments = findViewById(R.id.recycler_view_comments);
        recyclerViewComments.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        recyclerViewComments.setLayoutManager(linearLayoutManager);

        commentList = new ArrayList<>();

        commentAdapter = new CommentAdapter(this,commentList);

        recyclerViewComments.setAdapter(commentAdapter);


        getComments();


        getProfileImage();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentText = comment.getText().toString();

                if(commentText.equals(""))
                {
                    Toast.makeText(CommentActivity.this, "You can't post Empty Comment", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    postComment(commentText);
                }

            }
        });


    }

    private void getComments()
    {
        FirebaseDatabase.getInstance().getReference().child("Comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();

                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Comment comment = dataSnapshot.getValue(Comment.class);

                    if(comment.getPostId().equals(postId))
                    {
                        commentList.add(comment);
                    }

                }

                commentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void postComment(String commenttext)
    {
        String commentId = commentsRef.push().getKey();

        Map<String,Object> map = new HashMap<>();
        map.put("comment",commenttext);
        map.put("publisherId",FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("commentId",commentId);
        map.put("postId",postId);

        commentsRef.child(commentId).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CommentActivity.this, "Posted", Toast.LENGTH_SHORT).show();

                comment.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CommentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void getProfileImage() {

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Picasso.get().load(snapshot.child("profileImage").getValue().toString())
                                .placeholder(R.drawable.ic_person_blue_24dp).into(profileImage);
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