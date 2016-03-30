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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zjutkz.powerfulrecyclerview.animator.impl.ZoomInAnimator;
import com.zjutkz.powerfulrecyclerview.ptr.PowerfulRecyclerView;
import com.zjutkz.powerfulrecyclerview.listener.ItemTouchAdapter;
import com.zjutkz.powerfulrecyclerview.listener.OnLoadMoreListener;
import com.zjutkz.powerfulrecyclerview.listener.OnRefreshListener;
import com.example.kangzhe.sample.fragment.MyFragment1;
import com.example.kangzhe.sample.fragment.MyFragment2;
import com.example.kangzhe.sample.fragment.MyFragment3;
import com.example.kangzhe.sample.R;
import com.example.kangzhe.sample.view.HistoryThemeFooterView;
import com.example.kangzhe.sample.view.HistoryThemeHeaderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kangzhe on 16/1/5.
 */
public class RecyclerListViewActivity extends AppCompatActivity implements OnRefreshListener,OnLoadMoreListener {

    private static final String TAG = "RecyclerActivity";

    private PowerfulRecyclerView recycler;

    private ImageView returnToTop;

    private MyAdapter adapter;

    private List<Integer> datas;

    private HistoryThemeFooterView footer;

    private HistoryThemeHeaderView header;

    private LinearLayout listHeader;

    private ViewPager vp;

    private MyViewPagerAdapter pagerAdapter;

    private List<Fragment> fragments;

    private int loadMoreCount = 0;

    private int positionToRestore = 0;

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){

                getDatas(0);

                adapter.notifyDataSetChanged();
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
            }else if(msg.what == 3){
                recycler.hideSpecialInfoView();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_activity);

        recycler = (PowerfulRecyclerView)findViewById(R.id.ptr_container);

        returnToTop = (ImageView)findViewById(R.id.btn_return_to_top);

        if(datas == null){
            datas = new ArrayList<Integer>();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getDatas(1);
                adapter.notifyDataSetChanged();
                recycler.stopRefresh();
                recycler.stopLoadMore();
            }
        }, 1500);

        adapter = new MyAdapter(this,datas);

        recycler.setAdapter(adapter);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        //recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        header = (HistoryThemeHeaderView) LayoutInflater.from(this).inflate(R.layout.history_header_theme, recycler, false);

        footer = (HistoryThemeFooterView) LayoutInflater.from(this).inflate(R.layout.history_footer_theme, recycler, false);

        recycler.setHeaderView(header);

        recycler.setFooterView(footer);

        listHeader = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.list_header_viewpager, null);

        vp = (ViewPager)listHeader.findViewById(R.id.list_header_vp);

        fragments = new ArrayList<Fragment>();
        fragments.add(new MyFragment1());
        fragments.add(new MyFragment2());
        fragments.add(new MyFragment3());

        pagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());

        vp.setAdapter(pagerAdapter);

        recycler.setPositionToShowBtn(4);

        recycler.addRecyclerViewHeader(listHeader, true);

        recycler.prepareForDragAndSwipe(true, true);

        recycler.setScrollBarEnable(false);

        recycler.setOnRefreshListener(this);

        recycler.setOnLoadMoreListener(this);

        recycler.setOnItemClickListener(new PowerfulRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, RecyclerView.ViewHolder holder, int position) {
                if (position == 0) {
                    datas.add(1, R.mipmap.ic_launcher);
                    adapter.notifyItemInserted(1);
                } else if (position == 1) {
                    datas.remove(1);
                    adapter.notifyItemRemoved(1);
                }
            }
        });

        //如果设置了可以drag和swipe，不要设置长按监听器
        /*recycler.setOnItemLongClickListener(new WdPtrrecycler.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RecyclerView parent, RecyclerView.ViewHolder holder, int position) {
                if(holder instanceof MyAdapter.MyViewHolder){
                    ((MyAdapter.MyViewHolder) holder).android.setVisibility(View.VISIBLE);
                    showLogic.set(position,true);
                }
                return true;
            }
        });*/

        recycler.setItemAnimator(new ZoomInAnimator());

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
                recycler.autoRefresh();
            }
        });

        //recycler.showLoadingView();

        //mHandler.sendMessageDelayed(mHandler.obtainMessage(3), 2000);
    }

    @Override
    protected void onStop() {
        super.onStop();

        positionToRestore = recycler.getFirstVisiblePosition();
        Log.d(TAG, "onStop: " + recycler.getFirstVisiblePosition() + " " + recycler.getLastVisiblePosition());
    }

    @Override
    protected void onResume() {
        super.onResume();

        recycler.setSelection(positionToRestore);
    }

    private void getDatas(int msg) {


        if(msg == 0){
            datas.clear();
        }

        datas.add(R.drawable.img1);
        datas.add(R.drawable.img2);
        datas.add(R.drawable.img3);
        datas.add(R.drawable.img4);
        datas.add(R.drawable.img5);
        datas.add(R.drawable.img6);
        datas.add(R.drawable.img7);
        datas.add(R.drawable.img8);
        datas.add(R.drawable.img9);
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
        if(++loadMoreCount <= 2){
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
        private List<Integer> datas;

        public MyAdapter(Context mContext,List<Integer> datas){
            this.mContext = mContext;
            this.datas = datas;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.new_list_item,parent,false));


            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if(holder instanceof MyViewHolder){
                ((MyViewHolder) holder).setImage(datas.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        @Override
        public void onMove(int fromPosition, int toPosition) {
            if(fromPosition < 0 || toPosition >= datas.size()){
                return;
            }
            Collections.swap(datas, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onDismiss(int position) {
            if(position < 0 || position >= datas.size()){
                return;
            }
            datas.remove(position);
            notifyItemRemoved(position);
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{

            public ImageView iv;
            public ImageView android;

            public MyViewHolder(View itemView) {
                super(itemView);

                iv = (ImageView)itemView.findViewById(R.id.item_iv);
                android = (ImageView)itemView.findViewById(R.id.android);
            }

            public void setImage(int idImage) {
                Picasso.with(iv.getContext()).
                        load(idImage).
                        centerCrop().
                        resize(130,130).
                        into(iv);
            }
        }
    }
}
