package com.example.safetyapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;


public class User_Home extends AppCompatActivity {
  androidx.viewpager.widget.ViewPager vp1;


    Toolbar toolbar1;
    TabLayout tabs1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        vp1=findViewById(R.id.vp1);
        toolbar1=findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar1);
        tabs1=findViewById(R.id.tabs1);
        // replace toolbar in place of actionbar



        tabs1.addTab( tabs1.newTab().setText("ONE") );
        tabs1.addTab( tabs1.newTab().setText("TWO") );
        tabs1.addTab( tabs1.newTab().setText("THREE") );
        mypageradapter myad = new mypageradapter (getSupportFragmentManager());
        vp1.setAdapter(myad);


        // Now Connect Tabs With ViewPager
        tabs1.setupWithViewPager(vp1);

       //ActionBar actionBar = getSupportActionBar();
       // actionBar.setTitle("Safety App");
    }
    //This method is used to load menu from xml file
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu1,menu);
        return true;
    }
    //Assigning functions to menu options. This method is used to define logic of menu items
    public boolean onOptionsItemSelected(MenuItem item){
        return true;
        }


    private void setAdapter(mypageradapter myad) {
    }

    class mypageradapter extends FragmentPagerAdapter
    {

        mypageradapter(FragmentManager frm)
        {
            super(frm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0)
                return new Emergency();
            else if(position==1)
                return new ManageFriends();
            else
                return new Profile();
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if(position==0)
                return "Emergency";
            else if(position==1)
                return "Manage Friends";
            else
                return "Profile";
        }
    }
    ////////////////
}