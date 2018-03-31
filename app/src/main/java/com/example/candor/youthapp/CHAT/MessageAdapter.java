package com.example.candor.youthapp.CHAT;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.candor.youthapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mohammad Faisal on 12/3/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{



    private List <Messages> mMessageList;
    private FirebaseAuth mAuth;
    private static final int MESSAGE_SENT = 1;
    private static final int MESSAGE_RECEIVED = 2;
    Context context;

    public MessageAdapter(List<Messages> mMessageList , Context context){
        this.mMessageList = mMessageList;
        this.context  = context;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(viewType==MESSAGE_SENT){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_sent, parent , false);
        }
        else{
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_receieved , parent , false);
        }
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        Messages c = mMessageList.get(position);
        String from = c.getFrom();
        DatabaseReference mUsersRef = FirebaseDatabase.getInstance().getReference().child("users").child(from);
        mUsersRef.keepSynced(true);
        mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String imageURL = dataSnapshot.child("thumb_image").getValue(String.class);
                ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
                imageLoader.displayImage(imageURL, holder.messageImage);

                holder.setImage(imageURL , context , R.drawable.ic_blank_profile);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.messageText.setText(c.getMessage());
    }
    @Override public int getItemViewType(int position) {
        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        if (mMessageList.get(position).getFrom().equals(current_user_id)) return MESSAGE_SENT;
        return MESSAGE_RECEIVED;
    }
    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder  extends RecyclerView.ViewHolder{
        public TextView messageText;
        public CircleImageView messageImage;
        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_item_text);
            messageImage = itemView.findViewById(R.id.message_item_image);
        }

        public void setImage(final String imageURL , final Context context , int drawable_id ){
            if(imageURL.equals("default")){

            }
            else{
                Picasso.with(context).load(imageURL).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_blank_profile).into(messageImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        //do nothing if an image is found offline
                    }
                    @Override
                    public void onError() {
                        Picasso.with(context).load(imageURL).placeholder(R.drawable.ic_blank_profile).into(messageImage);
                    }
                });
            }
        }
    }



}
