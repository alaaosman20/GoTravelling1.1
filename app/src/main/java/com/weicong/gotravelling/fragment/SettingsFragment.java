package com.weicong.gotravelling.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.weicong.gotravelling.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
