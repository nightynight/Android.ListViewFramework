package com.brokepal.listviewframework.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brokepal.listviewframework.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LoadListView extends ListView implements AbsListView.OnScrollListener {
    View header;//头部布局
    int headerHeight;
    boolean isLoadNewestDataReady=false;//是否准备好加载最新数据，当在最顶端按下时即为准备好了
    int state;//当前的状态
    final int NONE=0;//正常状态
    final int PULL=1;//提示下拉状态，显示下拉可以刷新
    final int RELESE=2;//提示释放状态，显示松开可以刷新
    final int REFLASHING=3;//提示正在刷新状态，显示正在刷新

    View footer;//底部布局
    int footerHeight;
    boolean isLoadMoreDataReady=false;//是否准备好加载更多数据，当在最底端按下时即为准备好了
    boolean isLoadingMoreData=false;//是否正在加载更多数据

    BaseAdapter adapter;
    ILoadListener iLoadListener;
    int startY;////手指按下时的Y坐标
    int scrollState;//ListView的当前滚动状态
    int firstVisibleItem;//第一个可见的Item
    int lastVisibleItem;//最后一个可见的Item
    int totalItemCount;//item的总数

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.lastVisibleItem=firstVisibleItem+visibleItemCount;
        this.totalItemCount=totalItemCount;
        this.firstVisibleItem=firstVisibleItem;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        this.scrollState=scrollState;
    }

    //调用setAdapter时把adapter传进来
    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
        super.setAdapter(adapter);//别忘了调用父类的setAdapter方法
    }

    //三个构造方法
    public LoadListView(Context context) {
        super(context);
        initView(context);
    }
    public LoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public LoadListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化界面，把header_layout和footer_layout加载到avtivity_main布局，通过设置topPadding和bottomPadding来隐藏header和footer
     * @param context
     */
    private void initView(Context context){
        LayoutInflater inflater=LayoutInflater.from(context);

        header=inflater.inflate(R.layout.header_layout,null);
        measureView(header);//测量
        headerHeight = header.getMeasuredHeight();//拿到子控件的高
        setTopPadding(-headerHeight);
        this.addHeaderView(header);

        footer=inflater.inflate(R.layout.footer_layout,null);
        measureView(footer);
        footerHeight=footer.getMeasuredHeight();
        setBottomPadding(-footerHeight);
        this.addFooterView(footer);

        this.setOnScrollListener(this); //设置滚动监听器
    }

    /**
     * 测量，通知父布局，占用的宽、高；
     * @param view
     */
    private void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int height;
        int tempHeight = p.height;
        if (tempHeight > 0) {
            height = MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.EXACTLY);
        } else {
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }

    /**
     * 设置header的topPadding，控制header的显示
     * @param topPadding
     */
    private void setTopPadding(int topPadding) {
        header.setPadding(header.getPaddingLeft(), topPadding,
                header.getPaddingRight(), header.getPaddingBottom());
        header.invalidate();
    }

    /**
     * 设置footer的bottomPadding，控制footer的显示
     * @param bottomPadding
     */
    private  void setBottomPadding(int bottomPadding) {
        footer.setPadding(footer.getPaddingLeft(), footer.getPaddingTop(),
                footer.getPaddingRight(), bottomPadding);
        footer.invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN://手指按下
                startY= (int) ev.getY();//获取手指按下的Y坐标
                if (firstVisibleItem==0){//下拉刷新
                    isLoadNewestDataReady=true;
                }
                else if (totalItemCount==lastVisibleItem){//上拉加载更多
                    isLoadMoreDataReady=true;
                }
                break;
            case MotionEvent.ACTION_MOVE://手指在屏幕上移动
                onMove(ev);
                break;
            case MotionEvent.ACTION_UP://手指离开屏幕
                if (state == RELESE) {
                    state = REFLASHING;
                    reflashViewByState();
                    // 加载最新数据；
                    LoadNewestAsyncTask loadNewestAsyncTask=new LoadNewestAsyncTask();
                    loadNewestAsyncTask.execute();
                } else if(state!=REFLASHING) {
                    state = NONE;
                    isLoadNewestDataReady = false;
                    reflashViewByState();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 移动过程操作
     * @param ev
     */
    private void onMove(MotionEvent ev){
        //下拉刷新
        if(isLoadNewestDataReady){
            int tempY= (int) ev.getY();
            int space=tempY-startY;//space>0说明向下滑动
            int topPadding=space-headerHeight;
            switch (state){
                case NONE:
                    if(space>0)
                        state=PULL;
                    reflashViewByState();
                    break;
                case PULL:
                    setTopPadding(topPadding);
                    if(space>headerHeight+200 && scrollState==SCROLL_STATE_TOUCH_SCROLL){
                        state=RELESE;
                        reflashViewByState();
                    }else if(space<=0){
                        state=NONE;
                        isLoadNewestDataReady=false;
                        reflashViewByState();
                    }
                    break;
                case RELESE:
                    if(space<headerHeight+200 && space>0){
                        state=PULL;
                        reflashViewByState();
                    }
                    break;
            }
        }

        //上拉获取更多数据
        if (isLoadMoreDataReady) {
            setBottomPadding(footer.getPaddingTop());
            int tempY = (int) ev.getY();
            int space = startY - tempY;//space>0说明向上滑动
            if (space > 100 && !isLoadingMoreData) {//如果上滑距离足够，且当前没有在加载数据
                isLoadingMoreData = true;
                //加载更多
                LoadMoreAsyncTask loadMoreAsyncTask=new LoadMoreAsyncTask();
                loadMoreAsyncTask.execute();
            }
        }
    }

    /**
     * 根据下拉当前状态，改变界面显示；
     */
    private void reflashViewByState() {
        TextView tip = (TextView) header.findViewById(R.id.tip);
        ImageView arrow = (ImageView) header.findViewById(R.id.arrow);
        ProgressBar progress = (ProgressBar) header.findViewById(R.id.progress);
        RotateAnimation anim = new RotateAnimation(0, 180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(500);
        anim.setFillAfter(true);
        RotateAnimation anim1 = new RotateAnimation(180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim1.setDuration(500);
        anim1.setFillAfter(true);
        switch (state) {
            case NONE:
                arrow.clearAnimation();
                setTopPadding(-headerHeight);
                break;

            case PULL:
                arrow.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                tip.setText("下拉可以刷新！");
                arrow.clearAnimation();
                arrow.setAnimation(anim1);
                break;
            case RELESE:
                arrow.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                tip.setText("松开可以刷新！");
                arrow.clearAnimation();
                arrow.setAnimation(anim);
                break;
            case REFLASHING:
                setTopPadding(50);
                arrow.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                tip.setText("正在刷新...");
                arrow.clearAnimation();
                break;
        }
    }

    //把获取最新数据放在AsyncTask中
    class LoadNewestAsyncTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            iLoadListener.onLoadNewest();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        /**
         * 获取数据完成后更新界面，以及进行完成后的相关操作
         */
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();//获取数据后刷新界面
            loadNewsetComplete();
            super.onPostExecute(aVoid);
        }
    }

    //把获取更多数据放在AsyncTask中
    class LoadMoreAsyncTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            iLoadListener.onLoadMore();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        /**
         * 获取数据完成后更新界面，以及进行完成后的相关操作
         */
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();//获取数据后刷新界面
            loadMoreComplete();
            super.onPostExecute(aVoid);
        }
    }

    //上拉完成
    public void loadMoreComplete(){
        setBottomPadding(-footerHeight);
        isLoadingMoreData=false;
        isLoadMoreDataReady=false;
    }
    //下拉完成
    public void loadNewsetComplete(){
        setTopPadding(-headerHeight);
        isLoadNewestDataReady=false;
        state=NONE;

        //设置上次刷新时间
        TextView lastupdatetime = (TextView) header.findViewById(R.id.lastupdate_time);
        SimpleDateFormat format = new SimpleDateFormat("MM.dd hh:mm");
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        lastupdatetime.setText("上次刷新时间："+time);
    }

    /**
     * 加载数据的回调接口，暴露给用户，在这个方法中刷新列表
     * 重写方法时只需获取数据，把数据添加到List中即可
     */
    public interface ILoadListener{
        public void onLoadMore();
        public void onLoadNewest();
    }

    public void setInterface(ILoadListener iLoadListener){
        this.iLoadListener = iLoadListener;
    }
}
