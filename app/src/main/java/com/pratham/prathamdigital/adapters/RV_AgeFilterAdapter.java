package com.pratham.prathamdigital.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;
import com.pratham.prathamdigital.util.PD_Utility;

import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HP on 01-08-2017.
 */

public class RV_AgeFilterAdapter extends RecyclerView.Adapter<RV_AgeFilterAdapter.ViewHolder> {

    private Context context;
    private MainActivityAdapterListeners browseAdapter_clicks;
    private String[] age;
    private int[] childs;
    private int selectedIndex;

    public RV_AgeFilterAdapter(Context context, MainActivityAdapterListeners browseAdapter_clicks, String[] age, int[] childs) {
        this.context = context;
        this.browseAdapter_clicks = browseAdapter_clicks;
        this.age = age;
        this.childs = childs;
        selectedIndex = -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_age_filter, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        PD_Utility.setFont(context, holder.c_age);
        holder.c_age.setText(age[position]);
//        holder.child_avatar.setImageResource(childs[position]);
        holder.card_age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseAdapter_clicks.browserButtonClicked(position);
            }
        });
        if (selectedIndex != -1 && selectedIndex == position) {
            holder.card_age.setBackgroundColor(context.getResources().getColor(R.color.ghost_white));
        } else {
            holder.card_age.setBackground(context.getResources().getDrawable(R.drawable.pink_blue_gradient));
        }
    }

    public void setSelectedIndex(int ind) {
        selectedIndex = ind;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return age.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.child_avatar)
        ImageView child_avatar;
        @BindView(R.id.card_age)
        RelativeLayout card_age;
        @BindView(R.id.c_age)
        TextView c_age;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
