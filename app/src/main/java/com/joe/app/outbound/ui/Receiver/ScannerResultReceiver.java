package com.joe.app.outbound.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.joe.app.baseutil.util.EventBusUtil;
import com.joe.app.baseutil.util.UIHelper;
import com.joe.app.outbound.data.event.ScanResultEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Joe on 2016/6/9.
 * Email-joe_zong@163.com
 */
public class ScannerResultReceiver extends BroadcastReceiver{
    private static final String RES_ACTION = "android.intent.action.SCANRESULT";
    private final static String SCAN_ACTION = "scan.rcv.message";//device2

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(RES_ACTION)){
            //获取扫描结果
            //扫描结果在在意图中是一个String类型的Extra， 名为“value”
            final String scanResult = intent.getStringExtra("value");
            EventBus.getDefault().post(new ScanResultEvent(scanResult));
//            UIHelper.showLongToast(context,scanResult);
//            tvScanResult.append(scanResult);
//
//            if (isContinue){	//是否连续扫描.在这个示例程序里面，在keyUp里面停止扫描
//                Log.i("123", "hello world asdfasdfasdfas");
//                scanner.scan_stop();
//                try {
//                    Thread.sleep(100);//这里的时间间隔建议是200ms到300ms之间，因为蜂鸣声和震动也需要一定的时间
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                scanner.scan_start();
//            }
        }else if(intent.getAction().equals(SCAN_ACTION)){
            byte[] barocode = intent.getByteArrayExtra("barocode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte temp = intent.getByteExtra("barcodeType", (byte) 0);
            String barcodeStr = new String(barocode, 0, barocodelen);
            EventBus.getDefault().post(new ScanResultEvent(barcodeStr.replace("*","")));
        }
    }
}
