package com.brokepal.listviewframework;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;


import com.brokepal.listviewframework.utils.CommonAdapter;
import com.brokepal.listviewframework.utils.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/31.
 */
public class MyAdapterWithCommon extends CommonAdapter<Bean> {
    List<Integer> mPosition=new ArrayList<>();

    public MyAdapterWithCommon(Context context, List<Bean> datas) {
        super(context, datas,R.layout.item_listview);
    }

    @Override
    public void convert(final ViewHolder holder, final Bean bean) {
        //一般方法
//        ((TextView)holder.getView(R.id.title)).setText(bean.getTitle());
//        ((TextView)holder.getView(R.id.desc)).setText(bean.getDescribe());
//        ((TextView)holder.getView(R.id.time)).setText(bean.getTime());
//        ((TextView)holder.getView(R.id.phone)).setText(bean.getPhone());

        //辅助方法
        holder.setText(R.id.title,bean.getTitle())
                .setText(R.id.desc,bean.getDescribe())
                .setText(R.id.time,bean.getTime())
                .setText(R.id.phone,bean.getPhone());

        ((Button)holder.getView(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, bean.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        //下面演示如何解决ListView复用导致的CheckBox选中问题
        final CheckBox checkBox=holder.getView(R.id.checkbox);
        checkBox.setChecked(false);
        if ((mPosition.contains(holder.getPosition()))){
            checkBox.setChecked(true);
        }
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked())
                    mPosition.add((Integer)holder.getPosition());
                else
                    mPosition.remove((Integer)holder.getPosition());
            }
        });
    }
}
