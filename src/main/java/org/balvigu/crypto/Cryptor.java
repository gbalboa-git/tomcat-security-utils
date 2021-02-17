package org.balvigu.crypto;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public class Cryptor {
    
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 32;
    private static final int ITERATIONS=20000;

    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
    private static final String PASSWORD = "816&bd672%bbf#ded39"; //You should change yours (this is public now :))


    public String encrypt(String plainText) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);

        PBEKeySpec kSpec = new PBEKeySpec(PASSWORD.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey skSpec = new SecretKeySpec(skf.generateSecret(kSpec).getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skSpec);

        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        //output = {salt}{encrypted}
        byte[] output =  Arrays.copyOf(salt, SALT_LENGTH + encrypted.length);
        System.arraycopy(encrypted, 0, output, SALT_LENGTH, encrypted.length);
        return asHexString(output);

    }

    public String decrypt(String encryptedString) throws NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {

        byte[] salt = readSalt(encryptedString);

        PBEKeySpec kSpec = new PBEKeySpec(PASSWORD.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey skSpec = new SecretKeySpec(skf.generateSecret(kSpec).getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skSpec);
        byte[] original = cipher.doFinal(toByteArray(encryptedString));

        //Lets "desalt" :)
        byte[] output = new byte[original.length-SALT_LENGTH];
        System.arraycopy(original, SALT_LENGTH, output, 0, output.length);
        return new String(output);
    }


    private byte[] readSalt(String encryptedString) throws IllegalBlockSizeException {
        byte[] salt = new byte[SALT_LENGTH];
        byte[] encryptedBytes = toByteArray(encryptedString);
        if (encryptedBytes.length < salt.length) throw new IllegalBlockSizeException("Invalid encrypted string");

        System.arraycopy(encryptedBytes, 0, salt, 0, SALT_LENGTH);
        return salt;
    }


    private String asHexString(byte[] buf) {
        StringBuilder strbuf = new StringBuilder(buf.length * 2);
        int i;
        for (i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10) {
                strbuf.append("0");
            }
            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }
        return strbuf.toString();
    }

    private byte[] toByteArray(String hexString) {
        int arrLength = hexString.length() >> 1;
        byte[] buf = new byte[arrLength];
        for (int ii = 0; ii < arrLength; ii++) {
            int index = ii << 1;
            String l_digit = hexString.substring(index, index + 2);
            buf[ii] = (byte) Integer.parseInt(l_digit, 16);
        }
        return buf;
    }

    public static void main( String[] args ) throws Exception {
        String input, output;
        Cryptor aes =  new Cryptor();
        switch(args.length){
            case 1:
                input = args[0];
                output = aes.encrypt(input);
                break;
            case 2:
                input = args[1];
                if (args[0].equalsIgnoreCase("-d")){
                    output = aes.decrypt(input);
                    break;
                }
            default:
                System.out.println("Use: java -jar balvigu-tomcat-utils-1.0 [-d] string");
                return;
        }
        System.out.println(input + ":" + output);
    }


}
