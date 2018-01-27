package com.example.candor.youthapp.HOME.BLOG;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.candor.youthapp.HOME.POST.Posts;
import com.example.candor.youthapp.MainActivity;
import com.example.candor.youthapp.NewsFeedAdapter;
import com.example.candor.youthapp.OnLoadMoreListener;
import com.example.candor.youthapp.R;

import java.util.ArrayList;
import java.util.List;

public class BlogFragment extends Fragment {




    private List<Posts> posts;
    private NewsFeedAdapter newsFeedAdapter;


    public BlogFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static BlogFragment newInstance() {
        BlogFragment fragment = new BlogFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_blog, container, false);
        /*posts = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.newsfeed_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newsFeedAdapter = new NewsFeedAdapter(recyclerView, posts,getActivity(), getContext());
        recyclerView.setAdapter(newsFeedAdapter);*/

        //--- testing purpose ---//
        //set dummy data
       /* for (int i = 0; i < 10; i++) {
            Posts post = new Posts();
            post.setCaption("default");
            post.setLocation("dhaka");
            post.setTime_and_date("today");
            posts.add(post);
        }


        newsFeedAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (posts.size() <= 20) {
                    posts.add(null);
                    newsFeedAdapter.notifyItemInserted(posts.size() - 1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            posts.remove(posts.size() - 1);
                            newsFeedAdapter.notifyItemRemoved(posts.size());

                            //Generating more data
                            int index = posts.size();
                            int end = index + 10;
                            for (int i = index; i < end; i++) {

                                Posts post = new Posts();
                                post.setCaption("default");
                                post.setLocation("dhaka");
                                post.setTime_and_date("today");


                                posts.add(post);
                            }
                            newsFeedAdapter.notifyDataSetChanged();
                            newsFeedAdapter.setLoaded();
                        }
                    }, 5000);
                } else {
                    Toast.makeText(getContext(), "Loading data completed", Toast.LENGTH_SHORT).show();
                }
            }
        });*/


        return view;
    }

}
