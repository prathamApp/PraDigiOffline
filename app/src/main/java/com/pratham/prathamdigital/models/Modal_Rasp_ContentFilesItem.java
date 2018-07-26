package com.pratham.prathamdigital.models;

import com.google.gson.annotations.SerializedName;

public class Modal_Rasp_ContentFilesItem {

	@SerializedName("extension")
	private String extension;

	@SerializedName("thumbnail")
	private boolean thumbnail;

	@SerializedName("available")
	private boolean available;

	@SerializedName("download_url")
	private String downloadUrl;

	@SerializedName("supplementary")
	private boolean supplementary;

	@SerializedName("storage_url")
	private Object storageUrl;

	@SerializedName("id")
	private String id;

	@SerializedName("preset")
	private String preset;

	@SerializedName("priority")
	private int priority;

	@SerializedName("lang")
	private Object lang;

	@SerializedName("file_size")
	private int fileSize;

	public void setExtension(String extension){
		this.extension = extension;
	}

	public String getExtension(){
		return extension;
	}

	public void setThumbnail(boolean thumbnail){
		this.thumbnail = thumbnail;
	}

	public boolean isThumbnail(){
		return thumbnail;
	}

	public void setAvailable(boolean available){
		this.available = available;
	}

	public boolean isAvailable(){
		return available;
	}

	public void setDownloadUrl(String downloadUrl){
		this.downloadUrl = downloadUrl;
	}

	public String getDownloadUrl(){
		return downloadUrl;
	}

	public void setSupplementary(boolean supplementary){
		this.supplementary = supplementary;
	}

	public boolean isSupplementary(){
		return supplementary;
	}

	public void setStorageUrl(Object storageUrl){
		this.storageUrl = storageUrl;
	}

	public Object getStorageUrl(){
		return storageUrl;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setPreset(String preset){
		this.preset = preset;
	}

	public String getPreset(){
		return preset;
	}

	public void setPriority(int priority){
		this.priority = priority;
	}

	public int getPriority(){
		return priority;
	}

	public void setLang(Object lang){
		this.lang = lang;
	}

	public Object getLang(){
		return lang;
	}

	public void setFileSize(int fileSize){
		this.fileSize = fileSize;
	}

	public int getFileSize(){
		return fileSize;
	}
}