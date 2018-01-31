package com.example.candor.youthapp.HOME.POST.LIKE;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.candor.youthapp.PROFILE.ProfileActivity;
import com.example.candor.youthapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikersActivity extends AppCompatActivity {


    //variables
    private String postID;
    //firebase
    private DatabaseReference mLikesDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;
    private FirebaseUser mUser;
    //widgets
    RecyclerView mLikesActivityList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likers);


        postID =getIntent().getStringExtra("postID");



        mLikesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("likes").child(postID);
        mLikesDatabaseReference.keepSynced(true);
        mUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabaseReference.keepSynced(true);


        //widgets
        mLikesActivityList =findViewById(R.id.likers_recyclerview);
        mLikesActivityList.setHasFixedSize(true);
        mLikesActivityList.setLayoutManager(new LinearLayoutManager(LikersActivity.this));

    }




    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Likes,LikesViewHolder > firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Likes,LikesViewHolder>(
                Likes.class,
                R.layout.item_user,
                LikesViewHolder.class,
                mLikesDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(final LikesViewHolder viewHolder, Likes model, final int position) {


                final String userID = model.getUid();
                mUsersDatabaseReference.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String status = dataSnapshot.child("bio").getValue().toString();
                        String thumb_image_url = dataSnapshot.child("thumb_image").getValue().toString();
                        //online functionnality
                        if(dataSnapshot.hasChild("online")){
                            String online_state = dataSnapshot.child("online").getValue().toString();
                            //viewHolder.setUserOnline(online_state);
                        }
                        viewHolder.setName(name);
                        viewHolder.setStatus(status);
                        viewHolder.setImage(thumb_image_url,LikersActivity.this);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.mVIew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //alert dialog start


                        Intent profileIntent  = new Intent(LikersActivity.this , ProfileActivity.class);
                        profileIntent.putExtra("userID" , userID);
                        startActivity(profileIntent);


                        /*CharSequence options[] = new CharSequence[]{"Open profile" , "Send message"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(LikersActivity.this);
                        builder.setTitle("Select Options ");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //click event for each item
                                if(i==0){
                                    Intent profileIntent  = new Intent(LikersActivity.this , ProfileActivity.class);
                                    profileIntent.putExtra("userID" , userID);
                                    startActivity(profileIntent);
                                }else{
                                    //navigate to other chat intent
                                    Intent chatIntent  = new Intent(LikersActivity.this , ChatActivity.class);
                                    chatIntent.putExtra("userID" , list_user_id);
                                    startActivity(chatIntent);
                                }
                            }
                        });
                        builder.show();*/
                        //alert dialog end
                    }
                });
            }
        };
        mLikesActivityList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class LikesViewHolder extends RecyclerView.ViewHolder{

        View mVIew;

        public LikesViewHolder(View itemView) {
            super(itemView);
            mVIew =itemView;
        }


        public void setName(String name){
            TextView mUserNameView = mVIew.findViewById(R.id.item_user_name);
            mUserNameView.setText(name);
        }

        public void setStatus(String status){
            TextView mUserNameView = mVIew.findViewById(R.id.item_user_status);
            mUserNameView.setText(status);
        }


        //এখানে আমরা যেহেতু একটা সাবক্লাস ব্যাবহার করছি তাই পিকাসো তে কনটেক্সট নামের ভেরিএবল টা আমরা এই ক্লাস থেকে পাবনা
        //তাই আমরা কনটেক্সট তা কেও একটা ভেরিএবল হিসেবে পাস করে দিয়েছি আমাদের OnCreate() এই মেথড টা থেকে

        private void setImage(String ImageUrl , Context mContext){
            CircleImageView single_image = mVIew.findViewById(R.id.item_user_image);
            if(ImageUrl ==null){

            }
            else{
                Picasso.with(mContext).load(ImageUrl).placeholder(R.drawable.ic_blank_profile).into(single_image);
            }
        }

        /*public void setUserOnline(String state){
            CircleImageView single_image = mVIew.findViewById(R.id.user_online_state);
            if(state.equals("true")){
                single_image.setVisibility(View.VISIBLE);
            }else{
                single_image.setVisibility(View.INVISIBLE);
            }
        }*/
    }

}
