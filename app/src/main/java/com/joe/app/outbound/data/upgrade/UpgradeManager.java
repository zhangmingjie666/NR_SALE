package com.joe.app.outbound.data.upgrade;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.joe.app.baseutil.util.JSONUtils;
import com.joe.app.baseutil.util.UIHelper;
import com.joe.app.outbound.AppConstant;
import com.joe.app.outbound.MyApplication;
import com.joe.app.outbound.data.Api;
import com.joe.app.outbound.data.SharedPreference;
import com.joe.app.outbound.data.helper.NetworkHelper;
import com.joe.app.outbound.data.listener.OnNetRequest;
import com.joe.app.outbound.data.model.VersionInfo;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 升级管理，单例设计
 */
public class UpgradeManager {

    private Context appContext;
    private DownloadManager downloader;

    /**
     * 是否初始化
     */
    private String uriDownload;

    /**
     * 服务器返回的版本信息
     */
    private VersionInfo.Data versionInfo;

    /**
     * 下载apk文件绝对路径
     */
    private String downloadApkPath;

    private ScheduledExecutorService scheduledExecutorService;

    private DownloadChangeObserver downloadObserver;

    private DownloadReceiver downloaderReceiver;
    private NotificationClickReceiver notificationClickReceiver;

    public UpgradeManager() {
        appContext = MyApplication.getContext();
        downloaderReceiver = new DownloadReceiver();
        notificationClickReceiver = new NotificationClickReceiver();
    }

    /**
     * 开始更新
     */
    public void startUpgradeVersion() {

        // 首先确定下载apk文件的绝对路径
        final String apkName = downloadTempName();

        String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        dirPath = dirPath.endsWith(File.separator) ? dirPath : dirPath + File.separator;
        downloadApkPath = dirPath + apkName;
        Api api = new Api(appContext, new OnNetRequest(appContext, false, "正在加载...") {

            @Override
            public void onSuccess(String msg) {
                VersionInfo response = JSONUtils.fromJson(msg, VersionInfo.class);
                if (response != null && response.result != null) {
                    versionInfo = response.result;

                    if (comparedWithCurrentPackage(versionInfo)) {

                        if (NetworkHelper.getNetWorkType(appContext) == NetworkHelper.NETWORK_CLASS_UNKNOWN) {
                            UIHelper.showShortToast(appContext, "无法连接网络，请您检查后重试");
                        } else {
                            downloadApk();
                        }
                    } else {
                        currentIsLatest();
                        UIHelper.showShortToast(appContext, "已经是最新版本");
                    }
                }
            }

            @Override
            public void onFail() {
            }
        });

        api.upgrade();
    }

    /**
     * 初始化注册广播
     *
     * @param context
     */
    public void init(Context context) {
        appContext = context.getApplicationContext();
        appContext.registerReceiver(downloaderReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        appContext.registerReceiver(notificationClickReceiver, new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));
    }

    /**
     * 反初始化销毁广播
     */
    public void unInit() {
        if (downloaderReceiver != null) {
            appContext.unregisterReceiver(downloaderReceiver);
        }
        if (notificationClickReceiver != null) {
            appContext.unregisterReceiver(notificationClickReceiver);
        }
        unregisterContentObserver();

        appContext = null;
        downloadObserver = null;
    }

    /**
     * 版本号比较
     *
     * @return true表示需要更新，false表示不需要更新
     */
    private boolean comparedWithCurrentPackage(VersionInfo.Data version) {
        if (version == null) {
            return false;
        }

        int currentVersionCode = 0;
        try {
            PackageInfo pkg = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0);
            currentVersionCode = pkg.versionCode;
        } catch (PackageManager.NameNotFoundException exp) {
            exp.printStackTrace();
        }

