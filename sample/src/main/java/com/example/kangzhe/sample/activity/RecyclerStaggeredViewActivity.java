package com.example.kangzhe.sample.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zjutkz.powerfulrecyclerview.ptr.PowerfulRecyclerView;
import com.zjutkz.powerfulrecyclerview.listener.OnLoadMoreListener;
import com.zjutkz.powerfulrecyclerview.listener.OnRefreshListener;
import com.example.kangzhe.sample.fragment.MyFragment1;
import com.example.kangzhe.sample.fragment.MyFragment2;
import com.example.kangzhe.sample.fragment.MyFragment3;
import com.example.kangzhe.sample.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by kangzhe on 16/1/5.
 */
public class RecyclerStaggeredViewActivity extends AppCompatActivity implements OnRefreshListener,OnLoadMoreListener {

    private static final String TAG = "StaggeredViewActivity";

    private PowerfulRecyclerView recycler;

    private ImageView returnToTop;

    private MyAdapter adapter;

    private List<String> datas;

    private FrameLayout header;

    private LinearLayout staggeredHeader;

    private ViewPager vp;

    private MyViewPagerAdapter pagerAdapter;

    private List<Fragment> fragments;

    private int loadMoreCount = 0;

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


    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){

                getDatas(0);

                adapter.notifyItemRangeChanged(0,strArr.length);

                loadMoreCount = 0;

                recycler.stopRefresh();

                if(!recycler.isLoadMoreEnable()){
                    recycler.setLoadMoreEnable(true);
                }
            }else if(msg.what == 1){

                getDatas(1);

                adapter.notifyItemRangeInserted(adapter.getItemCount(), 9);

                recycler.stopLoadMore();
            }else if(msg.what == 2){
                recycler.setLoadMoreEnable(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_activity);

        recycler = (PowerfulRecyclerView)findViewById(R.id.ptr_container);

        returnToTop = (ImageView)findViewById(R.id.btn_return_to_top);

        getDatas(1);

        adapter = new MyAdapter();

        recycler.setAdapter(adapter);

        recycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));



        //recycler.addItemDecoration(new DividerGridItemDecoration(this));

        header = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.header, recycler, false);

        recycler.setHeaderView(header);

        //recycler.setFooterView(footer);

        staggeredHeader = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.list_header_viewpager, null);

        vp = (ViewPager)staggeredHeader.findViewById(R.id.list_header_vp);

        fragments = new ArrayList<Fragment>();
        fragments.add(new MyFragment1());
        fragments.add(new MyFragment2());
        fragments.add(new MyFragment3());

        pagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());

        vp.setAdapter(pagerAdapter);

        recycler.addRecyclerViewHeader(staggeredHeader);

        recycler.setOnRefreshListener(this);

        recycler.setOnLoadMoreListener(this);

        recycler.setOnItemClickListener(new PowerfulRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, RecyclerView.ViewHolder holder, int position) {
                Toast.makeText(RecyclerStaggeredViewActivity.this, "click" + position, Toast.LENGTH_SHORT).show();
            }
        });

        recycler.setOnItemLongClickListener(new PowerfulRecyclerView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RecyclerView parent, RecyclerView.ViewHolder holder, int position) {
                Toast.makeText(RecyclerStaggeredViewActivity.this, "long click" + position, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        recycler.setOnShowTopListener(new PowerfulRecyclerView.OnShowTopListener() {
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
                recycler.returnToTop();
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
        if(loadMoreCount++ <= 1) {
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

    public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(R.layout.staggered_item, parent);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            if(holder instanceof MyViewHolder){
                /*ImageFetcher.fetchImage(datas.get(position), new WdImageFetchListener() {
                    @Override
                    public void onFetchSuccess(String uri, Bitmap bitmap) {
                        ((MyViewHolder) holder).iv.setOriginalSize(bitmap.getWidth(),bitmap.getHeight());
                        ((MyViewHolder) holder).iv.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onFetchFailed(String uri) {

                    }

                    @Override
                    public void onCanceled(String uri) {

                    }
                });*/
                int max=500;
                int min=700;
                Random random = new Random();

                int height = random.nextInt(max)%(max-min+1) + min;

                ViewGroup.LayoutParams params = ((MyViewHolder) holder).iv.getLayoutParams();
                params.height = height;
                ((MyViewHolder) holder).iv.setLayoutParams(params);
                Picasso.with(((MyViewHolder) holder).iv.getContext()).
                        load(datas.get(position)).
                        into(((MyViewHolder) holder).iv);
            }
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{

            private ImageView iv;

            public MyViewHolder(@LayoutRes int resource, ViewGroup parent) {
                super(getLayoutInflater().inflate(resource,parent,false));

                iv = (ImageView)itemView.findViewById(R.id.staggered_item_iv);
            }
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
}
