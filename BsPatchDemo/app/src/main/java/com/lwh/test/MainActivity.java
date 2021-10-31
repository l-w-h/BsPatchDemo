package com.lwh.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.lwh.test.databinding.ActivityMainBinding;
import com.lwh.test.util.BsPatchUtil;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvVersion.setText("当前版本 -- " + getVersionName() + " -- " + getVersionCode());

        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                patch();
                Toast.makeText(MainActivity.this,"暂无更新",Toast.LENGTH_SHORT).show();
                checkSha();
            }
        });
    }

    public void patch() {
        String patchPath = getExternalFilesDir("apk").getAbsolutePath() + "/patch.patch";
        String newApkPath = getExternalFilesDir("apk").getAbsolutePath() + "/newApk.apk";
        String oldApkPath = BsPatchUtil.getApkInstalledSrc(this);
        new AsyncTask<Void, Void, File>() {

            @Override
            protected File doInBackground(Void... voids) {
                String patch = patchPath;
                File newApk = new File(newApkPath);
                if (!newApk.exists()) {
                    try {
                        newApk.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                String oldApk = oldApkPath;
                BsPatchUtil.patch(oldApk, newApk.getAbsolutePath(), patch);
                return newApk;
            }

            @Override
            protected void onPostExecute(File apkFile) {
                //安装
                if (!apkFile.exists()) {
                    Log.e("BsPatch", "new file not exist");
                    return;
                }
                BsPatchUtil.installApk(MainActivity.this, apkFile);
            }
        }.execute();
    }

    /**
     * 校验文件哈希值
     * window cmd命令：certutil -hashfile filepath
     */
    public void checkSha() {
        try {

            String patchApk = getExternalFilesDir("apk").getAbsolutePath() + "/newApk.apk";
            String originalApk = getExternalFilesDir("apk").getAbsolutePath() + "/original.apk";
            String patch = getExternalFilesDir("apk").getAbsolutePath() + "/patch.patch";
            String currentApk = BsPatchUtil.getApkInstalledSrc(this);
            String patchApkSha = BsPatchUtil.getFileSha(this, patchApk);
            String originalApkSha = BsPatchUtil.getFileSha(this, originalApk);
            String currentApkSha = BsPatchUtil.getFileSha(this, currentApk);
            String patchSha = BsPatchUtil.getFileSha(this, patch);
            Log.i("BsPatch", "patchApkSha:" + patchApkSha + " -- originalApkSha:" + originalApkSha + " -- " + (TextUtils.equals(patchApkSha, originalApkSha)));
            Log.i("BsPatch", "currentApkSha:" + currentApkSha + " -- patchSha:" + patchSha);
            StringBuilder builder = new StringBuilder("当前版本 -- " + getVersionName() + " -- " + getVersionCode() + "\n");
            builder.append("patchApkSha:")
                    .append(patchApkSha)
                    .append("\n")
                    .append("originalApkSha:")
                    .append(originalApkSha)
                    .append("\n")
                    .append("currentApkSha:")
                    .append(currentApkSha)
                    .append("\n")
                    .append("patchSha:")
                    .append(patchSha)
                    .append("\n");
            binding.tvVersion.setText(builder);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersionName() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "无法获取到版本号";
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public int getVersionCode() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}