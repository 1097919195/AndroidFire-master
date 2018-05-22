package com.jaydenxiao.common.commonutils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.jaydenxiao.common.baseapp.BaseApplication;

import java.io.IOException;
import java.util.List;

/**
 * 获取经纬度(自己搞的)
 *
 */
@SuppressLint("MissingPermission")
public class LocalizeUtil {

    private static String latLongString;
    private static LocationManager locationManager;
    public static String getLngAndLat(Context context) {
        double latitude = 0.0;
        double longitude = 0.0;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//从gps获取经纬度

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateWithNewLocation(location);//根据location获取地址
            //监视地理位置变化
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            } else {
                return getLngAndLatWithNetwork(context);//从网络获取经纬度
            }
        } else {//从网络获取经纬度
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            updateWithNewLocation(location);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
        return longitude + "," + latitude;
    }

    //从网络获取经纬度
    private static String getLngAndLatWithNetwork(Context context) {
        double latitude = 0.0;
        double longitude = 0.0;
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        updateWithNewLocation(location);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        return longitude + "," + latitude;
    }

    //监视地理位置变化
    private static LocationListener locationListener = new LocationListener() {

        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {

        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
        }

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }
    };

    //获取更新地址
    private static String updateWithNewLocation(Location location) {

        double lat =0.0;
        double lng =0.0;
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            latLongString = "纬度:" + lat + "经度:" + lng;//输出经纬度
        } else {
            latLongString = "无法获取地理信息";
        }
        List<Address> addList = null;
        Geocoder ge = new Geocoder(BaseApplication.getAppContext());
        try {
            addList = ge.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addList!=null && addList.size()>0){
            for(int i=0; i<addList.size(); i++){
                Address ad = addList.get(i);
//                latLongString = ad.getCountryName() + "--" + ad.getLocality();//输出国家城市
                latLongString = ad.getLocality();//输出城市
            }
        }
        LogUtils.loge("latLongString======"+latLongString);
        return latLongString;
    }

    public static void unregister(){
        if(locationManager!=null){
            try{
                locationManager.removeUpdates(locationListener);
            }
            catch(SecurityException e){
                e.printStackTrace();
            }
        }
    }

    public static String getLatLongString() {
        return latLongString;
    }
}
