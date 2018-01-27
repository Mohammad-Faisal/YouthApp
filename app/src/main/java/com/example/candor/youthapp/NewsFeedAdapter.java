package com.example.candor.youthapp;

import android.app.Activity;
import android.content.Context;
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
import com.example.candor.youthapp.HOME.POST.Posts;
import com.example.candor.youthapp.LazyImageLoading.ImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
            imageLoader.DisplayImage(post.getPost_image_url(),userViewHolder.postImage);
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
                            if(likeFunction){
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
            final String mUserID = post.getUid();
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
