package com.example.efelek16.shoppinglist;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

public class HandlePref extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref,rootKey);
    }
}
