package com.bs.coursehelper.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bs.coursehelper.R;
import com.bs.coursehelper.bean.CourseUserBean;
import com.bs.coursehelper.listener.IRVOnItemListener;
import com.bs.coursehelper.listener.IRVViewOnClickListener;
import com.vondear.rxtool.RxTextTool;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 */

public class CourseStuScoreAdapter extends RecyclerView.Adapter<CourseStuScoreAdapter.CourseStuScoreViewHolder> {

    private List<CourseUserBean> mDatas;
    private Context mContext;
    private IRVOnItemListener<CourseUserBean> mIRVOnItemListener;

    private IRVViewOnClickListener<CourseUserBean> mIRVViewOnClickListener;

    public void setIRVOnItemListener(IRVOnItemListener<CourseUserBean> iRVOnItemListener) {
        this.mIRVOnItemListener = iRVOnItemListener;
    }

    public void setIRVViewOnClickListener(IRVViewOnClickListener<CourseUserBean> iRVViewOnClickListener) {
        this.mIRVViewOnClickListener = iRVViewOnClickListener;
    }

    public CourseStuScoreAdapter(List<CourseUserBean> datas, Context context) {
        this.mDatas = datas;
        this.mContext = context;
    }

    @NonNull
    @Override
    public CourseStuScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CourseStuScoreViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recylcerview_course_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CourseStuScoreViewHolder holder, int position) {
        CourseUserBean courseInfoBean = mDatas.get(position);
        holder.idTvCourseName.setText(courseInfoBean.getName());
        holder.idTvTeacherName.setText(courseInfoBean.getTeacher());
        RxTextTool.getBuilder("课程成绩：")
                .append(String.valueOf(courseInfoBean.getCourseMark()))
                .setForegroundColor(mContext.getResources().getColor(R.color.tb_blue1))
                .setProportion(1.8f)
                .append("  课程学分：")
                .append(String.valueOf(courseInfoBean.getCourseScore()))
                .setForegroundColor(mContext.getResources().getColor(R.color.tb_blue1))
                .setProportion(1.8f)
                .into(holder.idTvStudentNum);
        holder.idClHomeList.setOnClickListener(view -> {
            if (mIRVOnItemListener != null) {
                mIRVOnItemListener.onItemClick(courseInfoBean, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class CourseStuScoreViewHolder extends RecyclerView.ViewHolder {

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

        public CourseStuScoreViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
