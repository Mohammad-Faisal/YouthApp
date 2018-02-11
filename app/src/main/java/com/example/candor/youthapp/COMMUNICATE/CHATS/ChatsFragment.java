package com.example.candor.youthapp.COMMUNICATE.CHATS;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.candor.youthapp.CHAT.ChatActivity;
import com.example.candor.youthapp.CHAT.ChatBuddies;
import com.example.candor.youthapp.GENERAL.GetTimeAgo;
import com.example.candor.youthapp.PROFILE.Users;
import com.example.candor.youthapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.model.Circle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatsFragment extends Fragment {


    // ------- WIDGETS -/
    RecyclerView mChatsFragmentHorizontalRecycler, mChatsFragmentVerticalRecycler, vertical_recycler_view, horizontal_recycler_view;

    //-------FIREBASE -//
    DatabaseReference mChatsDatabase, mFollowingsDatabase, mUsersDatabase;

    //-- ---- VARIABLES -//
    private String mUserID;

    //----- CONTAINER -
    private ArrayList<Actives> horizontalList;
    private ArrayList<ChatBuddies> verticalList;

    private HorizontalAdapter horizontalAdapter;
    private VerticalAdapter verticalAdapter;


    public ChatsFragment() {
        // Required empty public constructor
    }

    public static ChatsFragment newInstance(String param1, String param2) {
        ChatsFragment fragment = new ChatsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_chats, container, false);
        mUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //fiebase


        //-- SETTING THE DATAS ABOUT MY CHATS
        mChatsDatabase = FirebaseDatabase.getInstance().getReference().child("chat_buddies").child(mUserID);
        verticalList = new ArrayList<>();
        mChatsDatabase.keepSynced(true);
        mChatsDatabase.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatBuddies chatBuddies = dataSnapshot.getValue(ChatBuddies.class);
                verticalList.add(chatBuddies);
                verticalList.add(chatBuddies);
                verticalList.add(chatBuddies);
                verticalList.add(chatBuddies);
                verticalList.add(chatBuddies);
                verticalList.add(chatBuddies);
                verticalList.add(chatBuddies);
                verticalList.add(chatBuddies);
                verticalList.add(chatBuddies);
                verticalList.add(chatBuddies);
                verticalList.add(chatBuddies);
                verticalList.add(chatBuddies);
                verticalList.add(chatBuddies);
                verticalList.add(chatBuddies);
                verticalList.add(chatBuddies);
                verticalList.add(chatBuddies);
                verticalList.add(chatBuddies);
                verticalList.add(chatBuddies);
                verticalList.add(chatBuddies);
                verticalList.add(chatBuddies);
                verticalList.add(chatBuddies);

                verticalAdapter.notifyDataSetChanged();
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

        //-- ETTING THE DATA WHO ARE ONLINE AMONG THOSE WHOM I FOLLOW -//
        horizontalList = new ArrayList<>();
        mFollowingsDatabase =  FirebaseDatabase.getInstance().getReference().child("followings").child(mUserID);
        mFollowingsDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot != null){
                    String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String id = dataSnapshot.child("id").getValue().toString();
                    horizontalList.add(new Actives(id , name , thumb_image));
                    horizontalList.add(new Actives(id , name , thumb_image));
                    horizontalList.add(new Actives(id , name , thumb_image));
                    horizontalList.add(new Actives(id , name , thumb_image));
                    horizontalList.add(new Actives(id , name , thumb_image));
                    horizontalList.add(new Actives(id , name , thumb_image));
                    horizontalList.add(new Actives(id , name , thumb_image));
                    horizontalList.add(new Actives(id , name , thumb_image));
                    horizontalList.add(new Actives(id , name , thumb_image));
                    horizontalList.add(new Actives(id , name , thumb_image));
                    horizontalList.add(new Actives(id , name , thumb_image));
                    horizontalList.add(new Actives(id , name , thumb_image));
                    horizontalList.add(new Actives(id , name , thumb_image));
                    horizontalList.add(new Actives(id , name , thumb_image));
                    horizontalAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getContext(), "Some error occurede !!!", Toast.LENGTH_SHORT).show();
                }

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
        //mUsersDatabase  = FirebaseDatabase.getInstance().getReference().child("users");



        //----- FROM INTERNET -//
        vertical_recycler_view = (RecyclerView) mView.findViewById(R.id.vertical_recycler_view);
        horizontal_recycler_view = (RecyclerView) mView.findViewById(R.id.horizontal_recycler_view);


        horizontalAdapter = new HorizontalAdapter(horizontalList);
        verticalAdapter = new VerticalAdapter(verticalList);


        LinearLayoutManager verticalLayoutmanager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext() , 2);

        vertical_recycler_view.setLayoutManager(verticalLayoutmanager);

        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        horizontal_recycler_view.setLayoutManager(horizontalLayoutManagaer);

        vertical_recycler_view.setAdapter(verticalAdapter);
        horizontal_recycler_view.setAdapter(horizontalAdapter);


        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

        private List<Actives> horizontalList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView chats_actives_name;
            public CircleImageView chats_actives_image;

            public MyViewHolder(View view) {
                super(view);
                chats_actives_name = view.findViewById(R.id.chats_item_horizontal_text);
                chats_actives_image = view.findViewById(R.id.chats_item_horizontal_image);
            }
        }


        public HorizontalAdapter(List<Actives> horizontalList) {
            this.horizontalList = horizontalList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chats_horizontal, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.chats_actives_name.setText(horizontalList.get(position).getName());

            String ImageUrl = horizontalList.get(position).getThumb_image();
            if (ImageUrl == null) {

            } else {
                Picasso.with(getContext()).load(ImageUrl).placeholder(R.drawable.ic_blank_profile).into(holder.chats_actives_image);
            }
            final String mUserID = horizontalList.get(position).getUserID();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                    chatIntent.putExtra("userID", mUserID);
                    startActivity(chatIntent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return horizontalList.size();
        }
    }


    public class VerticalAdapter extends RecyclerView.Adapter<VerticalAdapter.MyViewHolder> {

        private List<ChatBuddies> verticalList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView chats_hor_name;
            public TextView chats_hor_message;
            public TextView chats_hor_time;
            public CircleImageView chats_hor_image;

            public MyViewHolder(View view) {
                super(view);
                chats_hor_message = view.findViewById(R.id.item_chats_text);
                chats_hor_name = view.findViewById(R.id.item_chats_user_name);
                chats_hor_time = view.findViewById(R.id.item_chats_time);
                chats_hor_image = view.findViewById(R.id.item_chats_image);

            }
        }
        public VerticalAdapter(List<ChatBuddies> verticalList) {
            this.verticalList = verticalList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chats_single_vertical, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            String message = verticalList.get(position).getLast_message();
            String upToNCharacters = message.substring(0, Math.min(message.length(), 30));
            if(message.length() >30)upToNCharacters = upToNCharacters+"...";

            holder.chats_hor_message.setText(upToNCharacters);
            holder.chats_hor_name.setText(verticalList.get(position).getUser_name());

            ////SETTING TIME AGO OF THE NOTIFICATION -//
            GetTimeAgo ob = new GetTimeAgo();
            long time = verticalList.get(position).getTime_stamp();
            String time_ago = ob.getTimeAgo(time ,getContext());
            holder.chats_hor_time.setText(time_ago);

            String ImageUrl = verticalList.get(position).getThumb_image_url();
            if (ImageUrl == null) {

            } else {
                Picasso.with(getContext()).load(ImageUrl).placeholder(R.drawable.ic_blank_profile).into(holder.chats_hor_image);
            }
            final String mUserID = verticalList.get(position).getUser_id();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                    chatIntent.putExtra("userID", mUserID);
                    startActivity(chatIntent);
                }
            });


            /*holder.txtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });*/
        }

        @Override
        public int getItemCount() {
            return verticalList.size();
        }
    }
}
