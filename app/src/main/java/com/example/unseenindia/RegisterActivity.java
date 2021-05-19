package com.example.unseenindia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    ImageView logo;
    TextView alreadyReg;
    EditText username,email,password,confirmPassword;
    Button register;
    Animation shake;
    private FirebaseAuth mAuth;
    private DatabaseReference mainDatabaseReference;
    ProgressDialog pd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        shake = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.shake);

        mAuth = FirebaseAuth.getInstance();
        mainDatabaseReference = FirebaseDatabase.getInstance().getReference();


        logo = findViewById(R.id.logo);
        alreadyReg = findViewById(R.id.already_registered);
        username = findViewById(R.id.username_edit_text);
        email = findViewById(R.id.email_edit_text);
        password = findViewById(R.id.password_edit_text);
        confirmPassword = findViewById(R.id.confirm_password_edit_text);
        register = findViewById(R.id.register_btn);
        pd = new ProgressDialog(this);

        logo.setY(-1000);
        alreadyReg.setY(1000);
        username.setX(-1000);
        email.setX(-1000);
        password.setX(-1000);
        confirmPassword.setX(-1000);
        register.setX(-1000);


        logo.animate().translationY(0).alpha(1).setDuration(1000);
        alreadyReg.animate().translationY(0).alpha(1).setDuration(2000);
        username.animate().translationX(0).alpha(1).setDuration(800);
        email.animate().translationX(0).alpha(1).setDuration(1000);
        password.animate().translationX(0).alpha(1).setDuration(1200);
        confirmPassword.animate().translationX(0).alpha(1).setDuration(1400);
        register.animate().translationX(0).alpha(1).setDuration(1600);

        alreadyReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String username_txt,email_txt,password_txt,confirm_passsword_txt;

                username_txt = username.getText().toString();
                email_txt = email.getText().toString();
                password_txt = password.getText().toString();
                confirm_passsword_txt = confirmPassword.getText().toString();

                if (username_txt.equals(""))
                {
                    username.requestFocus();
                    username.startAnimation(shake);
                    Toast.makeText(RegisterActivity.this, "Please Enter Username", Toast.LENGTH_SHORT).show();
                }
                else if (email_txt.equals(""))
                {
                    email.requestFocus();
                    email.startAnimation(shake);
                    Toast.makeText(RegisterActivity.this, "Please enter the email address", Toast.LENGTH_SHORT).show();
                }
                else if (password_txt.equals(""))
                {
                    password.requestFocus();
                    password.startAnimation(shake);
                    Toast.makeText(RegisterActivity.this, "Please enter the password", Toast.LENGTH_SHORT).show();
                }
                else if (confirm_passsword_txt.equals(""))
                {
                    confirmPassword.requestFocus();
                    confirmPassword.startAnimation(shake);
                    Toast.makeText(RegisterActivity.this, "Incorrrect password", Toast.LENGTH_SHORT).show();
                }
                else if(!password_txt.equals(confirm_passsword_txt))
                {
                    confirmPassword.requestFocus();
                    confirmPassword.startAnimation(shake);
                    Toast.makeText(RegisterActivity.this, "Incorrrect password", Toast.LENGTH_SHORT).show();
                }
                else if (password_txt.length() < 8)
                {
                    password.requestFocus();
                    password.startAnimation(shake);
                    Toast.makeText(RegisterActivity.this, "Password must contain at least 8 characters", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    pd.setTitle("Registering");
                    pd.setMessage("Please Wait...");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();

                    registerUserToFirebase(username_txt,email_txt,password_txt);
                }



            }
        });

    }

    private void registerUserToFirebase(final String username, final String email, final String password) {

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {

                            Map<String,Object> map = new HashMap<>();
                            map.put("username",username);
                            map.put("email",email);
                            map.put("password",password);
                            map.put("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());

                            mainDatabaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(map);
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "You are successfully registered", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this,ProfileSettingsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        }
                        else
                        {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


}
