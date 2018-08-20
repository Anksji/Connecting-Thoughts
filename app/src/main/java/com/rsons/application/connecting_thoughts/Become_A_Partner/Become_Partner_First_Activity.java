package com.rsons.application.connecting_thoughts.Become_A_Partner;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.rsons.application.connecting_thoughts.EditProfile_Activity;
import com.rsons.application.connecting_thoughts.Payment.PayUMoneyActivity;
import com.rsons.application.connecting_thoughts.R;

import java.util.Random;

/**
 * Created by ankit on 4/9/2018.
 */

public class Become_Partner_First_Activity extends AppCompatActivity {

    private FirebaseFirestore mFireStoreDatabase;
    private FirebaseAuth mAuth;
    private RelativeLayout SendEmailToUs,MakeAPaymentToUs;
    private Toolbar PartnerToolbar;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.become_a_parteners);

        PartnerToolbar = (Toolbar) findViewById(R.id.become_a_patener_toolbar);
        setSupportActionBar(PartnerToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.BECOME_A_PARTNER));

        SendEmailToUs = (RelativeLayout) findViewById(R.id.send_mail_to_us);
        MakeAPaymentToUs= (RelativeLayout) findViewById(R.id.finiancial_support);

        mFireStoreDatabase=FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFireStoreDatabase.setFirestoreSettings(settings);
        mAuth = FirebaseAuth.getInstance();

        MakeAPaymentToUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenPaymentDialog();
            }
        });

        SendEmailToUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMailToUs();
            }
        });
    }

    private void OpenPaymentDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(Become_Partner_First_Activity.this);
        View mView = Become_Partner_First_Activity.this.getLayoutInflater().inflate(R.layout.proceed_to_payment, null);

        RelativeLayout Proceed_Btn= (RelativeLayout) mView.findViewById(R.id.proceed_btn);
        RelativeLayout CancelBtn= (RelativeLayout) mView.findViewById(R.id.cancel_btn);
        TextView DialogTitle= (TextView) mView.findViewById(R.id.dialog_title);
        final EditText DonationAmount= (EditText) mView.findViewById(R.id.donation_amount);




        builder.setView(mView);
        final AlertDialog Warning_dialog = builder.create();

        Proceed_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DonationAmount.getText().toString().length()==0){
                    DonationAmount.setError(getString(R.string.PLEASE_ENTER_DONATION_AMOUNT));
                }else {
                    final double donation=Double.parseDouble(DonationAmount.getText().toString());
                    if (donation>0){
                        mProgressDialog=new ProgressDialog(Become_Partner_First_Activity.this);
                        mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT));
                        mProgressDialog.show();
                        mProgressDialog.setCanceledOnTouchOutside(false);

                        mFireStoreDatabase.collection("payment").document("Donation").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot dataSnapshot = task.getResult();
                                    if (dataSnapshot.exists()) {
                                            final String MerchantKey=dataSnapshot.getString("merchant_key");
                                            final String SaltKey=dataSnapshot.getString("salt_key");

                                        mFireStoreDatabase.collection("Users").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    DocumentSnapshot dataSnapshot=task.getResult();
                                                    if (dataSnapshot.exists()){
                                                        Warning_dialog.dismiss();
                                                        mProgressDialog.dismiss();
                                                        Intent intent=new Intent(Become_Partner_First_Activity.this,PayUMoneyActivity.class);
                                                        Bundle mBundle = new Bundle();
                                                        mBundle.putString("name", dataSnapshot.get("name").toString());
                                                        mBundle.putString("email", dataSnapshot.get("email").toString());
                                                        mBundle.putString("order_id","CT_DONATION_"+getRandomString(8));
                                                        mBundle.putString("userId",mAuth.getUid());
                                                        mBundle.putString("merchant_key",MerchantKey);
                                                        mBundle.putString("salt_key",SaltKey);
                                                        mBundle.putString("image",dataSnapshot.get("image").toString());
                                                        mBundle.putDouble("amount",donation);
                                                        mBundle.putString("phone", dataSnapshot.get("phone_no").toString());
                                                        mBundle.putInt("id", 1);
                                                        mBundle.putBoolean("isFromOrder",false);
                                                        intent.putExtras(mBundle);
                                                        startActivity(intent);

                                                    }
                                                    else {
                                                        Toast.makeText(Become_Partner_First_Activity.this,getString(R.string.SOMETHING_WENT_WRONG),Toast.LENGTH_SHORT).show();
                                                    }
                                                }else {
                                                    Toast.makeText(Become_Partner_First_Activity.this,getString(R.string.SOMETHING_WENT_WRONG),Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }
                                    else {
                                        Toast.makeText(Become_Partner_First_Activity.this,getString(R.string.SOMETHING_WENT_WRONG),Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(Become_Partner_First_Activity.this,getString(R.string.SOMETHING_WENT_WRONG),Toast.LENGTH_SHORT).show();
                                }

                            }
                        });




                    }
                    else {
                        DonationAmount.setError(getString(R.string.PLEASE_ENTER_DONATION_AMOUNT));
                    }
                }

            }
        });

        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Warning_dialog.dismiss();
            }
        });

        Warning_dialog.show();
    }
    protected String getRandomString(int length) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    private void sendMailToUs(){
        try{
            Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "connectingthoughtsofficial@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.I_WANT_TO_JOIN_CT));
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.I_WANT_GROW_CT));
            startActivity(intent);
        }catch(ActivityNotFoundException e){
            Toast.makeText(Become_Partner_First_Activity.this,getString(R.string.PLEASE_INSTALL_GMAIL_TO_SEND),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if( android.R.id.home==item.getItemId()){
            onBackPressed();
        }
        return true;
    }
}
