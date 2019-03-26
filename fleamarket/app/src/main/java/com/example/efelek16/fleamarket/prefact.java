package com.example.efelek16.fleamarket;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class prefact extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content,new pref())
                .commit();
    }
}
