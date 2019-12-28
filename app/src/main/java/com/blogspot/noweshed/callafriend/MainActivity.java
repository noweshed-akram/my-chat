package com.blogspot.noweshed.callafriend;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager mViewpager;
    private TabLayout mTablayout;

    //firebase Database
    private DatabaseReference databaseReference, mUserRef;
    private FirebaseUser firebaseUser;

    // header view
    View headerView;
    private CircleImageView nav_propic;
    private TextView nav_name;
    private TextView nav_status;
    private FloatingActionButton floatingActionButton;

    // add a class for tabs
    private TabPagesAdapter mTabpageadapter;

    //Set color for Floating Action Button
    int[] colorIntArray = {R.color.colorSecondary,
            R.color.colorSecondary,
            R.color.colorSecondary,
            R.color.colorSecondary};

    //Set icon for Floating Action Button
    int[] iconIntArray = {R.drawable.ic_group_msg,
            R.drawable.ic_call,
            R.drawable.ic_add_friend,
            R.drawable.ic_add_friend};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View.OnClickListener monClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

        if (!isConnected(MainActivity.this)) {
            Snackbar.make(findViewById(android.R.id.content), "Please connect to the internet first.", Snackbar.LENGTH_LONG)
                    .setAction("GOT IT!", monClickListener).setActionTextColor(Color.WHITE).show();
        }

        mAuth = FirebaseAuth.getInstance();
        mToolbar = findViewById(R.id.main_actionbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("My Chat");

        mViewpager = findViewById(R.id.OptionsPageId);
        mTabpageadapter = new TabPagesAdapter(getSupportFragmentManager());

        mViewpager.setAdapter(mTabpageadapter);

        mTablayout = findViewById(R.id.optionsTabId);
        mTablayout.setTabTextColors(Color.parseColor("#000000"), Color.parseColor("#FFFFFF"));
        mTablayout.setupWithViewPager(mViewpager);

        DrawerLayout drawer = findViewById(R.id.drawrlayoutId);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navviewId);
        headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        nav_propic = headerView.findViewById(R.id.navProPicId);
        nav_name = headerView.findViewById(R.id.nav_nameId);
        nav_status = headerView.findViewById(R.id.nav_statusId);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String user_uid = firebaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_uid);
        databaseReference.keepSynced(true);
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mUserRef.keepSynced(true);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("image").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();

                Picasso.get().load(image).placeholder(R.drawable.default_propic).into(nav_propic);
                nav_name.setText(name);
                nav_status.setText(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        floatingActionButton = findViewById(R.id.fabId);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = mTablayout.getSelectedTabPosition();

                switch (position) {
                    case 0:
                        //
                        break;
                    case 1:
                        //
                        break;
                    case 2:
                        Intent alluser = new Intent(MainActivity.this, Allusers.class);
                        startActivity(alluser);
                        finish();
                        break;
                    case 3:
                        Intent Ralluser = new Intent(MainActivity.this, Allusers.class);
                        startActivity(Ralluser);
                        finish();
                        break;
                }
            }
        });

        mTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewpager.setCurrentItem(tab.getPosition());
                animateFab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mUserRef.child("online").setValue(true);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
    }

    protected void animateFab(final int position) {
        final FloatingActionButton fab = findViewById(R.id.fabId);
        fab.clearAnimation();

        // Scale down animation
        ScaleAnimation shrink = new ScaleAnimation(1f, 0.1f, 1f, 0.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(100);     // animation duration in milliseconds
        shrink.setInterpolator(new AccelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Change FAB color and icon
                fab.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), colorIntArray[position]));
                fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), iconIntArray[position]));

                // Rotate Animation
                Animation rotate = new RotateAnimation(60.0f, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);
                rotate.setDuration(150);
                rotate.setInterpolator(new DecelerateInterpolator());

                // Scale up animation
                ScaleAnimation expand = new ScaleAnimation(0.1f, 1f, 0.1f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(150);     // animation duration in milliseconds
                expand.setInterpolator(new DecelerateInterpolator());

                // Add both animations to animation state
                AnimationSet s = new AnimationSet(false); //false means don't share interpolators
                s.addAnimation(rotate);
                s.addAnimation(expand);
                fab.startAnimation(s);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fab.startAnimation(shrink);
    }

    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnected()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else
                return false;
        } else
            return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawrlayoutId);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.alluserid) {
            Intent Userlist = new Intent(MainActivity.this, Allusers.class);
            startActivity(Userlist);
            finish();
        }
        if (item.getItemId() == R.id.cameraId) {
            Snackbar.make(findViewById(R.id.cameraId), "Camera function is under developed", Snackbar.LENGTH_LONG).show();
//            Intent cameraIntent = new Intent(MainActivity.this, Camera.class);
//            startActivity(cameraIntent);
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        if (id == R.id.accountsId) {
            Intent accountIntent = new Intent(MainActivity.this, MyAccount.class);
            startActivity(accountIntent);
            finish();

        } else if (id == R.id.settingsId) {
            Intent accountIntent = new Intent(MainActivity.this, MyAccount.class);
            startActivity(accountIntent);
            finish();
        } else if (id == R.id.shareId) {
            Toast.makeText(MainActivity.this, "Share", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.aboutId) {
            AboutDialog aboutDialog = new AboutDialog();
            aboutDialog.show(getSupportFragmentManager(), "About Dialog");
        } else if (id == R.id.quitId) {
            System.exit(0);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawrlayoutId);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}