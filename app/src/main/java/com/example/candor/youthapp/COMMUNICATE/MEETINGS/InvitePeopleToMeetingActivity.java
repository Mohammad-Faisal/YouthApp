package com.example.candor.youthapp.COMMUNICATE.MEETINGS;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.candor.youthapp.CHAT.ChatActivity;
import com.example.candor.youthapp.CHAT.ChatBuddies;
import com.example.candor.youthapp.COMMUNICATE.CHATS.Actives;
import com.example.candor.youthapp.COMMUNICATE.CHATS.ChatsFragment;
import com.example.candor.youthapp.GENERAL.GetTimeAgo;
import com.example.candor.youthapp.PROFILE.ProfileActivity;
import com.example.candor.youthapp.PROFILE.Users;
import com.example.candor.youthapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class InvitePeopleToMeetingActivity extends AppCompatActivity {

    //----------- VARIABLES ----------//
    private String mMeetingID;
    private MeetingRooms mMeetingRoom;
    int number_of_person = 0;


    // ------- WIDGETS -/
    RecyclerView mChatsFragmentHorizontalRecycler, mChatsFragmentVerticalRecycler, vertical_recycler_view, horizontal_recycler_view;



    //----- CONTAINER -
    private ArrayList<Users> horizontalList;
    private ArrayList<Users> verticalList;
    private ChatsFragment.HorizontalAdapter horizontalAdapter;
    private ChatsFragment.VerticalAdapter verticalAdapter;


    //-------FIREBASE -//
    DatabaseReference mChatsDatabase, mFollowingsDatabase, mUsersDatabase , mRootRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_people_to_meeting);
        mMeetingRoom = (MeetingRooms) getIntent().getSerializableExtra("meetingID");
        mMeetingID = mMeetingRoom.getMeeting_id();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mRootRef = FirebaseDatabase.getInstance().getReference();


        vertical_recycler_view =findViewById(R.id.invite_people_to_meeting_vertical_recycler_view);
        vertical_recycler_view.setHasFixedSize(true);
        vertical_recycler_view.setLayoutManager(new LinearLayoutManager(InvitePeopleToMeetingActivity.this));


        horizontal_recycler_view =findViewById(R.id.invite_people_to_meeting_horizontal_recycler_view);
        horizontal_recycler_view.setHasFixedSize(true);
        horizontal_recycler_view.setLayoutManager(new LinearLayoutManager(InvitePeopleToMeetingActivity.this));

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users,UsersViewHolder > firebaseRecyclerAdapterVer = new FirebaseRecyclerAdapter<Users,UsersViewHolder>(
                Users.class,
                R.layout.invite_to_meeting_vertical,
                UsersViewHolder.class,
                mUsersDatabase
        ) {
            @Override
            protected void populateViewHolder(final UsersViewHolder viewHolder, Users model, final int position) {


                String userName = model.getName();
                String status = model.getBio();
                String thumbImageUrl = model.getThumb_image();
                final String itemUserID = model.getId();


                viewHolder.setName(userName);
                viewHolder.setStatus(status);
                viewHolder.setImage(thumbImageUrl,InvitePeopleToMeetingActivity.this);
                viewHolder.mInviteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Map inviteMap = new HashMap();
                        inviteMap.put("person_meeting/"+itemUserID+"/"+mMeetingID , mMeetingRoom);
                        inviteMap.put("meeting_person/"+mMeetingID+"/"+itemUserID , mMeetingRoom);
                        number_of_person = number_of_person +1;
                        String t = String.valueOf(number_of_person);
                        inviteMap.put("meetings/"+mMeetingID+"/number_of_person" , t );
                        mRootRef.updateChildren(inviteMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            }
                        });
                    }
                });


            }
        };
        vertical_recycler_view.setAdapter(firebaseRecyclerAdapterVer);
    }




    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mVIew;
        Button mInviteButton;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mVIew =itemView;
            mInviteButton = mVIew.findViewById(R.id.item_invite_vertical_button);
        }


        public void setName(String name){
            TextView mUserNameView = mVIew.findViewById(R.id.item_invite_vertical_name);
            mUserNameView.setText(name);
        }

        public void setStatus(String status){
            TextView mUserNameView = mVIew.findViewById(R.id.item_invite_vertical_status);
            mUserNameView.setText(status);
        }
        private void setImage(String ImageUrl , Context mContext){
            CircleImageView single_image = mVIew.findViewById(R.id.item_invite_vertical_image);
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
