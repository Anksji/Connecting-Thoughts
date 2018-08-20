package com.rsons.application.connecting_thoughts.Payment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.rsons.application.connecting_thoughts.R;

import java.util.HashMap;
import java.util.Map;

public class PaymentStatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private FirebaseFirestore mFireStoreDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_status);


        mToolbar= (Toolbar) findViewById(R.id.payment_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFireStoreDatabase=FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFireStoreDatabase.setFirestoreSettings(settings);
        mAuth = FirebaseAuth.getInstance();


        TextView StatusText= (TextView) findViewById(R.id.status_message);
        ImageView SuccessImage= (ImageView) findViewById(R.id.success_image);
        ImageView FailedImage= (ImageView) findViewById(R.id.failed_image);

        boolean Status=getIntent().getBooleanExtra("status",false);
        final String OrderId=getIntent().getStringExtra("order_id");
        final String mTXNId=getIntent().getStringExtra("transaction_id");
        final String UserImg=getIntent().getStringExtra("user_img");
        final String DonationAmount=getIntent().getStringExtra("amount");

        if (Status){
            Map<String, Object> data = new HashMap<>();
            data.put("orderId",OrderId);
            data.put("transaction_id",mTXNId);
            data.put("user_image",UserImg);
            data.put("donation_amount",DonationAmount);

            mFireStoreDatabase.collection("payment").document("Donation").collection("Recieved_donation").document(mAuth.getUid())
                    .set(data, SetOptions.merge());


            getSupportActionBar().setTitle("Donation Success");
            StatusText.setText("Donation Success");
            FailedImage.setVisibility(View.GONE);
            SuccessImage.setVisibility(View.VISIBLE);
            StatusText.setTextColor(Color.parseColor("#6AC259"));
        }else {
           // payment_status_Back.setBackgroundColor(ContextCompat.getColor(this, R.color.red_error));
            StatusText.setText("Donation Failed");
            getSupportActionBar().setTitle("Donation Failed");
            FailedImage.setVisibility(View.VISIBLE);
            SuccessImage.setVisibility(View.GONE);
            StatusText.setTextColor(Color.parseColor("#E04F5F"));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:{
                onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
