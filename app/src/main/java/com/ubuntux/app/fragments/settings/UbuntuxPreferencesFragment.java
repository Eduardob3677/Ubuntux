package com.ubuntux.app.fragments.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.ubuntux.R;
import com.ubuntux.shared.ubuntux.settings.preferences.UbuntuxAppSharedPreferences;

@Keep
public class UbuntuxPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(UbuntuxPreferencesDataStore.getInstance(context));

        setPreferencesFromResource(R.xml.ubuntux_preferences, rootKey);
    }

}

class UbuntuxPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final UbuntuxAppSharedPreferences mPreferences;

    private static UbuntuxPreferencesDataStore mInstance;

    private UbuntuxPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = UbuntuxAppSharedPreferences.build(context, true);
    }

    public static synchronized UbuntuxPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new UbuntuxPreferencesDataStore(context);
        }
        return mInstance;
    }

}
