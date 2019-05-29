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
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class SelectedCourseAdapter extends RecyclerView.Adapter<SelectedCourseAdapter.SelectedHolder>{

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

    public SelectedCourseAdapter(List<MySubject> datas, Context context) {
        this.mDatas = datas;
        this.mContext = context;
    }

    @NonNull
    @Override
    public SelectedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectedCourseAdapter.SelectedHolder(LayoutInflater.from(mContext).inflate(R.layout.item_audit_class, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedHolder holder, int position) {
        MySubject mySubject = mDatas.get(position);
        String uriStr = "";
        if (!TextUtils.isEmpty(uriStr)) {
            holder.idCivTeacherHead.setImageURI(Uri.parse(uriStr));
        }
        holder.idTvCourseName.setText(mySubject.getName());
        int applications = mySubject.getCourseStuApplications();
        int stuNum = mySubject.getCourseStuNum();
        RxTextTool.getBuilder("蹭课人数：")
                .append(applications + " (" + stuNum + ")")
                .setForegroundColor(mContext.getResources().getColor(R.color.tb_blue1))
                .setProportion(1.8f)
                .into(holder.idTvStudentNum);
        RxTextTool.getBuilder("讲课时间：")
                .append("第" + mySubject.getWeekList().get(0) + "周，周" + mySubject.getDay() + "第" + mySubject.getStart() + "节")
                .setForegroundColor(mContext.getResources().getColor(R.color.tb_blue1))
                .setProportion(1.8f)
                .into(holder.teachPlace);
        holder.container.setOnClickListener(view -> {
            if (mIRVOnItemListener != null) {
                mIRVOnItemListener.onItemClick(mySubject, position);
            }
        });

        holder.container.setOnLongClickListener(view -> {
            if (mIRVOnLongListener != null) {
                mIRVOnLongListener.onLongClick(mySubject, position);
            }
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return null == mDatas ? 0 : mDatas.size();
    }

    public class SelectedHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.id_civ_teacher_head)
        CircleImageView idCivTeacherHead;
        @BindView(R.id.id_tv_course_name)
        TextView idTvCourseName;
        @BindView(R.id.id_tv_student_place)
        TextView teachPlace;
        @BindView(R.id.id_tv_student_num)
        TextView idTvStudentNum;
        @BindView(R.id.container)
        LinearLayout container;

        public SelectedHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
