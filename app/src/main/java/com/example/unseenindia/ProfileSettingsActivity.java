package com.example.unseenindia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class ProfileSettingsActivity extends AppCompatActivity {

    CircleImageView profileImage;
    TextView yourProfilePicture,name,quoteText,edit;
    EditText username,quote;
    private DatabaseReference userRef;
    Button save;
    public static final int PICK_IMAGE = 1;
    private StorageReference profileImageRef;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);


        getSupportActionBar().setTitle("Profile Settings");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0CBABA")));
        getSupportActionBar().show();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        profileImageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");

        profileImage = findViewById(R.id.profileImage);
        yourProfilePicture = findViewById(R.id.your_profile_picture);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        edit = findViewById(R.id.edit);
        quoteText = findViewById(R.id.quote_text);
        quote = findViewById(R.id.quote);
        save = findViewById(R.id.save_btn);
        pd = new ProgressDialog(ProfileSettingsActivity.this);


        profileImage.setY(1000);
        yourProfilePicture.setY(1000);
        name.setY(1000);
        username.setY(1000);
        quoteText.setY(1000);
        quote.setY(1000);
        save.setY(1000);

        profileImage.animate().translationY(0).alpha(1).setDuration(500);
        yourProfilePicture.animate().translationY(0).alpha(1).setDuration(700);
        name.animate().translationY(0).alpha(1).setDuration(900);
        username.animate().translationY(0).alpha(1).setDuration(1100);
        quoteText.animate().translationY(0).alpha(1).setDuration(1300);
        quote.animate().translationY(0).alpha(1).setDuration(1500);
        save.animate().translationY(0).alpha(1).setDuration(1500);

        retrievedata();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username.setEnabled(true);
                username.requestFocus();
                quote.setEnabled(true);
            }
        });





        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username.setEnabled(false);
                quote.setEnabled(false);

                String newUsername = username.getText().toString();
                String newQuote = quote.getText().toString();

                if(newUsername.equals(""))
                {
                    Toast.makeText(ProfileSettingsActivity.this, "Username Required", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Map<String,Object> map = new HashMap<>();
                    map.put("username",newUsername);
                    if(!newQuote.equals(""))
                    map.put("quote",newQuote);

                    userRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileSettingsActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(ProfileSettingsActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity()
                        .setAspectRatio(1,1)
                        .start(ProfileSettingsActivity.this);
            }
        });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            pd.setTitle("Updating Image");
            pd.setMessage("Please Wait..");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                final StorageReference filepath = profileImageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");

                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                pd.dismiss();
                                  final String downloadUrl = uri.toString();

                                  userRef.child("profileImage").setValue(downloadUrl);
                                Toast.makeText(ProfileSettingsActivity.this, "Profile Image Updated Successfully..", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileSettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileSettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }



    private void retrievedata() {

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username_txt = snapshot.child("username").getValue().toString();
                username.setText(username_txt);

                if(snapshot.hasChild("quote"))
                {
                    String quote_txt = snapshot.child("quote").getValue().toString();
                    quote.setText(quote_txt);
                }
                if (snapshot.hasChild("profileImage"))
                {
                    Picasso.get().load(snapshot.child("profileImage").getValue().toString())
                            .placeholder(R.drawable.ic_person_blue_24dp).into(profileImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(ProfileSettingsActivity.this,MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.static_anim,R.anim.static_anim);
    }
}
