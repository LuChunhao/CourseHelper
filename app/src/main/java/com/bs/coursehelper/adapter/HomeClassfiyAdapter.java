package com.bs.coursehelper.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bs.coursehelper.R;
import com.bs.coursehelper.bean.HomeClassfiyBean;
import com.bs.coursehelper.listener.IRVOnItemListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 首页的头部分类
 */

public class HomeClassfiyAdapter extends RecyclerView.Adapter<HomeClassfiyAdapter.HomeClassfiyViewHolder> {
    private static final String TAG = "HomeClassfiyAdapter";

    private List<HomeClassfiyBean> mDatas;
    private Context mContext;
    private IRVOnItemListener<HomeClassfiyBean> mIRVOnItemListener;

    public void setIRVOnItemListener(IRVOnItemListener<HomeClassfiyBean> iRVOnItemListener) {
        this.mIRVOnItemListener = iRVOnItemListener;
    }

    public HomeClassfiyAdapter(List<HomeClassfiyBean> datas, Context context) {
        this.mDatas = datas;
        this.mContext = context;
    }

    @NonNull
    @Override
    public HomeClassfiyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeClassfiyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recylcerview_home_classfiy, parent, false));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onBindViewHolder(@NonNull HomeClassfiyViewHolder holder, int position) {
        HomeClassfiyBean homeClassfiyBean = mDatas.get(position);
        Log.e(TAG, "onBindViewHolder: homeClassfiyBean==" + homeClassfiyBean.toString());
        holder.idIvHomeClassfiy.setImageResource(homeClassfiyBean.getImgId());
        holder.idTvHomeClassfiy.setTextColor(homeClassfiyBean.getColorId());
        holder.idTvHomeClassfiy.setText(homeClassfiyBean.getClassfiyName());
        holder.idClHomeClassfiy.setOnClickListener(view -> {
            if (mIRVOnItemListener != null) {
                mIRVOnItemListener.onItemClick(homeClassfiyBean, position);
            }
        });
    }

    public class HomeClassfiyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.id_iv_home_classfiy)
        ImageView idIvHomeClassfiy;
        @BindView(R.id.id_tv_home_classfiy)
        TextView idTvHomeClassfiy;
        @BindView(R.id.id_cl_home_classfiy)
        ConstraintLayout idClHomeClassfiy;

        public HomeClassfiyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
