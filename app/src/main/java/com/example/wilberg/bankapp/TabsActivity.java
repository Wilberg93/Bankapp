package com.example.wilberg.bankapp;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.example.wilberg.bankapp.DB.DBTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WILBERG on 8/18/2016.
 */
public class TabsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private final static String CAR_ID = "com.example.wilberg.bankapp.CAR_ID";
    private final static String IS_CHECKED_BOOLEAN = "com.example.wilberg.bankapp.IS_CHECKED_BOOLEAN";

    private PageFavoritesFragment favFrag;
    private PageSearchParentFragment searchFrag;

    private DrawerLayout mLeftDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Button/TextView click listeners
        findViewById(R.id.homeTextView).setOnClickListener(this);
        findViewById(R.id.sortButton).setOnClickListener(this);
        findViewById(R.id.filterButton).setOnClickListener(this);
        findViewById(R.id.doneDrawerButton).setOnClickListener(this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Add tabs with icon
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_search_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_star_black_24dp));

        // Initialize fragments
        favFrag = new PageFavoritesFragment();
        searchFrag = new PageSearchParentFragment();

        // Add fragments
        adapter.addFragment(searchFrag);
        adapter.addFragment(favFrag);

        //Set adapter
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(adapter);

        mLeftDrawer = (DrawerLayout) findViewById(R.id.drawer);

        // Set up sort drawer
        ListView sortDrawerListView = (ListView) findViewById(R.id.optonlist);
        sortDrawerListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, getResources().getStringArray(R.array.sort_drawer_list)));
        sortDrawerListView.setOnItemClickListener(this);

        /* TODO: different solution */
        getSupportActionBar().setTitle("");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.homeTextView:
                Intent homeIntent = new Intent(TabsActivity.this, MainActivity.class);
                startActivity(homeIntent);
                break;
            case R.id.sortButton:
                mLeftDrawer.openDrawer(GravityCompat.START);
                break;
            case R.id.filterButton:
                //TODO: Create filter
                break;
            case R.id.doneDrawerButton:
                mLeftDrawer.closeDrawers();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mLeftDrawer.closeDrawers();
        searchFrag.updateSortMethod(Integer.toString(position));
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

    /*
    public void setIconForTabs(int pos) {
        int[] tabsIcon = {
                R.drawable.ic_search_black_24dp,
                R.drawable.ic_star_black_24dp
        };

        // TODO: different solution?
        if(tabLayout.getTabAt(pos) != null && tabLayout != null) {
            tabLayout.getTabAt(pos).setIcon(tabsIcon[pos]);
            tabLayout.getTabAt(pos).getIcon().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_IN);
        }
    }
    */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO: cleanup.
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getBooleanExtra(IS_CHECKED_BOOLEAN, false) && !DBTools.getInstance(this).checkForCar(data.getStringExtra(CAR_ID)))
            favFrag.updateView(data.getStringExtra(CAR_ID));
        else if(!data.getBooleanExtra(IS_CHECKED_BOOLEAN, false) && !DBTools.getInstance(this).checkForCar(data.getStringExtra(CAR_ID)))
            favFrag.removeView(data.getStringExtra(CAR_ID));
    }

    @Override
    public void onActivityReenter(int requestCode, Intent data) {
        super.onActivityReenter(requestCode, data);
        searchFrag.onActivityReenter(data);
    }
}