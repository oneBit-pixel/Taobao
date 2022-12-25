package com.zxyapp.taobaounion.ui.adatper;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zxyapp.taobaounion.R;
import com.zxyapp.taobaounion.model.domain.IBaseInfo;
import com.zxyapp.taobaounion.model.domain.SelectedContent;
import com.zxyapp.taobaounion.utils.Constants;
import com.zxyapp.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectedPageContentAdapter extends RecyclerView.Adapter<SelectedPageContentAdapter.InnerHolder> {

    private List<SelectedContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> mData=
            new ArrayList<>();
    private OnSelectedPageContentItemClickListener mContentItemClickListener;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_page_content, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        //TODO:
        SelectedContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean itemData = mData.get(position);
        holder.setData(itemData);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContentItemClickListener != null) {
                    mContentItemClickListener.onClickItemClick(itemData);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(SelectedContent content) {
        if (content.getCode()== Constants.SUCCESS_CODE){
            List<SelectedContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> map_data = content.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data();
            this.mData.clear();
            this.mData.addAll(map_data);
            notifyDataSetChanged();
        }
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.selected_cover)
        public ImageView cover;

        @BindView(R.id.selected_off_prise)
        public TextView offPriseTv;

        @BindView(R.id.selected_title)
        public TextView title;


        @BindView(R.id.selected_buy_btn)
        public TextView buyBtn;

        @BindView(R.id.selected_original_prise)
        public TextView originPriseTv;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void setData(SelectedContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean itemData) {
            title.setText(itemData.getTitle());

            String coverPath = UrlUtils.getCoverPath(itemData.getPict_url());
            Glide.with(itemView.getContext()).load(coverPath).into(cover);
            //判断是否有优惠券
            if (TextUtils.isEmpty(itemData.getCoupon_click_url())) {
                originPriseTv.setText("晚啦，没有优惠券了");
                buyBtn.setVisibility(View.GONE);
            }else {
                originPriseTv.setText("原价："+itemData.getZk_final_price());
                buyBtn.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(itemData.getCoupon_info())) {
                offPriseTv.setVisibility(View.GONE);
            }else {
                offPriseTv.setVisibility(View.VISIBLE);
                offPriseTv.setText(itemData.getCoupon_info());
            }
        }
    }

    public void setOnSelectedPageContentItemClickListener(OnSelectedPageContentItemClickListener listener){
        this.mContentItemClickListener=listener;
    }

    public interface OnSelectedPageContentItemClickListener{
        void onClickItemClick(IBaseInfo item);
    }
}
