package com.pratham.prathamdigital.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.interfaces.FolderClick;
import com.pratham.prathamdigital.models.File_Model;

import java.util.ArrayList;

//import mayurmhm.mayur.ftpmodule.R;


/**
 * Created by PEF on 24/01/2018.
 */

public class File_Adapter extends RecyclerView.Adapter<File_Adapter.MyViewHolder> {

    private ArrayList<File_Model> dataSet;
    private FolderClick folderClick;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        ImageView imageViewIcon;
        ImageView btn_download;
        LinearLayout root;
        RelativeLayout rl_download;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.root = (LinearLayout) itemView.findViewById(R.id.root);
            this.rl_download = (RelativeLayout) itemView.findViewById(R.id.rl_download);
            this.textViewName = (TextView) itemView.findViewById(R.id.file_title);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.file_icon);
            this.btn_download = (ImageView) itemView.findViewById(R.id.btn_download);
        }
    }

    public File_Adapter(ArrayList<File_Model> data, FolderClick folderClick) {
        this.folderClick = folderClick;
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_file_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        Log.d("data_size::", dataSet.get(listPosition).getFileName() + "");
        Log.d("data_size::", dataSet.get(listPosition).isFile() + "");
        holder.textViewName.setText(dataSet.get(listPosition).getFileName());
        if (dataSet.get(listPosition).isFile) {
            holder.imageViewIcon.setImageResource(R.drawable.file);
            holder.btn_download.setVisibility(View.GONE);
            holder.rl_download.setVisibility(View.GONE);
        } else {
            if (dataSet.get(listPosition).getFileName().equalsIgnoreCase("app_PrathamGame")) {
                holder.imageViewIcon.setImageResource(R.drawable.folder);
                holder.btn_download.setVisibility(View.GONE);
                holder.rl_download.setVisibility(View.GONE);
            } else {
                holder.imageViewIcon.setImageResource(R.drawable.folder);
                holder.btn_download.setVisibility(View.VISIBLE);
                holder.rl_download.setVisibility(View.VISIBLE);

            }
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folderClick.onFolderClicked(listPosition, dataSet.get(listPosition).getFileName());
            }
        });
        holder.btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folderClick.onDownload(listPosition, dataSet.get(listPosition).getMfile());
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
