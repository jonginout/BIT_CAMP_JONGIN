package com.cloud.repository.vo;

import java.util.Date;
import java.util.List;

public class Tree {

	
	private String title;
	private Boolean isFolder;
	private Boolean isLazy;
	private String key;
	private String upperFolder;
	private List<Tree> children;
	
	//////////////////////////
	
	private String path;
	private String ext;
	private String contentType;
	private Date updateDate;
	private long maxSize;
	private long size;
	
	
	public long getMaxSize() {
		return maxSize;
	}
	public void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	///////////////////////////
	

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Boolean getIsFolder() {
		return isFolder;
	}
	public void setIsFolder(Boolean isFolder) {
		this.isFolder = isFolder;
	}
	public Boolean getIsLazy() {
		return isLazy;
	}
	public void setIsLazy(Boolean isLazy) {
		this.isLazy = isLazy;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public List<Tree> getChildren() {
		return children;
	}
	public void setChildren(List<Tree> children) {
		this.children = children;
	}
	public String getUpperFolder() {
		return upperFolder;
	}
	public void setUpperFolder(String upperFolder) {
		this.upperFolder = upperFolder;
	}
	@Override
	public String toString() {
		return "Tree [title=" + title + ", isFolder=" + isFolder + ", isLazy=" + isLazy + ", key=" + key
				+ ", upperFolder=" + upperFolder + ", children=" + children + ", path=" + path + ", ext=" + ext + "]";
	}
	
}
