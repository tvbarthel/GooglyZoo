package com.android.vending.billing.tvbarthel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Inventory;
import com.android.vending.billing.Purchase;
import com.android.vending.billing.SkuDetails;
import com.android.vending.billing.tvbarthel.adapter.SupportAdapter;

import java.util.ArrayList;
import java.util.List;

import fr.tvbarthel.attempt.googlyzooapp.R;

/**
 * Created by tbarthel on 02/03/14.
 */
public class SupportActivity extends Activity {

    /**
     * dev purpose
     */
    private static final String TAG = SupportActivity.class.getName();

    /**
     * arbitrary request code for the purchase flow
     */
    private static final int REQUEST_CODE_SUPPORT_DEV = 42;

    /**
     * helper for in app billing
     */
    private IabHelper mIabHelper;

    /**
     * used to manage Toast
     */
    private Toast mToast;

    /**
     * Listener for purchase finished callback
     */
    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;

    /**
     * Listener for purchase list query
     */
    private IabHelper.QueryInventoryFinishedListener mQueryInventoryListener;

    /**
     * Listener for purchase consumption when already owned
     */
    private IabHelper.OnConsumeFinishedListener mConsumeListener;

    /**
     * id for  espresso product
     */
    private static final String SKU_ESPRESSO = "espresso";

    /**
     * list view for coffee entry
     */
    private ListView mCoffeeListView;

    /**
     * adapter for coffee entry
     */
    private SupportAdapter mCoffeeAdapter;

    /**
     * loader displayed during data recovering
     */
    private ProgressBar mLoader;

    /**
     * TextView used to display info when errors occur
     */
    private TextView mErrorPlaceholder;

    /**
     * Use to know if IabHelper is set up well
     */
    private boolean mIsIabStarted;

    /**
     * Use to know if the IabHelper is currently requesting purchase list
     */
    private boolean mIsIabRequestingCoffeeList;

    /**
     * Remember the selected item since purchase == null when already owned
     */
    private int mSelectedPurchase;

    /**
     * Purchase list
     */
    private ArrayList<Purchase> mPurchaseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        //get ui components
        mCoffeeListView = (ListView) findViewById(R.id.support_purchase_list);
        mLoader = (ProgressBar) findViewById(R.id.support_progressbar);
        mErrorPlaceholder = (TextView) findViewById(R.id.support_error_placeholder);

        //init coffee adapter
        mCoffeeAdapter = new SupportAdapter(getBaseContext(), new ArrayList<SkuDetails>());

