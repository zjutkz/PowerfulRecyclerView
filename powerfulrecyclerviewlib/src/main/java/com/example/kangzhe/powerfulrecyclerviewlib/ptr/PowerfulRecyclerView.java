package com.example.kangzhe.powerfulrecyclerviewlib.ptr;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;

import com.example.kangzhe.powerfulrecyclerviewlib.R;
import com.example.kangzhe.powerfulrecyclerviewlib.adapter.PowerfulRecyclerAdapter;
import com.example.kangzhe.powerfulrecyclerviewlib.animator.base.BaseItemAnimator;
import com.example.kangzhe.powerfulrecyclerviewlib.listener.IHeaderView;
import com.example.kangzhe.powerfulrecyclerviewlib.listener.OnLoadMoreListener;
import com.example.kangzhe.powerfulrecyclerviewlib.listener.OnRefreshListener;
import com.example.kangzhe.powerfulrecyclerviewlib.listener.OnViewClick;
import com.example.kangzhe.powerfulrecyclerviewlib.touchHelper.ItemTouchListenerAdapter;
import com.example.kangzhe.powerfulrecyclerviewlib.utils.FooterUtils;
import com.example.kangzhe.powerfulrecyclerviewlib.utils.RecyclerViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kangzhe on 16/1/1.
 */
public class PowerfulRecyclerView extends LinearLayout implements OnViewClick,SwipeRefreshLayout.OnRefreshListener,NestedScrollingParent, NestedScrollingChild {

    private static final String TAG = "PowerfulPtrContainer";

    /**
     * 没有数据的布局
     */
    private static final int DEF_NO_DATA_VIEW_LAYOUT = R.layout.def_no_data_view_layout;

    public static final int TYPE_RECYCLER_FOOTER = -5;

    /**
     * 空置状态
     */
    private static final int STATE_IDLE = 0;

    /**
     * 下拉刷新状态
     */
    private static final int STATE_PULL_TO_REFRESH = 1;

    /**
     * 放手刷新状态
     */
    private static final int STATE_RELEASE_TO_REFRESH = 2;

    /**
     * 刷新状态
     */
    private static final int STATE_ON_REFRESH = 3;

    /**
     * 回滚状态
     */
    private static final int STATE_ON_RESET = 4;

    /**
     * 默认的触发刷新的最小距离
     */
    private static final int DISTANCE_TO_REFRESH = 200;

    /**
     * 默认的下拉的最大距离
     */
    private static final int MAX_TO_PULL = 250;

    /**
     * 默认的滚回到顶部的时间
     */
    private static final long BACK_TOP_DURATION = 450;

    /**
     * 默认显示返回头部的滚动位置
     */
    private static final int DEF_POSITION_TO_SHOW = 4;

    /**
     * layoutManager为vertical
     */
    private static final int ORIENTATION_VERTICAL = 1;

    /**
     * layoutManager为horizontal
     */
    private static final int ORIENTATION_HORIZONTAL = 2;

    /**
     * 头部View的根节点
     */
    protected View mHeaderViewContainer;

    /**
     * 根据刷新状态改变的头部View
     */
    protected List<IHeaderView> mHeaderViews;

    /**
     * 底部View
     */
    protected View mFooterView;

    private ViewStub mNoDataView;

    private int mNoDataViewLayout;

    private RecyclerView mRecyclerView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private PowerfulRecyclerAdapter mPowerfulRecyclerAdapter;

    /**
     * 手指放开的时候头部的高度
     */
    private float needToResetHeight;

    private int refreshDistance;

    /**
     * 最大下拉距离
     */
    private int pullMax;

    /**
     * 头部返回动画的时间
     */
    private long backTopDuration;

    private MarginLayoutParams mHeaderLayoutParams;

    /**
     * 用于判断是否需要回调onReset方法
     */
    private boolean needCallOnReset;

    private int refreshState = STATE_IDLE;

    private int positionToShow;

    private int scrollX = 0;

    private int scrollY = 0;

    private String showText = "点击加载更多";

    private String loadText = "正在加载更多";

    private boolean refreshEnable = true;

    private boolean loadMoreEnable = true;

    protected boolean isSwipeToRefresh;

    private FooterUtils mFooterUtils;

    private int orientationMode = ORIENTATION_VERTICAL;

    protected NestedScrollingParentHelper mNestedScrollingParentHelper;

