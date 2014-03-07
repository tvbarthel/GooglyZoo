package com.android.vending.billing.tvbarthel.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Inventory;

import fr.tvbarthel.attempt.googlyzooapp.R;

/**
 * Created by tbarthel on 06/03/14.
 */
public class SupportUtils {

    /**
     * Shared preferences key to store support action
     */
    public static final String SUPPORT_SHARED_KEY = "shared_preferences_support";

    /**
     * Shared preferences value for fresh install
     */
    public static final int SUPPORT_UNSET = 0x00000000;

    /**
     * Shared preferences value for supporter
     */
    public static final int SUPPORT_DONATE = 0x00000001;

    /**
     * Shared preferences value for future supporter
     */
    public static final int SUPPORT_NOT_YET = 0x00000002;

    /**
     * id for espresso product
     */
    public static final String SKU_ESPRESSO = "espresso";

    /**
     * id for cappuccino product
     */
    public static final String SKU_CAPPUCCINO = "cappuccino";

    /**
     * id for iced coffee product
     */
    public static final String SKU_ICED_COFFEE = "iced_coffee";

    /**
     * id for earl grey product
     */
    public static final String SKU_EARL_GREY = "earl_grey";

    /**
     * arbitrary request code for the purchase flow
     */
    public static final int REQUEST_CODE_SUPPORT_DEV = 42;

    /**
     * state for a purchased purchase
     * http://developer.android.com/google/play/billing/v2/billing_reference.html
     */
    public static final int SUPPORT_STATE_PURCHASED = 0;

    /**
     * check if user has already supported us
     *
     * @param i user inventory
     * @return true if already purchased
     */
    public static boolean hasPurchased(final Inventory i) {
        if (i.hasPurchase(SKU_ESPRESSO) && i.getPurchase(SKU_ESPRESSO).getPurchaseState() == SUPPORT_STATE_PURCHASED) {
            return true;
        }

        if (i.hasPurchase(SKU_CAPPUCCINO) && i.getPurchase(SKU_CAPPUCCINO).getPurchaseState() == SUPPORT_STATE_PURCHASED) {
            return true;
        }

        if (i.hasPurchase(SKU_ICED_COFFEE) && i.getPurchase(SKU_ICED_COFFEE).getPurchaseState() == SUPPORT_STATE_PURCHASED) {
            return true;
        }

        if (i.hasPurchase(SKU_EARL_GREY) && i.getPurchase(SKU_EARL_GREY).getPurchaseState() == SUPPORT_STATE_PURCHASED) {
            return true;
        }

        return false;
    }
}
