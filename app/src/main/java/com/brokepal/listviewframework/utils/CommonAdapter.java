package com.brokepal.listviewframework.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter {
    protected LayoutInflater mInflater;
    protected List<T> mDatas;
    protected Context mContext;
    int mItemViewId;

    public CommonAdapter(Context context, List<T> datas,int itemViewId){
        this.mInflater=LayoutInflater.from(context);
        this.mDatas=datas;
        this.mContext=context;
        this.mItemViewId=itemViewId;
    }

    public void onDateChange(List<T> mDatas) {
        this.mDatas = mDatas;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=ViewHolder.get(mContext,convertView,parent, mItemViewId,position);
        convert(holder,getItem(position));
        return holder.getConvertView();
    }

    public abstract void convert(ViewHolder holder,T t);
}