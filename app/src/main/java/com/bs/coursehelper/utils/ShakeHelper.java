package com.bs.coursehelper.utils;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.bs.coursehelper.R;


/**
 */

public class ShakeHelper {

    /**
     * edittext震动的辅助类
     *
     * @param editText
     */
    public static void shake(EditText editText){
        Animation shake = AnimationUtils.loadAnimation(editText.getContext(), R.anim.shake);
        editText.startAnimation(shake);
    }
}
