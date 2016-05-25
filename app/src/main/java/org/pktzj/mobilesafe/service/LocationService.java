package org.pktzj.mobilesafe.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;

import org.pktzj.mobilesafe.utils.MyConstants;
import org.pktzj.mobilesafe.utils.SPTool;

import java.util.List;

public class LocationService extends Service {
    private LocationManager lm;
    private LocationListener listener;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                //获取位置变化的结果
                float acuuracy = location.getAccuracy();//精确度，以米为单位
                double altitude = location.getAltitude();//获取海拔高度
                double longtitude = location.getLongitude();//获得经度
                double latirude = location.getLatitude();//获取纬度
                float speed = location.getSpeed();

                //定位信息
                StringBuilder tv_mess = new StringBuilder();
                tv_mess.append("accuracy: " + acuuracy + "\n");
                tv_mess.append("altitude: " + altitude + "\n");
                tv_mess.append("longtitude: " + longtitude + "\n");
                tv_mess.append("latirude: " + latirude + "\n");
                tv_mess.append("speed: " + speed + "\n");

                //发送短信
                String safenum = SPTool.getSring(LocationService.this, MyConstants.SAFENUM, "");
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(safenum, "", tv_mess + "", null, null);

                //关闭gps
                stopSelf();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        //获取所有提供的定位方式
        List<String> allProviders = lm.getAllProviders();
        for (String provider : allProviders) {
            Log.d(MyConstants.TAG, "定位方式： " + provider);
        }

        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);//允许产生费用
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//最佳精度
        //动态获取手机的最佳定位方式
        String bestProvider = lm.getBestProvider(criteria, true);
        //注册监听回掉  0,0代表有变化就回掉
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(MyConstants.TAG, "权限不足！");
            return;
        }
        lm.requestLocationUpdates(bestProvider, 0, 0, listener);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        //取消定位的监听
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(MyConstants.TAG, "权限不足！");
            return;
        }

        lm.removeUpdates(listener);
        lm = null;
        super.onDestroy();
    }
}
