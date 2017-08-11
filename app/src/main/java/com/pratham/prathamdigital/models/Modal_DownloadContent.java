package com.pratham.prathamdigital.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by HP on 11-08-2017.
 */

public class Modal_DownloadContent {
    @SerializedName("nodelist")
    List<Modal_ContentDetail> nodelist;
    @SerializedName("downloadurl")
    String downloadurl;
    @SerializedName("foldername")
    String foldername;

    public List<Modal_ContentDetail> getNodelist() {
        return nodelist;
    }

    public void setNodelist(List<Modal_ContentDetail> nodelist) {
        this.nodelist = nodelist;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    public String getFoldername() {
        return foldername;
    }

    public void setFoldername(String foldername) {
        this.foldername = foldername;
    }
}
