package com.org.rong.teambition.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.org.rong.teambition.R;
import com.org.rong.teambition.bean.EmailMessage;
import com.org.rong.teambition.utils.ScreenUtils;

import java.util.List;

/**
 * Created by Rong on 2015/9/30.
 */
public class SlideAdapter extends BaseAdapter {
    private List<EmailMessage> mList;
    private LayoutInflater inflater;
    private int screenWidth ;
    private OnDeteleListener onDeleteListener;
    //记录上个操作的view
    private View lastOpView;

    private static final int SCROLL_START_LEFT  =   1;
    private static final int SCROLL_CHECK       =   2;
    private static final int SCROLL_DELETE      =   3;
    private static final int SCROLL_DELETE_AUTO =   4;
    private static final int SCROLL_MENU        =   5;
    private static final int SCROLL_ALERT       =   6;
    private static final int SCROLL_START_RIGHT =   7;
    public SlideAdapter(Context context, List<EmailMessage> data){
        this.mList = data;
        this.inflater = LayoutInflater.from(context);
        screenWidth = ScreenUtils.getScreenWidth(inflater.getContext());
    }

    public void setOnDeleteListener(OnDeteleListener listener){
        this.onDeleteListener = listener;
    }
    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View contentView, ViewGroup viewGroup) {
        Holder holder = null;
        View view ;
        if(contentView == null ) {
            view = inflater.inflate(R.layout.silde_listview_item,viewGroup, false);
            //设置message content的长度为屏幕长度
            holder = new Holder();
            setViewHolder(view, holder);
        }
        else
        {
            holder = (Holder)contentView.getTag();
            view = contentView;
        }

        holder.leftLayout.findViewById(R.id.left_iv).setTag(i);
        holder.needInflate = false;
        //用于向左滑动超过3/4时执行自动删除
        holder.position = i;
        //更新消息页面
        //holder.fromTv.setText(mList.get(i).getFrom());
        //holder.contentTv.setText(mList.get(i).getContent());
        //holder.subjectTv.setText(mList.get(i).getSubject());
        //holder.timeTv.setText(mList.get(i).getData());
        //复位列表的位置
        if(view.getScrollX() != screenWidth)
            resetStatus((HorizontalScrollView)view,false);
        return view;
    }

    /**
     * 复位item
     * @param view
     * @param isSmooth
     */
    private void resetStatus(final HorizontalScrollView view, final boolean isSmooth)
    {
        view.post(new Runnable() {
            @Override
            public void run() {
                if (isSmooth)
                    view.smoothScrollTo(screenWidth, 0);
                else
                    view.scrollTo(screenWidth, 0);
                view.requestLayout();
            }
        });
    }
    class Holder{
        RelativeLayout contentLayout;
        RelativeLayout leftLayout;
        RelativeLayout rightLayout;
        TextView fromTv;
        TextView subjectTv;
        TextView timeTv;
        TextView contentTv;
        int position;
        boolean needInflate;
    }

    private void setViewHolder(View view, Holder holder){
        holder.contentLayout = (RelativeLayout)view.findViewById(R.id.content_ll);
        holder.contentLayout.getLayoutParams().width = screenWidth;
        holder.leftLayout = (RelativeLayout)view.findViewById(R.id.left_ll);
        holder.leftLayout.findViewById(R.id.left_iv).setOnClickListener(clickListener);
        holder.leftLayout.getLayoutParams().width = screenWidth;
        holder.rightLayout = (RelativeLayout) view.findViewById(R.id.right_ll);
        holder.rightLayout.getLayoutParams().width = screenWidth * 3/4;
        holder.fromTv = (TextView)holder.contentLayout.findViewById(R.id.from_tv);
        holder.contentTv = (TextView)holder.contentLayout.findViewById(R.id.content_tv);
        holder.timeTv = (TextView) holder.contentLayout.findViewById(R.id.time_tv);
        holder.subjectTv = (TextView) holder.contentLayout.findViewById(R.id.subject_tv);

        view.setOnTouchListener(onTouchListener);
        view.setTag(holder);
    }
    /**
     * 监听事件
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            switch(view.getId()){
                case R.id.left_iv:
                    int scrollStatus = getScrollStatus(((View)view.getParent().getParent().getParent()).getScrollX());
                    if(scrollStatus == SCROLL_DELETE || scrollStatus == SCROLL_DELETE_AUTO)
                        deleteData(position,(View)view.getParent().getParent().getParent());
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 删除数据
     * @param position
     */
    private void deleteData(final int position, final View view)
    {
        if(onDeleteListener != null)
            onDeleteListener.onDelete(position);

        //生成动画
        Animation.AnimationListener al = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                //删除数据
                mList.remove(position);
                Holder holder = (Holder)view.getTag();
                holder.needInflate = true;
                SlideAdapter.this.notifyDataSetChanged();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        collapse(view, al);
    }

    private void collapse(final View view, Animation.AnimationListener al) {
        final int originHeight = view.getMeasuredHeight();

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1.0f) {
                    view.setVisibility(View.GONE);

                } else {
                    view.getLayoutParams().height = originHeight - (int) (originHeight * interpolatedTime);
                    view.requestLayout();
                }
            }
            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        if (al != null) {
            animation.setAnimationListener(al);
        }
        animation.setDuration(300);
        view.startAnimation(animation);
    }

    /**
     * 滑动事件
     */
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            Holder holder = (Holder)view.getTag();
            switch(motionEvent.getAction())
            {

                case MotionEvent.ACTION_DOWN: {
                    //view.scrollTo(holder.leftLayout.getWidth(),0);
                    //还原上次操作的Item
                    if(lastOpView != null && lastOpView != view)
                    {
                        resetStatus((HorizontalScrollView)lastOpView, true);
                    }
                    lastOpView = view;
                    break;
                }

                case MotionEvent.ACTION_MOVE:{
                    int scrollX = view.getScrollX();
                    if(getScrollStatus(scrollX) == SCROLL_START_LEFT){
                        //向左滑动1/4时
                        holder.leftLayout.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.start_bg));
                        ((ImageView)holder.leftLayout.findViewById(R.id.left_iv)).setImageResource(R.mipmap.selected);
                    }else if(getScrollStatus(scrollX) == SCROLL_CHECK){
                        //向左滑动1/2时
                        holder.leftLayout.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.check_bg));
                        ((ImageView)holder.leftLayout.findViewById(R.id.left_iv)).setImageResource(R.mipmap.selected);
                    }else if(getScrollStatus(scrollX) == SCROLL_DELETE || getScrollStatus(scrollX) == SCROLL_DELETE_AUTO){
                        //向左滑动3/4时
                        holder.leftLayout.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.delete_bg));
                        ((ImageView)holder.leftLayout.findViewById(R.id.left_iv)).setImageResource(R.mipmap.delete);
                    }
                    //向右滑动1/2时
                    else if(getScrollStatus(scrollX) == SCROLL_ALERT){
                        holder.rightLayout.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.alert_bg));
                        ((ImageView)holder.rightLayout.findViewById(R.id.right_iv)).setImageResource(R.mipmap.alert);
                    }else if(getScrollStatus(scrollX) == SCROLL_MENU || getScrollStatus(scrollX) == SCROLL_START_RIGHT){
                        holder.rightLayout.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.menu_bg));
                        ((ImageView)holder.rightLayout.findViewById(R.id.right_iv)).setImageResource(R.mipmap.menu);
                    }
                    break;
                }

                case MotionEvent.ACTION_UP:{
                    //判断当前所处的位置进行适当的处理
                    int scrollX = view.getScrollX();

                    if(getScrollStatus(scrollX) == SCROLL_START_LEFT || getScrollStatus(scrollX) == SCROLL_START_RIGHT){
                        ((HorizontalScrollView)view).smoothScrollTo(screenWidth, 0);
                    }else if(getScrollStatus(scrollX) == SCROLL_CHECK){
                        ((HorizontalScrollView)view).smoothScrollTo(screenWidth * 1/2,0);
                    }else if(getScrollStatus(scrollX) == SCROLL_DELETE){
                        ((HorizontalScrollView)view).smoothScrollTo(screenWidth* 1/4,0);
                    }else if(getScrollStatus(scrollX) == SCROLL_DELETE_AUTO){
                        //执行删除动作
                        ((HorizontalScrollView)view).smoothScrollTo(0,0);
                        deleteData(holder.position, view);
                    }else if(getScrollStatus(scrollX) == SCROLL_ALERT){
                        ((HorizontalScrollView)view).smoothScrollTo(screenWidth * 7/4,0);
                    }else if(getScrollStatus(scrollX) == SCROLL_MENU ){
                        ((HorizontalScrollView)view).smoothScrollTo(screenWidth * 3/2,0);
                    }
                    return true;
                }
                default:
                    break;
            }
            return false;
        }
    };


    /**
     * 获取当前的滑动状态
     * @param scrollX
     * @return
     */
    private int getScrollStatus(int scrollX){
        if(scrollX > screenWidth * 3/4 &&
                scrollX <screenWidth){
            //向左滑动1/4时
            return SCROLL_START_LEFT;
        }else if(scrollX <  screenWidth * 3/4 &&
                scrollX >=  screenWidth * 1/2){
            //向左滑动1/2时
            return SCROLL_CHECK;
        } else if(scrollX <  screenWidth * 1/2 && scrollX >=  screenWidth * 1/4){
            //向左滑动3/4时
            return SCROLL_DELETE;
        }else if(scrollX < screenWidth * 1/4){
            return SCROLL_DELETE_AUTO;
        }
        //向右滑动1/2时
        else if(scrollX < screenWidth * 5/4 &&
                scrollX > screenWidth){
            return SCROLL_START_RIGHT;
        }
        else if(scrollX <=  screenWidth * 3/2&&
                scrollX > screenWidth  * 5/4){
            return SCROLL_MENU;
        }else if(scrollX > screenWidth * 3/2){
            return SCROLL_ALERT;
        }
        else
            return -1;
    }


    public static interface OnDeteleListener{

        /**
         * 数据删除前执行动作
         * @param position
         */
        void onDelete(int position);
    }
}
