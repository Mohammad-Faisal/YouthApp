package com.example.candor.youthapp.HOME.POST.COMMENT;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.candor.youthapp.PROFILE.ProfileActivity;
import com.example.candor.youthapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mohammad Faisal on 1/29/2018.
 */

public class PostCommentAdapter extends RecyclerView.Adapter<PostCommentAdapter.PostCommentViewHolder> {



    private List<Comments> mCommentList;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    Context context;

    // ---- CONSTRUCTOR --//
    public PostCommentAdapter(List<Comments> mCommentList ,Context context){
        this.mCommentList = mCommentList;
        this.context = context;
    }

    @Override
    public PostCommentAdapter.PostCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent , false);
        return new PostCommentAdapter.PostCommentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PostCommentAdapter.PostCommentViewHolder holder, int position) {
        Comments c = mCommentList.get(position);
        holder.commentText.setText(c.getComment());
        final String userID  = c.getUid();
        mRootRef.child("users").child(userID).keepSynced(true);
        mRootRef.child("users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String imageURL = dataSnapshot.child("thumb_image").getValue().toString();
                final String CommentName = dataSnapshot.child("name").getValue().toString();
                holder.commentName.setText(CommentName);
                if(imageURL.equals("default")){

                }
                else{
                    Picasso.with(context).load(imageURL).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.ic_blank_profile).into(holder.commentImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            //do nothing if an image is found offline
                        }
                        @Override
                        public void onError() {
                            Picasso.with(context).load(imageURL).placeholder(R.drawable.ic_blank_profile).into(holder.commentImage);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.commentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(context , ProfileActivity.class);
                profileIntent.putExtra("userID" , userID);
                context.startActivity(profileIntent);
            }
        });
        holder.commentName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(context ,ProfileActivity.class);
                profileIntent.putExtra("userID" , userID);
                context.startActivity(profileIntent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mCommentList.size();
    }


    public class PostCommentViewHolder  extends RecyclerView.ViewHolder{
        public TextView commentText;
        public CircleImageView commentImage;
        public TextView commentName;

        public PostCommentViewHolder(View itemView) {
            super(itemView);
            commentText = itemView.findViewById(R.id.comment_item_text);
            commentImage = itemView.findViewById(R.id.comment_item_image);
            commentName = itemView.findViewById(R.id.comment_item_name);
        }
    }



}
