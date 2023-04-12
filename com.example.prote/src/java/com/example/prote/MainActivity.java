/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.Context
 *  android.content.Intent
 *  android.net.Uri
 *  android.os.Bundle
 *  android.view.MenuItem
 *  android.view.View
 *  android.widget.Toast
 *  androidx.appcompat.app.ActionBarDrawerToggle
 *  androidx.appcompat.app.AppCompatActivity
 *  androidx.appcompat.widget.Toolbar
 *  androidx.drawerlayout.widget.DrawerLayout
 *  androidx.drawerlayout.widget.DrawerLayout$DrawerListener
 *  androidx.fragment.app.FragmentManager
 *  androidx.viewpager.widget.PagerAdapter
 *  androidx.viewpager.widget.ViewPager
 *  androidx.viewpager.widget.ViewPager$OnPageChangeListener
 *  com.google.android.material.navigation.NavigationView
 *  com.google.android.material.navigation.NavigationView$OnNavigationItemSelectedListener
 *  com.google.android.material.tabs.TabItem
 *  com.google.android.material.tabs.TabLayout
 *  com.google.android.material.tabs.TabLayout$BaseOnTabSelectedListener
 *  com.google.android.material.tabs.TabLayout$OnTabSelectedListener
 *  com.google.android.material.tabs.TabLayout$Tab
 *  com.google.android.material.tabs.TabLayout$TabLayoutOnPageChangeListener
 *  java.lang.CharSequence
 *  java.lang.Class
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 */
package com.example.prote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import com.example.prote.DetialContactaz;
import com.example.prote.PagerAdapter;
import com.example.prote.Profile;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class MainActivity
extends AppCompatActivity
implements NavigationView.OnNavigationItemSelectedListener {
    PagerAdapter adapt;
    TabItem contactt;
    DrawerLayout drawerL;
    TabItem five5;
    TabItem mhome;
    NavigationView navigation;
    ViewPager page;
    TabLayout tabLayoutm;
    TabItem ten10;
    ActionBarDrawerToggle toggl;

    protected void onCreate(Bundle bundle) {
        ActionBarDrawerToggle actionBarDrawerToggle;
        PagerAdapter pagerAdapter;
        super.onCreate(bundle);
        this.setContentView(2131427357);
        Toolbar toolbar = (Toolbar)this.findViewById(2131231034);
        this.setSupportActionBar(toolbar);
        this.page = (ViewPager)this.findViewById(2131231052);
        this.tabLayoutm = (TabLayout)this.findViewById(2131231009);
        this.mhome = (TabItem)this.findViewById(2131230885);
        this.five5 = (TabItem)this.findViewById(2131230873);
        this.ten10 = (TabItem)this.findViewById(2131231018);
        this.contactt = (TabItem)this.findViewById(2131230827);
        this.drawerL = (DrawerLayout)this.findViewById(2131230857);
        this.navigation = (NavigationView)this.findViewById(2131230923);
        this.toggl = actionBarDrawerToggle = new ActionBarDrawerToggle((Activity)this, this.drawerL, toolbar, 2131558440, 2131558433);
        this.drawerL.addDrawerListener((DrawerLayout.DrawerListener)actionBarDrawerToggle);
        this.navigation.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener)this);
        this.toggl.setDrawerIndicatorEnabled(true);
        this.toggl.syncState();
        this.adapt = pagerAdapter = new PagerAdapter(this.getSupportFragmentManager(), 1, this.tabLayoutm.getTabCount());
        this.page.setAdapter((androidx.viewpager.widget.PagerAdapter)pagerAdapter);
        this.tabLayoutm.addOnTabSelectedListener((TabLayout.BaseOnTabSelectedListener)new TabLayout.OnTabSelectedListener(){

            public void onTabReselected(TabLayout.Tab tab) {
            }

            public void onTabSelected(TabLayout.Tab tab) {
                MainActivity.this.page.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(TabLayout.Tab tab) {
            }
        });
        this.page.addOnPageChangeListener((ViewPager.OnPageChangeListener)new TabLayout.TabLayoutOnPageChangeListener(this.tabLayoutm));
    }

    public boolean onNavigationItemSelected(MenuItem menuItem) {
        this.drawerL.closeDrawer(8388611);
        int n = menuItem.getItemId();
        if (n != 2131230946) {
            switch (n) {
                default: {
                    break;
                }
                case 2131230915: {
                    this.startActivity(new Intent((Context)this, DetialContactaz.class));
                    break;
                }
                case 2131230914: {
                    try {
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.setType("text/plain");
                        intent.putExtra("android.intent.extra.SUBJECT", "Prot");
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("https://play.google.com/store/apps/details?id=");
                        stringBuilder.append(this.getApplicationContext().getPackageName());
                        intent.putExtra("android.intent.extra.TEXT", stringBuilder.toString());
                        this.startActivity(Intent.createChooser((Intent)intent, (CharSequence)"share with"));
                    }
                    catch (Exception exception) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("unable to share\n");
                        stringBuilder.append(exception.getMessage());
                        Toast.makeText((Context)this, (CharSequence)stringBuilder.toString(), (int)1).show();
                    }
                    break;
                }
                case 2131230913: {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("https://play.google.com/store/apps/details?id=");
                    stringBuilder.append(this.getApplicationContext().getPackageName());
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse((String)stringBuilder.toString()));
                    this.startActivity(intent);
                    try {
                        this.startActivity(intent);
                    }
                    catch (Exception exception) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("unable to open\n");
                        stringBuilder2.append(exception.getMessage());
                        Toast.makeText((Context)this, (CharSequence)stringBuilder2.toString(), (int)1).show();
                    }
                    break;
                }
                case 2131230912: {
                    Toast.makeText((Context)this, (CharSequence)"About", (int)1).show();
                    break;
                }
                case 2131230911: {
                    Toast.makeText((Context)this, (CharSequence)"ordering", (int)1).show();
                    break;
                }
            }
        } else {
            this.startActivity(new Intent((Context)this, Profile.class));
        }
        this.drawerL.closeDrawer(8388611);
        return true;
    }

}

