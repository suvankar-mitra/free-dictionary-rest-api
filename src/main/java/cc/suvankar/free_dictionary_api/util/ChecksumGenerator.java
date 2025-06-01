package cc.suvankar.free_dictionary_api.util;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ChecksumGenerator {

    public static String generateChecksum(Object obj) throws IOException, NoSuchAlgorithmException {
        final String algorithm = "SHA-256";
        ObjectMapper mapper = new ObjectMapper();
        byte[] serializedData = mapper.writeValueAsBytes(obj);
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] digest = md.digest(serializedData);

        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
