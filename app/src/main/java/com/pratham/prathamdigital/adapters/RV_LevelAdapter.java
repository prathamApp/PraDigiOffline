package com.pratham.prathamdigital.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.interfaces.Interface_Level;
import com.pratham.prathamdigital.models.Modal_Level;
import com.pratham.prathamdigital.util.PD_Utility;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HP on 01-08-2017.
 */

public class RV_LevelAdapter extends RecyclerView.Adapter<RV_LevelAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Modal_Level> levels;
    private Interface_Level interface_level;

    public RV_LevelAdapter(Context context, Interface_Level interface_level, ArrayList<Modal_Level> levels) {
        this.context = context;
        this.levels = levels;
        this.interface_level = interface_level;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_level, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        PD_Utility.setFont(context, holder.l_name);
        holder.l_name.setText(levels.get(holder.getAdapterPosition()).getName());
        holder.l_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interface_level.levelClicked(holder.getAdapterPosition());
            }
        });
    }

    public void updateLevel(ArrayList<Modal_Level> levels) {
        this.levels = levels;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return levels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.l_name)
        TextView l_name;
        @BindView(R.id.l_card)
        LinearLayout l_card;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
