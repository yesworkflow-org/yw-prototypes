package org.yesworkflow.urihash;

import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;



public class URIHash {

    // Properties
    private String filePath;

    // Constructor
    public URIHash(String filePath) {
        this.filePath = filePath;
    }

    public String getMD5Hash() throws IOException, NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("MD5");
        try(InputStream inputStream = new FileInputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int inputRead;
            while ((inputRead = inputStream.read(buffer)) != -1) {
                md.update(buffer, 0, inputRead);
            }
        }

        // change bytes to hex
        StringBuilder result = new StringBuilder();
        for(byte b: md.digest()) {
            result.append(String.format("%02x", b));
        }

        return result.toString();
    }

    public String getSHAHash() throws IOException, NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try(DigestInputStream inputStream = new DigestInputStream(new FileInputStream(filePath), md)) {
            // clear data with an empty loop
            while(inputStream.read() != -1);
            md = inputStream.getMessageDigest();
        }

        StringBuilder result = new StringBuilder();
        for(byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

}
