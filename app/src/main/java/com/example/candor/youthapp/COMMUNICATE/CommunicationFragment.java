package com.example.candor.youthapp.COMMUNICATE;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.example.candor.youthapp.COMMUNICATE.CHATS.ChatsFragment;
import com.example.candor.youthapp.COMMUNICATE.LEADERS.LeadersFragment;
import com.example.candor.youthapp.COMMUNICATE.MEETINGS.MeetingFragment;
import com.example.candor.youthapp.HOME.BLOG.BlogFragment;
import com.example.candor.youthapp.HOME.BLOG.CreateBlogActivity;
import com.example.candor.youthapp.HOME.POST.CREATE_SHOW.CreatePostActivity;
import com.example.candor.youthapp.HOME.POST.LOAD.NewsFeedFragment;
import com.example.candor.youthapp.HOME.RankingFragment;
import com.example.candor.youthapp.R;

import java.util.ArrayList;
import java.util.List;


public class CommunicationFragment extends Fragment {


    private int tab_position = 1;

    private FloatingActionButton mCommunicationFragmentFloating;



    public CommunicationFragment() {
    }


    public static CommunicationFragment newInstance() {
        CommunicationFragment fragment = new CommunicationFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_communication, container, false);






        // Setting ViewPager for each Tabs
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setCurrentItem(1);
        // Set Tabs inside Toolbar
        final TabLayout tabs = view.findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);

        mCommunicationFragmentFloating = view.findViewById(R.id.communication_fragment_floating);
        mCommunicationFragmentFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tab_position == 0 )
                {
                    Intent blogIntent = new Intent(getActivity() , CreateBlogActivity.class);
                    startActivity(blogIntent);
                }
                else if(tab_position==1)
                {
                    Intent blogIntent = new Intent(getActivity() , CreatePostActivity.class);
                    startActivity(blogIntent);
                }
                else
                {

                }
            }
        });

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0){
                    mCommunicationFragmentFloating.setBackgroundResource(R.drawable.ic_add_icon);
                    tab_position = 0;
                } else if(position == 1){
                    mCommunicationFragmentFloating.setBackgroundResource(R.drawable.ic_search_icon);
                    tab_position = 1;
                }else{
                    mCommunicationFragmentFloating.setBackgroundResource(R.drawable.ic_add_icon);
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
        return  view;
    }







    // ---------- ADDING FRAGMENTS --//
    private void setupViewPager(ViewPager viewPager) {
        CommunicationAdapter adapter = new CommunicationAdapter(getChildFragmentManager());
        adapter.addFragment(new ChatsFragment(), "Chats");
        adapter.addFragment(new LeadersFragment(), "Leaders");
        adapter.addFragment(new MeetingFragment(), "Meetings");
        viewPager.setAdapter(adapter);
    }
}

//---------- USE FRAGMENT INSIDE FRAGMENT -----------//
class CommunicationAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    public CommunicationAdapter(FragmentManager manager) {
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

