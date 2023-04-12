/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  androidx.fragment.app.Fragment
 *  androidx.fragment.app.FragmentManager
 *  androidx.fragment.app.FragmentPagerAdapter
 */
package com.example.prote;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.example.prote.Frag1;
import com.example.prote.Frag2;
import com.example.prote.Frag3;
import com.example.prote.Frag4;

public class PagerAdapter
extends FragmentPagerAdapter {
    private int tabnumber;

    public PagerAdapter(FragmentManager fragmentManager, int n, int n2) {
        super(fragmentManager, n);
        this.tabnumber = n2;
    }

    public int getCount() {
        return this.tabnumber;
    }

    public Fragment getItem(int n) {
        if (n != 0) {
            if (n != 1) {
                if (n != 2) {
                    if (n != 3) {
                        return null;
                    }
                    return new Frag4();
                }
                return new Frag3();
            }
            return new Frag2();
        }
        return new Frag1();
    }
}

