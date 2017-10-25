package com.example.BonusHub.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.example.BonusHub.activity.HostMainActivity;
import com.example.BonusHub.db.host.Host;
import com.example.BonusHub.retrofit.HostApiInterface;
import com.example.BonusHub.retrofit.RetrofitFactory;
import com.example.BonusHub.retrofit.getInfo.GetInfoResponse;
import com.example.BonusHub.retrofit.loyality.EditLoyalityRequest;
import com.example.BonusHub.retrofit.loyality.EditLoyalityResponse;
import com.example.BonusHub.utils.AuthUtils;
import com.example.BonusHub.utils.NumberPreference.NumberPickerPreferenceDialog;
import com.example.BonusHub.utils.NumberPreference.NumberPreference;
import com.example.timur.BonusHub.R;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OwnerSettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OwnerSettingsFragment#newInstance} factoryhttp://netology.ru/programs/data-scientist?utm_source=context&utm_medium=1267&utm_campaign=ds-rsya&utm_content=4171435107&utm_term=python%20machine%20learning&yclid=2600899913511344354&stop=1 method to
 * create an instance of this fragment.
 */
public class OwnerSettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private boolean resumed = false;
    public final static String TAG = "OwnerSettingsFragment";
    private SharedPreferences sp;
    protected final String THIS_FRAGMENT = "android.support.v7.preference.PreferenceFragment.DIALOG";
    protected final int BONUS_SYSTEM_KEY_ADDRESS = R.string.bonus_system_key;
    protected final int TYPED_SETTINGS_KEYS[] = {
            R.string.cup_param_key,
            R.string.percent_param_key
    };
    protected final int TYPED_SETTINGS[] = {
            R.xml.xml_cup_settings,
            R.xml.xml_percent_settings//,
//            R.xml.xml_total_discout_settings
    };
    protected int bst = 1; // bonus system type (before update)
    protected int bsp = 10; // bonus system param (before update)
    HostApiInterface hostApiInterface;

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
        // retrofit init
        hostApiInterface = RetrofitFactory.retrofitHost().create(HostApiInterface.class);

        final Call<GetInfoResponse> call =
                hostApiInterface.getInfo(AuthUtils.getCookie(((HostMainActivity) getActivity()).getApplicationContext()));
        call.enqueue(new Callback<GetInfoResponse>() {
            @Override
            public void onResponse(Call<GetInfoResponse> call, Response<GetInfoResponse> response) {
                bst = response.body().getLoyalityType();
                if (bst != 0 && bst != 1) return;
                bsp = (int) response.body().getLoyalityParam();
                ((ListPreference) findPreference(getString(BONUS_SYSTEM_KEY_ADDRESS))).setValueIndex(bst);
                render();
                ((NumberPreference) findPreference(getString(TYPED_SETTINGS_KEYS[bst]))).setValue(bsp);
            }

            @Override
            public void onFailure(Call<GetInfoResponse> call, Throwable t) {

            }
        });
    }

    // register listener for bonus type change
    @Override
    public void onResume() {
        super.onResume();
        resumed = true;
        sp.registerOnSharedPreferenceChangeListener(this);
        // update bonus system type
        bst = Integer.parseInt(sp.getString(getString(BONUS_SYSTEM_KEY_ADDRESS), "1"));
        bsp = (int) sp.getInt(getString(TYPED_SETTINGS_KEYS[bst]), 10);
        render();
    }

    @Override
    public void onPause() {
        super.onPause();
        resumed = false;
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    // list of actions perfomed depending on a certain preference changed
    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, String key) {
        // this if in order to use getString()
        if (!isAdded()) {
            Log.d(TAG, "onSharedPreferenceChanged: not added to activity");
            return;
        }
        // if any preference changed:
        if (getString(BONUS_SYSTEM_KEY_ADDRESS).equals(key) ||
                getString(TYPED_SETTINGS_KEYS[0]).equals(key) ||
                getString(TYPED_SETTINGS_KEYS[1]).equals(key)) {
            // take loyality values from sp
            final int loyalityType = Integer.parseInt(sharedPreferences.getString(
                    getString(BONUS_SYSTEM_KEY_ADDRESS), "1"));
            final int loyalityParam = (int) sharedPreferences.getInt(
                    getString(TYPED_SETTINGS_KEYS[loyalityType]), 10);
            // make a request with them
            final Call<EditLoyalityResponse> call =
                    hostApiInterface.editLoyality(new EditLoyalityRequest(loyalityType, loyalityParam, ""),
                    AuthUtils.getCookie(((HostMainActivity) getActivity()).getApplicationContext()));
            // success => ok, failure => we need to reset values
            call.enqueue(new Callback<EditLoyalityResponse>() {
                @Override
                public void onResponse(Call<EditLoyalityResponse> call, Response<EditLoyalityResponse> response) {
                    Log.d(TAG, "onResponse: " + response.body().getCode());
                    bst = loyalityType;
                    bsp = loyalityParam;
                    render();
                    Snackbar snackbar = Snackbar.make(getView(), getString(R.string.loyality_changed), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

                @Override
                public void onFailure(Call<EditLoyalityResponse> call, Throwable t) {
                    Snackbar snackbar = Snackbar.make(getView(), "Ошибка соединения с сервером. Проверьте интернет подключение.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    if (resumed) sp.unregisterOnSharedPreferenceChangeListener(OwnerSettingsFragment.this);
                    // change back
                    ((ListPreference) findPreference(getString(BONUS_SYSTEM_KEY_ADDRESS))).setValueIndex(bst);
                    ((NumberPreference) findPreference(getString(TYPED_SETTINGS_KEYS[bst]))).setValue(bsp);
                    if (resumed) sp.registerOnSharedPreferenceChangeListener(OwnerSettingsFragment.this);
                }
            });
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
