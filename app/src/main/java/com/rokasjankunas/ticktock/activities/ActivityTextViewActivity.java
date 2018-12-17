package com.rokasjankunas.ticktock.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.rokasjankunas.ticktock.R;
import com.rokasjankunas.ticktock.activities.custom.BatteryPercentageActivity;
import com.rokasjankunas.ticktock.activities.custom.TimeActivity;
import com.rokasjankunas.ticktock.objects.ActivityOption;

import java.util.ArrayList;
import java.util.List;

public class ActivityTextViewActivity extends Activity implements BillingProcessor.IBillingHandler{

    private List<ActivityOption> values = new ArrayList<>();
    private SettingsAdapter mAdapter;
    private BillingProcessor bp;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wearablerecyclerview_activity);

        WearableRecyclerView mWearableRecyclerView = findViewById(R.id.wearable_recycler_view);
        mWearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);

        mAdapter = new SettingsAdapter();
        mWearableRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mWearableRecyclerView.setAdapter(mAdapter);

        if (getIntent().getStringExtra("Activity").equals("restrictions")) {
            Toast.makeText(getApplicationContext(),"After changing settings go back to the main screen",Toast.LENGTH_SHORT).show();
            generateRestrictionsValues();
        } else if (getIntent().getStringExtra("Activity").equals("premium_options")) {
            boolean isAvailable = BillingProcessor.isIabServiceAvailable(getApplicationContext());
            if(!isAvailable) {
                Toast.makeText(getApplicationContext(),"In-app billing not available",Toast.LENGTH_SHORT).show();
                finish();
            }

            bp = BillingProcessor.newBillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiVXI8QRi6ydzuP9LWnBbph0xgnKqimf0vNPF4ubs9TbzXHlvT9qNkPbPTdN2qluuQg/0xg+AHRxKXSADZAoiVj365dkS4Neqf2od0h8MJQ4xB2mkKMRMqQZ494Lzf+I+rCOqQW/0evZ16M2qf8kiYEcuc14ZRNodfUb2PLaFhse/hjSQ+yUBiiI/t8+pt02DvbJ5W/VowoVFvItJ+uN2qnafDd5TGYc03XOjO0+mFndnGBTqtY10J5s/mar5u2+3zS612fkEfyMyTdnqgO7gxu50A7ArMGwxPBMoR40kS9aDixkdlxhlKB8cV32CjciWEJFqLGH4E4sfq7dKihGx9QIDAQAB", this);
            bp.initialize();

            sharedPreferences = getSharedPreferences(getString(R.string.sharedPreferences),Context.MODE_PRIVATE);

            bp.loadOwnedPurchasesFromGoogle();

            if(!bp.isPurchased("premium")) {
                purchased();
                Toast.makeText(getApplicationContext(),"After changing settings go back to the main screen",Toast.LENGTH_SHORT).show();
            } else {
                notPurchased();
            }
        }
    }

    private void generateRestrictionsValues(){
        ActivityOption activityOption = new ActivityOption();
        activityOption.setName("Battery percentage");
        activityOption.setActivity(BatteryPercentageActivity.class);
        activityOption.setExtra("battery_percentage");
        values.add(activityOption);

        activityOption = new ActivityOption();
        activityOption.setName("Charging");
        activityOption.setActivity(BooleanSwitchActivity.class);
        activityOption.setExtra("charging");
        values.add(activityOption);

        activityOption = new ActivityOption();
        activityOption.setName("Time");
        activityOption.setActivity(TimeActivity.class);
        activityOption.setExtra("time");
        values.add(activityOption);

        mAdapter.notifyDataSetChanged();
    }

    private void generatePremiumValues(){
        ActivityOption activityOption = new ActivityOption();
        activityOption.setName("Sounds");
        activityOption.setActivity(TextTextViewActivity.class);
        activityOption.setExtra("battery_percentage");
        values.add(activityOption);

        activityOption = new ActivityOption();
        activityOption.setName("Hourly chime");
        activityOption.setActivity(BooleanSwitchActivity.class);
        activityOption.setExtra("hourly_beep");
        values.add(activityOption);

        mAdapter.notifyDataSetChanged();
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
        } else {
            notPurchased();
        }
    }

    private void purchased() {
        sharedPreferences.edit().putBoolean(getString(R.string.premium_preference),true).apply();
        generatePremiumValues();
    }

    private void notPurchased() {
        finish();
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.MyViewHolder>{

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView name;

            MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.textView);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition(); // gets item position
                        Intent intent;
                        intent = new Intent(ActivityTextViewActivity.this, values.get(position).getActivity());
                        intent.putExtra("Activity",values.get(position).getExtra());
                        ActivityTextViewActivity.this.startActivity(intent);
                    }
                });
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.textview_item,parent,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            ActivityOption activityOption = values.get(position);
            holder.name.setText(activityOption.getName());

        }

        @Override
        public int getItemCount() {
            return values.size();
        }
    }

}
