package org.pktzj.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by pktzj on 2016/5/19.
 */
public class MD5Utils {
    public static String md5(String str) {
        StringBuilder mess = new StringBuilder();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = str.getBytes();
            byte[] digest = md.digest(bytes);

            for (byte b : digest) {
                //把每个字节转换称16进制数

                int d = b & 0xff;
                String hexStr = Integer.toHexString(d);
                if (hexStr.length() == 1) {
                    hexStr = "0" + hexStr;
                }
                mess.append(hexStr);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return mess + "";
    }
}