        return Integer.parseInt(version.versionCode) > currentVersionCode;
    }

    /**
     * 已经是最新版本
     */
    private void currentIsLatest() {
        // 要检查本地是否有安装包，有则删除
        File apkFile = new File(downloadApkPath);
        if (apkFile.exists()) {
            apkFile.delete();
        }
    }

    /**
     * 注册ContentObserver
     */
    private void registerContentObserver() {
        if (downloadObserver != null) {
            appContext.getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), false, downloadObserver);
        }
    }

    /**
     * 注销ContentObserver
     */
    private void unregisterContentObserver() {
        if (downloadObserver != null) {
            appContext.getContentResolver().unregisterContentObserver(downloadObserver);
        }
    }

    /**
     * DownloadManager下载apk安装包
     */
    private void downloadApk() {
        // 先检查本地是否已经有需要升级版本的安装包，如有就不需要再下载
        File targetApkFile = new File(downloadApkPath);
        if (targetApkFile.exists()) {
            PackageManager pm = appContext.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(downloadApkPath, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                String versionCode = String.valueOf(info.versionCode);
                // 比较已下载到本地的apk安装包，与服务器上apk安装包的版本号是否一致
                if (versionInfo.versionCode.equals(versionCode)) {
                    // 弹出框提示用户安装
                    downLoadHandler.obtainMessage(WHAT_ID_INSTALL_APK, downloadApkPath).sendToTarget();
                    return;
                }
            }
        }

        // 要检查本地是否有安装包，有则删除重新下
        File apkFile = new File(downloadApkPath);
        if (apkFile.exists()) {
            apkFile.delete();
        }

        if (downloader == null) {
            downloader = (DownloadManager) appContext.getSystemService(Context.DOWNLOAD_SERVICE);
        }

        Query query = new Query();

        long downloadTaskId = SharedPreference.getDownloadTaskId();
        query.setFilterById(downloadTaskId);
        Cursor cur = downloader.query(query);

        // 检查下载任务是否已经存在
        if (cur != null && cur.moveToFirst()) {
            int columnIndex = cur.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cur.getInt(columnIndex);
            if (DownloadManager.STATUS_PENDING == status || DownloadManager.STATUS_RUNNING == status || DownloadManager.STATUS_PAUSED == status) {
                cur.close();
                return;
            }
        }
        if (cur != null) {
            cur.close();
        }

        if (downloadObserver == null) {
            downloadObserver = new DownloadChangeObserver();
        }
        registerContentObserver();

        Request task = new Request(Uri.parse(versionInfo.downloadUrl));
        //定制Notification的样式
        String title = "最新版本:" + versionInfo.version;
        task.setTitle(title);
        task.setDescription(versionInfo.versionDesc);

        // 如果我们希望下载的文件可以被系统的Downloads应用扫描到并管理，我们需要调用Request对象的setVisibleInDownloadsUi方法，传递参数true
        task.setVisibleInDownloadsUi(true);

        // 设置是否允许手机在漫游状态下下载
        task.setAllowedOverRoaming(false);

        // 限定在WiFi、手机流量下进行下载
        task.setAllowedNetworkTypes(Request.NETWORK_WIFI | Request.NETWORK_MOBILE);

        task.setMimeType("application/vnd.android.package-archive");

        // 在通知栏通知下载中和下载完成下载完成后该Notification才会被显示3.0(11)以后才有该方法。
        // 在下载过程中通知栏会一直显示该下载的Notification，在下载完成后该Notification会继续显示，直到用户点击该Notification或者消除该Notification
        task.setNotificationVisibility(Request.VISIBILITY_HIDDEN);
        task.allowScanningByMediaScanner();

        // 可能无法创建Download文件夹，如无sdcard情况，系统会默认将路径设置为/data/data/com.android.providers.downloads/cache/xxx.apk
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String apkName = downloadTempName();
            task.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apkName);
        }

        downloadTaskId = downloader.enqueue(task);
        SharedPreference.setDownloadTaskId(downloadTaskId);

        UIHelper.showShortToast(appContext, "正在后台下载更新");
    }

