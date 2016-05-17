package org.pktzj.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.domain.UrlBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SplashActivity extends Activity {

    private static final String TAG = "SplashActivity";
    private RelativeLayout rl_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initAnimation();
        checkVersion();
    }

    private void checkVersion() {
        //将耗时的操作放在子线程
        new Thread() {
            @Override
            public void run() {

                BufferedReader bfr = null;
                HttpURLConnection conn = null;
                UrlBean parseJson = null;
                try {
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
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
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
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
    }
}
