package com.rsons.application.connecting_thoughts.Payment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.rsons.application.connecting_thoughts.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by ankit on 11/14/2017.
 */

public class PayUMoneyActivity extends AppCompatActivity {

    /**
     * Adding WebView as setContentView
     */
    WebView webView;

    /**
     * Context for Activity
     */
    Context activity;
    /**
     * Order Id
     * To Request for Updating Payment Status if Payment Successfully Done
     */
    int mId; //Getting from Previous Activity
    /**
     * Required Fields
     */
    // Test Variables
    /*
    private String mMerchantKey = "FCyqqZ";
    private String mSalt = "sfBpGA8E";
    private String mBaseURL = "https://test.payu.in";
    */

    // Final Variables
    private String mMerchantKey = "";
    private String mSalt = "";
    private String mBaseURL = "https://secure.payu.in";
    private String UserImage="";
    private FirebaseAuth mAuth;
    private String mAction = ""; // For Final URL
    private String mTXNId; // This will create below randomly
    private String mHash; // This will create below randomly
    private String mProductInfo ; //Passing String only
    private String mFirstName; // From Previous Activity
    private String mEmailId; // From Previous Activity
    private double mAmount; // From Previous Activity
    private String mPhone; // From Previous Activity
    private String mServiceProvider = "payu_paisa";
    private String mSuccessUrl = "https://www.payumoney.com/dev-guide/products/invoicing.html";
    private String mFailedUrl = "https://www.payumoney.com/support.html";
    private String OrderId;

    private ProgressDialog mProgressDialog;

    boolean isFromOrder;
    /**
     * Handler
     */
    Handler mHandler = new Handler();

