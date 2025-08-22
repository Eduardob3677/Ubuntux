package com.ubuntux.app.fragments.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.ubuntux.R;
import com.ubuntux.shared.ubuntux.settings.preferences.UbuntuxWidgetAppSharedPreferences;

@Keep
public class UbuntuxWidgetPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(UbuntuxWidgetPreferencesDataStore.getInstance(context));

        setPreferencesFromResource(R.xml.ubuntux_widget_preferences, rootKey);
    }

}

class UbuntuxWidgetPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final UbuntuxWidgetAppSharedPreferences mPreferences;

    private static UbuntuxWidgetPreferencesDataStore mInstance;

    private UbuntuxWidgetPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = UbuntuxWidgetAppSharedPreferences.build(context, true);
    }

    public static synchronized UbuntuxWidgetPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new UbuntuxWidgetPreferencesDataStore(context);
        }
        return mInstance;
    }

}
