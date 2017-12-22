package com.pratham.prathamdigital.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.custom_fab.FloatingActionButton;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.util.PD_Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HP on 01-08-2017.
 */

public class RV_ContentAdapter extends RecyclerView.Adapter<RV_ContentAdapter.ViewHolder> {

    private Context context;
    private MainActivityAdapterListeners browseAdapter_clicks;
    private ArrayList<Modal_ContentDetail> sub_content;
    private int selectedIndex;
    private int progress = 0;

    public RV_ContentAdapter(Context context, MainActivityAdapterListeners browseAdapter_clicks,
                             ArrayList<Modal_ContentDetail> sub_content) {
        this.context = context;
        this.browseAdapter_clicks = browseAdapter_clicks;
        this.sub_content = sub_content;
        selectedIndex = -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //TODO set Font here
        PD_Utility.setFont(context, holder.c_name);
        holder.c_name.setText(sub_content.get(position).getNodetitle());
        Picasso.with(context).load(sub_content.get(position).getNodeserverimage()).into(holder.item_content_img);
        if (sub_content.get(position).getNodetype().equalsIgnoreCase("Resource")) {
            if (selectedIndex != -1 && selectedIndex == position) {
            } else {
                holder.c_fab_download.setVisibility(View.VISIBLE);
            }
            holder.item_parent.setOnClickListener(null);
        } else {
            holder.c_fab_download.setVisibility(View.GONE);
            holder.item_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    browseAdapter_clicks.contentButtonClicked(holder.getAdapterPosition(), null);
                }
            });
        }
        holder.c_fab_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseAdapter_clicks.downloadClick(holder.getAdapterPosition(), holder.itemView);
            }
        });
    }

    public void setSelectedIndex(int position, ViewHolder holder) {
        if (selectedIndex != position) {
            selectedIndex = position;
            progress = 0;
        }
        notifyDataSetChanged();
    }

    public void setProgress(int pro) {
        progress = pro;
        Log.d("selectedIndex::", selectedIndex + "");
        notifyItemChanged(selectedIndex);
    }

    public void updateData(ArrayList<Modal_ContentDetail> arrayList_content) {
        this.sub_content = arrayList_content;
        selectedIndex = -1;
        progress = 0;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return sub_content.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //        @BindView(R.id.c_img_download)
//        ImageView c_img_download;
        @BindView(R.id.c_name)
        TextView c_name;
        //        @BindView(R.id.item_progressbar2)
//        CircleProgressView item_progressbar2;
        @BindView(R.id.item_content_img)
        ImageView item_content_img;
        @BindView(R.id.item_parent)
        RelativeLayout item_parent;
        @BindView(R.id.c_fab_download)
        FloatingActionButton c_fab_download;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
//            item_progressbar2.setInterpolator(new AccelerateDecelerateInterpolator());
        }
    }
}
