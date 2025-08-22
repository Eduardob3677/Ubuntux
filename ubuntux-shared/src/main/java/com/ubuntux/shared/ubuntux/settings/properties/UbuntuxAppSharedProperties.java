package com.ubuntux.shared.ubuntux.settings.properties;

import android.content.Context;

import androidx.annotation.NonNull;

import com.ubuntux.shared.ubuntux.UbuntuxConstants;

public class UbuntuxAppSharedProperties extends UbuntuxSharedProperties {

    private static UbuntuxAppSharedProperties properties;


    private UbuntuxAppSharedProperties(@NonNull Context context) {
        super(context, UbuntuxConstants.UBUNTUX_APP_NAME,
            UbuntuxConstants.UBUNTUX_PROPERTIES_FILE_PATHS_LIST, UbuntuxPropertyConstants.UBUNTUX_APP_PROPERTIES_LIST,
            new UbuntuxSharedProperties.SharedPropertiesParserClient());
    }

    /**
     * Initialize the {@link #properties} and load properties from disk.
     *
     * @param context The {@link Context} for operations.
     * @return Returns the {@link UbuntuxAppSharedProperties}.
     */
    public static UbuntuxAppSharedProperties init(@NonNull Context context) {
        if (properties == null)
            properties = new UbuntuxAppSharedProperties(context);

        return properties;
    }

    /**
     * Get the {@link #properties}.
     *
     * @return Returns the {@link UbuntuxAppSharedProperties}.
     */
    public static UbuntuxAppSharedProperties getProperties() {
        return properties;
    }

}
