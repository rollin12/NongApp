package com.sywl.nongapp.dotviewpage;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.sywl.nongapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * author: yun.wang
 * Time: 2017/04/06
 * Description: ${Dot image view pager for banners}
 * Version: ${1.1}
 * </pre>
 */

public class CustomDotGroup extends FrameLayout {
    private final String TAG = CustomDotGroup.class.getSimpleName();

    //banner imageviews
    public List<ImageView> myTextBannerImageViews = new ArrayList<ImageView>();
    public List<String> myWebImagePageList;
    //dot tips
    private ImageView[] tips;
    //views
    private CustomViewPage myViewPage;
    private ViewGroup dotGroup;
    //click listener
    private OnSingleTouchListener onSingleTouchListener;
    //auto slide cycle
    private boolean isAutoSlide = false;
    //context
    public Context myContext;

    //请求更新显示的View。
    protected static final int MSG_UPDATE_IMAGE = 1;
    //请求暂停轮播。
    protected static final int MSG_KEEP_SILENT = 2;
    //请求恢复轮播。
    protected static final int MSG_BREAK_SILENT = 3;
    /**
     * 记录最新的页号，当用户手动滑动时需要记录新页号，否则会使轮播的页面出错。 例如当前如果在第一页，本来准备播放的是第二页，而这时候用户滑动到了末页，
     * 则应该播放的是第一页，如果继续按照原来的第二页播放，则逻辑上有问题。
     */
    protected static final int MSG_PAGE_CHANGED = 4;

    protected static final int MSG_UPDATE_LOCATION = 5;
    // 轮播间隔时间
    protected static final long MSG_DELAY = 5000;

    public CustomDotGroup(Context context) {
        super(context);
        myContext = context;
        initView();
    }

    public CustomDotGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        myContext = context;
        initView();
    }

    public void initView() {
        View parent = LayoutInflater.from(myContext).inflate(R.layout.layout_dotviewpage, this);

        dotGroup = (ViewGroup) parent.findViewById(R.id.viewGroup);
        myViewPage = (CustomViewPage) parent.findViewById(R.id.viewPager_Home);
    }

    /**
     * must called in ui thread
     *
     * @param isAutoSlide
     * @param imageResources
     */
    public void showWebImage(boolean isAutoSlide, List<String> imageResources) {
        if (imageResources == null || imageResources.size() <= 0) {
            return;
        }
        this.isAutoSlide = isAutoSlide;
        myWebImagePageList = imageResources;
        int resourceSize = myWebImagePageList.size();

        //create dot tips
        tips = new ImageView[resourceSize];
        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(myContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
            tips[i] = imageView;

            imageView.setBackgroundResource(R.drawable.selector_dot_indicator);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            layoutParams.topMargin = 10;
            layoutParams.bottomMargin = 10;
            dotGroup.addView(imageView, layoutParams);
        }
        setDotImageBackground(0);

        if(myTextBannerImageViews == null)
        {
            myTextBannerImageViews = new ArrayList<ImageView>();
        }
        //create imageview
        for (int i = 0; i < myWebImagePageList.size(); i++) {
            ImageView imageView = new ImageView(myContext);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            myTextBannerImageViews.add(imageView);

            //load image by glide
            Glide.with(myContext).load(myWebImagePageList.get(i))
                    .placeholder(R.drawable.image_view_page_default)
                    .error(R.drawable.image_view_page_default)
                    .into(imageView);

        }

        // load Adapter
        myViewPage.setAdapter(new MyAdapter());

        //view pagelistener
        myViewPage.addOnPageChangeListener(myPageChangeListener);

        if(isAutoSlide)
        {
            autoSlideHandler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
        }
    }

    ViewPager.OnPageChangeListener myPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Log.d(TAG, "onPageSelected, postion=" + position);
//            autoSlideHandler.sendMessage(Message.obtain(autoSlideHandler, MSG_PAGE_CHANGED, position,
//                    0));
            setDotImageBackground(position % myWebImagePageList.size());
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            Log.d(TAG, "onPageScrollStateChanged, state= " + state);
            if(!isAutoSlide)
            {
                return;
            }
            switch (state) {
                case ViewPager.SCROLL_STATE_DRAGGING://1
                    autoSlideHandler.sendEmptyMessage(MSG_KEEP_SILENT);
                    break;
                case ViewPager.SCROLL_STATE_IDLE://0
                    autoSlideHandler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case ViewPager.SCROLL_STATE_SETTLING://2
                    break;
                default:
                    break;
            }
        }
    };

    private void setDotImageBackground(int selectItems) {
        for (int i = 0; i < tips.length; i++) {
            tips[i].setEnabled(i == selectItems);
//            if (i == selectItems) {
//                tips[i].setEnabled(true);
////                tips[i].setBackgroundResource(R.drawable.dot_indicator_focused);
//            } else {
//                tips[i].setEnabled(false);
////                tips[i].setBackgroundResource(R.drawable.dot_indicator);
//            }
        }
    }

    //auto slide cycle handler
    private Handler autoSlideHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "receive message = " + msg.what);
            if(!isAutoSlide)
            {
                return;
            }
            // 检查消息队列并移除未发送的消息，这主要是避免在复杂环境下消息出现重复等问题。
            if (autoSlideHandler.hasMessages(MSG_UPDATE_IMAGE)) {
                Log.d(TAG, "Home Page Handler removeMessages = " + msg.what);
                autoSlideHandler.removeMessages(MSG_UPDATE_IMAGE);
            }
            switch (msg.what) {
                case MSG_UPDATE_IMAGE://1
                    myViewPage.setCurrentItem(myViewPage.getCurrentItem() + 1);
                    // 准备下次播放
                    // handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_KEEP_SILENT://2
                    // 只要不发送消息就暂停了
                    break;
                case MSG_BREAK_SILENT://3
                    autoSlideHandler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_PAGE_CHANGED://4
                    // 记录当前的页号，避免播放的时候页面显示不正确。
                    break;
                default:
                    break;
            }
        }
    };

    /**
     *
     */
    class MyAdapter extends PagerAdapter {
        public MyAdapter() {
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;//to cycle show
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {

        }

        @Override
        public Object instantiateItem(View container, int position) {
            ImageView imageView = myTextBannerImageViews.get(position % myWebImagePageList.size());
            try {
                final int index = position;

                ((ViewPager) container).addView(imageView, 0);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if(onSingleTouchListener != null)
                        {
                            onSingleTouchListener.onSingleTouch();
                        }
                        // Toast.makeText(Main_HomeActivity.this,
                        // "you pressed index = " + index,
                        // Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                // handler something
            }

            return imageView;
        }
    }

    public interface OnSingleTouchListener {
        public void onSingleTouch();
    }

    public void setOnSingleTouchListener(
            OnSingleTouchListener onSingleTouchListener) {
        this.onSingleTouchListener = onSingleTouchListener;
    }
}
