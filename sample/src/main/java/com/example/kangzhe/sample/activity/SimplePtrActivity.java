package com.example.kangzhe.sample.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zjutkz.powerfulrecyclerview.ptr.PowerfulRecyclerView;
import com.zjutkz.powerfulrecyclerview.ptr.PowerfulSimpleRecyclerView;
import com.zjutkz.powerfulrecyclerview.listener.OnLoadMoreListener;
import com.zjutkz.powerfulrecyclerview.listener.OnRefreshListener;
import com.example.kangzhe.sample.fragment.MyFragment1;
import com.example.kangzhe.sample.fragment.MyFragment2;
import com.example.kangzhe.sample.fragment.MyFragment3;
import com.example.kangzhe.sample.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kangzhe on 16/1/5.
 */
public class SimplePtrActivity extends AppCompatActivity implements OnRefreshListener,OnLoadMoreListener {

    private PowerfulSimpleRecyclerView recycler;

    private ImageView returnToTop;

    private List<String> datas;

    private MyRecyclerAdapter recyclerAdapter;

    private LinearLayout listHeader;

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

                recyclerAdapter.notifyDataSetChanged();

                loadMoreCount = 0;

                //recycler.setRefreshing(false);
                recycler.stopRefresh();

                if(!recycler.isLoadMoreEnable()){
                    recycler.setLoadMoreEnable(true);
                }
            }else if(msg.what == 1){

                getDatas(1);

                recyclerAdapter.notifyDataSetChanged();

                recycler.stopLoadMore();
            }else if(msg.what == 2){
                recycler.setLoadMoreEnable(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_ptr_activity);

        recycler = (PowerfulSimpleRecyclerView)findViewById(R.id.ptr_container);

        returnToTop = (ImageView)findViewById(R.id.btn_return_to_top);

        //recycler.setHeaderStrings(headerStrings).setHeaderDrawables(headerDrawables);

        //recycler.setFooterView(footer);

        getDatas(1);

        recyclerAdapter = new MyRecyclerAdapter();

        //recycler.setListAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(SimplePtrActivity.this));

        //recycler.addItemDecoration(new DividerItemDecoration(SimplePtrActivity.this, DividerItemDecoration.VERTICAL_LIST));

        recycler.setAdapter(recyclerAdapter);

        listHeader = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.list_header_viewpager, null);

        vp = (ViewPager)listHeader.findViewById(R.id.list_header_vp);

        fragments = new ArrayList<Fragment>();
        fragments.add(new MyFragment1());
        fragments.add(new MyFragment2());
        fragments.add(new MyFragment3());

        pagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());

        vp.setAdapter(pagerAdapter);

        recycler.setPositionToShowBtn(4);

        recycler.addRecyclerViewHeader(listHeader);

        recycler.setOnRefreshListener(this);

        recycler.setOnLoadMoreListener(this);

        recycler.setOnItemClickListener(new PowerfulSimpleRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, RecyclerView.ViewHolder holder, int position) {
                if(holder.getItemViewType() == PowerfulRecyclerView.TYPE_RECYCLER_FOOTER){
                    return;
                }
                Toast.makeText(SimplePtrActivity.this, "click" + position, Toast.LENGTH_SHORT).show();
            }
        });

        recycler.setOnItemLongClickListener(new PowerfulRecyclerView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RecyclerView parent, RecyclerView.ViewHolder holder, int position) {
                Toast.makeText(SimplePtrActivity.this, "long click" + position, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        recycler.setOnShowTopListener(new PowerfulSimpleRecyclerView.OnShowTopListener() {
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

        datas.add("drawable://" + R.drawable.img1);
        datas.add("drawable://" + R.drawable.img2);
        datas.add("drawable://" + R.drawable.img3);
        datas.add("drawable://" + R.drawable.img4);
        datas.add("drawable://" + R.drawable.img5);
        datas.add("drawable://" + R.drawable.img6);
        datas.add("drawable://" + R.drawable.img7);
        datas.add("drawable://" + R.drawable.img8);
        datas.add("drawable://" + R.drawable.img9);
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

    public class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        @Override
        public int getItemViewType(int position) {
            if(position < datas.size()){
                return 1;
            }
            return PowerfulRecyclerView.TYPE_RECYCLER_FOOTER;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh = null;
            if(viewType == 1){
                vh = new MyViewHolder(LayoutInflater.from(SimplePtrActivity.this).inflate(R.layout.big_photo_item_layout,parent,false));
            }else {
                vh = new FooterViewHolder(LayoutInflater.from(SimplePtrActivity.this).inflate(R.layout.recycler_footer,parent,false));
            }

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if(!(holder instanceof MyViewHolder)){
                return;
            }

            ImageLoader.getInstance().displayImage(datas.get(position), new ImageViewAware(((MyViewHolder) holder).iv),
                    new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view,
                                                    FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view,
                                                      Bitmap loadedImage) {

                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });

        }

        @Override
        public int getItemCount() {
            return datas.size() + 1;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{

            private ImageView iv;

            public MyViewHolder(View itemView) {
                super(itemView);

                iv = (ImageView)itemView.findViewById(R.id.item_iv);
            }
        }

        public class FooterViewHolder extends RecyclerView.ViewHolder {

            public FooterViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}

