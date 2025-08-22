package com.ubuntux.app.fragments.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.ubuntux.R;
import com.ubuntux.shared.ubuntux.settings.preferences.UbuntuxAPIAppSharedPreferences;

@Keep
public class UbuntuxAPIPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(UbuntuxAPIPreferencesDataStore.getInstance(context));

        setPreferencesFromResource(R.xml.ubuntux_api_preferences, rootKey);
    }

}

class UbuntuxAPIPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final UbuntuxAPIAppSharedPreferences mPreferences;

    private static UbuntuxAPIPreferencesDataStore mInstance;

    private UbuntuxAPIPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = UbuntuxAPIAppSharedPreferences.build(context, true);
    }

    public static synchronized UbuntuxAPIPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new UbuntuxAPIPreferencesDataStore(context);
        }
        return mInstance;
    }

}
