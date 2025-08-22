package com.ubuntux.shared.ubuntux.theme;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubuntux.shared.ubuntux.settings.properties.TermuxPropertyConstants;
import com.ubuntux.shared.ubuntux.settings.properties.TermuxSharedProperties;
import com.ubuntux.shared.theme.NightMode;

public class TermuxThemeUtils {

    /** Get the {@link TermuxPropertyConstants#KEY_NIGHT_MODE} value from the properties file on disk
     * and set it to app wide night mode value. */
    public static void setAppNightMode(@NonNull Context context) {
        NightMode.setAppNightMode(TermuxSharedProperties.getNightMode(context));
    }

    /** Set name as app wide night mode value. */
    public static void setAppNightMode(@Nullable String name) {
        NightMode.setAppNightMode(name);
    }

}
