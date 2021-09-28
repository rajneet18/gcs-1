package com.dto;

import org.springframework.web.multipart.MultipartFile;

public class FileDetail {

	private MultipartFile files;
	private String bucketname;
	
	public String getBucketname() {
		return bucketname;
	}

	public void setBucketname(String bucketname) {
		this.bucketname = bucketname;
	}

	public MultipartFile getFiles() {
		return files;
	}

	public void setFiles(MultipartFile files) {
		this.files = files;
	}
}
