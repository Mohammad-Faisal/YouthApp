package com.example.candor.youthapp.HOME.POST;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.candor.youthapp.CHAT.MessageAdapter;
import com.example.candor.youthapp.CHAT.Messages;
import com.example.candor.youthapp.R;

import java.util.List;

/**
 * Created by Mohammad Faisal on 1/29/2018.
 */

public class PostCommentAdapter extends RecyclerView.Adapter<PostCommentAdapter.PostCommentViewHolder> {



    private List<Comments> mCommentList;

    // ---- CONSTRUCTOR --//
    public PostCommentAdapter(List<Comments> mCommentList){
        this.mCommentList = mCommentList;
    }


    @Override
    public PostCommentAdapter.PostCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent , false);
        return new PostCommentAdapter.PostCommentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PostCommentAdapter.PostCommentViewHolder holder, int position) {
        Comments c = mCommentList.get(position);
        holder.commentText.setText(c.getComment());
    }


    @Override
    public int getItemCount() {
        return mCommentList.size();
    }


    public class PostCommentViewHolder  extends RecyclerView.ViewHolder{
        public TextView commentText;

        public PostCommentViewHolder(View itemView) {
            super(itemView);
            commentText = itemView.findViewById(R.id.comment_item_text);
        }
    }



}
