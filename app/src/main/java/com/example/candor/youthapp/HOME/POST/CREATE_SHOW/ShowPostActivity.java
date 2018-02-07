package com.example.candor.youthapp.HOME.POST.CREATE_SHOW;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.candor.youthapp.CHAT.Messages;
import com.example.candor.youthapp.HOME.POST.COMMENT.Comments;
import com.example.candor.youthapp.HOME.POST.COMMENT.PostCommentAdapter;
import com.example.candor.youthapp.NotificationFragment.Notifications;
import com.example.candor.youthapp.PROFILE.ProfileActivity;
import com.example.candor.youthapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShowPostActivity extends AppCompatActivity {

    //-- FIREBASE ---//
    DatabaseReference mRootRef;

    //--VARIEABLES---//
    private String postID;
    private String mUserID;


    //---------- WIDGETS --//
    private ImageView mShowPostImage;
    private TextView mSHowPostLikeCount;
    private EditText mSHowPostCreateComment;
    private ImageButton mSHowPostDoComment;
    private RecyclerView mShowPostCommentRecycler;


    //------- COMMENT LOADING STUFF ----//
    final List<Comments> commentList = new ArrayList<>();
    LinearLayoutManager mLinearLayout;
    PostCommentAdapter mPostCommentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_post);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        postID = getIntent().getStringExtra("postID");
        mUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //----- WIDGETS ----//
        mShowPostImage = findViewById(R.id.show_post_image);
        mShowPostCommentRecycler = findViewById(R.id.show_post_comment_recycler);
        mSHowPostCreateComment = findViewById(R.id.show_post_comment_write);
        mSHowPostDoComment = findViewById(R.id.show_post_comment_post);
        mSHowPostLikeCount= findViewById(R.id.show_post_like_count);




        final DatabaseReference postDatabase = mRootRef.child("posts");
        postDatabase.keepSynced(true);
        postDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Posts post = dataSnapshot.child(postID).getValue(Posts.class);
                final String postImageUrl = post.getPost_image_url();
                final String postLikeCount = post.getLike_cnt();
                mSHowPostLikeCount.setText(postLikeCount + "  people liked this post !!");
                Picasso.with(ShowPostActivity.this).load(postImageUrl).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_blank_profile).into(mShowPostImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        //do nothing if an image is found offline
                    }
                    @Override
                    public void onError() {
                        Picasso.with(ShowPostActivity.this).load(postImageUrl).placeholder(R.drawable.ic_blank_profile).into(mShowPostImage);
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



        DatabaseReference commentDatabase   = mRootRef.child("comments").child(postID);
        commentDatabase.keepSynced(true);
        commentDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comments comment = dataSnapshot.getValue(Comments.class);
                commentList.add(comment);
                mPostCommentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //--------- SETTING THE COMMENT ADAPTERS --//
        mPostCommentAdapter = new PostCommentAdapter(commentList , ShowPostActivity.this);
        mShowPostCommentRecycler = findViewById(R.id.show_post_comment_recycler);
        mLinearLayout = new LinearLayoutManager(ShowPostActivity.this);
        mShowPostCommentRecycler.hasFixedSize();
        mShowPostCommentRecycler.setLayoutManager(mLinearLayout);
        mShowPostCommentRecycler.setAdapter(mPostCommentAdapter);

        mSHowPostDoComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Posts post = dataSnapshot.child(postID).getValue(Posts.class);

                        // ------- BUILDING A NOTIFICATION FOR THIS EVENT -----//
                        String commentNotificatoinPushID = mRootRef.child("notifications").child(post.getUid()).push().getKey();
                        String time_stamp = String.valueOf(new Date().getTime());
                        Notifications pushNoti = new Notifications( "comment" ,mUserID , postID , time_stamp,"n"  );
                        mRootRef.child("notifications").child(post.getUid()).child(commentNotificatoinPushID).setValue(pushNoti);


                        Comments  comment =  new Comments(mSHowPostCreateComment.getText().toString() , mUserID , postID  , commentNotificatoinPushID  , time_stamp);
                        mSHowPostCreateComment.setText("");
                        mRootRef.child("comments").child(postID).push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("comment status  " , "done !");
                            }
                        });

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });





    }
}
