package com.example.wilberg.bankapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by WILBERG on 8/18/2016.
 */
public class MainTabActivity extends AppCompatActivity {

    private DrawerLayout leftDrawer;
    private ListView mDrawerList;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LinearLayout mDrawerLinearLayout;
    private Button doneDrawerButton;
    private Button sortButton;

    private String[] mDrawerListItems;

    private FavoriteTabFragment favFrag;
    private SearchTabFragment searchFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        leftDrawer = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerLinearLayout = (LinearLayout) findViewById(R.id.left_drawer);
        mDrawerList = (ListView) findViewById(R.id.optonlist);
        mDrawerListItems = getResources().getStringArray(R.array.drawer_list);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mDrawerListItems));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                leftDrawer.closeDrawers();
                Intent updateIntent = new Intent(getApplicationContext(), MainTabActivity.class);
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
        sortButton = (Button) findViewById(R.id.sortButton);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                leftDrawer.openDrawer(Gravity.LEFT);

            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

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

        favFrag = new FavoriteTabFragment();
        searchFrag = new SearchTabFragment();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SearchTabFragment());
        adapter.addFragment(favFrag);
        viewPager.setAdapter(adapter);
        setIconForTabs();

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
    }

        public void setIconForTabs() {

            int[] tabsIcon = {

                    R.mipmap.ic_launcher,
                    R.mipmap.ic_launcher

            };

            tabLayout.getTabAt(0).setIcon(tabsIcon[0]);
            tabLayout.getTabAt(1).setIcon(tabsIcon[1]);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        favFrag.setUpUI();

    }
}
