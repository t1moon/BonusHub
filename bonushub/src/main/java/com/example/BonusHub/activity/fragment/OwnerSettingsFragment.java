package com.example.BonusHub.activity.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.example.BonusHub.activity.utils.NumberPreference.NumberPickerPreferenceDialog;
import com.example.BonusHub.activity.utils.NumberPreference.NumberPreference;
import com.example.timur.BonusHub.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OwnerSettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OwnerSettingsFragment#newInstance} factoryhttp://netology.ru/programs/data-scientist?utm_source=context&utm_medium=1267&utm_campaign=ds-rsya&utm_content=4171435107&utm_term=python%20machine%20learning&yclid=2600899913511344354&stop=1 method to
 * create an instance of this fragment.
 */
public class OwnerSettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    public final static String TAG = "OwnerSettingsFragment";
    private SharedPreferences sp;
    protected final String THIS_FRAGMENT = "android.support.v7.preference.PreferenceFragment.DIALOG";
    protected final int BONUS_SYSTEM_KEY_ADDRESS = R.string.bonus_system_key;
    protected final int TYPED_SETTINGS_KEY = R.string.typed_settings_key;
    protected final int TYPED_SETTINGS[] = {
            R.xml.xml_nfree_settings,
            R.xml.xml_bonus_account_settings,
            R.xml.xml_total_discout_settings
    };
    protected int bst = -1; // bonus system type

    public OwnerSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // add xml
        addPreferencesFromResource(R.xml.xml_owner_settings);
        // define shared preferences to use
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // register listener in order to change settings appearence depending
        // on bonus system type
        sp.registerOnSharedPreferenceChangeListener(this);
        // find preferences for chosen bonus type
    }

    // register listener for bonus type change
    @Override
    public void onResume() {
        super.onResume();
        sp.registerOnSharedPreferenceChangeListener(this);
        // update bonus system type
        bst = Integer.parseInt(sp.getString(getString(BONUS_SYSTEM_KEY_ADDRESS), "1"));
        render();
    }

    @Override
    public void onPause() {
        super.onPause();
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    // list of actions perfomed depending on a certain preference changed
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // this if in order to use getString()
        if (!isAdded()) {
            Log.d(TAG, "onSharedPreferenceChanged: not added to activity");
            return;
        }

        if (getString(BONUS_SYSTEM_KEY_ADDRESS).equals(key)) {
            int bonusSystemType = Integer.parseInt(sharedPreferences.getString(key, ""));
            bst = bonusSystemType;
            render();
        }
    }

    // render settings screen
    protected void render() {

        // this if in order to use getString()
        if (!isAdded()) {
            Log.d(TAG, "render: not added to activity");
            return;
        }
        // show bonus system type picker
        setPreferencesFromResource(R.xml.xml_owner_settings, getString(R.string.owner_settings_screen));

        if (bst >= TYPED_SETTINGS.length || bst < 0) {
            Log.d(TAG, "render: bonus system type out of range");
            return;
        }

        //set bonus system type summary
        findPreference(getString(BONUS_SYSTEM_KEY_ADDRESS)).setSummary(
                        getResources().getStringArray(R.array.bonus_system)[bst]);
        // show specialized settings
        addPreferencesFromResource(TYPED_SETTINGS[bst]);
    }

    // needed for custom dialog preference to work
    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment dialogFragment = null;
        if (preference instanceof NumberPreference) {
            dialogFragment = NumberPickerPreferenceDialog.newInstance(preference.getKey());
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(), THIS_FRAGMENT);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
