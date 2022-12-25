package com.zxyapp.taobaounion.ui.adatper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zxyapp.taobaounion.R;
import com.zxyapp.taobaounion.model.domain.HomePagerContent;
import com.zxyapp.taobaounion.model.domain.IBaseInfo;
import com.zxyapp.taobaounion.ui.fragment.HomePagerFragment;
import com.zxyapp.taobaounion.utils.LogUtils;
import com.zxyapp.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomePageContentAdapter extends RecyclerView.Adapter<HomePageContentAdapter.InnerHolder> {
    List<HomePagerContent.DataDTO> mData =new ArrayList<> ();
    private OnListItemClickListener mItemClickListener=null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from (parent.getContext ()).inflate (R.layout.item_home_pager_content, parent, false);
        return new InnerHolder (itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        HomePagerContent.DataDTO dataBean = mData.get (position);
        //设置数据
        holder.setData(dataBean);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {
                    HomePagerContent.DataDTO item = mData.get(position);
                    mItemClickListener.onItemClick(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size ();
    }

    public void setData(List<HomePagerContent.DataDTO> contents) {
        mData.clear ();
        mData.addAll (contents);
        notifyDataSetChanged ();
    }

    public void addData(List<HomePagerContent.DataDTO> contents) {
        //添加之前拿到原先的size
        int olderSize=mData.size ();
        mData.addAll (contents);
        //更新UI
        notifyItemRangeChanged (olderSize,contents.size ());
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView (R.id.goods_cover)
        public ImageView cover;

        @BindView (R.id.goods_title)
        public TextView title;

        @BindView (R.id.goods_off_prise)
        public TextView offPriseTv;

        @BindView (R.id.goods_after_off_prise)
        public TextView finalPriseTv;
        @BindView (R.id.goods_original_prise)
        public TextView originalPrise;
        @BindView (R.id.goods_sells_count)
        public TextView sellCount;

        public InnerHolder(@NonNull View itemView) {
            super (itemView);
            ButterKnife.bind (this,itemView);
        }

        @SuppressLint("StringFormatMatches")
        public void setData(HomePagerContent.DataDTO dataBean) {
            Context context=itemView.getContext ();
            title.setText (dataBean.getTitle ());

            ViewGroup.LayoutParams layoutParams = cover.getLayoutParams();
            int width = layoutParams.width;
            int height=layoutParams.height;
            int coverSize=(width>height?width:height)/2;
//            LogUtils.d(this,"width------>"+width);
//            LogUtils.d(this,"height----->"+height);
//            LogUtils.d (this,"url == >"+dataBean.getPictUrl ());
            Glide.with (context).load (UrlUtils.getCoverPath(dataBean.getPictUrl (),coverSize)).into (cover);
            long couponAmount= dataBean.getCouponAmount ();
            String finalPrise=dataBean.getZkFinalPrice ();
            float resultPrise=Float.parseFloat (finalPrise)-couponAmount;
//            LogUtils.d (this,"resultPrise------>"+resultPrise);
            offPriseTv.setText (String.format (itemView.getContext ().getString (R.string.text_goods_off_prise),dataBean.getCouponAmount ()));
            finalPriseTv.setText (String.format ("%.2f",resultPrise));
            originalPrise.setText (String.format (context.getString (R.string.text_goods_original_prise,finalPrise)));
            //中划线
            originalPrise.setPaintFlags (Paint.STRIKE_THRU_TEXT_FLAG );
            sellCount.setText (String.format (context.getString (R.string.text_goods_sells_count),dataBean.getVolume ()));
        }
    }

    public void setOnListItemOnClickItemListener(OnListItemClickListener listener){
        this.mItemClickListener=listener;
    }

    public interface OnListItemClickListener{
        void onItemClick(IBaseInfo item);
    }
}