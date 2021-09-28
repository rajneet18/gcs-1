package com.gcs.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.dto.FileDetail;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.StorageOptions;

@Service
public class GcsUploadService {

	@Autowired
	ResourceLoader resourceLoader;

	private ServiceAccountCredentials creds; // Service Account Credentials
	private String saEmail; // Service Account email

	public String getLocation(String bucketName, String objectName, String mimeType,ResourceLoader resourceLoader1) throws IOException {

		Resource resource = resourceLoader1.getResource("classpath:key.json");
		InputStream input = resource.getInputStream();

		GoogleCredentials credentials = GoogleCredentials.fromStream(input)
				.createScoped("https://www.googleapis.com/auth/cloud-platform");
		Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
		BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, objectName)).build();

		// Generate Signed URL
		Map<String, String> extensionHeaders = new HashMap<>();
		extensionHeaders.put("Content-Type", "application/octet-stream");

		URL url = storage.signUrl(blobInfo, 15, TimeUnit.MINUTES, Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
				Storage.SignUrlOption.withExtHeaders(extensionHeaders), Storage.SignUrlOption.withV4Signature());

		System.out.println("Generated PUT signed URL:");
		System.out.println(url);
		return url.toString();
	}

	/* Do the actual upload and return the PUT Response */
	public Response doUpload(String url, InputStream inputStream, String mimeType) {
		System.out.println(url);
		Response response = null;
		Client client = ClientBuilder.newClient();
		try {
			response = client.target(url).request().put(Entity.entity(inputStream, mimeType));
			if (response.getStatus() != 200) {
				System.out.println("Request failed with " + response.getStatus());
			}
		} catch (ResponseProcessingException e) {
			e.printStackTrace();
		}
		return response;
	}

	public void processRequest(FileDetail filedetail) {
		try {
			File file = getFile();
			try (InputStream inputStream = new FileInputStream(file)) {
				this.creds = ServiceAccountCredentials.fromStream(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.saEmail = "service account email";

			InputStream is = filedetail.getFiles().getInputStream();
			GcsUploadService uploader = new GcsUploadService();
			System.out.println("file name = " + filedetail.getFiles().getOriginalFilename());

			String url = uploader.getLocation(filedetail.getBucketname(), filedetail.getFiles().getOriginalFilename(),
					"application/octet-stream",resourceLoader);
			Response r = uploader.doUpload(url, is, "application/octet-stream");
			System.out.println("Response : " + r.getStatus());
			System.out.println(r.getHeaders());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public List<String> getListOfObject(String bucketName) {
		try {
			File file = getFile();
			GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(file))
					.createScoped("https://www.googleapis.com/auth/cloud-platform");
			Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

			for (Bucket buck : storage.list().iterateAll()) {
				System.out.println(buck);
			}

			// The name of the GCS bucket

			List<String> results = new ArrayList<>();

			BlobListOption listOptions = BlobListOption.currentDirectory();
			Page<Blob> blobs = storage.list(bucketName, listOptions);
			for (Blob blob : blobs.iterateAll()) {
				System.out.println("=======> " + blob.getName());

				results.add(blob.getName());

			}
			return results;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public List<String> getAvailableBucket() {
		try {

			File file = getFile();
			GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(file))
					.createScoped("https://www.googleapis.com/auth/cloud-platform");
			Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
			List<String> results = new ArrayList<>();
			for (Bucket buck : storage.list().iterateAll()) {
				System.out.println(buck);
				results.add(buck.getName());
			}
			return results;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private File getFile() throws IOException {
		Resource resource = resourceLoader.getResource("classpath:key.json");
		InputStream input = resource.getInputStream();
		File file = resource.getFile();
		return file;
	}

}
