package org.pktzj.mobilesafe.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by pktzj on 2016/5/30.
 */

public class AssetsUtils {

    /**
     * 从assets下拷贝文件
     *
     * @param context
     * @param name
     */
    public static void filecopy(Context context, String name) throws IOException {
        //io流拷贝 输入流
        AssetManager assetManager = context.getAssets();
        //读取asset的文件,转换成InputStream
        InputStream is = assetManager.open(name);

        //输出流
        FileOutputStream fos = context.openFileOutput(name, Context.MODE_PRIVATE);

        //拷贝流  定义缓冲区 10K
        byte[] buffer = new byte[10240];

        //读取的长度
        int len = is.read(buffer);
        int counts = 1;
        //循环读取
        while (len != -1) {
            //把缓冲区的数据写到输出流
            fos.write(buffer, 0, len);
            //每次100K的时候刷型缓冲区的数据到文件中
            if (counts % 10 == 0) {
                fos.flush();
            }
            //继续读取
            len = is.read(buffer);
            counts++;
        }
        fos.flush();
        fos.close();
        is.close();
    }
}
