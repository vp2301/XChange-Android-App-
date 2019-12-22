package com.example.xchange;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.example.xchange.helper.ConfigurationFirebase;
import com.example.xchange.ui.add.AddFragment;
import com.example.xchange.ui.myItems.MyItems;
import com.example.xchange.ui.home.HomeFragment;
import com.example.xchange.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import dmax.dialog.SpotsDialog;

public class NavigationActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private AlertDialog dialog;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_navigation);


            BottomNavigationView navigationView = findViewById(R.id.nav_view);

            navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    int id = menuItem.getItemId();

                    if (id == R.id.navigation_home){
                        HomeFragment fragment = new HomeFragment();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                        fragmentTransaction.commit();

                    }

                    if (id == R.id.navigation_add){
                        AddFragment fragment = new AddFragment();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                        fragmentTransaction.commit();
                    }

                    if (id == R.id.navigation_profile) {
                        ProfileFragment fragment = new ProfileFragment();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                        fragmentTransaction.commit();
                    }

                    if (id == R.id.navigation_trades) {
                        MyItems fragment = new MyItems();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                        fragmentTransaction.commit();
                    }
                    return true;
                }
            });

            navigationView.setSelectedItemId(R.id.navigation_home);

        }

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.overflow_menu, menu);
            return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


            menu.setGroupVisible(R.id.group_logged,  true);
            return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

            switch (item.getItemId()) {
                case R.id.logOut:
                    auth = ConfigurationFirebase.getFirebaseAuthentication();
                    auth.signOut();
                    startActivity(new Intent(this, MainActivity.class));
                    this.finish();
                    break;

                case R.id.aboutus:
                    dialog = new SpotsDialog.Builder()
                            .setContext(this)
                            .setMessage("Version 1")
                            .setCancelable(true)
                            .build();
                    dialog.show();
                    dialog.dismiss();
                    break;

            }
            return super.onOptionsItemSelected(item);
    }



}
