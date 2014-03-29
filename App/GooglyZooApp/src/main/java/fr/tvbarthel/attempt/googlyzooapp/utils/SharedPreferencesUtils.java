package fr.tvbarthel.attempt.googlyzooapp.utils;

/**
 * Created by tbarthel on 23/02/14.
 */
public final class SharedPreferencesUtils {

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    public static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * Tracks the last selected pet between two launch
     */
    public static final String PREF_USER_GOOGLY_PET_SELECTED = "googly_pet_selected";

    //Non instantiable class.
    private SharedPreferencesUtils() {
    }
}
