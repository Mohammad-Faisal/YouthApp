package com.example.candor.youthapp;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.candor.youthapp.HOME.POST.Posts;
import com.example.candor.youthapp.LazyImageLoading.ImageLoader;
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
            Posts post = data.get(position);
            UserViewHolder userViewHolder = (UserViewHolder) holder;

            userViewHolder.postCaption.setText(post.getCaption());
            userViewHolder.postDateTime.setText(post.getTime_and_date());
            userViewHolder.postUserName.setText("Faisal");
            imageLoader.DisplayImage(post.getPost_image_url(),userViewHolder.postImage);

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

    public void balsal(){};

}
