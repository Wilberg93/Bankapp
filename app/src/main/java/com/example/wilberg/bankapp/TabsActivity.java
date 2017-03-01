package com.example.wilberg.bankapp;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.example.wilberg.bankapp.DB.DBTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WILBERG on 8/18/2016.
 */
public class TabsActivity extends AppCompatActivity {

    private final static String CAR_ID = "com.example.wilberg.bankapp.CAR_ID";
    private final static String IS_CHECKED_BOOLEAN = "com.example.wilberg.bankapp.IS_CHECKED_BOOLEAN";

    private DrawerLayout leftDrawer;
    private ListView mDrawerList;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LinearLayout mDrawerLinearLayout;
    private Button doneDrawerButton;
    private TextView homeTextView;
    private Button sortButton;
    private Button filterButton;

    private String[] mDrawerListItems;

    private PageFavoritesFragment favFrag;
    private PageSearchParentFragment searchFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("NICE", "NICE");

            }
        });
        */



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);
        /*
        leftDrawer = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerLinearLayout = (LinearLayout) findViewById(R.id.left_drawer);
        mDrawerList = (ListView) findViewById(R.id.optonlist);
        mDrawerListItems = getResources().getStringArray(R.array.drawer_list);
        //mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_selectable_list_item, mDrawerListItems));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                leftDrawer.closeDrawers();
                Intent updateIntent = new Intent(getApplicationContext(), TabsActivity.class);
                updateIntent.putExtra("sortMethod", Integer.toString(position));
                startActivity(updateIntent);


            }
        });
        doneDrawerButton = (Button) findViewById(R.id.doneDrawerButton);
        doneDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                leftDrawer.closeDrawers();

            }
        });
        */
        homeTextView = (TextView) findViewById(R.id.homeTextView);
        homeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(TabsActivity.this, MainActivity.class);
                startActivity(homeIntent);
            }
        });
        /*
        sortButton = (Button) findViewById(R.id.sortButton);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                leftDrawer.openDrawer(Gravity.LEFT);

            }
        });
        filterButton = (Button) findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                leftDrawer.openDrawer(Gravity.LEFT);

            }
        });
*/
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int pos) { return mFragmentList.get(pos); }

        @Override
        public int getCount() {  return mFragmentList.size(); }

        @Override
        public CharSequence getPageTitle(int pos) { return null; }

        public void addFragment(Fragment fragment) { mFragmentList.add(fragment); }

    }

    private void setupViewPager(ViewPager viewPager) {

        favFrag = new PageFavoritesFragment();
        searchFrag = new PageSearchParentFragment();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(searchFrag);
        adapter.addFragment(favFrag);
        viewPager.setAdapter(adapter);
        for(int pos=0; pos<adapter.getCount(); pos++)
            setIconForTabs(pos);

        /*
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }


            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        getSupportActionBar().setTitle("Home");
                        break;
                    case 1:
                        getSupportActionBar().setTitle("Favorites");
                        break;
                }
            }


            @Override
            public void onPageScrollStateChanged(int state) { }
        });
        */
    }


        public void setIconForTabs(int pos) {

            int[] tabsIcon = {

                    R.drawable.ic_search_black_24dp,
                    R.drawable.ic_star_black_24dp

            };
            tabLayout.getTabAt(pos).setIcon(tabsIcon[pos]);
            tabLayout.getTabAt(pos).getIcon().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getBooleanExtra(IS_CHECKED_BOOLEAN, false) && !DBTools.getInstance(this).checkForCar(data.getStringExtra(CAR_ID)))
            favFrag.updateView(data.getStringExtra(CAR_ID));
        else if(!data.getBooleanExtra(IS_CHECKED_BOOLEAN, false) && !DBTools.getInstance(this).checkForCar(data.getStringExtra(CAR_ID)))
            favFrag.removeView(data.getStringExtra(CAR_ID));
    }
}
