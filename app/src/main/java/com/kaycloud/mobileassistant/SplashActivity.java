package com.kaycloud.mobileassistant;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaycloud.mobileassistant.http.RetrofitClient;
import com.kaycloud.mobileassistant.http.downloadprogress.DownloadProgressListener;
import com.kaycloud.mobileassistant.http.entity.UpdateInfoEntity;
import com.kaycloud.mobileassistant.utils.PackageUtils;

import java.io.File;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * @author kaycloud
 *         <p>
 *         * 主页面逻辑：
 *         1.获取版本名并显示
 *         2.检查更新
 */
public class SplashActivity extends AppCompatActivity {

    public static final int PERMISSION_WRITE_STORAGE = 1;
    public static final int RESULT_START_INSTALLER = 10;

    private TextView mTvVersionName;
    private ImageView mIvSplashBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mIvSplashBg = (ImageView) findViewById(R.id.iv_splash);
        mTvVersionName = (TextView) findViewById(R.id.tv_versionName);

        //背景渐变动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
        alphaAnimation.setDuration(2000);
        mIvSplashBg.startAnimation(alphaAnimation);

        // 获取版本名显示在splash页面
        mTvVersionName.setText("v" + PackageUtils.getVersionName(this));
        // 检查更新
        checkUpdate();
    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
        // 1.请求服务器数据
        RetrofitClient.getInstance().getUpdateInfo(new Observer<UpdateInfoEntity>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull UpdateInfoEntity updateInfoEntity) {
                // 2.请求成功，解析json数据，得到版本号
                int versionCode = updateInfoEntity.getVersionCode();
                String desc = updateInfoEntity.getDesc();
                String downloadUrl = updateInfoEntity.getUrl();
                // 是否检测到新版本
                if (versionCode > PackageUtils.getVersionCode(SplashActivity.this)) {
                    // 有更新，弹窗提示用户可以更新
                    showUpdateDialog(desc, downloadUrl);
                } else {
                    //无更新，进入主界面
                    enterHome();
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                enterHome();
            }

            @Override
            public void onComplete() {
//                enterHome();
            }
        });
    }

    /**
     * 显示可更新窗口
     *
     * @param desc 更新描述
     */
    private void showUpdateDialog(String desc, String downloadUrl) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("有更新了！！");
        dialog.setMessage(desc);
        dialog.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //请求权限
                if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SplashActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_STORAGE);
                } else {
                    // 下载新版本APK
                    downloadApk();
                }

            }

        });
        dialog.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 用户不更新，进入主界面
                enterHome();
            }
        });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_WRITE_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //得到了权限，进行下载
                downloadApk();
            } else {
                // 用户拒绝了，无法下载
                // 提示并进入主界面
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                enterHome();
            }
        }
    }

    /**
     * 下载新版本APK
     */
    private void downloadApk() {
        //显示更新进度条
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("正在下载中");
        progressDialog.setIndeterminate(false);
        progressDialog.show();

        DownloadProgressListener progressListener = new DownloadProgressListener() {
            @Override
            public void onProgress(long bytesRead, long contentLength, boolean done) {
                int progress = (int) ((bytesRead*100/contentLength));
                Log.d("SplashActivity", "progress:" + progress);
                progressDialog.setProgress(progress);
            }
        };

        //下载APK
        final File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "app.apk");
        RetrofitClient.downloadApk(progressListener, file, new Observer() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Object o) {

            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                Log.d("SplashActivity", e.getMessage());
            }

            @Override
            public void onComplete() {
                //发送给系统安装器进行安装
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                startActivityForResult(intent, RESULT_START_INSTALLER);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //不管是安装成功，还是用户取消安装，或者用户点了返回等等，都是进入主界面
        enterHome();
    }

    /**
     * 进入主界面
     */
    private void enterHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
