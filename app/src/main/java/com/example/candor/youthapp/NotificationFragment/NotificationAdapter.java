package com.example.candor.youthapp.NotificationFragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.candor.youthapp.GENERAL.GetTimeAgo;
import com.example.candor.youthapp.HOME.POST.CREATE_SHOW.ShowPostActivity;
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
 * Created by Mohammad Faisal on 1/30/2018.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notifications> mNotificationList;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    Context context;

    // ---- CONSTRUCTOR --//
    public NotificationAdapter(List<Notifications> mNotificationList ,Context context){
        this.mNotificationList = mNotificationList;
        this.context = context;
    }


    @Override
    public int getItemViewType(int position) {
        if( mNotificationList.get(position).getType().equals("follow")){
            //ble ble ble
            return 1;
        }
        else{
            return 1;
        }
    }
    @Override
    public NotificationAdapter.NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(viewType == 1){  //follow notification
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent , false);
            return new NotificationAdapter.NotificationViewHolder(v);
        }
        else
        return null;
    }

    @Override
    public void onBindViewHolder(final NotificationAdapter.NotificationViewHolder holder, int position) {
        Notifications notiItem = mNotificationList.get(position);
        final String type = notiItem.getType();

        if(type.equals("follow")){
            final String userID = notiItem.getUser_id();
            final String online = notiItem.getTime_stamp();
            mRootRef.child("users").child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String imageURL = dataSnapshot.child("thumb_image").getValue().toString();
                    final String userName = dataSnapshot.child("name").getValue().toString();
                    String sourceString =  "<b>" + userName + "<b>" + "  has followed your profile";
                    holder.notificationText.setText(Html.fromHtml(sourceString));
                    holder.setImage(imageURL , context , R.drawable.ic_blank_profile);

                    ////SETTING TIME AGO OF THE NOTIFICATION -//
                    /*GetTimeAgo ob = new GetTimeAgo();
                    long time = Long.parseLong(online);
                    String time_ago = ob.getTimeAgo(time ,context);
                    holder.notificationTime.setText( time_ago);*/
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            holder.notificationImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent = new Intent(context , ProfileActivity.class);
                    profileIntent.putExtra("userID" , userID);
                    context.startActivity(profileIntent);
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent = new Intent(context , ProfileActivity.class);
                    profileIntent.putExtra("userID" , userID);
                    context.startActivity(profileIntent);
                }
            });
        }
        else if (type.equals("comment")  || type.equals("like")){

            final String userID = notiItem.getUser_id();
            final String online = notiItem.getTime_stamp();
            final String postID = notiItem.getContent_id();
            mRootRef.child("users").child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String imageURL = dataSnapshot.child("thumb_image").getValue().toString();
                    final String userName = dataSnapshot.child("name").getValue().toString();
                    String sourceString = "default notification";
                    if(type.equals("comment")){
                        sourceString =  "<b>" + userName + "<b>" + "  has commented on your post";
                    }
                    else if(type.equals("like")) {
                        sourceString =  "<b>" + userName + "<b>" + "  has liked your post";
                    }

                    holder.notificationText.setText(Html.fromHtml(sourceString));
                    holder.setImage(imageURL , context , R.drawable.ic_blank_profile);

                    ////SETTING TIME AGO OF THE NOTIFICATION -//
                    GetTimeAgo ob = new GetTimeAgo();
                    long time = Long.parseLong(online);
                    String time_ago = ob.getTimeAgo(time ,context);
                    holder.notificationTime.setText( time_ago);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            holder.notificationImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent = new Intent(context , ProfileActivity.class);
                    profileIntent.putExtra("userID" , userID);
                    context.startActivity(profileIntent);
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent showPostIntent = new Intent(context , ShowPostActivity.class);
                    showPostIntent.putExtra("postID" , postID);
                    context.startActivity(showPostIntent);
                }
            });
        }else if(type.equals("like")){  ///add something
        }

    }


    @Override
    public int getItemCount() {
        return mNotificationList.size();
    }


    public class NotificationViewHolder  extends RecyclerView.ViewHolder{
        public TextView notificationText;
        public CircleImageView notificationImage;
        public TextView notificationTime;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            notificationImage = itemView.findViewById(R.id.item_notification_image);
            notificationText = itemView.findViewById(R.id.item_notification_details);
            notificationTime = itemView.findViewById(R.id.item_notification_time_ago);
        }

        public void setImage(final String imageURL , final Context context , int drawable_id ){
            if(imageURL.equals("default")){

            }
            else{
                Picasso.with(context).load(imageURL).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_blank_profile).into(notificationImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        //do nothing if an image is found offline
                    }
                    @Override
                    public void onError() {
                        Picasso.with(context).load(imageURL).placeholder(R.drawable.ic_blank_profile).into(notificationImage);
                    }
                });
            }
        }
    }



}
