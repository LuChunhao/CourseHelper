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
import com.bs.coursehelper.bean.User;
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

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentListViewHolder> {
    private static final String TAG = "StudentListAdapter";

    private List<User> mDatas;
    private Context mContext;
    private IRVOnItemListener<User> mIRVOnItemListener;
    private IRVOnLongListener<User> mIRVOnLongListener;

    public void setIRVOnItemListener(IRVOnItemListener<User> iRVOnItemListener) {
        this.mIRVOnItemListener = iRVOnItemListener;
    }

    public void setIRVOnLongListener(IRVOnLongListener<User> iRVOnLongListener) {
        this.mIRVOnLongListener = iRVOnLongListener;
    }

    public StudentListAdapter(List<User> datas, Context context) {
        this.mDatas = datas;
        this.mContext = context;
    }

    @NonNull
    @Override
    public StudentListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StudentListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recylcerview_stud_list, parent, false));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onBindViewHolder(@NonNull StudentListViewHolder holder, int position) {
        User user = mDatas.get(position);
        String uriStr = user.getUserHeadUrl();
        if (!TextUtils.isEmpty(uriStr)) {
            holder.idCivStuHead.setImageURI(Uri.parse(uriStr));
        }
        holder.idTvStuName.setText(user.getUserName());
        if (user.getUserType() == 2) {  // 老师
            String courseName;
            if (null != user.getCourseTeacherBean() && !TextUtils.isEmpty(user.getCourseTeacherBean().getCourseName())) {
                courseName = user.getCourseTeacherBean().getCourseName();
            } else {
                courseName = "暂未新建课程";
            }
            RxTextTool.getBuilder("课程名称：")
                    .append(courseName)
                    .setForegroundColor(mContext.getResources().getColor(R.color.tb_blue1))
                    .setProportion(1.8f)
                    .into(holder.idTvStuCourseNum);
            holder.idTvStuScore.setVisibility(View.GONE);
        } else {
            RxTextTool.getBuilder("已修课程门数：")
                    .append(String.valueOf(user.getUserCourses().size()))
                    .setForegroundColor(mContext.getResources().getColor(R.color.tb_blue1))
                    .setProportion(1.8f)
                    .into(holder.idTvStuCourseNum);
            holder.idTvStuScore.setVisibility(View.VISIBLE);
            RxTextTool.getBuilder("已修课程学分：")
                    .append(String.valueOf(user.getUserCourseScore()))
                    .setForegroundColor(mContext.getResources().getColor(R.color.tb_blue1))
                    .setProportion(1.8f)
                    .into(holder.idTvStuScore);
        }

        holder.idClStuList.setOnClickListener(view -> {
            if (mIRVOnItemListener != null) {
                mIRVOnItemListener.onItemClick(user, position);
            }
        });

        holder.idClStuList.setOnLongClickListener(view -> {
            if (mIRVOnLongListener != null) {
                mIRVOnLongListener.onLongClick(user, position);
            }
            return false;
        });
    }

    public class StudentListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.id_civ_stu_head)
        CircleImageView idCivStuHead;
        @BindView(R.id.id_tv_stu_name)
        TextView idTvStuName;
        @BindView(R.id.id_tv_stu_course_num)
        TextView idTvStuCourseNum;
        @BindView(R.id.id_tv_stu_score)
        TextView idTvStuScore;
        @BindView(R.id.id_cl_stu_list)
        ConstraintLayout idClStuList;

        public StudentListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
