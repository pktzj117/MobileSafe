package org.pktzj.mobilesafe.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by pktzj on 2016/5/19.
 */
public class MD5Utils {
    public static String md5(String str) {
        StringBuilder mess = new StringBuilder();

        try {
            //获得MD5加密器
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

    public static String getFileMD5(String path) {
        StringBuffer mess = new StringBuffer();
        //获得MD5加密器
        try {
            FileInputStream fis = new FileInputStream(path);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[10240];
            int len = fis.read(buffer);
            while (len != -1) {
                md.update(buffer, 0, len);
                len = fis.read(buffer);
            }
            byte[] digest = md.digest();

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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mess + "";
    }
}