        //set adapter
        mCoffeeListView.setAdapter(mCoffeeAdapter);
        mCoffeeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "coffee selected for purchase : " + mCoffeeAdapter.getItem(position).getTitle());
                mSelectedPurchase = position;
                mIabHelper.launchPurchaseFlow(SupportActivity.this,
                        mCoffeeAdapter.getItem(position).getSku(),
                        REQUEST_CODE_SUPPORT_DEV,
                        mPurchaseFinishedListener);
            }
        });

        String base64EncodedPublicKey = getResources().getString(R.string.support_key);
        mIabHelper = new IabHelper(this, base64EncodedPublicKey);

        //dev purpose
        mIabHelper.enableDebugLogging(true);

        //Iab not started
        mIsIabStarted = false;
        mIsIabRequestingCoffeeList = false;
        mSelectedPurchase = -1;
        mPurchaseList = new ArrayList<Purchase>();

        //init listener for purchase callback
        initPurchaseListener();

        //init listener for inventory callback
        initInventoryListener();

        //init listener for purchase consumption
        initConsumeListener();

        //start helper
        startIabHelper();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mIabHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mIabHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //request the coffee list on each onResume since network state can change
        if (mIsIabStarted && !mIsIabRequestingCoffeeList) {
            //retrieve coffee list
            requestCoffeeList();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //hide list and error place holder, since a refresh request is made on each onResume
        mCoffeeListView.setVisibility(View.GONE);
        mErrorPlaceholder.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mIabHelper != null) {
            mIabHelper.dispose();
            mIabHelper = null;
        }
    }

    /**
     * use to don't have toast queue
     *
     * @param message toast content
     */
    private void makeToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        mToast.show();
    }

    /**
     * request asynchronously the coffee list
     */
    private void requestCoffeeList() {
        //retrieve purchase list
        List<String> inventory = new ArrayList<String>();
        inventory.add(SKU_ESPRESSO);
        mIsIabRequestingCoffeeList = true;
        mIabHelper.queryInventoryAsync(true, inventory, mQueryInventoryListener);
    }

    /**
     * init purchase listener for async purchase request
     */
    private void initPurchaseListener() {
        mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                Log.d(TAG, "Purchase finished: " + result + ", purchase: " + info);

                // if we were disposed of in the meantime, quit.
                if (mIabHelper == null) return;

                //if success, thanks the user
                if (result.isSuccess()) {
                    purchaseSuccess(info);
                } else if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
                    //if purchase already owned consume it
                    Log.d(TAG, "alreadyOwned !");
                    if (mPurchaseList.get(mSelectedPurchase) != null) {
                        Log.d(TAG, "start consumption");
                        //workaround to get the purchase since info == null when already owned
                        mIabHelper.consumeAsync(mPurchaseList.get(mSelectedPurchase), mConsumeListener);
                    }
                } else {
                    //display error
                    makeToast(result.getMessage());
                }

            }
        };
    }

    /**
     * init callback for purchase consumption when purchase is already owned
     */
    private void initConsumeListener() {
        mConsumeListener = new IabHelper.OnConsumeFinishedListener() {
            @Override
            public void onConsumeFinished(Purchase purchase, IabResult result) {
                //if consume or not owned (due to cache from API v3, not owned purchase can be
                // asked for consumption) buy roduct again
                if (result.isSuccess() ||
                        result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED) {
                    Log.d(TAG, "pruchase consume : " + purchase.getSku());
                    Log.d(TAG, "Buy it again : " + purchase.getOriginalJson());
                    mIabHelper.launchPurchaseFlow(SupportActivity.this,
                            purchase.getSku(),
                            REQUEST_CODE_SUPPORT_DEV,
                            mPurchaseFinishedListener);
                }
            }
        };
    }

    /**
     * init inventory callback for async purchase list request
     */
    private void initInventoryListener() {
        mQueryInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                mIsIabRequestingCoffeeList = false;
                Log.d(TAG, "mQueryInventoryListener : " + result);
                if (result.isFailure()) {
                    makeToast("Fail to load coffee list, check your internet connection.");
                    mLoader.setVisibility(View.GONE);
                    mErrorPlaceholder.setVisibility(View.VISIBLE);
                    return;
                }
                if (inv != null && inv.hasDetails(SKU_ESPRESSO)) {
                    //clear the list
                    mCoffeeAdapter.clear();
                    mPurchaseList.clear();

                    //get purchase details
                    SkuDetails espressoDetails = inv.getSkuDetails(SKU_ESPRESSO);
                    Log.d(TAG, "espresso : " + espressoDetails.getDescription() + " " + espressoDetails.getPrice());

                    //add espresso to the coffee list
                    mCoffeeAdapter.add(espressoDetails);
                    mPurchaseList.add(inv.getPurchase(SKU_ESPRESSO));

                    //TODO remove when more purchase will be available
                    mCoffeeAdapter.add(espressoDetails);
                    mCoffeeAdapter.add(espressoDetails);
                    mCoffeeAdapter.add(espressoDetails);
                    mCoffeeAdapter.add(espressoDetails);
                    mCoffeeAdapter.add(espressoDetails);
                    mCoffeeAdapter.add(espressoDetails);
                    mCoffeeAdapter.add(espressoDetails);
                } else {
                    Log.d(TAG, "inv empty or espresso no details");
                }

                //hide loader
                mLoader.setVisibility(View.GONE);

                //show list
                mCoffeeListView.setVisibility(View.VISIBLE);

                //update list view
                mCoffeeAdapter.notifyDataSetChanged();
            }
        };
    }

    /**
     * setup IabHelper asynchronously
     */
    private void startIabHelper() {
        mIabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    makeToast("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mIabHelper == null) return;

                // IAB is fully set up. Retrieve purchases list
                Log.d(TAG, "Setup successful. Querying purchase list.");
                mIsIabStarted = true;

                //retrieve coffee list
                requestCoffeeList();
            }
        });
    }

    /**
     * dialog to thanks user for his support
     */
    private void purchaseSuccess(Purchase info) {
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setPositiveButton("Thanks !", null);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View thanksView = inflater.inflate(R.layout.support_thanks, null);
        if (thanksView != null) {
            final TextView thanksMessage =
                    (TextView) thanksView.findViewById(R.id.support_thanks_message);
            thanksMessage.setText(String.format(
                    getResources().getString(R.string.support_thanks), info.getSku()));
            build.setView(thanksView);
            build.create().show();
        }
    }
}
