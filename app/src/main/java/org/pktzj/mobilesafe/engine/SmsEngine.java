package org.pktzj.mobilesafe.engine;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;

import com.google.gson.Gson;

import org.pktzj.mobilesafe.domain.SMSBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Created by pktzj on 2016/6/3.
 */

public class SmsEngine {

    public interface BackProgress {
        /**
         * 进度的显示回调
         */
        void show();

        /**
         * 回调显示进度的最大值
         *
         * @param count
         */
        void setMax(int count);

        /**
         * 回调显示当前进度
         *
         * @param progress
         */
        void setProgress(int progress);

        /**
         * 进度完成的回调
         */
        void end();
    }

    /**
     * 在子线程中备份短信保存格式为json
     *
     * @param context Activity
     * @param pd      进度条
     */
    public static void smsBackJson(final Activity context, final BackProgress pd) {
        new Thread() {
            @Override
            public void run() {
                //1.通过内容提动辄获取短信
                Uri uri = Uri.parse("content://sms");
                final Cursor cursor = context.getContentResolver().query(uri, new String[]{"address", "date", "body", "type"}, null, null, "_id desc");

                //2.写到文件中
                File file = new File(context.getFilesDir().getPath(), "sms.json");

                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    PrintWriter writer = new PrintWriter(fos);
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.show();
                            pd.setMax(cursor.getCount());
                        }
                    });
                    final Data data = new Data();

                    //写根标记  {"count":"10"
                    assert cursor != null;
                    writer.println("{\"count\":\"" + cursor.getCount() + "\"");
                    //,"smses":[
                    writer.println(",\"smses\":[");

                    while (cursor.moveToNext()) {
                        SystemClock.sleep(100);
                        if (cursor.getPosition() == 0) {
                            writer.println("{");
                        } else {
                            writer.println(",{");
                        }

                        //address 封装 "address":"hello"
                        writer.println("\"address\":\"" + cursor.getString(0) + "\",");
                        //date 封装 "date":"hello"
                        writer.println("\"date\":\"" + cursor.getString(1) + "\",");
                        //body 封装 "body":"hello"
                        writer.println("\"body\":\"" + cursor.getString(2) + "\",");
                        //type 封装 "type":"hello"
                        writer.println("\"type\":\"" + cursor.getString(3) + "\"");

                        writer.println("}");
                        data.progress++;
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.setProgress(data.progress);
                            }
                        });
                    }
                    writer.println("]}");

                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.end();
                        }
                    });

                    //写根标记

                    fos.flush();
                    cursor.close();
                    writer.close();
                    fos.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();


    }

    public static void smsBackGson(final Activity context, final BackProgress pd) {
        new Thread() {
            @Override
            public void run() {
                //1.通过内容提动辄获取短信
                Uri uri = Uri.parse("content://sms");

                //2.写到文件中
                File file = new File(context.getFilesDir().getPath(), "sms.json");


                try {
                    FileInputStream fis = new FileInputStream(file);
                    //json数据的合并
                    String jsonSMSStr = "";
                    //io流的封装 把字节流封装成缓冲的字符流
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

                    String line = reader.readLine();
                    while (line != null) {
                        jsonSMSStr += line;
                        line = reader.readLine();
                    }
                    reader.close();

                    //解析json数据
                    Gson gson = new Gson();

                    final SMSBean smsBean = gson.fromJson(jsonSMSStr, SMSBean.class);
                    final int count = Integer.parseInt(smsBean.count);
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.show();
                            pd.setMax(count);
                        }
                    });

                    for (int i = 0; i < count; i++) {

                        final int finalI = i;
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.setProgress(finalI);
                            }
                        });
                        Log.d("SMS","address: " + smsBean.smses.get(i).address +
                                "body: " + smsBean.smses.get(i).body +
                                "date: " + smsBean.smses.get(i).date +
                                "type: " + smsBean.smses.get(i).type
                        );
                        ContentValues values = new ContentValues();
                        values.put("address",smsBean.smses.get(i).address);
                        values.put("body",smsBean.smses.get(i).body);
                        values.put("date",smsBean.smses.get(i).date);
                        values.put("type",smsBean.smses.get(i).type);
                        context.getContentResolver().insert(uri, values);
                    }


                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.end();
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();


    }

    private static class Data {
        int progress;
    }
}
