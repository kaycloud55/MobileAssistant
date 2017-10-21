package com.kaycloud.mobileassistant.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @author: kaycloud
 * @date: 2017/10/19.
 */

public class PackageUtils {

    /**
     * 获取版本号
     * @return 版本号
     */
    public static int getVersionCode(Context ctx) {
        PackageManager packageManager = ctx.getPackageManager();
        int versionCode;
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(ctx.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * 获取版本名
     * @return 版本名
     */
    public static String getVersionName(Context ctx) {
        PackageManager packageManager = ctx.getPackageManager();
        String versionName;
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(ctx.getPackageName(), 0);
            versionName = packageInfo.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
