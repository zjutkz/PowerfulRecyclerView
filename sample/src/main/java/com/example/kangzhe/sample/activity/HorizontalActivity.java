package com.example.kangzhe.sample.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zjutkz.powerfulrecyclerview.listener.ItemTouchAdapter;
import com.zjutkz.powerfulrecyclerview.listener.OnLoadMoreListener;
import com.zjutkz.powerfulrecyclerview.listener.OnRefreshListener;
import com.zjutkz.powerfulrecyclerview.ptr.PowerfulRecyclerView;
import com.zjutkz.powerfulrecyclerview.ptr.PowerfulSimpleRecyclerView;
import com.example.kangzhe.sample.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kangzhe on 16/3/9.
 */
public class HorizontalActivity extends AppCompatActivity implements OnRefreshListener,OnLoadMoreListener {

    private static final String TAG = "HorizontalActivity";

    private MyAdapter adapter;

    private List<Integer> datas = new ArrayList<Integer>();

    PowerfulSimpleRecyclerView horizontalList;

    int loadMoreCount = 0;

    private int positionToRestore = 0;

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){

                getDatas(0);

                adapter.notifyDataSetChanged();
                loadMoreCount = 0;

                horizontalList.stopRefresh();

                if(!horizontalList.isLoadMoreEnable()){
                    horizontalList.setLoadMoreEnable(true);
                }
            }else if(msg.what == 1){

                getDatas(1);

                adapter.notifyItemRangeInserted(adapter.getItemCount(), 9);

                horizontalList.stopLoadMore();
            }else if(msg.what == 2){
                horizontalList.setLoadMoreEnable(false);
            }else if(msg.what == 3){
                horizontalList.hideSpecialInfoView();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal);

        getDatas(0);

        horizontalList = (PowerfulSimpleRecyclerView)findViewById(R.id.horizontal_list);

        adapter = new MyAdapter(this,datas);

        horizontalList.setAdapter(adapter);

        horizontalList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        horizontalList.setOnRefreshListener(this);

        horizontalList.setOnLoadMoreListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        positionToRestore = horizontalList.getFirstVisiblePosition();
        Log.d(TAG, "onStop: " + horizontalList.getFirstVisiblePosition() + " " + horizontalList.getLastVisiblePosition());
    }

    @Override
    protected void onResume() {
        super.onResume();

        horizontalList.setSelection(positionToRestore);
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

    public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchAdapter {

        private Context mContext;
        private List<Integer> datas;

        public MyAdapter(Context mContext,List<Integer> datas){
            this.mContext = mContext;
            this.datas = datas;
        }

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
                vh = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.new_list_item,parent,false));
            }else {
                vh = new FooterViewHolder(LayoutInflater.from(mContext).inflate(R.layout.recycler_footer,parent,false));
            }

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
            return datas.size() + 1;
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

        public class FooterViewHolder extends RecyclerView.ViewHolder {

            public FooterViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
