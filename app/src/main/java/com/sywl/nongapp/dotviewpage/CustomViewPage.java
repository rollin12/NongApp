package com.sywl.nongapp.dotviewpage;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * <pre>
 * author: yun.wang
 * Time: 2017/04/06
 * Description: ${CustomViewPage}
 * Version: ${1.0}
 * </pre>
 */

public class CustomViewPage extends ViewPager {


    public CustomViewPage(Context context) {
        super(context);
    }

    public CustomViewPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
}
