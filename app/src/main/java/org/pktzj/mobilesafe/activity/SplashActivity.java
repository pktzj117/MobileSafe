package org.pktzj.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.domain.UrlBean;
import org.pktzj.mobilesafe.utils.XUtil;
import org.xutils.common.Callback;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class SplashActivity extends Activity {

    private static final String TAG = "SplashActivity";
    private static final int NOERROR = -1;
    private static final int LOADMAIN = 1;//进入主界面
    private static final int SHOWUPDATEDIALOG = 2;//弹出更新对话框
    private static final int ERROR = 3;//错误统一代号
    private RelativeLayout rl_root;
    private TextView tv_versionName;
    private int versionCode;
    private String versionName;
    private UrlBean parseJson = null;
    private long startTimeMillis;
    private ProgressBar pb_download;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化界面
        initView();
        //初始化数据
        initData();
        //c初始化动画
        initAnimation();
        //检测服务器的版本
        checkVersion();
    }

    private void initData() {
        //获取自己的版本信息
        PackageManager pm = getPackageManager();

        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            //版本号
            versionCode = packageInfo.versionCode;
            //版本名
            versionName = packageInfo.versionName;
            Log.d(TAG, "版本名: " + versionName + "  版本号: " + versionCode + "");
            tv_versionName.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            //can not reach
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //处理消息
            switch (msg.what) {
                case LOADMAIN://加载主界面
                    loadMain();
                    break;
                case ERROR:
                    switch (ERROR) {
                        case 404:
                            Toast.makeText(getApplicationContext(), "404 not found!", Toast.LENGTH_SHORT).show();
                            break;
                        case 4001:
                            Toast.makeText(getApplicationContext(), "错误4001", Toast.LENGTH_SHORT).show();
                            break;
                        case 4002:
                            Toast.makeText(getApplicationContext(), "错误4002 json", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(),"一般错误",Toast.LENGTH_SHORT).show();
                            break;
                    }
                    loadMain();
                    break;
                case SHOWUPDATEDIALOG://显示更新版本的对话框
                    showUpdateDialog();
                    break;
            }
        }
    };

    private void loadMain() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        }).setTitle("提醒")
                .setMessage("是否更新新版本?新版本的特性:" + parseJson.getDesc())
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadNewAPK();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //进入主界面
                loadMain();
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //用户点击取消进入主界面
                loadMain();
            }
        })
        ;

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void downloadNewAPK() {

        pb_download.setVisibility(View.VISIBLE);
        //文件下载地址
        String url = parseJson.getUrl();
        //文件保存在本地的路径
        final String filepath = Environment.getExternalStorageDirectory() + url.substring(url.lastIndexOf("/"));
        XUtil.DownLoadFile(url, filepath, new MyProgressCallBack<File>() {

            @Override
            public void onSuccess(File result) {
                super.onSuccess(result);
                Log.d(TAG, "onSuccess");
                Toast.makeText(getApplicationContext(), "下载成功!", Toast.LENGTH_SHORT).show();
                //安装apk
                installAPK(filepath);
                pb_download.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                Log.d(TAG, ex.toString());
                Toast.makeText(getApplicationContext(), "下载失败!", Toast.LENGTH_SHORT).show();
                //载入home界面
                loadMain();
                pb_download.setVisibility(View.GONE);
            }

            @Override
            public void onLoading(long total, long current,
                                  boolean isDownloading) {
                super.onLoading(total, current, isDownloading);
                pb_download.setMax((int) total);
                pb_download.setProgress((int) current);
            }
        });
    }

    private void installAPK(String filepath) {
//        <action android:name="android.intent.action.VIEW" />
//        <action android:name="android.intent.action.INSTALL_PACKAGE" />
//        <category android:name="android.intent.category.DEFAULT" />
//        <data android:scheme="file" />
//        <data android:mimeType="application/vnd.android.package-archive" />
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(new File(filepath)), "application/vnd.android.package-archive");
        startActivity(intent);
    }


    private class MyProgressCallBack<File> implements Callback.ProgressCallback<File> {

        @Override
        public void onWaiting() {

        }

        @Override
        public void onStarted() {

        }

        @Override
        public void onLoading(long total, long current, boolean isDownloading) {

        }

        @Override
        public void onSuccess(File result) {

        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {

        }

        @Override
        public void onCancelled(CancelledException cex) {

        }

        @Override
        public void onFinished() {

        }
    }


    private void checkVersion() {
        //将耗时的操作放在子线程
        new Thread() {
            @Override
            public void run() {

                BufferedReader bfr = null;
                HttpURLConnection conn = null;
                int error = NOERROR;//正常码
                try {
                    startTimeMillis = System.currentTimeMillis();
                    URL url = new URL("http://192.168.1.101/safejson");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5 * 1000);
                    conn.setConnectTimeout(5 * 1000);
                    conn.setRequestMethod("GET");
                    if (conn.getResponseCode() == 200) {
                        //获取读取的字节流
                        InputStream is = conn.getInputStream();
                        //把字节流转换成字符流
                        bfr = new BufferedReader(new InputStreamReader(is));
                        String line = bfr.readLine();
                        //jison字符串数据的封装
                        StringBuilder json = new StringBuilder();
                        while (line != null) {
                            json.append(line);
                            line = bfr.readLine();
                        }
                        //返回数据封装信息
                        parseJson = parseJson(json);
                        Log.d(TAG, parseJson.toString());
                    } else {
                        error = 404;
                    }
                } catch (IOException e) {
                    error = 4001;
                    e.printStackTrace();
                } catch (JSONException e) {
                    error = 4002;
                    e.printStackTrace();
                } finally {
                    Message msg = Message.obtain();
                    if (error == NOERROR) {
                        msg.what = isNewVersion(parseJson);//检测是否有新版本
                    } else {
                        msg.what = ERROR;
                        msg.arg1 = error;
                    }
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTimeMillis < 3000) {
                        SystemClock.sleep(3000 - (endTime - startTimeMillis));
                        //时间不足3秒,补足3秒
                    }
                    handler.sendMessage(msg);//发送消息
                    try {
                        //关闭连接资源
                        if (bfr == null || conn == null) {
                            return;
                        }
                        bfr.close();
                        conn.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private int isNewVersion(UrlBean parseJson) {
        int serverCode = parseJson.getVersionCode();//获得服务器的版本
        if (serverCode == versionCode) {
            return LOADMAIN;//进入主界面
        } else {
            return SHOWUPDATEDIALOG;//有版本更新
        }
    }

    private UrlBean parseJson(StringBuilder json) throws JSONException {
        UrlBean bean = new UrlBean();
        JSONObject jsonObject;

        jsonObject = new JSONObject(json.toString());
        int versionCode = jsonObject.getInt("version");
        String url = jsonObject.getString("urlapk");
        String desc = jsonObject.getString("desc");
        //封装结果数据
        bean.setVersionCode(versionCode);
        bean.setUrl(url);
        bean.setDesc(desc);
        return bean;
    }

    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(3000);
        alphaAnimation.setFillAfter(true);
        rl_root.startAnimation(alphaAnimation);
    }

    private void initView() {
        setContentView(R.layout.activity_splash);
        x.Ext.init(getApplication());
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
        tv_versionName = (TextView) findViewById(R.id.tv);
        pb_download = (ProgressBar) findViewById(R.id.pb_download);
    }


}
