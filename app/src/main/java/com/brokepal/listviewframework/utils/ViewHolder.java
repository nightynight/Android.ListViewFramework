package com.brokepal.listviewframework.utils;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/8/31.
 */
public class ViewHolder {
    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public ViewHolder(Context context, ViewGroup parent,int layoutId,int position){
        this.mPosition=position;
        this.mViews=new SparseArray<View>();
        mConvertView= LayoutInflater.from(context).inflate(layoutId,parent,false);
        mConvertView.setTag(this);
    }
    public static ViewHolder get(Context context,View convertView,ViewGroup parent,int layoutId,int position){
        if(convertView==null){
            return new ViewHolder(context,parent,layoutId,position);
        }else {
            ViewHolder holder=(ViewHolder)convertView.getTag();
            holder.mPosition=position;
            return holder;
        }
    }

    /**
     * 通过viewID获取控件
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int viewId){
        View view =mViews.get(viewId);
        if (view==null){
            view =mConvertView.findViewById(viewId);
            mViews.put(viewId,view);
        }
        return (T)view;
    }

    public View getConvertView() {
        return mConvertView;
    }


    //下面为常用控件的辅助方法

    /**
     * 设置TextView的值
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setText(int viewId,String text){
        TextView textView=getView(viewId);
        textView.setText(text);
        return this;
    }

    /**
     * 设置ImageView的图片
     * @param viewId
     * @param resId
     * @return
     */
    public  ViewHolder setImageResource(int viewId,int resId){
        ImageView view=getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    //在这里写常用到的控件设置UI的方法
    //...
}
