package com.joe.app.outbound.data;

import android.content.Context;
import android.util.Log;

import com.joe.app.baseutil.api.Client;
import com.joe.app.baseutil.api.OnRequestCallback;
import com.joe.app.baseutil.util.UIHelper;
import com.joe.app.outbound.AppConstant;
import com.joe.app.outbound.data.listener.OnNetRequest;
import com.squareup.okhttp.Request;

import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by Joe on 2016/6/7.
 * Email-joe_zong@163.com
 */
public class Api {
    private Context mContext;
    private OnRequestCallback callback;
    private Client mClient;

    public Api(Context aContext, final OnNetRequest listener){
        this.mContext = aContext;
        this.mClient = Client.getInstance(SharedPreference.getHost());
        callback = new OnRequestCallback(mContext,listener.isShowLoading(),listener.getLoadingText()) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                Log.i("Response",response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.optBoolean("status",false)){
                        listener.onSuccess(response);
                    }else{
                        String errorMessage = jsonObject.optString("result");
                        Log.e("Response", errorMessage);
                        UIHelper.showLongToast(mContext, errorMessage);
                        listener.onFail();
                    }
                }catch (Exception e){
                    UIHelper.showLongToast(mContext,"Json解析错误");
                    listener.onFail();
                }
            }

            @Override
            public void onError(Request request, Exception e) {
                super.onError(request, e);
                if(e instanceof TimeoutException){
                    UIHelper.showLongToast(mContext,"请求超时");
                    listener.onFail();
                }else if(e instanceof ConnectException){
                    UIHelper.showLongToast(mContext,"请求出错，检查网络是否正常");
                    listener.onFail();
                }else{
                    UIHelper.showLongToast(mContext,"您输入的不正确，请确认后重新输入");
                    listener.onFail();
                }
            }
        };
    }

    /**
     * 获取销售发货单
     */
    public final static String GET_SALESEND = "salesend/list";
    public void getSaleSendOrderInfoList(){
        mClient.get(GET_SALESEND, null, callback);
    }

    /**
     * 获取员工信息
     */
    public final static String GET_EMPLOYEE = "employee/list";
    public void getEmployeeInfo(){
        mClient.get(GET_EMPLOYEE,null,callback);
    }

    /**
     * 根据销售发货单据id获取该订单的所有发货码单的列表
     */
    public final static String GET_PACKAGE_LIST = "salesend/listpack";
    public void getPackageList(String order_id){
        Map<String,String> params = new HashMap<>();
        params.put("order_id",order_id);
        mClient.get(GET_PACKAGE_LIST, params, callback);
    }

    /**
     * 扫码出库操作
     * "order_id" : 1,                 // 单据id
       "employee_id" : 1,              // 员工id
       "barcode" : "6912111120373",    // 条码 6913111538101 / 6913111538132 / 6913111538154
        "bale" : '1#'                   // 包号
     */
    public final static String POST_ADD_PACKAGE = "salesend/addpack";
    public void addPackage(String order_id, String employee_id, String barcode, String bale){
        Map<String,String> params = new HashMap<>();
        params.put("order_id",order_id);
        params.put("employee_id",employee_id);
        params.put("barcode",barcode);
        params.put("bale",bale);
        mClient.post(POST_ADD_PACKAGE,params,callback);
    }

    /**
     * 删除出库条码操作
     */
    public final static String POST_DELETE_PACKAGE = "salesend/delpack";
    public void deletePackage(String id){
        Map<String,String> params = new HashMap<>();
        params.put("id",id);
        mClient.post(POST_DELETE_PACKAGE,params,callback);
    }
    /**
     * APP升级信息
     */
    public void upgrade() {
        String method = "upgrade/info";
        Map<String, String> params = new HashMap<>();
        mClient.get(method,params, callback);
    }

    /**
     * 获取零售发货单
     */
    public final static String GET_RETAILSEND = "saleretail/list";
    public void getRetailList(String search){
        Map<String, String> params = new HashMap<>();
        params.put("code",search);
        mClient.get(GET_RETAILSEND, params, callback);
    }

    /**
     * 用户登录
     */

    public final static String POST_USERNAME = "login/index";

    public void userLogin(String username, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        mClient.post(POST_USERNAME, params, callback);
    }

    /**
     * 验证token
     */

    public final static String POST_TOKEN = "login/validate";

    public void validate(String tokenId) {
        Map<String, String> params = new HashMap<>();
        params.put("token", tokenId);
        mClient.post(POST_TOKEN, params, callback);
    }

    /**
     * 获取客户信息
     */
    public final static String GET_COMAPNY = "company/list";

    public void getCompanyList(String type,String search) {
        Map<String, String> params = new HashMap<>();
        params.put("type", type);
        params.put("company", search);
        mClient.get(GET_COMAPNY, params, callback);
    }

    /**
     * 添加零售单
     */

    public final static String POST_RETAIL = "saleretail/add";

    public void addRetail(String company_) {
        Map<String, String> params = new HashMap<>();
        params.put("company_", company_);
        mClient.post(POST_RETAIL, params, callback);
    }
    /**
     * 扫码出库零售单
     */
    public final static String POST_ADD_RETAIL = "saleretail/additem";
    public void addPack(String order_id,String barcode ){
        Map<String,String> params = new HashMap<>();
        params.put("retail_id",order_id);
        params.put("barcode",barcode);
        mClient.post(POST_ADD_RETAIL,params,callback);
    }
    /**
     * 扫码出库零售单明细
     */
    public final static String POST_ADD_RETAILITEM = "saleretail/item";
    public void addPackageItem(String id ){
        Map<String,String> params = new HashMap<>();
        params.put("id",id);
        mClient.get(POST_ADD_RETAILITEM,params,callback);
    }

}
