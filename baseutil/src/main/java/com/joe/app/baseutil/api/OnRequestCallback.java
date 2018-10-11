package com.joe.app.baseutil.api;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.ProgressBar;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;

/**
 * Created by Joe on 2016/5/19.
 * Email-joe.zong@xiaoniubang.com
 */
public abstract class OnRequestCallback extends Callback<String> {
    private boolean showLoading = false;
    private ProgressDialog mProgressDialog;
    private String showText = "正在加载......";
    public Context mContext;
    private boolean isShowProgress = false;
    private AlertDialog progressDialog = null;
    private ProgressBar progressBar;


    public OnRequestCallback(Context mContext) {
        this.mContext = mContext;
        this.isShowProgress = false;
    }

    public OnRequestCallback(Context mContext, boolean showLoading, String showText) {
        this.mContext = mContext;
        this.showLoading = showLoading;
        this.showText = showText;
    }

//    public OnRequestCallback(Context mContext, b) {
//        this.mContext = mContext;
//        this.showLoading = false;
//        this.isShowProgress = isShowProgress;
//    }

    @Override
    public String parseNetworkResponse(Response response) throws IOException
    {
        return response.body().string();
    }


    @Override
    public void onBefore(Request request) {
        super.onBefore(request);
        if (showLoading) {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(mContext);
            } else {
                mProgressDialog.cancel();
            }
            mProgressDialog.setMessage(showText);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

//        if(isShowProgress){
//            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_progress_view,null);
//            progressBar = (ProgressBar)view.findViewById(R.id.progressbar);
//            progressBar.setProgress(0);
//            if(progressDialog == null){
//                AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
//                builder.setTitle("正在下载...");
//                builder.setView(view);
//                progressDialog = builder.create();
//            }
//            progressDialog.cancel();
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//        }
    }

    @Override
    public void onResponse(String response) {
        if (showLoading && mProgressDialog != null
                && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    @Override
    public void onError(Request request, Exception e) {
        if (showLoading && mProgressDialog != null
                && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    @Override
    public void onAfter() {
        super.onAfter();
        if (showLoading && mProgressDialog != null
                && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }


//
//        if(isShowProgress&&progressDialog!=null&&progressDialog.isShowing()){
//            progressDialog.cancel();
//        }
    }

//    @Override
//    public void inProgress(float progress) {
//        super.inProgress(progress);
//        if(progressBar!=null){
//            progressBar.setProgress((int) (100 * progress));
//        }
//    }
}
