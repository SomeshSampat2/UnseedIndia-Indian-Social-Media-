package com.example.unseenindia;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class PostFullScreenActivity extends AppCompatActivity {

    private ImageView fullScreenImage;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_full_screen);

        fullScreenImage = findViewById(R.id.full_screen_image);

        imageUrl = getIntent().getStringExtra("imageUrl");

        Picasso.get().load(imageUrl).into(fullScreenImage);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.static_anim,R.anim.static_anim);

    }
}