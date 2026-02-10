package toy.com.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONArray;

public class CmUtil {
    private static final Logger LOG_DEBUG = LoggerFactory.getLogger(CmUtil.class);

    public static String generateRandomPassword() {
        final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String lower = "abcdefghijklmnopqrstuvwxyz";
        final String digit = "0123456789";
        final String special = "!@#$%^&*()-_=+[]{};:,.?/";
        final String all = upper + lower + digit + special;
        java.security.SecureRandom random = new java.security.SecureRandom();
        char[] pwd = new char[8];
        pwd[0] = upper.charAt(random.nextInt(upper.length()));
        pwd[1] = lower.charAt(random.nextInt(lower.length()));
        pwd[2] = digit.charAt(random.nextInt(digit.length()));
        pwd[3] = special.charAt(random.nextInt(special.length()));
        for (int i = 4; i < pwd.length; i++) {
            pwd[i] = all.charAt(random.nextInt(all.length()));
        }
        // Shuffle (Fisher-Yates)
        for (int i = pwd.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char tmp = pwd[i];
            pwd[i] = pwd[j];
            pwd[j] = tmp;
        }
        return new String(pwd);
    }


    public static boolean isPasswordValid(String rawPwd) {
        if (rawPwd == null) {
            return false;
        }
        String pwd = rawPwd.trim();
        if (pwd.isEmpty()) {
            return false;
        }
        // Disallow whitespace inside password
        if (pwd.matches(".*\\s+.*")) {
            return false;
        }
        // Minimum length
        if (pwd.length() < 8) {
            return false;
        }
        boolean hasUpper = pwd.matches(".*[A-Z].*");
        boolean hasLower = pwd.matches(".*[a-z].*");
        boolean hasDigit = pwd.matches(".*\\d.*");
        boolean hasSpecial = pwd.matches(".*[^A-Za-z0-9].*");
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    public static String encryptPassword(String password, String id) throws Exception {
        if (password == null) {
            return "";
        } else {
            byte[] hashValue = null;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.reset();
            md.update(id.getBytes());
            hashValue = md.digest(password.getBytes());
            return new String(Base64.encodeBase64(hashValue));
        }
    }

    public static String encryptAES256(String msg, String key) throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), bytes, 70000, 256);
        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(1, secret);
        AlgorithmParameters params = cipher.getParameters();
        byte[] ivBytes = ((IvParameterSpec)params.getParameterSpec(IvParameterSpec.class)).getIV();
        byte[] encryptedTextBytes = cipher.doFinal(msg.getBytes("UTF-8"));
        byte[] buffer = new byte[bytes.length + ivBytes.length + encryptedTextBytes.length];
        System.arraycopy(bytes, 0, buffer, 0, bytes.length);
        System.arraycopy(ivBytes, 0, buffer, bytes.length, ivBytes.length);
        System.arraycopy(encryptedTextBytes, 0, buffer, bytes.length + ivBytes.length, encryptedTextBytes.length);
        return new String(Base64.encodeBase64(buffer));
    }

    public static String decryptAES256(String msg, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        ByteBuffer buffer = ByteBuffer.wrap(Base64.decodeBase64(msg));
        byte[] saltBytes = new byte[20];
        buffer.get(saltBytes, 0, saltBytes.length);
        byte[] ivBytes = new byte[cipher.getBlockSize()];
        buffer.get(ivBytes, 0, ivBytes.length);
        byte[] encryoptedTextBytes = new byte[buffer.capacity() - saltBytes.length - ivBytes.length];
        buffer.get(encryoptedTextBytes);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), saltBytes, 70000, 256);
        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
        cipher.init(2, secret, new IvParameterSpec(ivBytes));
        byte[] decryptedTextBytes = cipher.doFinal(encryoptedTextBytes);
        return new String(decryptedTextBytes);
    }

    public static String[] findGeoPoint(String address) {
        String[] coords = new String[2];
        if (EgovStringUtil.isEmpty(address)) {
            return coords;
        } else {
            String clientId = "q7t2gev1k6";
            String clientSecret = "gFprichnH8VEpYLC7JU5ROJGtW5m9J3nzhZ07p37";

            try {
                String addr = URLEncoder.encode(address, "UTF-8");
                String apiURL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + addr;
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
                con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if (responseCode == 200) {
                    br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                } else {
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }

                StringBuffer response = new StringBuffer();

                String inputLine;
                while((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }

                br.close();
                JSONTokener tokener = new JSONTokener(response.toString());
                JSONObject object = new JSONObject(tokener);
                JSONArray arr = object.getJSONArray("addresses");

                for(int i = 0; i < arr.length(); ++i) {
                    JSONObject temp = (JSONObject)arr.get(i);
                    coords[0] = (String)temp.get("y");
                    coords[1] = (String)temp.get("x");
                }
            } catch (Exception e) {
                LOG_DEBUG.debug(e.toString());
            }

            return coords;
        }
    }

    public static String nameMasking(String name) {
        int length = name.length();
        String middleMask = "";
        if (length > 2) {
            middleMask = name.substring(1, length - 1);
        } else {
            middleMask = name.substring(1, length);
        }

        String dot = "";

        for(int i = 0; i < middleMask.length(); ++i) {
            dot = dot + "*";
        }

        if (length > 2) {
            String var6 = name.substring(0, 1);
            return var6 + middleMask.replace(middleMask, dot) + name.substring(length - 1, length);
        } else {
            String var10000 = name.substring(0, 1);
            return var10000 + middleMask.replace(middleMask, dot);
        }
    }

    public static ModelAndView ajaxReturnMAV(String resultFlag, String errorMessage) {
        Map<String, Object> resultMap = new HashMap();
        resultMap.put("result", resultFlag);
        resultMap.put("errorMessage", errorMessage);
        return new ModelAndView("jsonView", resultMap);
    }
}
