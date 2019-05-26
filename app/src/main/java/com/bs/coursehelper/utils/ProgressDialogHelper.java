package com.bs.coursehelper.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

/**
 * 抽取加载圈的辅助类
 *
 *
 * @author
 * @time 2018/5/26 0026 16:11
 * @version
 */

public class ProgressDialogHelper {

    private static final String TAG = "ProgressDialogHelper";

    private ProgressDialog pd;

    private Context mContext;

    public ProgressDialogHelper(Context context) {
        this.mContext = context;
        pd=  new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
    }

    /**
     * 展示加载圈
     *
     * @param title
     * @param msg
     */
    public void show(String title, String msg){
        Log.i(TAG, "show: ==" + pd.isShowing());
        if (pd != null && !pd.isShowing()) {
            pd.setTitle(title);
            pd.setMessage(msg);
            pd.show();
//            pd.show(mContext, title, msg);
        }
    }


    /**
     * 隐藏加载全
     *
     */
    public void dismiss(){
        Log.i(TAG, "dismiss: ==" + pd.isShowing());
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

}
