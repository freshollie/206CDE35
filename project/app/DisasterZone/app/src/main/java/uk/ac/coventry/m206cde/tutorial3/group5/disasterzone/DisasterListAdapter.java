package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by freshollie on 2/27/17.
 */

public class DisasterListAdapter extends RecyclerView.Adapter<DisasterListAdapter.DisasterHolder> {
    private Disaster[] disasters = new Disaster[0];
    private DisasterListActivity disasterListActivity;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class DisasterHolder extends RecyclerView.ViewHolder {
        public TextView disasterNameText;
        public TextView disasterTypeText;
        public CardView disasterCardView;

        public DisasterHolder(View v) {
            super(v);
            disasterNameText = (TextView) v.findViewById(R.id.disaster_name);
            disasterTypeText = (TextView) v.findViewById(R.id.disaster_type);
            disasterCardView = (CardView) v.findViewById(R.id.disaster_card);
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

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final DisasterHolder holder, int position) {
        holder.disasterNameText.setText(disasters[position].getName());
        holder.disasterTypeText.setText(disasters[position].getType());
        holder.disasterCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disasterListActivity.onItemClicked(holder.getAdapterPosition());
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return disasters.length;
    }

    public void setDisasters(Disaster[] newDisasters) {
        disasters = newDisasters;
        notifyDataSetChanged();
    }
}


