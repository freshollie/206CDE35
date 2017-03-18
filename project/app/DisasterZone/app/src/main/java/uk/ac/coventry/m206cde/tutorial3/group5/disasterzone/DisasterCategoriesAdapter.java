package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by freshollie on 3/13/17.
 */

public class DisasterCategoriesAdapter extends RecyclerView.Adapter<DisasterCategoriesAdapter.CategoryHolder> {

    private static final String TAG = DisasterCategoriesAdapter.class.getSimpleName();

    private DisasterCategory[] categories = new DisasterCategory[0];
    private DisasterCategoriesActivity disasterCategoriesActivity;

    public static class CategoryHolder extends RecyclerView.ViewHolder {
        public TextView categoryText;
        public ImageView categoryImage;
        public RelativeLayout categoryCardLayout;
        public CardView categoryCard;

        public CategoryHolder(View v) {
            super(v);
            categoryText = (TextView) v.findViewById(R.id.card_category_text);
            categoryImage = (ImageView) v.findViewById(R.id.card_category_image);
            categoryCardLayout = (RelativeLayout) v.findViewById(R.id.card_category_layout);
            categoryCard = (CardView) v.findViewById(R.id.card_category);
        }
    }

    public DisasterCategoriesAdapter(DisasterCategoriesActivity parentActivity) {
        disasterCategoriesActivity = parentActivity;
    }

    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout_category, parent, false);

        CategoryHolder vh = new CategoryHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final CategoryHolder holder, int position) {
        holder.categoryText.setText(categories[position].getText());

        String stringImageResource = categories[position].getImageResource();
        if (!stringImageResource.isEmpty()) {
            if (!stringImageResource.contains(".")) {
                int resource = DisasterZoneApplication.getResId(stringImageResource, Drawable.class);
                if (resource != -1){
                    holder.categoryImage.setImageResource(resource);
                }
            }
        }

        int color = categories[position].getColour();
        if (color != -1) {
            holder.categoryCard.setCardBackgroundColor(color);
        }

        holder.categoryCardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disasterCategoriesActivity.onCategoryClicked(categories[holder.getAdapterPosition()].getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.length;
    }

    public void setCategories(DisasterCategory[] disasterCategories) {
        categories = disasterCategories;
        notifyDataSetChanged();
    }
}


