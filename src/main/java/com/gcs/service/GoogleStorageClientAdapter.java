package com.gcs.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;


@Service
public class GoogleStorageClientAdapter {
	
	// Google Service Account Client ID. Replace with your account.
    static  String CLIENT_ACCOUNT ;
 
    // Private key from the private_key field in the download JSON file. Replace
    // this!
    static  String PRIVATE_KEY;
    // base url of google cloud storage objects
    static  String BASE_GCS_URL = "https://storage.googleapis.com";
 
    // bucket and object we are trying to access. Replace this!
   // static final String OBJECT_PATH = "/deepak13110/earth.jpg";
 
    // full url to the object.
    static  String FULL_OBJECT_URL = BASE_GCS_URL ;
 
    // expiry time of the url in Linux epoch form (seconds since january 1970)
    static String expiryTime;
    static String givenExpiryTimeInMinute;
    
    public GoogleStorageClientAdapter() {
    	
    	Resource resource = new ClassPathResource("/application.properties");
        Properties props;
		try {
			props = PropertiesLoaderUtils.loadProperties(resource);
			CLIENT_ACCOUNT = props.getProperty("CLIENT_ACCOUNT");
			PRIVATE_KEY = props.getProperty("PRIVATE_KEY");
			givenExpiryTimeInMinute = props.getProperty("givenExpiryTimeInMinute");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
 
    public String generateSignedURL(String objectName) throws Exception {
  
    	System.out.print("rec object " + objectName);
        // Set Url expiry to one minute from now!
        setExpiryTimeInEpoch();
 
        String stringToSign = getSignInput("/deepak13110/"+objectName);
        PrivateKey pk = getPrivateKey();
        String signedString = getSignedString(stringToSign, pk);
 
        // URL encode the signed string so that we can add this URL
        signedString = URLEncoder.encode(signedString, "UTF-8");
 
        String signedUrl = getSignedUrl(signedString,"/deepak13110/"+objectName);
        System.out.println(signedUrl);
        return signedUrl;
    }
 
    // Set an expiry date for the signed url. Sets it at one minute ahead of
    // current time.
    // Represented as the epoch time (seconds since 1st January 1970)
    private static void setExpiryTimeInEpoch() {
        long now = System.currentTimeMillis();
        // expire in a minute!
        // note the conversion to seconds as needed by GCS.
        long expiredTimeInSeconds = (now + 60 * 1000L) / 1000;
        expiryTime = expiredTimeInSeconds + "";
    }
 
    // The signed URL format as required by Google.
    private static String getSignedUrl(String signedString,String objectName) {
        String signedUrl = FULL_OBJECT_URL  + objectName
                           + "?GoogleAccessId=" + CLIENT_ACCOUNT 
                           + "&Expires=" + expiryTime
                           + "&Signature=" + signedString;
        return signedUrl;
    }
 
    // We sign the expiry time and bucket object path
    private static String getSignInput(String objename) {
        return "GET" + "\n"
                    + "" + "\n"
                    + "" + "\n"
                    + expiryTime + "\n"
                    + objename;
    }
 
    // Use SHA256withRSA to sign the request
    private static String getSignedString(String input, PrivateKey pk) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(pk);
        privateSignature.update(input.getBytes("UTF-8"));
        byte[] s = privateSignature.sign();
        return Base64.getEncoder().encodeToString(s);
    }
 
    // Get private key object from unencrypted PKCS#8 file content
    private static PrivateKey getPrivateKey() throws Exception {
        // Remove extra characters in private key.
        String realPK = PRIVATE_KEY.replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("-----BEGIN PRIVATE KEY-----", "").replaceAll("\n", "");
        byte[] b1 = Base64.getDecoder().decode(realPK);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b1);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
 
	 
}