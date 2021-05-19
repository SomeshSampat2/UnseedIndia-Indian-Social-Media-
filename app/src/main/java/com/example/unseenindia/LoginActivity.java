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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    ImageView logo;
    TextView welcomeBack,newUser;
    Button login;
    EditText email,password;
    Animation shake;
    private FirebaseAuth mAuh;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuh = FirebaseAuth.getInstance();

        logo = findViewById(R.id.logo);
        welcomeBack = findViewById(R.id.welcomeBack);
        newUser = findViewById(R.id.new_user);
        login = findViewById(R.id.login_btn);
        email = findViewById(R.id.email_edit_text);
        password = findViewById(R.id.password_edit_text);
        pd = new ProgressDialog(this);


        shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);


        logo.setY(-1000);
        email.setX(-1000);
        password.setX(-1000);
        login.setX(-1000);
        newUser.setY(1000);

        logo.animate().translationY(0).alpha(1).setDuration(1000);
        welcomeBack.animate().alpha(1).setDuration(5000);
        email.animate().translationX(0).alpha(1).setDuration(1000);
        password.animate().translationX(0).alpha(1).setDuration(1200);
        login.animate().translationX(0).alpha(1).setDuration(1400);
        newUser.animate().translationY(0).alpha(1).setDuration(2000);

        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_txt,password_txt;

                email_txt = email.getText().toString();
                password_txt = password.getText().toString();

                if(email_txt.equals(""))
                {
                    email.requestFocus();
                    email.startAnimation(shake);
                    Toast.makeText(LoginActivity.this, "Please enter the email address", Toast.LENGTH_SHORT).show();
                }
                if (password_txt.equals(""))
                {
                    password.requestFocus();
                    password.startAnimation(shake);
                    Toast.makeText(LoginActivity.this, "Please enter the password", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    pd.setTitle("Registering");
                    pd.setMessage("Please Wait...");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();
                    signInWithEmailandPassword(email_txt,password_txt);
                }

            }
        });


    }

    private void signInWithEmailandPassword(String email, String password) {

        mAuh.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
