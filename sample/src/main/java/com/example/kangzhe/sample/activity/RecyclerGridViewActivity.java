package com.example.kangzhe.sample.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zjutkz.powerfulrecyclerview.ptr.PowerfulRecyclerView;
import com.zjutkz.powerfulrecyclerview.listener.ItemTouchAdapter;
import com.zjutkz.powerfulrecyclerview.listener.OnLoadMoreListener;
import com.zjutkz.powerfulrecyclerview.listener.OnRefreshListener;
import com.example.kangzhe.sample.fragment.MyFragment1;
import com.example.kangzhe.sample.fragment.MyFragment2;
import com.example.kangzhe.sample.fragment.MyFragment3;
import com.example.kangzhe.sample.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by kangzhe on 16/1/5.
 */
// TODO: 16/3/24 The speed of loading picture may low,so you can replace the url by yourself
public class RecyclerGridViewActivity  extends AppCompatActivity implements OnRefreshListener,OnLoadMoreListener {

    private static final String TAG = "RecyclerActivity";

    private PowerfulRecyclerView container;

    private ImageView returnToTop;

    private MyAdapter adapter;

    private List<String> datas;

    private FrameLayout header;

    private LinearLayout gridHeader;

    private ViewPager vp;

    private MyViewPagerAdapter pagerAdapter;

    private List<Fragment> fragments;

