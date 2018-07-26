package com.pratham.prathamdigital.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HP on 10-08-2017.
 */

public class Modal_ContentDetail {
    @SerializedName("nodeid")
    String nodeid;
    @SerializedName("nodetype")
    String nodetype;
    @SerializedName("nodetitle")
    String nodetitle;
    @SerializedName("nodekeywords")
    String nodekeywords;
    @SerializedName("nodeeage")
    String nodeeage;
    @SerializedName("nodedesc")
    String nodedesc;
    @SerializedName("nodeimage")
    String nodeimage;
    @SerializedName("nodeserverimage")
    String nodeserverimage;
    @SerializedName("resourceid")
    String resourceid;
    @SerializedName("resourcetype")
    String resourcetype;
    @SerializedName("resourcepath")
    String resourcepath;
    @SerializedName("nodeserverpath")
    String nodeserverpath;
    @SerializedName("level")
    int level;
    @SerializedName("parentid")
    String parentid;
    boolean isDownloading = false;

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public String getNodetype() {
        return nodetype;
    }

    public void setNodetype(String nodetype) {
        this.nodetype = nodetype;
    }

    public String getNodetitle() {
        return nodetitle;
    }

    public void setNodetitle(String nodetitle) {
        this.nodetitle = nodetitle;
    }

    public String getNodekeywords() {
        return nodekeywords;
    }

    public void setNodekeywords(String nodekeywords) {
        this.nodekeywords = nodekeywords;
    }

    public String getNodeeage() {
        return nodeeage;
    }

    public void setNodeeage(String nodeeage) {
        this.nodeeage = nodeeage;
    }

    public String getNodedesc() {
        return nodedesc;
    }

    public void setNodedesc(String nodedesc) {
        this.nodedesc = nodedesc;
    }

    public String getNodeimage() {
        return nodeimage;
    }

    public void setNodeimage(String nodeimage) {
        this.nodeimage = nodeimage;
    }

    public String getNodeserverimage() {
        return nodeserverimage;
    }

    public void setNodeserverimage(String nodeserverimage) {
        this.nodeserverimage = nodeserverimage;
    }

    public String getResourceid() {
        return resourceid;
    }

    public void setResourceid(String resourceid) {
        this.resourceid = resourceid;
    }

    public String getResourcetype() {
        return resourcetype;
    }

    public void setResourcetype(String resourcetype) {
        this.resourcetype = resourcetype;
    }

    public String getResourcepath() {
        return resourcepath;
    }

    public void setResourcepath(String resourcepath) {
        this.resourcepath = resourcepath;
    }

    public String getNodeserverpath() {
        return nodeserverpath;
    }

    public void setNodeserverpath(String nodeserverpath) {
        this.nodeserverpath = nodeserverpath;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }
}
