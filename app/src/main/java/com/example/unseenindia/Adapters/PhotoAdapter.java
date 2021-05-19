package com.example.unseenindia.Adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.example.unseenindia.Models.Post;
import com.example.unseenindia.PostFullScreenActivity;
import com.example.unseenindia.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mPosts;

    public PhotoAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.photo_item,parent,false);

        return new PhotoAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Post post = mPosts.get(position);

        Picasso.get().load(post.getImageurl()).placeholder(R.drawable.ic_person_blue_24dp).into(holder.photo);

       holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {



                Intent intent = new Intent(mContext, PostFullScreenActivity.class);
                intent.putExtra("imageUrl",post.getImageurl());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)mContext,
                        Pair.<View, String>create(holder.photo,"image_trans"));

                mContext.startActivity(intent,options.toBundle());





            }
        });





    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView photo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.my_photo);

        }
    }

}
