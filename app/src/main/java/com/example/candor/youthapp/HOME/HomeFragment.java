package com.example.candor.youthapp.HOME;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.candor.youthapp.HOME.BLOG.CreateBlogActivity;
import com.example.candor.youthapp.HOME.POST.CREATE_SHOW.CreatePostActivity;
import com.example.candor.youthapp.HOME.BLOG.BlogFragment;
import com.example.candor.youthapp.HOME.POST.LOAD.NewsFeedFragment;
import com.example.candor.youthapp.R;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {



    private int tab_position = 0;

    private FloatingActionButton mHomeFragmentFloating;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_home, container, false);


        // Setting ViewPager for each Tabs
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        final TabLayout tabs = view.findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);

        mHomeFragmentFloating = view.findViewById(R.id.home_fragment_floating);

        mHomeFragmentFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(tab_position == 1 )
                {
                    Intent blogIntent = new Intent(getActivity() , CreateBlogActivity.class);
                    startActivity(blogIntent);
                }
                else
                {
                    Intent blogIntent = new Intent(getActivity() , CreatePostActivity.class);
                    startActivity(blogIntent);
                }
            }
        });

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0){
                    mHomeFragmentFloating.setVisibility(View.VISIBLE);
                    tab_position = 0;
                } else if(position == 1){
                    mHomeFragmentFloating.setVisibility(View.VISIBLE);
                    tab_position = 1;
                }else{
                    mHomeFragmentFloating.setVisibility(View.INVISIBLE);
                    tab_position = 2;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }


    // ---------- ADDING FRAGMENTS --//
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new NewsFeedFragment(), "NewsFeed");
        adapter.addFragment(new BlogFragment(), "Blog");
        adapter.addFragment(new RankingFragment(), "Ranking");
        viewPager.setAdapter(adapter);
    }
}



//---------- USE FRAGMENT INSIDE FRAGMENT -----------//
class Adapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    public Adapter(FragmentManager manager) {
        super(manager);
    }
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}

