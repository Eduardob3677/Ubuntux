package com.ubuntux.app.fragments.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.ubuntux.R;
import com.ubuntux.shared.ubuntux.settings.preferences.UbuntuxFloatAppSharedPreferences;

@Keep
public class UbuntuxFloatPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(UbuntuxFloatPreferencesDataStore.getInstance(context));

        setPreferencesFromResource(R.xml.ubuntux_float_preferences, rootKey);
    }

}

class UbuntuxFloatPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final UbuntuxFloatAppSharedPreferences mPreferences;

    private static UbuntuxFloatPreferencesDataStore mInstance;

    private UbuntuxFloatPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = UbuntuxFloatAppSharedPreferences.build(context, true);
    }

    public static synchronized UbuntuxFloatPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new UbuntuxFloatPreferencesDataStore(context);
        }
        return mInstance;
    }

}