    /**
     * @param savedInstanceState
     */
    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);

        /**
         * Setting WebView to Screen
         */
        setContentView(R.layout.activity_webview_for_payumoney);

        /**
         * Creating WebView
         */
        webView = (WebView) findViewById(R.id.payumoney_webview);


        /**
         * Context Variable
         */
        activity = getApplicationContext();

        mProgressDialog=new ProgressDialog(PayUMoneyActivity.this);

        //mProgressDialog.setTitle("Please wait..!!");
        mProgressDialog.setMessage("Please wait..!!");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        mAuth = FirebaseAuth.getInstance();
        /**
         * Actionbar Settings
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        // enabling action bar app icon and behaving it as toggle button
        ab.setHomeButtonEnabled(true);
        ab.setTitle("Make Donation");

        /**
         * Getting Intent Variables...
         */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            UserImage=bundle.getString("image");
            mMerchantKey=bundle.getString("merchant_key");
            mSalt=bundle.getString("salt_key");
            mFirstName = bundle.getString("name");
            mEmailId = bundle.getString("email");
            mProductInfo=bundle.getString("services");
            mAmount = bundle.getDouble("amount");
            mPhone = bundle.getString("phone");
            OrderId=bundle.getString("order_id");
            mId = bundle.getInt("id");
            isFromOrder = bundle.getBoolean("isFromOrder");

            Log.i("skfskfjd", "" + mFirstName + " : " + mEmailId + " : " + mAmount + " : " + mPhone);

            /**
             * Creating Transaction Id
             */
            Random rand = new Random();
            String randomString = Integer.toString(rand.nextInt()) + (System.currentTimeMillis() / 1000L);
            mTXNId = hashCal("SHA-256", randomString).substring(0, 20);

            mAmount = new BigDecimal(mAmount).setScale(0, RoundingMode.UP).intValue();


            /**
             * Creating Hash Key
             */
            mHash = hashCal("SHA-512", mMerchantKey + "|" +
                    mTXNId + "|" +
                    mAmount + "|" +
                    mProductInfo + "|" +
                    mFirstName + "|" +
                    mEmailId + "|||||||||||" +
                    mSalt);

            /**
             * Final Action URL...
             */
            mAction = mBaseURL.concat("/_payment");

            /**
             * WebView Client
             */



            webView.setWebChromeClient(new WebChromeClient() {
                // For 3.0+ Devices (Start)
                // onActivityResult attached before constructor

                public void onProgressChanged(WebView view, int progress) {
                    mProgressDialog.show();
                    if (progress == 100) {
                        mProgressDialog.hide();

                    }else{
                        mProgressDialog.show();

                    }

                }

            });


            webView.setWebViewClient(new WebViewClient() {

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    Toast.makeText(activity, "Oh no! " + error, Toast.LENGTH_SHORT).show();
                    mProgressDialog.hide();
                }

               /* @Override
                public void onReceivedSslError(WebView view,
                                               SslErrorHandler handler, SslError error) {
                    Toast.makeText(activity, "SSL Error! " + error, Toast.LENGTH_SHORT).show();
                    handler.proceed();
                    mProgressDialog.hide();
                }*/


                @Override
                public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(PayUMoneyActivity.this);
                    builder.setTitle("SSL Error!");
                    builder.setMessage(""+error);
                    builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.proceed();
                        }
                    });
                    builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.cancel();
                        }
                    });
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return super.shouldOverrideUrlLoading(view, url);
                }

                @Override
                public void onPageFinished(WebView view, String url) {

                    if (url.equals(mSuccessUrl)) {

                        mProgressDialog.hide();
                        Intent intent = new Intent(PayUMoneyActivity.this, PaymentStatusActivity.class);
                        intent.putExtra("status", true);
                        intent.putExtra("transaction_id", mTXNId);
                        intent.putExtra("id", mId);
                        intent.putExtra("amount",String.valueOf(mAmount));
                        intent.putExtra("user_img",UserImage);
                        intent.putExtra("order_id",OrderId);
                        intent.putExtra("isFromOrder", isFromOrder);
                        startActivity(intent);
                        finish();

                    } else if (url.equals(mFailedUrl)) {
                        mProgressDialog.hide();
                        Intent intent = new Intent(PayUMoneyActivity.this, PaymentStatusActivity.class);
                        intent.putExtra("status", false);
                        intent.putExtra("transaction_id", mTXNId);
                        intent.putExtra("id", mId);
                        intent.putExtra("amount",String.valueOf(mAmount));
                        intent.putExtra("user_img",UserImage);
                        intent.putExtra("isFromOrder", isFromOrder);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else if (url.contains("https://checkout.citruspay.com/payu/checkout/")){
                        mProgressDialog.hide();
                    }
                    Log.e("sdfsf","Current Url is "+url);
                    super.onPageFinished(view, url);
                }
            });

            webView.setVisibility(View.VISIBLE);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setCacheMode(2);
            webView.getSettings().setDomStorageEnabled(true);
            webView.clearHistory();
            webView.clearCache(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setSupportZoom(true);
            webView.getSettings().setUseWideViewPort(false);
            webView.getSettings().setLoadWithOverviewMode(false);
            webView.addJavascriptInterface(new PayUJavaScriptInterface(PayUMoneyActivity.this), "PayUMoney");

            /**
             * Mapping Compulsory Key Value Pairs
             */
            Map<String, String> mapParams = new HashMap<>();

            mapParams.put("key", mMerchantKey);
            mapParams.put("txnid", mTXNId);
            mapParams.put("amount", String.valueOf(mAmount));
            mapParams.put("productinfo", mProductInfo);
            mapParams.put("firstname", mFirstName);
            mapParams.put("email", mEmailId);
            mapParams.put("phone", mPhone);
            mapParams.put("surl", mSuccessUrl);
            mapParams.put("furl", mFailedUrl);
            mapParams.put("hash", mHash);
            mapParams.put("service_provider", mServiceProvider);

            webViewClientPost(webView, mAction, mapParams.entrySet());
        } else {
            Toast.makeText(activity, "Something went wrong, Try again.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Posting Data on PayUMoney Site with Form
     *
     * @param webView
     * @param url
     * @param postData
     */
    public void webViewClientPost(WebView webView, String url,
                                  Collection<Map.Entry<String, String>> postData) {
        StringBuilder sb = new StringBuilder();
        mProgressDialog.show();
        sb.append("<html><head></head>");
        sb.append("<body onload='form1.submit()'>");
        sb.append(String.format("<form id='form1' action='%s' method='%s'>", url, "post"));

        for (Map.Entry<String, String> item : postData) {
            sb.append(String.format("<input name='%s' type='hidden' value='%s' />", item.getKey(), item.getValue()));
        }
        sb.append("</form></body></html>");

        Log.d("TAG", "webViewClientPost called: " + sb.toString());
        webView.loadData(sb.toString(), "text/html", "utf-8");
    }

    /**
     * Hash Key Calculation
     *
     * @param type
     * @param str
     * @return
     */
    public String hashCal(String type, String str) {
        byte[] hashSequence = str.getBytes();
        StringBuffer hexString = new StringBuffer();
        try {
            MessageDigest algorithm = MessageDigest.getInstance(type);
            algorithm.reset();
            algorithm.update(hashSequence);
            byte messageDigest[] = algorithm.digest();

            for (int i = 0; i < messageDigest.length; i++) {
                String hex = Integer.toHexString(0xFF & messageDigest[i]);
                if (hex.length() == 1)
                    hexString.append("0");
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException NSAE) {
        }
        return hexString.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onPressingBack();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(PayUMoneyActivity.this,"back is pressed",Toast.LENGTH_SHORT).show();
        onPressingBack();
    }

    /**
     * On Pressing Back
     * Giving Alert...
     */
    private void onPressingBack() {

        final Intent intent;

        Toast.makeText(PayUMoneyActivity.this,"Please have a patience",Toast.LENGTH_SHORT).show();
        /*if(isFromOrder){
            intent = new Intent(PayUMoneyActivity.this, PaymentStatusActivity.class);
            Toast.makeText(PayUMoneyActivity.this,"Please have a patience",Toast.LENGTH_SHORT).show();
        }
        else
            intent = new Intent(PayUMoneyActivity.this, MainNavigationActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PayUMoneyActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Warning");

        // Setting Dialog Message
        alertDialog.setMessage("Do you cancel this transaction?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();*/
    }

    public class PayUJavaScriptInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        PayUJavaScriptInterface(Context c) {
            mContext = c;
        }

        public void success(long id, final String paymentId) {
            mHandler.post(new Runnable() {

                public void run() {
                    mHandler = null;
                    Toast.makeText(PayUMoneyActivity.this, "Payment Successfully.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}