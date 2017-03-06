package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by freshollie on 2/27/17.
 */

public class DisasterListAdapter extends RecyclerView.Adapter<DisasterListAdapter.DisasterHolder> {

    private static final String TAG = DisasterListAdapter.class.getSimpleName();
    private HashMap<String, Integer> typeColours = new HashMap<>();

    private Disaster[] disasters = new Disaster[0];
    private DisasterListActivity disasterListActivity;

    public static class DisasterHolder extends RecyclerView.ViewHolder {
        public TextView disasterNameText;
        public TextView disasterTypeText;
        public RelativeLayout disasterCardLayout;

        public DisasterHolder(View v) {
            super(v);
            disasterNameText = (TextView) v.findViewById(R.id.disaster_name);
            disasterTypeText = (TextView) v.findViewById(R.id.disaster_type);
            disasterCardLayout = (RelativeLayout) v.findViewById(R.id.disaster_card_layout);
        }
    }

    public DisasterListAdapter(DisasterListActivity parentActivity) {
        disasterListActivity = parentActivity;
    }

    @Override
    public DisasterHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout_disaster, parent, false);

        DisasterHolder vh = new DisasterHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final DisasterHolder holder, int position) {
        holder.disasterNameText.setText(disasters[position].getName());
        holder.disasterTypeText.setText(disasters[position].getType());


        int colour = typeColours.getOrDefault(disasters[position].getType(), 0);

        holder.disasterCardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disasterListActivity.onDisasterClicked(disasters[holder.getAdapterPosition()].getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return disasters.length;
    }

    public void setDisasters(Disaster[] newDisasters) {
        disasters = newDisasters;
        notifyDataSetChanged();
    }
}


