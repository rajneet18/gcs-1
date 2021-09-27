package com.gcs.service;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.swing.text.Utilities;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.Response;

import com.google.api.client.util.Base64;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class ManageUploadService {

	private ServiceAccountCredentials creds; // Service Account Credentials
	private String saEmail; // Service Account email

	public ManageUploadService() {
		/* Initialize credentials and service account email */
		try (InputStream inputStream = new FileInputStream("C:\\Users\\deepak_kumarmishra\\Downloads\\Key.json")) {
			this.creds = ServiceAccountCredentials.fromStream(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.saEmail = "service account email";
	}

	/* Sign and return the URL for POST, using credentials from above */
	private String getSignedUrl(String bucketName, String objectName, String mimeType) {
		String signed_url = null;
		try {
			String verb = "POST";
			long expiration = System.currentTimeMillis() / 1000 + 60;
			String Canonicalized_Extension_Headers = "x-goog-resumable:start";
			String content_type = mimeType;

			byte[] sr = creds.sign((verb + "\n\n" + content_type + "\n" + expiration + "\n"
					+ Canonicalized_Extension_Headers + "\n" + "/" + bucketName + "/" + objectName).getBytes());
			String url_signature = new String(Base64.encodeBase64(sr));
			signed_url = "https://storage.googleapis.com/" + bucketName + "/" + objectName + "?GoogleAccessId="
					+ saEmail + "&Expires=" + expiration + "&Signature=" + URLEncoder.encode(url_signature, "UTF-8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return signed_url;
	}

	/*
	 * Send POST request to the signed URL using custom headers and an empty body,
	 * which returns the actual upload location
	 */
	public String getLocation(String bucketName, String objectName, String mimeType) throws IOException {
		String urls = getSignedUrl(bucketName, objectName, mimeType);
		System.out.println(urls);
		URL myURL = new URL(urls);
		/*
		 * HttpURLConnection myURLConnection = (HttpURLConnection)
		 * myURL.openConnection(); myURLConnection.setRequestMethod("POST");
		 * myURLConnection.setRequestProperty("Content-Type", mimeType);
		 * myURLConnection.setRequestProperty("x-goog-resumable", "start"); // Send post
		 * request myURLConnection.setDoOutput(true); DataOutputStream wr = new
		 * DataOutputStream(myURLConnection.getOutputStream()); wr.flush(); wr.close();
		 */
		return "1";

	}

	/* Do the actual upload and return the PUT Response */
	public Response doUpload(String url, InputStream inputStream, String mimeType) {
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

	public static void main(String[] args) throws Exception {
		ManageUploadService uploader = new ManageUploadService();
		String filePath = "file/path";
		File file = new File(filePath);
		// byte[] bytes = Utilities.fileToByteArray(file); // convert file to bytes
		// String mimeType = Utilities.getMimeType(bytes); // bytes from above used with
		// tika
		String url = uploader.getLocation("deepak13110", file.getName(), "text/multipart");
		Response r = uploader.doUpload(url, new FileInputStream(file), "text/multipart");
		System.out.println("Response : " + r.getStatus());
		System.out.println(r.getHeaders());
	}

	public String uploadImage(String fileName, String filePath, String fileType) throws IOException {
		Bucket bucket = getBucket("deepak13110");
		InputStream inputStream = new FileInputStream(new File(filePath));
		Blob blob = bucket.create(fileName, inputStream, fileType);
		System.out.println("Blob Link:" + blob.getMediaLink());
		return blob.getMediaLink();
	}

	private Bucket getBucket(String bucketName) throws IOException {
		GoogleCredentials credentials = GoogleCredentials
				.fromStream(new FileInputStream("C:\\Users\\deepak_kumarmishra\\Downloads\\Key.json"))
				.createScoped("https://www.googleapis.com/auth/cloud-platform");
		Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
		Bucket bucket = storage.get(bucketName);
		if (bucket == null) {
			throw new IOException("Bucket not found:" + bucketName);
		}
		return bucket;
	}
}
