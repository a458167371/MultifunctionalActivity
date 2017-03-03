package com.leibown.library;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leibown.library.utils.DisplayUtil;

public abstract class MultifunctionalActivity extends Activity {

    private View mContentView;
    private LinearLayout mLlTittleBar;


    private TextView statusTextView;


    //用于装载Loading,retry等View的容器
    private RelativeLayout mStatusContainer;
    //当前状态是否是重试状态
    private boolean isShowReTryView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("leibown", "进入BaseActivity11111111111111111111");

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout containerView = (LinearLayout) inflater.inflate(R.layout.activity_base, null);
        setContentView(containerView);

        //用来填充Android版本在4.4以上的状态栏
        View statusBar = findViewById(R.id.status_bar);
        mLlTittleBar = (LinearLayout) findViewById(R.id.ll_tittle_bar);

        mContentView = inflater.inflate(getResId(), null);
        containerView.addView(mContentView);
        LinearLayout.LayoutParams childParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
        mContentView.setLayoutParams(childParams);

        if (isNeedStatusView()) {
            mStatusContainer = (RelativeLayout) inflater.inflate(R.layout.status_layout, null);
            containerView.addView(mStatusContainer);
            mStatusContainer.setLayoutParams(childParams);
            mStatusContainer.setVisibility(View.GONE);
            statusTextView = (TextView) mStatusContainer.findViewById(R.id.tv_status_content);
            mStatusContainer.findViewById(R.id.ll_status_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isShowReTryView)
                        reTry();
                }
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decorView = getWindow().getDecorView();
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
                Log.e("leibown", "进入BaseActivity2222222222222222222222");
            } else {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                Log.e("leibown", "进入BaseActivity3333333333333333333333");
            }
            //如果Android版本大于4.4，说明状态栏就可以被透明，我们自己的布局就可以放到状态栏之下
            //我们把自定义的ActionBar的高度增高
            ViewGroup.LayoutParams params = mLlTittleBar.getLayoutParams();
            params.height = DisplayUtil.getBarHeight(getApplicationContext()) + DisplayUtil.dip2px(getApplicationContext(), 58);
            mLlTittleBar.requestLayout();
            //把用于填充状态栏的View高度设置成跟状态栏一样的高度
            ViewGroup.LayoutParams params2 = statusBar.getLayoutParams();
            params2.height = DisplayUtil.getBarHeight(getApplicationContext());
            statusBar.requestLayout();
        }
        bindViews(savedInstanceState);
    }

    //是否需要装载能显示各种状态的ViewGroup
    public abstract boolean isNeedStatusView();

    public abstract int getResId();

    //子类初始化view的方法
    public abstract void bindViews(Bundle savedInstanceState);


    public void setActionBar(View actionBar) {
        mLlTittleBar.setVisibility(View.VISIBLE);
        mLlTittleBar.addView(actionBar);
    }

    public void setActionBarBackgroudColor(int color) {
        mLlTittleBar.setBackgroundColor(color);
    }

    public void setActionBarBackgroudResource(int imgRes){
        mLlTittleBar.setBackgroundResource(imgRes);
    }

    /**
     * 重新请求网络数据，如果首次加载就出现加载失败，界面显示加载失败，在点击“加载失败，请重试”时执行的方法
     */
    public void reTry() {
    }


    /**
     * 自定义Loading,ReTry,Empty这种状态时显示的View
     *
     * @param view 能装各种状态的View
     */
    protected void setStatusView(View view) {
        if (isNeedStatusView()) {
            mStatusContainer.removeAllViews();
            mStatusContainer.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isShowReTryView)
                        reTry();
                }
            });
        }
    }

    /**
     * 自定义Loading,ReTry,Empty这种状态时显示的View
     *
     * @param view          能装各种状态的View
     * @param textViewResId 来标识各种状态的TextView
     */
    protected void setStatusView(View view, int textViewResId) {
        if (isNeedStatusView()) {
            mStatusContainer.removeAllViews();
            mStatusContainer.addView(view);
            statusTextView = (TextView) mStatusContainer.findViewById(textViewResId);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isShowReTryView)
                        reTry();
                }
            });
        }
    }

    /**
     * 获取loading,retry,empty状态下界面中的ImageView
     */
    protected ImageView getStatusImageView() {
        if (!isNeedStatusView()) {
            return null;
        }
        return (ImageView) mStatusContainer.findViewById(R.id.iv_status_img);
    }

    private String loadingText = "", emptyText = "", reTryText = "";

    protected void setLoadingText(String loadingText) {
        this.loadingText = loadingText;
    }

    protected void setEmptyText(String emptyText) {
        this.emptyText = emptyText;
    }

    protected void setReTryText(String reTryText) {
        this.reTryText = reTryText;
    }

    private void setStatusTextViewText(String statusContent) {
        if (statusTextView != null)
            statusTextView.setText(statusContent);
    }

    /**
     * 显示Loading状态
     */
    protected void showLoading() {
        if (isNeedStatusView()) {
            mContentView.setVisibility(View.GONE);
            mStatusContainer.setVisibility(View.VISIBLE);
            if ("".equals(loadingText)) {
                loadingText = getResources().getString(R.string.loading_content);
            }
            setStatusTextViewText(loadingText);
            isShowReTryView = false;
        }
    }

    /**
     * 显示Empty状态
     */
    protected void showEmpty() {
        if (isNeedStatusView()) {
            mContentView.setVisibility(View.GONE);
            mStatusContainer.setVisibility(View.VISIBLE);
            if ("".equals(emptyText)) {
                emptyText = getResources().getString(R.string.empty_content);
            }
            setStatusTextViewText(emptyText);
            isShowReTryView = false;
        }
    }

    /**
     * 显示Retry状态
     */
    protected void showRetry() {
        if (isNeedStatusView()) {
            mContentView.setVisibility(View.GONE);
            mStatusContainer.setVisibility(View.VISIBLE);
            if ("".equals(reTryText)) {
                reTryText = getResources().getString(R.string.retry_content);
            }
            setStatusTextViewText(reTryText);
            isShowReTryView = true;
        }
    }

    /**
     * 显示内容
     */
    protected void showContent() {
        if (isNeedStatusView()) {
            mContentView.setVisibility(View.VISIBLE);
            mStatusContainer.setVisibility(View.GONE);
            isShowReTryView = false;
        }
    }

    public Context getContext() {
        return this;
    }
}
