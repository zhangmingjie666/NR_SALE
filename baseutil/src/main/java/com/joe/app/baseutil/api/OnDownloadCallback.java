package com.joe.app.baseutil.api;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.joe.app.baseutil.R;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

/**
 * Created by Joe on 2016/6/5.
 * Email-joe_zong@163.com
 */
public class OnDownloadCallback extends FileCallBack {
    private Context mContext;
    private AlertDialog progressDialog = null;
    private ProgressBar progressBar;
    private TextView txtvProgress;

    private boolean isShowProgress = false;


    public OnDownloadCallback(Context aContext, String filePath, String fileName){
        super(filePath, fileName);
        this.mContext = aContext;
        this.isShowProgress = false;
    }

    public OnDownloadCallback(Context aContext, String filePath, String fileName, boolean isShowProgress){
        super(filePath, fileName);
        this.mContext = aContext;
        this.isShowProgress = isShowProgress;
    }

    @Override
    public void onBefore(Request request) {
        super.onBefore(request);
        if(isShowProgress){
            showProgressDialog();
        }
    }

    @Override
    public void inProgress(float progress) {
        if(isShowProgress){
            if(progressBar!=null){
                progressBar.setProgress((int) (100 * progress));
            }
            if(txtvProgress!=null){
                txtvProgress.setText((int) (100 * progress)+"%");
            }
        }
    }

    @Override
    public void onResponse(File response) {
        cancelProgressDialog();
    }

    @Override
    public void onAfter() {
        super.onAfter();
        cancelProgressDialog();
    }


    @Override
    public void onError(Request request, Exception e) {
        cancelProgressDialog();
    }

    public void showProgressDialog(){
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_progress_view,null);
        progressBar = (ProgressBar)view.findViewById(R.id.progressbar);
        txtvProgress = (TextView)view.findViewById(R.id.txtvProgress);
        progressBar.setProgress(0);
        txtvProgress.setText(0+"%");
        if(progressDialog == null){
            AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
            builder.setTitle("正在下载...");
            builder.setView(view);
            progressDialog = builder.create();
        }
        progressDialog.cancel();
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void cancelProgressDialog(){
        if(progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.cancel();
        }
    }
}
