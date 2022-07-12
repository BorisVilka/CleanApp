package com.smartbooster.junkcleaner.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.smartbooster.junkcleaner.R;
import com.smartbooster.junkcleaner.model.ChildItem;
import com.smartbooster.junkcleaner.model.GroupItem;
import com.smartbooster.junkcleaner.task.TypeFile;
import com.smartbooster.junkcleaner.task.TypeOpen;
import com.smartbooster.junkcleaner.utils.AnimatedExpandableListView;
import com.smartbooster.junkcleaner.utils.Utils;

import java.io.File;
import java.util.List;

public class CleanAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private final LayoutInflater inflater;
    private final Context mContext;
    private final List<GroupItem> items;
    private final OnGroupClickListener mOnGroupClickListener;

    public CleanAdapter(Context context, List<GroupItem> items,
                        OnGroupClickListener onGroupClickListener) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        this.items = items;
        mOnGroupClickListener = onGroupClickListener;
    }

    @Override
    public ChildItem getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).getItems().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildHolder holder;
        ChildItem item = getChild(groupPosition, childPosition);
        if (convertView == null) {
            holder = new ChildHolder();
            convertView = inflater.inflate(R.layout.item, parent, false);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvName.setSelected(true);
            holder.tvSize = (TextView) convertView.findViewById(R.id.tvSize);
            holder.imgIconApp = (ImageView) convertView.findViewById(R.id.imgIconApp);
            holder.viewIconFile = (RelativeLayout) convertView.findViewById(R.id.viewIconFile);
            holder.imgFileApk = (ImageView) convertView.findViewById(R.id.imgFileApk);
            holder.ckItem = (CheckBox) convertView.findViewById(R.id.ckItem);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }

        holder.tvName.setText(item.getApplicationName());
        holder.tvSize.setText(Utils.formatSize(item.getCacheSize()));
        holder.ckItem.setChecked(item.isCheck());
        holder.ckItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnGroupClickListener.onSelectItem(groupPosition, childPosition, holder.ckItem.isChecked());
            }
        });
        switch (item.getType()) {
            case ChildItem.TYPE_APKS:
                holder.viewIconFile.setVisibility(View.VISIBLE);
                holder.imgIconApp.setVisibility(View.GONE);
                holder.imgFileApk.setImageDrawable(ContextCompat.getDrawable(mContext,
                    R.drawable.ic_android_white_24dp));

                holder.ckItem.setVisibility(View.VISIBLE);
                break;
            case ChildItem.TYPE_DOWNLOAD_FILE:
                holder.viewIconFile.setVisibility(View.VISIBLE);
                holder.imgIconApp.setVisibility(View.GONE);
                holder.imgFileApk.setImageDrawable(ContextCompat.getDrawable(mContext,
                    R.drawable.ic_file_download_white_24dp));

                holder.ckItem.setVisibility(View.VISIBLE);
                break;
            case ChildItem.TYPE_LARGE_FILES:
                holder.viewIconFile.setVisibility(View.VISIBLE);
                holder.imgIconApp.setVisibility(View.GONE);
                holder.imgFileApk.setImageDrawable(ContextCompat.getDrawable(mContext,
                    R.drawable.ic_description_white_24dp));

                holder.ckItem.setVisibility(View.VISIBLE);
                break;
            case ChildItem.TYPE_CACHE:
                holder.viewIconFile.setVisibility(View.GONE);
                holder.imgIconApp.setVisibility(View.VISIBLE);
                holder.imgIconApp.setImageDrawable(item.getApplicationIcon());

                holder.ckItem.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        String path = item.getPath();
        if (path != null) {
            Log.d("Clean Adapter", "path: " + path);
            switch (TypeFile.getType(path)) {
                case TypeFile.IMAGE: {
                    holder.imgFileApk.setVisibility(View.GONE);
                    holder.imgIconApp.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                        .load(Uri.fromFile(new File(path)))
                        .centerCrop()
                        .circleCrop()
                        .into(holder.imgIconApp);
                }
                case TypeFile.VIDEO: {
                    holder.imgFileApk.setVisibility(View.GONE);
                    holder.imgIconApp.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                        .load(Uri.fromFile(new File(path)))
                        .centerCrop()
                        .circleCrop()
                        .into(holder.imgIconApp);
//                    holder.imgFileApk.setImageBitmap(
//                            ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND)
//                    );
//                    MediaMetadataRetriever media = new MediaMetadataRetriever();
//                    media.setDataSource(path);
//                    holder.imgFileApk.setImageBitmap(media.getFrameAtTime());
                }
            }
        }
        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return items.get(groupPosition).getItems().size();
    }

    @Override
    public GroupItem getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final GroupHolder holder;
        GroupItem item = getGroup(groupPosition);
        if (convertView == null) {
            holder = new GroupHolder();
            convertView = inflater.inflate(R.layout.item_header, parent, false);
            holder.tvHeaderSize = (TextView) convertView.findViewById(R.id.tvHeaderSize);
            holder.tvName = (TextView) convertView.findViewById(R.id.item_header_name);
            holder.ckHeader = (CheckBox) convertView.findViewById(R.id.ckHeader);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnGroupClickListener.onGroupClick(groupPosition);
                }
            });
            holder.ckHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnGroupClickListener.onSelectItemHeader(groupPosition, holder.ckHeader.isChecked());
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }

        holder.tvName.setText(item.getTitle());
        holder.tvHeaderSize.setText(Utils.formatSize(item.getTotal()));
        holder.ckHeader.setChecked(item.isCheck());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public interface OnGroupClickListener {
        void onGroupClick(int groupPosition);

        void onSelectItemHeader(int position, boolean isCheck);

        void onSelectItem(int groupPosition, int childPosition, boolean isCheck);
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    private static class ChildHolder {
        private TextView tvName;
        private TextView tvSize;
        private ImageView imgIconApp;
        private RelativeLayout viewIconFile;
        private ImageView imgFileApk;
        private CheckBox ckItem;
    }

    private static class GroupHolder {
        private TextView tvName;
        private TextView tvHeaderSize;
        private CheckBox ckHeader;
    }
}