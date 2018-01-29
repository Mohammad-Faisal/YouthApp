package com.example.candor.youthapp.HOME.POST;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.candor.youthapp.CHAT.MessageAdapter;
import com.example.candor.youthapp.CHAT.Messages;
import com.example.candor.youthapp.HOME.POST.Posts;
import com.example.candor.youthapp.LazyImageLoading.ImageLoader;
import com.example.candor.youthapp.OnLoadMoreListener;
import com.example.candor.youthapp.PROFILE.ProfileActivity;
import com.example.candor.youthapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mohammad Faisal on 1/21/2018.
 */

public class NewsFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    List<Posts> data = Collections.emptyList();
    Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    boolean isLoading;
    public ImageLoader imageLoader;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    RecyclerView recyclerView;
    private Activity activity;
    public boolean likeFunction = false;

    private DatabaseReference mPostsDatabaseReference , mUserDatabaseReference ,  mRootRef;
    private String mUserID;


    //for comment
    //containers
    private final List<Messages> messageList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private PostCommentAdapter mCommentAdapter;


    //initialize the inflator by yourself
    private LayoutInflater inflater;

    private OnLoadMoreListener onLoadMoreListener;
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }


    public NewsFeedAdapter(RecyclerView recyclerView , List<Posts> data , Activity activity , final Context context) {
        inflater = LayoutInflater.from(context);
        this.context  = context;
        this.activity = activity;
        this.data = data;
        this.recyclerView = recyclerView;
        imageLoader=new ImageLoader(context);
        this.mRootRef = FirebaseDatabase.getInstance().getReference();
        this.mRootRef.keepSynced(true);
        this.mUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });

    }


    @Override
    public int getItemViewType(int position) {
        return data.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_post, parent, false);
            return new UserViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserViewHolder) {
            final Posts post = data.get(position);
            final UserViewHolder userViewHolder = (UserViewHolder) holder;

            userViewHolder.postCaption.setText(post.getCaption());
            userViewHolder.postDateTime.setText(post.getTime_and_date());
            //imageLoader.DisplayImage(post.getPost_image_url(),userViewHolder.postImage);
            /*Picasso.with(userViewHolder.postImage.getContext())
                    .load(post.getPost_image_url())
                    .fit()
                    .into(userViewHolder.postImage);*/

            Picasso.with(context).load(post.getPost_image_url()).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_blank_profile).into(userViewHolder.postImage, new Callback() {
                @Override
                public void onSuccess() {
                    //do nothing if an image is found offline
                }
                @Override
                public void onError() {
                    Picasso.with(context).load(post.getPost_image_url()).placeholder(R.drawable.ic_blank_profile).into(userViewHolder.postImage);
                }
            });


            userViewHolder.postDateTime.setText(post.getTime_and_date());
            userViewHolder.postLocaiton.setText(post.getLocation());
            userViewHolder.postLikeCount.setText(post.getLike_cnt());



            userViewHolder.postLikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    likeFunction = true;
                    final String postPushID = post.getPost_push_id();
                    mRootRef.child("likes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(likeFunction==true){
                                if(dataSnapshot.child(postPushID).hasChild(mUserID)){ //like already exists
                                    mRootRef.child("likes").child(postPushID).child(mUserID).removeValue();
                                    //like count changing
                                    String current_likes =String.valueOf(dataSnapshot.getChildrenCount());
                                    likeFunction = false;
                                    Log.d("Home Fragment    ", "like count is    "+ current_likes);
                                    int number = Integer.parseInt(current_likes);
                                    number--;
                                    current_likes = String.valueOf(number);
                                    userViewHolder.postLikeCount.setText(current_likes);
                                    userViewHolder.postLikeButton.setImageResource(R.drawable.ic_love_empty);
                                }
                                else{
                                    mRootRef.child("likes").child(postPushID).child(mUserID).setValue("y");
                                    String current_likes =String.valueOf(dataSnapshot.getChildrenCount());
                                    likeFunction = false;
                                    int number = Integer.parseInt(current_likes);
                                    number++;
                                    current_likes = String.valueOf(number);
                                    userViewHolder.postLikeCount.setText(current_likes);
                                    userViewHolder.postLikeButton.setImageResource(R.drawable.ic_love_full);
                                }
                            }
                            else{
                                Log.d("inside else    " , "hups");
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

            userViewHolder.postCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog commentDialog = new Dialog(context);
                    commentDialog.setContentView(R.layout.comment_pop_up_dialog);


                    //containers
                    final RecyclerView mCommentList;
                    final List<Comments> commentList = new ArrayList<>();
                    LinearLayoutManager mLinearLayout;
                    PostCommentAdapter mPostCommentAdapter;

                    final String postPushID = post.getPost_push_id();
                    //mRootRef.child("comments").child(postPushID).push().setValue(new Comments("hi" , "hlw"));
                    //-------------LOADING COMMENTS------------//
                    mRootRef.child("comments").child(postPushID).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Comments c = dataSnapshot.getValue(Comments.class);
                            commentList.add(c);
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

                     final TextView commentBox  = commentDialog.findViewById(R.id.comment_write);
                     ImageButton commentPost =  commentDialog.findViewById(R.id.comment_post);

                     commentPost.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View view) {

                             Comments  comment =  new Comments(commentBox.getText().toString() , mUserID );
                             commentBox.setText("");
                             mRootRef.child("comments").child(postPushID).push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {
                                     Log.d("comment sstatus  " , "done !");
                                 }
                             });
                         }
                     });



                    //--------- SETTING THE COMMENT ADAPTERS --//
                    mPostCommentAdapter = new PostCommentAdapter(commentList);
                    mCommentList = commentDialog.findViewById(R.id.comment_list);
                    mLinearLayout = new LinearLayoutManager(context);
                    mCommentList.hasFixedSize();
                    mCommentList.setLayoutManager(mLinearLayout);
                    mCommentList.setAdapter(mPostCommentAdapter);


                    commentDialog.show();
                    Log.d("comment" , "asche  ");
                }
            });


            userViewHolder.postUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String destinationUserID = post.getUid();
                    Intent profileIntent = new Intent(context , ProfileActivity.class);
                    profileIntent.putExtra("userID" , destinationUserID);
                    context.startActivity(profileIntent);
                }
            });

            userViewHolder.postUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String destinationUserID = post.getUid();
                    Intent profileIntent = new Intent(context , ProfileActivity.class);
                    profileIntent.putExtra("userID" , destinationUserID);
                    context.startActivity(profileIntent);
                }
            });



            // ------------ POPULATE USER DETAILS ----//
            final String list_user_id = post.getUid();
            mRootRef.child("users").child(list_user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    final String thumb_image_url = dataSnapshot.child("thumb_image").getValue().toString();
                    imageLoader.DisplayImage(thumb_image_url,userViewHolder.postUserImage);
                    userViewHolder.postUserName.setText(name);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            //---------------   SETTING IMAGE RESOURCE BASED ON THE LOGGED IN USER -----//
            final String postPushID = post.getPost_push_id();
            mRootRef.child("likes").child(postPushID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String current_likes =String.valueOf(dataSnapshot.getChildrenCount());
                    userViewHolder.postLikeCount.setText(current_likes);
                    if(dataSnapshot.hasChild(mUserID)){ //like already exists
                        userViewHolder.postLikeButton.setImageResource(R.drawable.ic_love_full);
                    }
                    else{
                        userViewHolder.postLikeButton.setImageResource(R.drawable.ic_love_empty);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }
    public void setLoaded() {
        isLoading = false;
    }
    // "Loading item" ViewHolder
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = view.findViewById(R.id.progressBar1);
        }
    }

    // "Normal item" ViewHolder
    private class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView postUserName;
        public TextView postCaption;
        public TextView postDateTime;
        public TextView postLocaiton;
        public TextView postLikeCount;
        public TextView postCommentCount;
        public ImageView postImage;
        public CircleImageView postUserImage;
        public ImageButton postLikeButton;
        public ImageButton postCommentButton;

        public UserViewHolder(View view) {
            super(view);

            postUserName  =   view.findViewById(R.id.post_user_name);
            postCaption   =   view.findViewById(R.id.post_caption);
            postDateTime   =   view.findViewById(R.id.post_time_date);
            postLocaiton   =   view.findViewById(R.id.post_location);
            postImage   =   view.findViewById(R.id.post_image);
            postUserImage   =   view.findViewById(R.id.post_user_single_imagee);
            postLikeButton   =   view.findViewById(R.id.post_like_button);
            postCommentButton   =   view.findViewById(R.id.post_comment_button);
            postLikeCount =  view.findViewById(R.id.post_like_number);
            postCommentCount = view.findViewById(R.id.post_comment_number);


        }
    }


    /*@Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        info currentInfo = data.get(position);
        holder.txt.setText(currentInfo.getName());

        imageLoader.DisplayImage(currentInfo.getImgURL(), holder.img);

        *//*Picasso.with(holder.img.getContext())
                .load(currentInfo.getImgURL())
                .fit()
                .into(holder.img);*//*

        holder.txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(  context ,"nothing",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    public Object getItem(int position) {return position;}
    public long getItemId(int position) {
        return position;
    }


}
