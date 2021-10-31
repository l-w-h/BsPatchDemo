package com.lwh.test.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.lwh.test.R;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class BsPatchUtil {
    static {
        System.loadLibrary("apkpatch");
    }

    public static  native int patch(String oldApkPath, String newApkPath, String patchPath);

    public static String getApkInstalledSrc(Context context){
        return context.getApplicationInfo().sourceDir;
    }

    public static void installApk(Context context, File apkFile){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //Android 7.0及以上
            // 参数2 清单文件中provider节点里面的authorities ; 参数3  共享的文件,即apk包的file类
            apkUri = FileProvider.getUriForFile(context,
                    context.getPackageName()+".fileProvider", apkFile);
            //对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            apkUri = Uri.fromFile(apkFile);
        }
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 获取apk文件哈希值
     * @param context
     * @param filePath
     * @return
     */
    public static String getFileSha(Context context, String filePath){

//        String filePath = context.getPackageCodePath();
        MessageDigest msgDigest = null;

        try {

            msgDigest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = new byte[1024];
            int byteCount;
            FileInputStream fis = new FileInputStream(new File(filePath));

            while ((byteCount = fis.read(bytes)) > 0){
                msgDigest.update(bytes, 0, byteCount);
            }

            BigInteger bi = new BigInteger(1, msgDigest.digest());
            String sha = bi.toString(16);
            Log.i("BsPatch", "apk sha=" + sha);
            fis.close();

            return sha;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