    private int loadMoreCount = 0;

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){

                getDatas(0);

                adapter.notifyDataSetChanged();

                loadMoreCount = 0;

                container.setRefreshing(false);

                if(!container.isLoadMoreEnable()){
                    container.setLoadMoreEnable(true);
                }
            }else if(msg.what == 1){

                getDatas(1);

                adapter.notifyDataSetChanged();

                container.stopLoadMore();
            }else if(msg.what == 2){
                container.setLoadMoreEnable(false);
            }else if(msg.what == 3){
                container.hideSpecialInfoView();
            }
        }
    };

    private String[] strArr = new String[]{"http://ww4.sinaimg.cn/large/7a8aed7bgw1f0orab74l4j20go0p0jw5.jpg"
            ,"http://ww4.sinaimg.cn/large/7a8aed7bgw1f0k67eluxej20fr0m8whw.jpg","http://ww2.sinaimg.cn/large/7a8aed7bgw1f0k6706308j20vg18gqfl.jpg"
            ,"http://ww1.sinaimg.cn/large/7a8aed7bgw1f0k66sk2qbj20rs130wqf.jpg","http://ww1.sinaimg.cn/large/7a8aed7bgw1f0ixu5rmtcj20hs0qojv5.jpg"
            ,"http://ww4.sinaimg.cn/large/7a8aed7bjw1f0f9fkzu78j20f00qo0xl.jpg","http://ww3.sinaimg.cn/large/7a8aed7bjw1f0e4suv1tgj20hs0qo0w5.jpg"
            ,"http://ww2.sinaimg.cn/large/7a8aed7bjw1f0cw7swd9tj20hy0qogoo.jpg","http://ww2.sinaimg.cn/large/7a8aed7bjw1f0buzmnacoj20f00liwi2.jpg"
            ,"http://ww1.sinaimg.cn/large/7a8aed7bjw1f0bifjrh39j20v018gwtj.jpg","http://ww4.sinaimg.cn/large/7a8aed7bjw1f082c0b6zyj20f00f0gnr.jpg"
            ,"http://ww3.sinaimg.cn/large/610dc034jw1f070hyadzkj20p90gwq6v.jpg","http://ww1.sinaimg.cn/large/7a8aed7bjw1f05pbp0p0yj20go0mu77b.jpg"
            ,"http://ww1.sinaimg.cn/large/7a8aed7bjw1f04m5ngwwaj20dw0kmwgn.jpg","http://ww1.sinaimg.cn/large/7a8aed7bjw1f03emebr4jj20ez0qoadk.jpg"
            ,"http://ww1.sinaimg.cn/large/7a8aed7bjw1ezzaw04857j20p00gp40w.jpg","http://ww1.sinaimg.cn/large/7a8aed7bjw1ezysj9ytj5j20f00m8wh0.jpg"
            ,"http://ww2.sinaimg.cn/large/7a8aed7bjw1ezxog636o8j20du0kujsg.jpg","http://ww2.sinaimg.cn/large/7a8aed7bjw1ezwgshzjpmj21ao1y0tf0.jpg"
            ,"http://ww2.sinaimg.cn/large/7a8aed7bjw1ezvbmuqz9cj20hs0qoq6o.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swip_refresh_recycler_activity);

        container = (PowerfulRecyclerView)findViewById(R.id.ptr_container);

        returnToTop = (ImageView)findViewById(R.id.btn_return_to_top);

        getDatas(1);

        adapter = new MyAdapter(this,datas);

        container.setLayoutManager(new GridLayoutManager(this, 2));

        container.setAdapter(adapter);

        container.setFooterLoadText("简单自定义onLoadMore");

        container.setFooterShowText("简单自定义onShow");
        //container.addItemDecoration(new DividerGridItemDecoration(this));

        gridHeader = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.list_header_viewpager, null);

        vp = (ViewPager)gridHeader.findViewById(R.id.list_header_vp);

        fragments = new ArrayList<Fragment>();
        fragments.add(new MyFragment1());
        fragments.add(new MyFragment2());
        fragments.add(new MyFragment3());

        pagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());

        vp.setAdapter(pagerAdapter);

        container.addRecyclerViewHeader(gridHeader);

        container.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        //container.setFooterView(footer);

        container.setOnRefreshListener(this);

        container.setOnLoadMoreListener(this);

        container.setOnItemClickListener(new PowerfulRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, RecyclerView.ViewHolder holder, int position) {
                Toast.makeText(RecyclerGridViewActivity.this, "click" + position, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "" + position);
            }
        });

        container.setOnItemLongClickListener(new PowerfulRecyclerView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RecyclerView parent, RecyclerView.ViewHolder holder, int position) {
                Toast.makeText(RecyclerGridViewActivity.this, "long click" + position, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //container.prepareForDragAndSwipe(true,true);

        container.setOnShowTopListener(new PowerfulRecyclerView.OnShowTopListener() {
            @Override
            public void showTop(boolean isShow) {
                if (isShow) {
                    returnToTop.setVisibility(View.VISIBLE);
                } else {
                    returnToTop.setVisibility(View.GONE);
                }
            }
        });

        returnToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container.returnToTop();
            }
        });
    }

    private void getDatas(int msg) {
        if(datas == null){
            datas = new ArrayList<String>();
        }

        if(msg == 0){
            datas.clear();
        }

        if(msg == 1){
            List<String> temp = Arrays.asList(strArr);
            datas.addAll(temp);
            return;
        }

        datas.addAll(Arrays.asList(strArr));
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    mHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onLoadMore() {
        if(loadMoreCount++ <= 1){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        mHandler.sendEmptyMessage(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        mHandler.sendEmptyMessage(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public class MyViewPagerAdapter extends FragmentPagerAdapter {

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchAdapter {

        private Context mContext;
        private List<String> datas;

        public MyAdapter(Context mContext,List<String> datas){
            this.mContext = mContext;
            this.datas = datas;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder vh = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.new_grid_layout,parent,false));

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            Picasso.with(((MyViewHolder) holder).iv.getContext()).
                    load(datas.get(position)).
                    into(((MyViewHolder) holder).iv);
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        @Override
        public void onMove(int fromPosition, int toPosition) {
            Collections.swap(datas, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
            notifyItemRangeChanged(Math.min(fromPosition,toPosition),Math.abs(fromPosition - toPosition) + 1);
        }

        @Override
        public void onDismiss(int position) {
            datas.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,datas.size() - position);
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{

            private ImageView iv;

            public MyViewHolder(View itemView) {
                super(itemView);

                iv = (ImageView)itemView.findViewById(R.id.item_iv);
            }
        }
    }
}
