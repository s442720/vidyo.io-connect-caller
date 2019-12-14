package com.example.pfuternik.vidyoiodemo;

import android.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class GenerateToken {

    public static final String PROVISION_TOKEN = "provision";
    private static final long EPOCH_SECONDS = 62167219200l;
    private static final String DELIM = "\0";

    public static String generateProvisionToken(String key, String jid, String expires, String vcard) throws NumberFormatException {
        String payload = join(DELIM, PROVISION_TOKEN, jid, calculateExpiry(expires), vcard);
        return new String(Base64.encode(join(DELIM, payload, hmacSha384Hex(key, payload)).getBytes(), Base64.NO_WRAP));
    }

    private static String calculateExpiry(String expires) throws NumberFormatException {
        long expiresLong = 0l;
        long currentUnixTimestamp = System.currentTimeMillis() / 1000;
        expiresLong = Long.parseLong(expires);
        return "" + (EPOCH_SECONDS + currentUnixTimestamp + expiresLong);
    }

    private static String join(String with, String... list) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < list.length; i++) {
            if (i != 0) {
                buf.append(with);
            }
            buf.append(list[i]);
        }
        return buf.toString();
    }

    private final static char[] hexArray = "0123456789abcdef".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static String hmacSha384Hex(String key, String payload) {

        Mac mac;
        String result = "";
        try {
            final SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA384");
            mac = Mac.getInstance("HmacSHA384");
            mac.init(secretKey);
            byte[] macData = mac.doFinal(payload.getBytes());
            result = bytesToHex(macData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}