package com.example.unseenindia;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.unseenindia.Fragments.FollowersPostsFragment;
import com.example.unseenindia.Fragments.HomeFragment;
import com.example.unseenindia.Fragments.NotificationsFragment;
import com.example.unseenindia.Fragments.ProfileFragment;
import com.example.unseenindia.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.nav_home:
                        selectorFragment = new HomeFragment();
                        break;

                    case R.id.followers_posts:
                        selectorFragment = new FollowersPostsFragment();
                        break;

                    case R.id.nav_search:
                        selectorFragment = new SearchFragment();
                        break;


                    case R.id.nav_add:
                        selectorFragment = null;
                        Intent intent = new Intent(MainActivity.this,PostActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_profile:
                        selectorFragment = new ProfileFragment();
                        break;

                }

                if(selectorFragment != null)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectorFragment).commit();
                }

                return true;
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater;
        menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.top_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, ProfileSettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent signOutIntent = new Intent(MainActivity.this,StartActivity.class);
                signOutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(signOutIntent);
                finish();
                Toast.makeText(this, "Logged Out Successfully..", Toast.LENGTH_SHORT).show();
                break;

            default:
                return false;

        }

        return true;
    }
}
