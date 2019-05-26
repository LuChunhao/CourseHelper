package com.bs.coursehelper.bean;

/**
 */

public class HomeClassfiyBean {
    private int imgId;
    private int colorId;
    private String classfiyName;

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getClassfiyName() {
        return classfiyName;
    }

    public void setClassfiyName(String classfiyName) {
        this.classfiyName = classfiyName;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    @Override
    public String toString() {
        return "HomeClassfiyBean{" +
                "imgId=" + imgId +
                ", colorId=" + colorId +
                ", classfiyName='" + classfiyName + '\'' +
                '}';
    }
}
