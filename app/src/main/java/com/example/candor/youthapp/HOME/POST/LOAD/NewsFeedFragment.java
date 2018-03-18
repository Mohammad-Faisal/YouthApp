package com.example.candor.youthapp.HOME.POST.LOAD;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.candor.youthapp.GENERAL.MainActivity;
import com.example.candor.youthapp.HOME.BLOG.CreateBlogActivity;
import com.example.candor.youthapp.HOME.POST.CREATE_SHOW.CreatePostActivity;
import com.example.candor.youthapp.HOME.POST.CREATE_SHOW.Posts;
import com.example.candor.youthapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;


public class NewsFeedFragment extends Fragment {


    //for recyclcer view
    private List<Posts> posts;
    private List<Posts> container = new ArrayList<>();
    private NewsFeedAdapter newsFeedAdapter;

    //CONSTANTS
    private static final int TOTAL_ITEMS_TO_LOAD = 2;
    private int mCurrentPage = 1;
    private String lastkey = "";
    private  String prevLastkey = "";
    int cur_index = 0;
    long currently_minimum_timestamp = 0;

    //variables
    private String mCurrentUserID;

    //firebase
    private DatabaseReference mRootRef;

    public NewsFeedFragment() {
        // Required empty public constructor
    }

    public static NewsFeedFragment newInstance() {
        NewsFeedFragment fragment = new NewsFeedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_news_feed, container, false);

        posts = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.newsfeed_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newsFeedAdapter = new NewsFeedAdapter(recyclerView, posts,getActivity(), getContext());
        recyclerView.setAdapter(newsFeedAdapter);


        //floating action button
        FloatingActionButton mNewsFragmentFloating = view.findViewById(R.id.news_fragment_floating);
        mNewsFragmentFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent postIntent = new Intent(getActivity() , CreatePostActivity.class);
                    startActivity(postIntent);
            }
        });



        //firebase
        //mUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUserID = MainActivity.mUserID;
        mRootRef = FirebaseDatabase.getInstance().getReference();

        initial_load_from_net();
        newsFeedAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (posts.size() <= 1000) {
                    posts.add(null);
                    newsFeedAdapter.notifyItemInserted(posts.size() - 1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(posts.get(posts.size() - 1 ) == null)
                            {
                                posts.remove(posts.size() - 1);
                                newsFeedAdapter.notifyItemRemoved(posts.size());
                            }
                            loadMorePosts();
                            newsFeedAdapter.setLoaded();
                        }
                    }, 2000);
                } else {
                    Toast.makeText(getContext(), "Loading data completed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }


    private void initial_load_from_net()
    {
        DatabaseReference postsQueryRef = mRootRef.child("posts");
        Query postsQuery = postsQueryRef.orderByChild("timestamp").limitToFirst(1000);
        postsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Posts post = dataSnapshot.getValue(Posts.class);
                container.add(post);
                long t = post.getTimestamp();
                if(t<currently_minimum_timestamp)
                {
                    currently_minimum_timestamp = t;
                    posts.add(0,post);
                    cur_index++;
                    newsFeedAdapter.notifyDataSetChanged();
                    newsFeedAdapter.setLoaded();
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
    }
    private void loadMorePosts() {
        Log.d("news fragment", "loadmore post1");
        if(container.size() > cur_index)
        {
            for(int i=cur_index; i< min(cur_index+2  , container.size()) ;i++)
            {
                Log.d("news fragment", "loadmorepost2");
                Posts post = container.get(i);
                posts.add(post);
                newsFeedAdapter.notifyDataSetChanged();
            }
            cur_index+=2;
        }
    }
}
