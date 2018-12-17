package com.rokasjankunas.ticktock.activities;

import android.app.Activity;
import android.content.Context;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.rokasjankunas.ticktock.R;
import com.rokasjankunas.ticktock.objects.BooleanOption;

import java.util.ArrayList;
import java.util.List;

public class BooleanSwitchActivity extends Activity {
    private List<BooleanOption> values = new ArrayList<>();
    private MiscOptionsAdapter mAdapter;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTheme(com.rokasjankunas.ticktock.R.style.MainStyle);
        setContentView(com.rokasjankunas.ticktock.R.layout.wearablerecyclerview_activity);

        prefs = this.getSharedPreferences(getString(com.rokasjankunas.ticktock.R.string.sharedPreferences), Context.MODE_PRIVATE);

        WearableRecyclerView mWearableRecyclerView = findViewById(com.rokasjankunas.ticktock.R.id.wearable_recycler_view);
        mWearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);

        mAdapter = new MiscOptionsAdapter();
        mWearableRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mWearableRecyclerView.setAdapter(mAdapter);

        if (getIntent().getStringExtra("Activity").equals("charging"))
            generateChargingValues();
        else if (getIntent().getStringExtra("Activity").equals("ambient&interactive_modes"))
            generateAmbientAndInteractiveModesValues();
        else if (getIntent().getStringExtra("Activity").equals("hourly_beep"))
            generateHourlyBeepValues();
    }

    private void generateChargingValues(){
        BooleanOption option = new BooleanOption();
        option.setName("Play while charging");
        option.setKey(getString(com.rokasjankunas.ticktock.R.string.charging_preference));
        option.setDefaultValue(true);
        values.add(option);

        option = new BooleanOption();
        option.setName("Play while not charging");
        option.setKey(getString(com.rokasjankunas.ticktock.R.string.notcharging_preference));
        option.setDefaultValue(true);
        values.add(option);

        mAdapter.notifyDataSetChanged();
    }

    private void generateAmbientAndInteractiveModesValues(){
        BooleanOption option = new BooleanOption();
        option.setName("Play while in ambient mode");
        option.setKey(getIntent().getStringExtra("Package")+"."+getString(com.rokasjankunas.ticktock.R.string.ambient_preference));
        option.setDefaultValue(true);
        values.add(option);

        option = new BooleanOption();
        option.setName("Play while in interactive mode");
        option.setKey(getIntent().getStringExtra("Package")+"."+getString(com.rokasjankunas.ticktock.R.string.interactive_preference));
        option.setDefaultValue(true);
        values.add(option);

        mAdapter.notifyDataSetChanged();
    }

    private void generateHourlyBeepValues(){
        BooleanOption option = new BooleanOption();
        option.setName("Beep hourly");
        option.setKey(getString(R.string.hourly_beep_preference));
        option.setDefaultValue(false);
        values.add(option);

        mAdapter.notifyDataSetChanged();
    }

    public class MiscOptionsAdapter extends RecyclerView.Adapter<MiscOptionsAdapter.MyViewHolder>{

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView name;
            Switch switcher;

            MyViewHolder(View view) {
                super(view);
                name = view.findViewById(com.rokasjankunas.ticktock.R.id.miscoptionsListTextView);
                switcher = view.findViewById(com.rokasjankunas.ticktock.R.id.miscoptionsListSwitch);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition(); // gets item position
                        BooleanOption selectedMenuItem = values.get(position);
                        prefs.edit().putBoolean(selectedMenuItem.getKey(),!selectedMenuItem.getValue()).apply();
                        selectedMenuItem.setValue(!selectedMenuItem.getValue());
                        switcher.setChecked(selectedMenuItem.getValue());
                        if (getIntent().getStringExtra("Activity").equals("ambient&interactive_modes"))
                            Toast.makeText(getApplicationContext(),"When you change the watch face restart TickTock",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @NonNull
        @Override
        public MiscOptionsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(com.rokasjankunas.ticktock.R.layout.switch_and_textview_item,parent,false);
            return new MiscOptionsAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MiscOptionsAdapter.MyViewHolder holder, int position) {
            BooleanOption option = values.get(position);
            holder.name.setText(option.getName());
            Boolean shouldBeOn = prefs.getBoolean(option.getKey(),option.getDefaultValue());
            holder.switcher.setChecked(shouldBeOn);
            option.setValue(shouldBeOn);
        }

        @Override
        public int getItemCount() {
            return values.size();
        }
    }
}
