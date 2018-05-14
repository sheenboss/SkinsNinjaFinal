package ggappsdev.skinsninja;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import ggappsdev.Adapters.ViewPagerAdapter;
import ggappsdev.Fragments.FragmentCSGO;
import ggappsdev.Fragments.FragmentPUBG;

public class GetSkinsActivity extends AppCompatActivity {

    private BottomNavigationViewEx mBottomNavigationViewEx;
    private MenuItem mMenuItem;
    private static ViewPager sViewPager;

    FragmentCSGO mFragmentCSGO;
    FragmentPUBG mFragmentPUBG;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_skins);


        setToolbar();
        setBottomMenu();

    }


    private void setToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        TextView toolbartxt = (TextView) findViewById(R.id.toolbar_title);
        toolbartxt.setText("Redeem Skins");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

    }

    private void setBottomMenu() {
        sViewPager = (ViewPager) findViewById(R.id.viewpager);
        sViewPager.setOffscreenPageLimit(2);
        mBottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.navigation);
        mBottomNavigationViewEx.setTextVisibility(false);
        mBottomNavigationViewEx.setIconSize(40,40);
        mBottomNavigationViewEx.setItemHeight(BottomNavigationViewEx.dp2px(this,52));

        mBottomNavigationViewEx.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_category:
                                sViewPager.setCurrentItem(0);
                                break;
                            case R.id.action_video:
                                sViewPager.setCurrentItem(1);
                                break;
                        }


                        return false;
                    }
                });

        sViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (mMenuItem != null) {
                    mMenuItem.setChecked(false);
                } else {
                    mBottomNavigationViewEx.getMenu().getItem(0).setChecked(false);
                }
                mBottomNavigationViewEx.getMenu().getItem(position).setChecked(true);
                mMenuItem = mBottomNavigationViewEx.getMenu().getItem(position);


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        setupViewPager(sViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        mFragmentCSGO = new FragmentCSGO();
        mFragmentPUBG = new FragmentPUBG();
        adapter.addFragment(mFragmentCSGO);
        adapter.addFragment(mFragmentPUBG);
        viewPager.setAdapter(adapter);
    }
}
