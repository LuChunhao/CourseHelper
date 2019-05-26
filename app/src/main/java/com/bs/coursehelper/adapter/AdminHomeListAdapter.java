package com.bs.coursehelper.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bs.coursehelper.R;
import com.bs.coursehelper.bean.MySubject;
import com.bs.coursehelper.listener.IRVOnItemListener;
import com.bs.coursehelper.listener.IRVOnLongListener;
import com.vondear.rxtool.RxTextTool;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 首页的头部分类
 */

public class AdminHomeListAdapter extends RecyclerView.Adapter<AdminHomeListAdapter.HomeListViewHolder> {
    private static final String TAG = "adminHomeListAdapter";

    private List<MySubject> mDatas;
    private Context mContext;
    private IRVOnItemListener<MySubject> mIRVOnItemListener;
    private IRVOnLongListener<MySubject> mIRVOnLongListener;

    public void setIRVOnItemListener(IRVOnItemListener<MySubject> iRVOnItemListener) {
        this.mIRVOnItemListener = iRVOnItemListener;
    }

    public void setIRVOnLongListener(IRVOnLongListener<MySubject> iRVOnLongListener) {
        this.mIRVOnLongListener = iRVOnLongListener;
    }

    public AdminHomeListAdapter(List<MySubject> datas, Context context) {
        this.mDatas = datas;
        this.mContext = context;
    }

    @NonNull
    @Override
    public HomeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recylcerview_admin_home_list, parent, false));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onBindViewHolder(@NonNull HomeListViewHolder holder, int position) {
        MySubject mySubject = mDatas.get(position);
        String uriStr = "";
        if (!TextUtils.isEmpty(uriStr)) {
            holder.idCivTeacherHead.setImageURI(Uri.parse(uriStr));
        }
        holder.idTvCourseName.setText(mySubject.getName());
        holder.idTvTeacherName.setText(mySubject.getTeacher());
        RxTextTool.getBuilder("报名人数：")
                .append(mySubject.getCourseStuApplications() + " (" + mySubject.getCourseStuNum() + ")")
                .setForegroundColor(mContext.getResources().getColor(R.color.tb_blue1))
                .setProportion(1.8f)
                .into(holder.idTvStudentNum);
        holder.idClHomeList.setOnClickListener(view -> {
            if (mIRVOnItemListener != null) {
                mIRVOnItemListener.onItemClick(mySubject, position);
            }
        });
        holder.idClHomeList.setOnLongClickListener(view -> {
            if (mIRVOnLongListener != null) {
                mIRVOnLongListener.onLongClick(mySubject, position);
            }
            return true;
        });
    }

    public class HomeListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.id_civ_teacher_head)
        CircleImageView idCivTeacherHead;
        @BindView(R.id.id_tv_course_name)
        TextView idTvCourseName;
        @BindView(R.id.id_tv_teacher_name)
        TextView idTvTeacherName;
        @BindView(R.id.id_tv_student_num)
        TextView idTvStudentNum;
        @BindView(R.id.id_cl_home_list)
        ConstraintLayout idClHomeList;

        public HomeListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
