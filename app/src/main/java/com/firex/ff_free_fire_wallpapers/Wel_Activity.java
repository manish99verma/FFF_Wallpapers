package com.firex.ff_free_fire_wallpapers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.firex.ff_free_fire_wallpapers.Fragments.WelFragment1;
import com.firex.ff_free_fire_wallpapers.Utilities.Utils;

public class Wel_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Utils.changeFragment(this,new WelFragment1(),R.id.fragContainerWelcome);
    }



}