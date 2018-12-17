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

import com.rokasjankunas.ticktock.R;
import com.rokasjankunas.ticktock.objects.TextOption;

import java.util.ArrayList;
import java.util.List;

public class TextTextViewActivity extends Activity {

    private List<TextOption> values = new ArrayList<>();
    private PreferencesAdapter mAdapter;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wearablerecyclerview_activity);

        prefs = this.getSharedPreferences(getString(com.rokasjankunas.ticktock.R.string.sharedPreferences), Context.MODE_PRIVATE);

        WearableRecyclerView mWearableRecyclerView = findViewById(R.id.wearable_recycler_view);
        mWearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);

        mAdapter = new PreferencesAdapter();
        mWearableRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mWearableRecyclerView.setAdapter(mAdapter);

        generateValues();
    }


    private void generateValues(){
        TextOption textOption = new TextOption();
        textOption.setName("Beep");
        values.add(textOption);

        textOption = new TextOption();
        textOption.setName("Small Watch");
        values.add(textOption);

        textOption = new TextOption();
        textOption.setName("Wall Clock");
        values.add(textOption);

        textOption = new TextOption();
        textOption.setName("Heavy Tock");
        values.add(textOption);

        textOption = new TextOption();
        textOption.setName("Default");
        values.add(textOption);

        mAdapter.notifyDataSetChanged();
    }


    public class PreferencesAdapter extends RecyclerView.Adapter<PreferencesAdapter.MyViewHolder>{

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView name;

            MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.textView);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition(); // gets item position
                        prefs.edit().putString(getString(R.string.sound_preference),values.get(position).getName()).apply();
                        finish();
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
            TextOption activityOption = values.get(position);
            holder.name.setText(activityOption.getName());

        }

        @Override
        public int getItemCount() {
            return values.size();
        }
    }

}
