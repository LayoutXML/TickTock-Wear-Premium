package com.rokasjankunas.ticktock.activities.custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.rokasjankunas.ticktock.R;
import com.rokasjankunas.ticktock.activities.ActivityTextViewActivity;

public class NotPremiumActivity extends Activity implements BillingProcessor.IBillingHandler {

    private BillingProcessor bp;
    private SharedPreferences sharedPreferences;
    private Button purchase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.not_premium_activity);

        boolean isAvailable = BillingProcessor.isIabServiceAvailable(getApplicationContext());
        if(!isAvailable) {
            Toast.makeText(getApplicationContext(),"In-app billing not available",Toast.LENGTH_SHORT).show();
            finish();
        }

        bp = BillingProcessor.newBillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiVXI8QRi6ydzuP9LWnBbph0xgnKqimf0vNPF4ubs9TbzXHlvT9qNkPbPTdN2qluuQg/0xg+AHRxKXSADZAoiVj365dkS4Neqf2od0h8MJQ4xB2mkKMRMqQZ494Lzf+I+rCOqQW/0evZ16M2qf8kiYEcuc14ZRNodfUb2PLaFhse/hjSQ+yUBiiI/t8+pt02DvbJ5W/VowoVFvItJ+uN2qnafDd5TGYc03XOjO0+mFndnGBTqtY10J5s/mar5u2+3zS612fkEfyMyTdnqgO7gxu50A7ArMGwxPBMoR40kS9aDixkdlxhlKB8cV32CjciWEJFqLGH4E4sfq7dKihGx9QIDAQAB", this);
        bp.initialize();

        sharedPreferences = getSharedPreferences(getString(R.string.sharedPreferences),Context.MODE_PRIVATE);

        bp.loadOwnedPurchasesFromGoogle();

        if(bp.isPurchased("premium")) {
            purchased();
        }

        purchase = findViewById(R.id.buy);
        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.purchase(NotPremiumActivity.this, "premium");
            }
        });
    }

    @Override
    public void onBillingInitialized() {}

    @Override
    public void onProductPurchased(@NonNull String productId, TransactionDetails details) {
        if(bp.isPurchased("premium")) {
            purchased();
        }
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        if (errorCode!=Constants.BILLING_RESPONSE_RESULT_USER_CANCELED) {
            Toast.makeText(getApplicationContext(),"In-app billing failed",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {
        if(bp.isPurchased("premium")) {
            purchased();
        }
    }

    private void purchased() {
        sharedPreferences.edit().putBoolean(getString(R.string.premium_preference),true).apply();
        Intent intent = new Intent(this, ActivityTextViewActivity.class);
        intent.putExtra("Activity","premium_options");
        this.startActivity(intent);
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }
}