//    public static String downloadTempName() {
//        String apkName = "_temp@" + AppConstant.GUID + ".apk";
//        return apkName;
//    }
    public static String downloadTempName() {
        String apkName = "_temp@" + AppConstant.uuid + ".apk";
        return apkName;
    }

    /**
     * 安装apk 通过广播来监听安装完成再删除apk文件
     */
    private void installAPKFile() {
        if (TextUtils.isEmpty(uriDownload)) {
            return;
        }

        File apkFile = new File(Uri.parse(uriDownload).getPath());
        if (!apkFile.exists()) {
            return;
        }

        Intent installIntent = new Intent();
        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.setAction(Intent.ACTION_VIEW);

        Uri apkFileUri;
        // 在24及其以上版本，解决崩溃异常：
        // android.os.FileUriExposedException: file:///storage/emulated/0/xxx exposed beyond app through Intent.getData()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            apkFileUri = FileProvider.getUriForFile(appContext, BuildConfig.APPLICATION_ID + ".provider", apkFile);
        } else {
//            apkFileUri = Uri.fromFile(apkFile);
        }
        apkFileUri = Uri.fromFile(apkFile);
        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        installIntent.setDataAndType(apkFileUri, "application/vnd.android.package-archive");
        try {
            appContext.startActivity(installIntent);
        } catch (ActivityNotFoundException e) {
        }
    }

    /**
     * 通过query查询下载状态，包括已下载数据大小，总大小，下载状态
     *
     * @param downloadId 下载任务id
     */
    private int[] getBytesAndStatus(long downloadId) {
        int[] bytesAndStatus = new int[]{
                -1, -1, 0
        };
        Query query = new Query().setFilterById(downloadId);
        Cursor cursor = null;
        try {
            cursor = downloader.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                //已经下载文件大小
                bytesAndStatus[0] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                //下载文件的总大小
                bytesAndStatus[1] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return bytesAndStatus;
    }

    /**
     * 发送Handler消息更新进度和状态
     */
    private void updateProgress() {
        long downloadTaskId = SharedPreference.getDownloadTaskId();
        int[] bytesAndStatus = getBytesAndStatus(downloadTaskId);
        downLoadHandler.sendMessage(downLoadHandler.obtainMessage(WHAT_ID_DOWNLOAD_INFO, bytesAndStatus[0], bytesAndStatus[1]));
    }

    private final int WHAT_ID_INSTALL_APK = 1;
    private final int WHAT_ID_DOWNLOAD_INFO = 2;

    private Handler downLoadHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_ID_INSTALL_APK) {
                uriDownload = (String) msg.obj;
                installAPKFile();
            } else if (msg.what == WHAT_ID_DOWNLOAD_INFO) {
                // 被除数可以为0，除数必须大于0
                if (msg.arg1 >= 0 && msg.arg2 > 0) {
                    int progress = (int) (msg.arg1 / (float) msg.arg2 * 100);
                    String title = "最新版本:" + versionInfo.version;
                    String versionDesc = "";
                    if (!TextUtils.isEmpty(versionInfo.versionDesc)) {
                        versionDesc = versionInfo.versionDesc.replace("\n", "");
                    }

                    DownloadNotificationHelper.sendDefaultNotice(appContext, title, versionDesc, progress);
                }
            }
        }
    };

    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    /**
     * 监听下载进度
     */
    private class DownloadChangeObserver extends ContentObserver {

        DownloadChangeObserver() {
            super(downLoadHandler);
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        }

        /**
         * 当所监听的Uri发生改变时，就会回调此方法
         *
         * @param selfChange 此值意义不大, 一般情况下该回调值false
         */
        @Override
        public void onChange(boolean selfChange) {
            scheduledExecutorService.scheduleAtFixedRate(progressRunnable, 0, 1, TimeUnit.SECONDS);
        }
    }

    /**
     * 下载完成的广播
     */
    class DownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (downloader == null) {
                return;
            }
            long completeId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            long downloadTaskId = SharedPreference.getDownloadTaskId();
            if (completeId != downloadTaskId) {
                return;
            }

            Query query = new Query();
            query.setFilterById(downloadTaskId);
            Cursor cur = downloader.query(query);
            if (cur == null || !cur.moveToFirst()) {
                return;
            }

            int columnIndex = cur.getColumnIndex(DownloadManager.COLUMN_STATUS);
            if (DownloadManager.STATUS_SUCCESSFUL == cur.getInt(columnIndex)) {
                // 下载完成这里也要通知刷新消息通知栏
                updateProgress();

                String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                downLoadHandler.obtainMessage(WHAT_ID_INSTALL_APK, uriString).sendToTarget();
            }

            // 下载任务已经完成，清除
            SharedPreference.deleteDownloadTaskId();
            cur.close();
        }
    }

    /**
     * 点击通知栏下载项目，下载完成前点击都会进来，下载完成后点击不会进来。
     */
    public class NotificationClickReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            long[] completeIds = intent.getLongArrayExtra(
                    DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);

            // 正在下载的任务ID
            long downloadTaskId = SharedPreference.getDownloadTaskId();
            if (completeIds == null || completeIds.length <= 0) {
                openDownloadsPage(appContext);
                return;
            }

            for (long completeId : completeIds) {
                if (completeId == downloadTaskId) {
                    openDownloadsPage(appContext);
                    break;
                }
            }
        }

        /**
         * Open the Activity which shows a list of all downloads.
         *
         * @param context 上下文
         */
        private void openDownloadsPage(Context context) {
            Intent pageView = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
            pageView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(pageView);
        }
    }
}
