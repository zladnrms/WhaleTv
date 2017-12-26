package zladnrms.defytech.kim.BroadcastTv.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import zladnrms.defytech.kim.BroadcastTv.*;
import zladnrms.defytech.kim.BroadcastTv.Contract.MainContract;
import zladnrms.defytech.kim.BroadcastTv.Netty.Client.NettyClient;
import zladnrms.defytech.kim.BroadcastTv.Packet.ConnectPacket;
import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.databinding.ActivityMainBinding;
import zladnrms.defytech.kim.BroadcastTv.presenter.MainPresenter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TabLayout.OnTabSelectedListener, MainContract.View {

    private ActivityMainBinding binding;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Pager adapter;

    private MainPresenter presenter;

    /* Netty & UserInfo */
    private NettyClient nc = NettyClient.getInstance();

    private boolean connectFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* MVP & binding */
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        presenter = new MainPresenter();
        presenter.attachView(MainActivity.this);

        /* toolbar */
        toolbar = binding.navFragment.toolbar;
        setSupportActionBar(toolbar);

        //Tablayout
        tabLayout = binding.navFragment.tabLayout;
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setOnTabSelectedListener(this);

        // ViewPager
        viewPager = binding.navFragment.mainViewpager;
        adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setAdapter(adapter);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
        TextView tv_my_bookmark = (TextView) hView.findViewById(R.id.tv_my_bookmark);
        tv_my_bookmark.setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, MyBookmarkActivity.class);
            startActivity(intent);
        });
        TextView tv_logout = (TextView) hView.findViewById(R.id.tv_logout);
        tv_logout.setOnClickListener(v-> {
            presenter.logout();

            Intent intent = new Intent(MainActivity.this, IntroActivity.class);
            startActivity(intent);
            finish();
        });

        binding.navFragment.ivUser.setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(intent);
        });

        /* Activity Setting End */
        Intent intent = getIntent();
        presenter.saveUserNickname(MainActivity.this, intent.getStringExtra("nickname"));
        presenter.saveUserRoomId(MainActivity.this, -1);

        presenter.sendFCMToken(MainActivity.this);
    }

    @Override
    public int getUserRoomId() {
        return presenter.getUserRoomId(MainActivity.this);
    }

    @Override
    public String getUserNickname() {
        return presenter.getUserNickname(MainActivity.this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // ViewPager 처리
    public class Pager extends FragmentStatePagerAdapter {

        int tabCount;

        public Pager(android.support.v4.app.FragmentManager fm, int tabCount) {
            super(fm);
            this.tabCount = tabCount;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    HomeFragment homeTab = new HomeFragment();
                    return homeTab;
                case 1:
                    VideoChannelFragment videoChannelTab = new VideoChannelFragment();
                    return videoChannelTab;
                default:
                    return null;
            }
        }

        public void destroyFragment(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return tabCount;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    /* Life Cycle */

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.d("onStart");

        if (!connectFlag) {
            if(nc.getState() == Thread.State.NEW) {
                nc.setContext(MainActivity.this);
                nc.start();
            }

            ConnectPacket connectPacket = new ConnectPacket(presenter.getUserRoomId(MainActivity.this), presenter.getUserNickname(MainActivity.this));
            nc.send(100, connectPacket);

            connectFlag = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        presenter.realmClose();
        presenter.detachView(this);
    }



}
