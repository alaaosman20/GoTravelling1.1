package com.weicong.gotravelling.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;
import com.weicong.gotravelling.R;

public class FlatButton extends TextView {
    public FlatButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public FlatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FlatButton(Context context) {
        super(context);
        init();
    }

    /**
     * 执行初始化操作
     */
    private void init() {
        setTextColor(getResources().getColor(R.color.green));
        setBackgroundResource(R.drawable.dialog_button);
        setGravity(Gravity.CENTER);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            setTextColor(getResources().getColor(R.color.green));
        } else {
            setTextColor(getResources().getColor(R.color.gray));
        }
    }
}
