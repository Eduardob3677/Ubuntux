package com.ubuntux.shared.ubuntux.theme;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubuntux.shared.ubuntux.settings.properties.UbuntuxPropertyConstants;
import com.ubuntux.shared.ubuntux.settings.properties.UbuntuxSharedProperties;
import com.ubuntux.shared.theme.NightMode;

public class UbuntuxThemeUtils {

    /** Get the {@link UbuntuxPropertyConstants#KEY_NIGHT_MODE} value from the properties file on disk
     * and set it to app wide night mode value. */
    public static void setAppNightMode(@NonNull Context context) {
        NightMode.setAppNightMode(UbuntuxSharedProperties.getNightMode(context));
    }

    /** Set name as app wide night mode value. */
    public static void setAppNightMode(@Nullable String name) {
        NightMode.setAppNightMode(name);
    }

}
