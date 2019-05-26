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
 */

public class MineDescAdapter extends RecyclerView.Adapter<MineDescAdapter.MineDescViewHolder> {

    private static final String TAG = "HomeClassfiyAdapter";

    private List<HomeClassfiyBean> mDatas;
    private Context mContext;
    private IRVOnItemListener<HomeClassfiyBean> mIRVOnItemListener;

    public void setIRVOnItemListener(IRVOnItemListener<HomeClassfiyBean> iRVOnItemListener) {
        this.mIRVOnItemListener = iRVOnItemListener;
    }

    public MineDescAdapter(List<HomeClassfiyBean> datas, Context context) {
        this.mDatas = datas;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MineDescViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MineDescViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recylcerview_mine_desc, parent, false));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MineDescViewHolder holder, int position) {
        HomeClassfiyBean homeClassfiyBean = mDatas.get(position);
        Log.e(TAG, "onBindViewHolder: homeClassfiyBean==" + homeClassfiyBean.toString());
        holder.idTvItemNameMine.setText(homeClassfiyBean.getClassfiyName());
        holder.idIvItemIconLeft.setImageResource(homeClassfiyBean.getImgId());
        if (mIRVOnItemListener != null) {
            holder.idClItemMine.setOnClickListener(view -> mIRVOnItemListener.onItemClick(homeClassfiyBean, position));
        }
    }

    public class MineDescViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.id_iv_item_icon_left)
        ImageView idIvItemIconLeft;
        @BindView(R.id.id_tv_item_name_mine)
        TextView idTvItemNameMine;
        @BindView(R.id.id_cl_item_mine)
        ConstraintLayout idClItemMine;

        public MineDescViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
