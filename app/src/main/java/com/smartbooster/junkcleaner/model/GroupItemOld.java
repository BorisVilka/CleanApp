package com.smartbooster.junkcleaner.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class GroupItemOld implements Parcelable {
    public static final int TYPE_FILE = 0;
    public static final int TYPE_CACHE = 1;

    private String title;
    private long total;
    private boolean isCheck;
    private int type;
    private List<ChildItemOld> items = new ArrayList<>();

    public GroupItemOld() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public List<ChildItemOld> getItems() {
        return items;
    }

    public void setItems(List<ChildItemOld> items) {
        this.items = items;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