    protected NestedScrollingChildHelper mNestedScrollingChildHelper;

    /**
     * recyclerView在y坐标上未消费的总距离
     */
    protected float mTotalUnconsumed;

    protected final int[] mParentScrollConsumed = new int[2];

    protected final int[] mParentOffsetInWindow = new int[2];

    /**
     * 用于刷新的listener
     */
    protected OnRefreshListener mRefreshListener;

    /**
     * 用于加载更多的listener
     */
    protected OnLoadMoreListener mLoadMoreListener;

    /**
     * 用于item点击的listener
     */
    protected OnItemClickListener mListener;

    /**
     * 用于item长按的listener
     */
    protected OnItemLongClickListener mLongClickListener;

    /**
     * 用于显示返回头部按钮的listener
     */
    protected OnShowTopListener mShowTopListener;

    private int autoRefreshHeight = 0;

    private Runnable autoRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            if(autoRefreshHeight < pullMax){
                autoRefreshHeight += 70;
                updateHeaderHeight(autoRefreshHeight);

                autoRefreshHandler.postDelayed(this,1);
            }else{
                refreshState = STATE_RELEASE_TO_REFRESH;
                refreshByState();
            }
        }
    };

    private static Handler autoRefreshHandler = new Handler(){
    };

    /**
     * recyclerView的item点击事件回调
     */
    @Override
    public void onClick(RecyclerView.ViewHolder holder, int position) {
        if(mListener != null){
            mListener.onItemClick(mRecyclerView,holder,position);
        }
    }

    /**
     * recyclerView的item长按事件回调
     */
    @Override
    public boolean onLongClick(RecyclerView.ViewHolder holder, int position) {
        if(mLongClickListener != null){
            return mLongClickListener.onItemLongClick(mRecyclerView,holder,position);
        }
        return false;
    }

    /**
     * swipeRefreshLayout的onRefresh回调
     */
    @Override
    public void onRefresh() {
        if(mRefreshListener != null && mSwipeRefreshLayout != null){
            mRefreshListener.onRefresh();
        }
    }

    public interface OnItemClickListener{
        void onItemClick(RecyclerView parent, RecyclerView.ViewHolder holder, int position);
    }

    public interface OnItemLongClickListener{
        boolean onItemLongClick(RecyclerView parent, RecyclerView.ViewHolder holder, int position);
    }

    public interface OnShowTopListener{
        void showTop(boolean isShow);
    }

    public void setOnItemClickListener(OnItemClickListener mListener){
        this.mListener = mListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mLongClickListener){
        this.mLongClickListener = mLongClickListener;
    }

    public void setOnShowTopListener(OnShowTopListener mShowTopListener){
        this.mShowTopListener = mShowTopListener;
    }

    public void addOnScrollListener(RecyclerView.OnScrollListener listener){
        if(mRecyclerView != null){
            mRecyclerView.addOnScrollListener(listener);
        }
    }

    public void removeOnScrollListener(RecyclerView.OnScrollListener listener){
        if(mRecyclerView != null){
            mRecyclerView.removeOnScrollListener(listener);
        }
    }

    public void setOnRefreshListener(OnRefreshListener mRefreshListener){
        this.mRefreshListener = mRefreshListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mLoadMoreListener){
        this.mLoadMoreListener = mLoadMoreListener;
    }

    public PowerfulRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PowerfulRecyclerView(Context context) {
        this(context, null);
    }

    public PowerfulRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        setOrientation(VERTICAL);

        //这里使用xml的形式而不直接在java代码中生成recyclerView是因为只有在xml中定义的recyclerView才会有滚动条
        mRecyclerView  = (RecyclerView)LayoutInflater.from(context).inflate(R.layout.recycler_view,this,false);

        mNoDataView = new ViewStub(getContext());

        mFooterUtils = new FooterUtils(this);

        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);

        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);

        mHeaderViews = new ArrayList<IHeaderView>();

        setAttrs(context, attrs, defStyleAttr);

        setNestedScrollingEnabled(true);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int headerCount = 0;

                if (mRecyclerView.getAdapter() instanceof PowerfulRecyclerAdapter) {
                    headerCount = ((PowerfulRecyclerAdapter) mRecyclerView.getAdapter()).getHeaderViewCount();
                }

                if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

                    int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                    if (mShowTopListener != null) {
                        if (position >= positionToShow + headerCount) {
                            mShowTopListener.showTop(true);
                        } else {
                            mShowTopListener.showTop(false);
                        }
                    }
                } else {
                    int[] position = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPositions(null);

                    if (mShowTopListener != null) {
                        if (position[0] > positionToShow + headerCount) {
                            mShowTopListener.showTop(true);
                        } else {
                            mShowTopListener.showTop(false);
                        }
                    }
                }
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                scrollX += dx;

                scrollY += dy;
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void setAttrs(Context context,AttributeSet attrs,int defStyleAttr) {

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs,R.styleable.PowerfulRecyclerView,defStyleAttr,0);

        refreshDistance = ta.getDimensionPixelOffset(R.styleable.PowerfulRecyclerView_refresh_distance, DISTANCE_TO_REFRESH);
        pullMax = ta.getDimensionPixelOffset(R.styleable.PowerfulRecyclerView_max_to_pull, MAX_TO_PULL);
        backTopDuration = ta.getInteger(R.styleable.PowerfulRecyclerView_back_top_duration, (int) BACK_TOP_DURATION);
        positionToShow = ta.getInteger(R.styleable.PowerfulRecyclerView_position_to_show, DEF_POSITION_TO_SHOW);
        mNoDataViewLayout = ta.getResourceId(R.styleable.PowerfulRecyclerView_NoDataView, DEF_NO_DATA_VIEW_LAYOUT);
        isSwipeToRefresh = ta.getBoolean(R.styleable.PowerfulRecyclerView_isSwipeToRefresh,false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        addView(mRecyclerView, 0);
        //mRecyclerView.addOnItemTouchListener(new ItemTouchListenerAdapter(mRecyclerView, this));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        addView(mNoDataView, 1);

        if(isSwipeToRefresh){
            useSwipeRefreshLayout();
        }

        needToResetHeight = -pullMax;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(mFooterView == null){
            View footer;
            footer = LayoutInflater.from(getContext()).inflate(R.layout.default_footer, this,false);

            setFooterView(footer);
        }

        this.post(new Runnable() {
            @Override
            public void run() {
                if (mLoadMoreListener != null) {
                    mFooterUtils.addFooterView(mRecyclerView, mFooterView, mLoadMoreListener);
                }
            }
        });

        ItemTouchListenerAdapter mTouchListenerAdapter = new ItemTouchListenerAdapter(mRecyclerView,this);
        mRecyclerView.addOnItemTouchListener(mTouchListenerAdapter);
    }

    /**
     * 设置footer onShow状态下的文字
     * @param  showText
     */
    public void setFooterShowText(String showText){
        this.showText = showText;
    }

    /**
     * 设置footer onLoadMore状态下的文字
     * @param  loadText
     */
    public void setFooterLoadText(String loadText){
        this.loadText = loadText;
    }

    /**
     * 添加header，将其topMargin或者leftMargin设置成－pullMax用于隐藏
     */
    private void addHeaderView() {

        if(orientationMode == ORIENTATION_HORIZONTAL){
            mHeaderLayoutParams = (MarginLayoutParams) mHeaderViewContainer.getLayoutParams();
            mHeaderLayoutParams.width = pullMax;
            mHeaderLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mHeaderLayoutParams.leftMargin = -pullMax;

            mHeaderViewContainer.setLayoutParams(mHeaderLayoutParams);

            addHeaderViewInternal(mHeaderViewContainer);

            return;
        }

        mHeaderLayoutParams = (MarginLayoutParams) mHeaderViewContainer.getLayoutParams();
        mHeaderLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mHeaderLayoutParams.height = pullMax;
        mHeaderLayoutParams.topMargin = -pullMax;

        mHeaderViewContainer.setLayoutParams(mHeaderLayoutParams);

        addHeaderViewInternal(mHeaderViewContainer);
    }

    private void addHeaderViewInternal(View mHeaderView) {
        super.addView(mHeaderView, 0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(refreshState == STATE_ON_REFRESH){
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void refreshByState() {
        if(orientationMode == ORIENTATION_HORIZONTAL){
            if(!isSwipeToRefresh && mHeaderLayoutParams != null && mHeaderViewContainer != null && refreshEnable){
                needToResetHeight = mHeaderLayoutParams.leftMargin;

                if (refreshState == STATE_RELEASE_TO_REFRESH) {

                    refreshState = STATE_ON_REFRESH;
                    updateHeaderState();
                    if (mRefreshListener != null) {
                        mRefreshListener.onRefresh();
                    }

                } else {
                    resetHeader();
                }
            }
            return;
        }

        if(!isSwipeToRefresh && mHeaderLayoutParams != null && mHeaderViewContainer != null && refreshEnable){
            needToResetHeight = mHeaderLayoutParams.topMargin;

            if (refreshState == STATE_RELEASE_TO_REFRESH) {

                refreshState = STATE_ON_REFRESH;
                updateHeaderState();
                if (mRefreshListener != null) {
                    mRefreshListener.onRefresh();
                }

            } else {
                resetHeader();
            }
        }
    }

    private void updateHeaderHeight(float distance) {
        if(orientationMode == ORIENTATION_HORIZONTAL){
            if(!isSwipeToRefresh && mHeaderLayoutParams != null && mHeaderViewContainer != null && refreshEnable){
                updateHeaderState();

                distance = Math.min(pullMax * 2, distance);
                distance = Math.max(0, distance);
                //为了产生阻尼效果，实际下拉的距离为手指下拉距离的一半
                float offsetX = (int)(distance / 2);

                mHeaderLayoutParams.leftMargin = (int)offsetX - pullMax;
                mHeaderViewContainer.setLayoutParams(mHeaderLayoutParams);

                if (mHeaderLayoutParams.leftMargin >= (refreshDistance - pullMax)) {
                    refreshState = STATE_RELEASE_TO_REFRESH;
                }else{
                    refreshState = STATE_PULL_TO_REFRESH;
                }

                if(mHeaderViews != null && mHeaderViews.size() > 0){

                    for (IHeaderView headerView : mHeaderViews) {

                        headerView.onPull(offsetX,offsetX / mHeaderViewContainer.getWidth());
                    }
                }
            }
            return;
        }

        if(!isSwipeToRefresh && mHeaderLayoutParams != null && mHeaderViewContainer != null && refreshEnable){
            updateHeaderState();

            distance = Math.min(pullMax * 2, distance);
            distance = Math.max(0, distance);
            //为了产生阻尼效果，实际下拉的距离为手指下拉距离的一半
            float offsetY = (int)(distance / 2);

            mHeaderLayoutParams.topMargin = (int)offsetY - pullMax;
            mHeaderViewContainer.setLayoutParams(mHeaderLayoutParams);

            if (mHeaderLayoutParams.topMargin >= (refreshDistance - pullMax)) {
                refreshState = STATE_RELEASE_TO_REFRESH;
            }else{
                refreshState = STATE_PULL_TO_REFRESH;
            }

            if(mHeaderViews != null && mHeaderViews.size() > 0){

                for (IHeaderView headerView : mHeaderViews) {

                    headerView.onPull(offsetY,offsetY / mHeaderViewContainer.getHeight());
                }
            }
        }
    }

    private void updateHeaderState() {
        switch (refreshState){
            case STATE_PULL_TO_REFRESH:
                makeIHeaderPullToRefresh();
                break;
            case STATE_RELEASE_TO_REFRESH:
                makeIHeaderReleaseToRefresh();
                break;
            case STATE_ON_REFRESH:
                makeIHeaderOnRefresh();
                break;
        }
    }


    private void makeIHeaderReleaseToRefresh() {
        for (IHeaderView headerView : mHeaderViews) {
            headerView.releaseToRefresh();
        }
    }

    private void makeIHeaderOnRefresh() {
        for (IHeaderView headerView : mHeaderViews) {
            headerView.onRefresh();
        }
    }

    private void makeIHeaderPullToRefresh() {
        for (IHeaderView headerView : mHeaderViews) {
            headerView.pullToRefresh();
        }
    }

    /**
     * 将头部重新隐藏
     */
    private void resetHeader(){
        if(refreshState == STATE_IDLE || refreshState == STATE_ON_RESET){
            return;
        }

        refreshState = STATE_ON_RESET;

        ValueAnimator backTopAnim = makeBackAnim();
        backTopAnim.setDuration(backTopDuration);
        backTopAnim.start();
    }

    /**
     * 生成隐藏头部的动画
     */
    private ValueAnimator makeBackAnim(){
        ValueAnimator backTopAnim = ValueAnimator.ofFloat(needToResetHeight, -pullMax);

        if(orientationMode == ORIENTATION_HORIZONTAL){
            backTopAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                    if (mHeaderViewContainer.getWidth() - Math.abs(needToResetHeight) >= refreshDistance) {
                        needCallOnReset = true;
                    } else {
                        needCallOnReset = false;
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    refreshState = STATE_IDLE;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    refreshState = STATE_IDLE;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            backTopAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();

                    if (mHeaderLayoutParams == null) {
                        throw new NullPointerException("mHeaderLayoutParams is null,are you using swipeRefreshLayout?if so,please use setRefreshing()!");
                    }
                    mHeaderLayoutParams.leftMargin = (int) value;
                    mHeaderViewContainer.setLayoutParams(mHeaderLayoutParams);

                    //只有刷新过了以后才会回调到该接口
                    if (needCallOnReset) {
                        float fraction = (pullMax - Math.abs(value)) / pullMax;
                        if (mHeaderViews != null && mHeaderViews.size() > 0) {
                            for (IHeaderView headerView : mHeaderViews) {
                                headerView.onReset(pullMax - Math.abs(value), fraction);
                            }
                        }
                    }
                }
            });

            return backTopAnim;
        }

        backTopAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                if (mHeaderViewContainer.getHeight() - Math.abs(needToResetHeight) >= refreshDistance) {
                    needCallOnReset = true;
                } else {
                    needCallOnReset = false;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                refreshState = STATE_IDLE;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                refreshState = STATE_IDLE;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        backTopAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                if (mHeaderLayoutParams == null) {
                    throw new NullPointerException("mHeaderLayoutParams is null,are you using swipeRefreshLayout?if so,please use setRefreshing()!");
                }
                mHeaderLayoutParams.topMargin = (int) value;
                mHeaderViewContainer.setLayoutParams(mHeaderLayoutParams);

                //只有刷新过了以后才会回调到该接口
                if (needCallOnReset) {
                    float fraction = (pullMax - Math.abs(value)) / pullMax;
                    if (mHeaderViews != null && mHeaderViews.size() > 0) {
                        for (IHeaderView headerView : mHeaderViews) {
                            headerView.onReset(pullMax - Math.abs(value), fraction);
                        }
                    }
                }
            }
        });

        return backTopAnim;
    }

    /**
     * 停止刷新
     */
    public void stopRefresh(){
        if(isSwipeToRefresh){
            setRefreshing(false);
            return;
        }

        resetHeader();
    }

    /**
     * 设置header
     */
    public void setHeaderView(View headerView){
        if(isSwipeToRefresh){
            throw new RuntimeException("you cannot add a header while using SwipeRefreshLayout!");
        }

        findIHeaderView(headerView);

        mHeaderViewContainer = headerView;

        this.post(new Runnable() {
            @Override
            public void run() {
                addHeaderView();
            }
        });
    }

    /**
     * 递归找出继承了IHeader接口的view
     */
    private void findIHeaderView(View headerView){
        if(headerView instanceof IHeaderView){
            mHeaderViews.add((IHeaderView)headerView);
        }
        if(headerView instanceof ViewGroup){
            for(int i = 0;i < ((ViewGroup) headerView).getChildCount();i++){
                View child = ((ViewGroup) headerView).getChildAt(i);
                findIHeaderView(child);
            }
        }
    }

    /**
     * 设置footer
     */
    public void setFooterView(View footerView){
        this.mFooterView = footerView;
    }

    /**
     * 停止加载更多
     */
    public void stopLoadMore(){

        mFooterUtils.stopLoadMore();
    }

    private void resetLoadMore(){
        if (mLoadMoreListener != null) {
            mFooterUtils.resetFooterView(mRecyclerView, mFooterView, mLoadMoreListener);
        }
    }

    /**
     * 设置滚动条是否可见
     */
    public void setScrollBarEnable(boolean isEnable){
        if(mRecyclerView != null){
            mRecyclerView.setVerticalScrollBarEnabled(isEnable);
        }
    }

    /**
     * 设置是否可以加载更多
     */
    public void setLoadMoreEnable(boolean enable){
        if(loadMoreEnable != enable){
            loadMoreEnable = enable;

            if(enable){
                resetLoadMore();
            }else{
                canNotLoadMore();
            }
        }
    }

    public boolean isRefreshEnable() {
        return refreshEnable;
    }

    public void setRefreshEnable(boolean refreshEnable) {
        this.refreshEnable = refreshEnable;
    }

    private void canNotLoadMore() {
        mFooterUtils.removeFooterView(mRecyclerView, mFooterView);
    }

    /**
     * 新建一个powerfulRecyclerAdapter封装原adapter,并赋给recyclerView
     * 注意在使用grid的时候这个函数必须要在setLayoutManager之后调用，不然在onAttachedToRecyclerView
     * 方法中layoutManager会为空，footer和header的宽度会不对
     */
    public void setAdapter(RecyclerView.Adapter adapter){

        if(mPowerfulRecyclerAdapter == null){
            mPowerfulRecyclerAdapter = new PowerfulRecyclerAdapter(this,adapter);
        }

        if(mRecyclerView != null){
            mRecyclerView.setAdapter(mPowerfulRecyclerAdapter);
        }
    }

    /**
     * 给recyclerView增加头部，这个头部不是刷新的头部，可能是banner，运营区等等,这个函数必须要在prepareForDragAndSwipe
     * 之前调用。
     */
    public void addRecyclerViewHeader(View recyclerViewHeader){
        addRecyclerViewHeader(recyclerViewHeader,false);
    }

    public void addRecyclerViewHeader(View recyclerViewHeader,boolean needAddListener){
        if(mRecyclerView != null){

            RecyclerViewUtils.getInstance().setHeaderView(mRecyclerView, recyclerViewHeader);
            if(needAddListener){
                RecyclerViewUtils.getInstance().addOnItemTouchListener(mRecyclerView, recyclerViewHeader);
            }
        }
    }

    /**
     * 给recyclerView设置setLayoutManager
     */
    public void setLayoutManager(final RecyclerView.LayoutManager manager){
        if(mRecyclerView != null){

            mRecyclerView.setLayoutManager(manager);
        }

        if(manager.canScrollHorizontally()){
            orientationMode = ORIENTATION_HORIZONTAL;
            setOrientation(HORIZONTAL);
        }
    }

    /**
     * 给recyclerView设置ItemDecoration
     */
    public void addItemDecoration(RecyclerView.ItemDecoration decoration){
        if(mRecyclerView != null){
            mRecyclerView.addItemDecoration(decoration);
        }
    }

    /**
     * 给recyclerView设置ItemAnimator（ItemAnimator必须继承自BaseItemAnimator）
     */
    public void setItemAnimator(RecyclerView.ItemAnimator mAnimator){
        if(mAnimator  instanceof BaseItemAnimator && mRecyclerView != null){
            mRecyclerView.setItemAnimator(mAnimator);
            ((BaseItemAnimator) mAnimator).setAdapter(mRecyclerView.getAdapter());
        }
    }

    /**
     * 获取内部的recyclerView，这个函数不应该出现，但是以防万一，还是将内部的recyclerView暴露给使用者
     */
    public RecyclerView getScrollChild(){
        if(mRecyclerView != null){
            return mRecyclerView;
        }

        return null;
    }

    /**
     * 设置滚动过几个item后返回头部按钮可见（不包括header）
     */
    public void setPositionToShowBtn(int positionToShowBtn){
        this.positionToShow = positionToShowBtn;
    }

    /**
     * 返回顶部
     */
    public void returnToTop(){
        if(mRecyclerView != null){
            scrollX = loadMoreEnable ? 0 : mFooterView.getMeasuredWidth();

            scrollY = loadMoreEnable ? 0 : mFooterView.getMeasuredHeight();

            mRecyclerView.scrollToPosition(0);
        }
    }

    public int getFirstVisiblePosition(){
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();

        if(mRecyclerView != null){
            if(mRecyclerView.getLayoutManager() instanceof LinearLayoutManager){
                int firstVisiblePosition = Math.max(0, ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition() - ((PowerfulRecyclerAdapter) adapter).getHeaderViewCount());
                return firstVisiblePosition;
            }else{
                int[] firstVisibleItemPosition = ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPositions(null);
                int firstVisiblePosition = Math.max(0, firstVisibleItemPosition[firstVisibleItemPosition.length - 1] - ((PowerfulRecyclerAdapter) adapter).getHeaderViewCount());
                return firstVisiblePosition;
            }
        }

        return 0;
    }

    public int getLastVisiblePosition(){
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();

        if(mRecyclerView != null){
            if(mRecyclerView.getLayoutManager() instanceof LinearLayoutManager){
                return ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition() - ((PowerfulRecyclerAdapter) adapter).getHeaderViewCount();
            }else{
                int[] firstVisibleItemPosition = ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPositions(null);
                return firstVisibleItemPosition[firstVisibleItemPosition.length - 1] - ((PowerfulRecyclerAdapter) adapter).getHeaderViewCount();
            }
        }

        return 0;
    }


    /**
     * 设置recyclerView的位置
     *
     * @param position
     */
    public void setSelection(int position){
        int headerCount = 0;

        RecyclerView.Adapter mAdaper = mRecyclerView.getAdapter();
        if(mAdaper instanceof PowerfulRecyclerAdapter){
            headerCount += ((PowerfulRecyclerAdapter) mAdaper).getHeaderViewCount();
        }

        if(mRecyclerView != null){
            mRecyclerView.scrollToPosition(position + headerCount);
        }
    }

    /**
     * 是否可以加载更多
     */
    public boolean isLoadMoreEnable(){
        return loadMoreEnable;
    }

    /**
     * 设置滑动删除和长按交换
     * @param  shouldDrag
     * @param shouldSwipe
     */
    public void prepareForDragAndSwipe(boolean shouldDrag,boolean shouldSwipe){
        if(mRecyclerView != null){
            RecyclerViewUtils.getInstance().prepareForDragAndSwipe(mRecyclerView, shouldDrag, shouldSwipe);
        }
    }
    
    public void showNoDataView(){
        if(mRecyclerView != null && mRecyclerView.getVisibility() == View.VISIBLE){
            mRecyclerView.setVisibility(GONE);
        }

        if(mHeaderViewContainer != null && mHeaderViewContainer.getVisibility() == VISIBLE){
            mHeaderViewContainer.setVisibility(GONE);
        }

        if(mSwipeRefreshLayout != null && mSwipeRefreshLayout.getVisibility() == VISIBLE){
            mSwipeRefreshLayout.setVisibility(GONE);
        }
        mNoDataView.setLayoutResource(mNoDataViewLayout);
        ViewGroup.LayoutParams params = mNoDataView.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mNoDataView.setLayoutParams(params);
        mNoDataView.setVisibility(VISIBLE);
    }
    
    public void hideSpecialInfoView(){
        if(mRecyclerView != null && mRecyclerView.getVisibility() == View.GONE){
            mRecyclerView.setVisibility(VISIBLE);
        }

        if(mHeaderViewContainer != null && mHeaderViewContainer.getVisibility() == GONE){
            mHeaderViewContainer.setVisibility(VISIBLE);
        }

        if(mSwipeRefreshLayout != null && mSwipeRefreshLayout.getVisibility() == GONE){
            mSwipeRefreshLayout.setVisibility(VISIBLE);
        }

        if(mNoDataView.getVisibility() == VISIBLE){
            mNoDataView.setVisibility(GONE);
        }
    }
    
    public void setNoDataViewLayout(int mNoDataViewLayout){
        this.mNoDataViewLayout = mNoDataViewLayout;
    }
    
    public boolean isSpecialInfoShow() {
        return mNoDataView.getVisibility() == View.VISIBLE;
    }

    /**
     * 自动加载
     *
     */
    public void autoRefresh(){
        returnToTop();
        autoRefreshHandler.postDelayed(autoRefreshRunnable,50);
    }

    /**
     * 设置使用swipeRefreshLayout
     */
    public void useSwipeRefreshLayout(){
        if(mSwipeRefreshLayout == null){
            isSwipeToRefresh = true;

            removeAllViews();

            mSwipeRefreshLayout = new SwipeRefreshLayout(getContext());

            mSwipeRefreshLayout.addView(mRecyclerView);

            addView(mSwipeRefreshLayout,0);

            addView(mNoDataView, 1);

            if(mSwipeRefreshLayout != null){
                mSwipeRefreshLayout.setOnRefreshListener(this);
            }
        }
    }

    /**
     * 给swipeRefreshLayout圆圈设置颜色，后面的几个函数均为swipeRefreshLayout的函数，这里做一个代理
     */
    public void setColorSchemeResources(@ColorRes int... colorResIds) {
        if(mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setColorSchemeResources(colorResIds);
        }
    }

    public void setProgressBackgroundColorSchemeColor(@ColorInt int color) {
        if(mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(color);
        }
    }

    public void setProgressBackgroundColorSchemeResource(@ColorRes int colorRes) {
        if(mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(colorRes);
        }
    }

    public void setRefreshing(boolean refreshing){
        if(mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }
    }

    /**
     * Set the distance to trigger a sync in dips
     *
     * @param distance
     */
    public void setDistanceToTriggerSync(int distance){
        if(mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setDistanceToTriggerSync(distance);
        }
    }

    public void setProgressViewOffset(boolean scale, int start, int end) {
        if(mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setProgressViewOffset(scale, start, end);
        }
    }

    public void setProgressViewEndTarget(boolean scale, int end) {
        if(mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setProgressViewEndTarget(scale, end);
        }
    }

    public void setSize(int size) {
        if(mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setSize(size);
        }
    }

    /**
     * 从这里开始为NestedScrollingParentHelper的函数
     */
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        if(orientationMode == ORIENTATION_HORIZONTAL){
            return isEnabled() && !(refreshState == STATE_ON_REFRESH) && !(refreshState == STATE_ON_RESET)
                    && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_HORIZONTAL) != 0;
        }
        return isEnabled() && !(refreshState == STATE_ON_REFRESH) && !(refreshState == STATE_ON_RESET)
                && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        // 通知父view开始滚动

        if(orientationMode == ORIENTATION_HORIZONTAL){
            startNestedScroll(axes & ViewCompat.SCROLL_AXIS_HORIZONTAL);
        }else{
            startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        }

        mTotalUnconsumed = 0;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if(orientationMode == ORIENTATION_HORIZONTAL){
            if (dx > 0 && mTotalUnconsumed > 0) {
                if (dx > mTotalUnconsumed) {
                    consumed[0] = dx - (int) mTotalUnconsumed;
                    mTotalUnconsumed = 0;
                } else {
                    mTotalUnconsumed -= dx;
                    consumed[0] = dx;

                }
                updateHeaderHeight(mTotalUnconsumed);
            }

            int[] parentConsumed = mParentScrollConsumed;
            if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
                consumed[0] += parentConsumed[0];
                consumed[1] += parentConsumed[1];
            }

            return;
        }
        if (dy > 0 && mTotalUnconsumed > 0) {
            if (dy > mTotalUnconsumed) {
                consumed[1] = dy - (int) mTotalUnconsumed;
                mTotalUnconsumed = 0;
            } else {
                mTotalUnconsumed -= dy;
                consumed[1] = dy;

            }
            updateHeaderHeight(mTotalUnconsumed);
        }

        int[] parentConsumed = mParentScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    @Override
    public void onStopNestedScroll(View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);

        //头部回滚
        if (mTotalUnconsumed > 0) {
            refreshByState();

            mTotalUnconsumed = 0;
        }

        autoRefreshHeight = 0;

        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        if(orientationMode == ORIENTATION_HORIZONTAL){
            // 让父view先尝试滚动
            dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                    mParentOffsetInWindow);

            final int dx = dxUnconsumed + mParentOffsetInWindow[0];
            if (dx < 0) {
                mTotalUnconsumed += Math.abs(dx);
                updateHeaderHeight(mTotalUnconsumed);
            }

            return;
        }

        // 让父view先尝试滚动
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                mParentOffsetInWindow);

        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        if (dy < 0) {
            mTotalUnconsumed += Math.abs(dy);
            updateHeaderHeight(mTotalUnconsumed);
        }
    }

    /**
     * 从这里开始为NestedScrollingChildHelper的函数
     */
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    /**
     * 获取recyclerView滚动距离
     */
    public int getChildScrollY(){
        int footerHeight = loadMoreEnable ? 0 : mFooterView.getMeasuredHeight();
        return Math.max(0, scrollY - footerHeight);
    }

    public int getChildScrollX(){
        int footerWidth = loadMoreEnable ? 0 : mFooterView.getMeasuredWidth();
        return Math.max(0,scrollX - footerWidth);
    }

    public int getPullMax() {
        return pullMax;
    }

    public void setPullMax(int pullMax) {
        this.pullMax = pullMax;
    }

    public long getBackTopDuration() {
        return backTopDuration;
    }

    public void setBackTopDuration(long backTopDuration) {
        this.backTopDuration = backTopDuration;
    }

    public int getRefreshDistance() {
        return refreshDistance;
    }

    public void setRefreshDistance(int refreshDistance) {
        this.refreshDistance = refreshDistance;
    }
}
