package com.brokepal.listviewframework;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.BaseAdapter;

import com.brokepal.listviewframework.utils.LoadListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoadListView.ILoadListener {
    private LoadListView listView;
    private List<Bean> datas=new ArrayList<Bean>();;
    //    private MyAdapter adapter;
    private MyAdapterWithCommon adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDatas();
        initView();
    }

    private void initDatas() {
        datas.add(new Bean("Android万能适配器1","Android打造万能的ListView和GridView适配器","2016-8-31","123456"));
        datas.add(new Bean("Android万能适配器2","Android打造万能的ListView和GridView适配器","2016-8-31","123456"));
        datas.add(new Bean("Android万能适配器3","Android打造万能的ListView和GridView适配器","2016-8-31","123456"));
        datas.add(new Bean("Android万能适配器4","Android打造万能的ListView和GridView适配器","2016-8-31","123456"));
        datas.add(new Bean("Android万能适配器5","Android打造万能的ListView和GridView适配器","2016-8-31","123456"));
        datas.add(new Bean("Android万能适配器6","Android打造万能的ListView和GridView适配器","2016-8-31","123456"));
        datas.add(new Bean("Android万能适配器7","Android打造万能的ListView和GridView适配器","2016-8-31","123456"));
        datas.add(new Bean("Android万能适配器8","Android打造万能的ListView和GridView适配器","2016-8-31","123456"));
        datas.add(new Bean("Android万能适配器9","Android打造万能的ListView和GridView适配器","2016-8-31","123456"));
        datas.add(new Bean("Android万能适配器10","Android打造万能的ListView和GridView适配器","2016-8-31","123456"));
        datas.add(new Bean("Android万能适配器11","Android打造万能的ListView和GridView适配器","2016-8-31","123456"));

//        adapter=new MyAdapter(this,datas);
        adapter=new MyAdapterWithCommon(this,datas);
    }
    private void initView() {
        listView=(LoadListView) findViewById(R.id.listView);
        listView.setInterface(this);

//        listView.setAdapter(adapter); //传统方法
        listView.setAdapter(adapter);   //使用万能适配器
//        listView.setAdapter(new CommonAdapter<Bean>(MainActivity.this,datas) {  //使用匿名内部类
//            @Override
//            public void convert(ViewHolder holder, Bean bean) {
//                holder.setText(R.id.title,bean.getTitle())
//                        .setText(R.id.desc,bean.getDescribe())
//                        .setText(R.id.time,bean.getTime())
//                        .setText(R.id.phone,bean.getPhone());
//            }
//        });
    }

    private void getLoadMoreData(){
        datas.add(new Bean("Android万能适配器0","Android打造万能的ListView和GridView适配器","2016-8-31","123456"));
        datas.add(new Bean("Android万能适配器00","Android打造万能的ListView和GridView适配器","2016-8-31","123456"));
    }

    private void getLoadNewData(){
        datas.add(0,new Bean("Android万能适配器0","Android打造万能的ListView和GridView适配器","2016-8-31","123456"));
        datas.add(0,new Bean("Android万能适配器00","Android打造万能的ListView和GridView适配器","2016-8-31","123456"));
    }


    @Override
    public void onLoadMore() {
        try {
            Thread.sleep(2000);//模拟网络延迟
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getLoadMoreData();
    }

    @Override
    public void onLoadNewest() {
        // TODO Auto-generated method stub
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getLoadNewData();
    }
}