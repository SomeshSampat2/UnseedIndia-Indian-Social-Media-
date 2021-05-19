package com.example.unseenindia.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unseenindia.CommentActivity;


import com.example.unseenindia.Fragments.ProfileFragment;
import com.example.unseenindia.Models.Comment;
import com.example.unseenindia.Models.Post;
import com.example.unseenindia.Models.User;
import com.example.unseenindia.ProfileActivity;
import com.example.unseenindia.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;


import java.util.List;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {


    private Context mContext;
    private List<Post> mPosts;




    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

         View view = LayoutInflater.from(mContext).inflate(R.layout.post_item,parent,false);
         return new PostAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

         final Post post = mPosts.get(position);



        Picasso.get().load(post.getImageurl()).into(holder.postImage);
        holder.description.setText(post.getDescription());


        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                holder.username.setText(user.getUsername());

                Picasso.get().load(user.getProfileImage()).placeholder(R.drawable.ic_person_blue_24dp).into(holder.profileImage);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

                FirebaseDatabase.getInstance().getReference().child("Likes")
                        .child(post.getPostid()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);



            }

            @Override
            public void unLiked(LikeButton likeButton) {

                FirebaseDatabase.getInstance().getReference().child("Likes")
                        .child(post.getPostid()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();


            }
        });

        isLiked(post.getPostid(),FirebaseAuth.getInstance().getCurrentUser().getUid(),holder.likeButton);

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Activity activity = (Activity) mContext;
                Intent intent = new Intent(mContext, CommentActivity.class);

                intent.putExtra("postId",post.getPostid());

                activity.startActivity(intent);

                activity.overridePendingTransition(R.anim.static_anim,R.anim.static_anim);



            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!post.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Intent intent = new Intent(mContext, ProfileActivity.class);

                    intent.putExtra("userId", post.getPublisher());

                    mContext.startActivity(intent);
                }
                else
                {
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment myFragment = new ProfileFragment();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();

                }
            }
        });


        getNoOfLikes(post.getPostid(),holder.noOfLikes);


        holder.postOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popUpMenus(holder.postOptions,post.getPostid(),mContext,post.getPublisher());

            }
        });


    }

    private void popUpMenus(ImageView postOptions, final String postid, final Context mContext, final String publisherId)
    {
       final PopupMenu popupMenu = new PopupMenu(mContext,postOptions);

       popupMenu.getMenuInflater().inflate(R.menu.post_menu,popupMenu.getMenu());

       popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(MenuItem menuItem) {

               switch (menuItem.getItemId())
               {
                   case R.id.delete:

                       if(publisherId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                       {
                           FirebaseDatabase.getInstance().getReference().child("Posts").child(postid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {

                                   if (task.isSuccessful())
                                   {
                                       FirebaseDatabase.getInstance().getReference().child("Likes").child(postid).removeValue();

                                       FirebaseDatabase.getInstance().getReference().child("Comments").addValueEventListener(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull DataSnapshot snapshot) {



                                               for(DataSnapshot dataSnapshot:snapshot.getChildren()) {

                                                   Comment comment = dataSnapshot.getValue(Comment.class);

                                                   if (postid.equals(comment.getPostId())) {
                                                       FirebaseDatabase.getInstance().getReference().child("Comments").child(comment.getCommentId()).removeValue();
                                                   }
                                               }
                                           }

                                           @Override
                                           public void onCancelled(@NonNull DatabaseError error) {

                                           }
                                       });

                                       Toast.makeText(mContext, "Post Deleted Successfully", Toast.LENGTH_SHORT).show();
                                   }
                                   else
                                   {
                                       Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                   }

                               }
                           });
                       }
                       else
                       {
                           Toast.makeText(mContext, "You cannot delete this Post", Toast.LENGTH_SHORT).show();
                       }

                       break;


                   case R.id.report:

                       FirebaseDatabase.getInstance().getReference().child("Posts").child(postid)
                               .child("Reports").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

                       Toast.makeText(mContext, "Reported the Post ..", Toast.LENGTH_SHORT).show();


                       break;

                   default:

                       return false;




               }

               return true;
           }
       });

        popupMenu.show();

    }


    private void getNoOfLikes(final String postid, final TextView noOfLikes)
    {
       FirebaseDatabase.getInstance().getReference().child("Likes").addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {

               if(snapshot.hasChild(postid))
               {
                   noOfLikes.setText(String.valueOf(snapshot.child(postid).getChildrenCount()) + " Likes");
               }
               else
               {
                   noOfLikes.setText("No Likes");
               }


           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }


    private void isLiked(final String postid, final String uid,final LikeButton likeButton) {

        FirebaseDatabase.getInstance().getReference().child("Likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.hasChild(postid))
                {
                    if(snapshot.child(postid).hasChild(uid))
                    {
                        likeButton.setLiked(true);
                    }
                    else
                    {
                        likeButton.setLiked(false);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


   public ImageView profileImage,postImage,comments,postOptions;
   public TextView username,noOfLikes,description,impressions;
   public LikeButton likeButton;




       public ViewHolder(@NonNull View itemView) {
           super(itemView);

           profileImage = itemView.findViewById(R.id.profile_image);
           postImage = itemView.findViewById(R.id.posted_image);
           comments = itemView.findViewById(R.id.comments);
           username = itemView.findViewById(R.id.username);
           noOfLikes = itemView.findViewById(R.id.no_of_likes);
           description = itemView.findViewById(R.id.description);
           impressions = itemView.findViewById(R.id.impressions);
           likeButton = itemView.findViewById(R.id.like_button);
           postOptions = itemView.findViewById(R.id.post_options);

       }
   }

}
