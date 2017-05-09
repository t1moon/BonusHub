package com.example.BonusHub.activity.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.timur.BonusHub.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OwnerSettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OwnerSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OwnerSettingsFragment extends PreferenceFragmentCompat {
    private SharedPreferences sharedPreferences;

    public OwnerSettingsFragment() {
        // Required empty public constructor
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        addPreferencesFromResource(R.xml.xml_owner_settings);
//    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        //add xml
        addPreferencesFromResource(R.xml.xml_owner_settings);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

    }
}
