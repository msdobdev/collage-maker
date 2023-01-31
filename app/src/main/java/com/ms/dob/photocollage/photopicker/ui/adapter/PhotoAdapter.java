package com.ms.dob.photocollage.photopicker.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.ms.dob.photocollage.photopicker.Define;
import com.ms.dob.photocollage.photopicker.datatype.Photo;
import com.ms.dob.photocollage.photopicker.ui.custom.SquareImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ms.dob.photocollage.R;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
  private final String TAG = PhotoAdapter.class.getSimpleName();

  private List<Photo> mData;
  private ArrayList<Photo> mSelectedPhotos;
  private Set<Integer> mSelectedPhotoPositions;

  private OnPhotoSelectedListener mOnPhotoSelectedListener;
  private OnPhotoUnSelectedListener mOnPhotoUnSelectedListener;
  private OnSelectedMaxListener mOnSelectedMaxListener;

  private int mMaxCount = Define.DEFAULT_MAX_COUNT;

  private int mSelectedResId = R.color.photo_selected_shadow;

  public PhotoAdapter() {
    mSelectedPhotos = new ArrayList<>();
    mSelectedPhotoPositions = new HashSet<>();
  }

  public List<Photo> getData() {
    return mData;
  }

  public void setData(List<Photo> data) {
    mData = data;
  }

  public int getMaxCount() {
    return mMaxCount;
  }

  public void setMaxCount(int maxCount) {
    mMaxCount = maxCount;
  }

  public void setOnSelectedMaxListener(OnSelectedMaxListener onSelectedMaxListener) {
    mOnSelectedMaxListener = onSelectedMaxListener;
  }

  public void setOnPhotoSelectedListener(OnPhotoSelectedListener onPhotoSelectedListener) {
    mOnPhotoSelectedListener = onPhotoSelectedListener;
  }

  public void setOnPhotoUnSelectedListener(OnPhotoUnSelectedListener onPhotoUnSelectedListener) {
    mOnPhotoUnSelectedListener = onPhotoUnSelectedListener;
  }

  public ArrayList<Photo> getSelectedPhotos() {
    return mSelectedPhotos;
  }

  public ArrayList<String> getSelectedPhotoPaths() {
    ArrayList<String> paths = new ArrayList<>();
    for (Photo photo : mSelectedPhotos) {
      paths.add(photo.getPath());
    }

    return paths;
  }

  public void refreshData(List<Photo> dataNew) {
    mData = dataNew;
    mSelectedPhotos.clear();
    notifyDataSetChanged();
  }

  public void reset(){
    mSelectedPhotos.clear();
    mSelectedPhotoPositions.clear();

    notifyDataSetChanged();
  }

  @Override
  public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.poiphoto_item_photo, parent, false);
    return new PhotoViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(final PhotoViewHolder holder, final int position) {

    holder.mShadow.setBackgroundResource(mSelectedResId);


    if (!mSelectedPhotoPositions.contains(position) && holder.mShadow.getVisibility() == View.VISIBLE) {
      holder.mShadow.setVisibility(View.GONE);
    } else if (mSelectedPhotoPositions.contains(position) && holder.mShadow.getVisibility() != View.VISIBLE) {
      holder.mShadow.setVisibility(View.VISIBLE);
    }

    final String path = mData.get(position).getPath();

    Picasso.with(holder.itemView.getContext())
            .load("file:///" + path)
            .fit()
            .centerInside()
            .into(holder.mIvPhoto, new Callback() {
              @Override
              public void onSuccess() {

              }

              @Override
              public void onError() {
                Log.e(TAG, "Picasso failed load photo -> " + path);
              }
            });

    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int pos = holder.getAdapterPosition();
        if (mSelectedPhotoPositions.contains(pos)) {

          mSelectedPhotoPositions.remove(pos);
          mSelectedPhotos.remove(mData.get(pos));
          if (mOnPhotoUnSelectedListener != null) {
            mOnPhotoUnSelectedListener.onPhotoUnSelected(mData.get(pos), pos);
          }

          holder.mShadow.setVisibility(View.GONE);

        } else {
          if (mSelectedPhotoPositions.size() >= mMaxCount) {
            if (mOnSelectedMaxListener != null) mOnSelectedMaxListener.onSelectedMax();
          } else {
            mSelectedPhotoPositions.add(pos);
            mSelectedPhotos.add(mData.get(pos));
            if (mOnPhotoSelectedListener != null) {
              mOnPhotoSelectedListener.onPhotoSelected(mData.get(pos), pos);
            }
            holder.mShadow.setVisibility(View.VISIBLE);
          }
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return mData == null ? 0 : mData.size();
  }

  public void setSelectedResId(int selectedResId) {
    mSelectedResId = selectedResId;
  }

  public static class PhotoViewHolder extends RecyclerView.ViewHolder {
    SquareImageView mIvPhoto;
    View mShadow;

    public PhotoViewHolder(View itemView) {
      super(itemView);

      mIvPhoto = (SquareImageView) itemView.findViewById(R.id.iv_photo);
      mShadow = itemView.findViewById(R.id.shadow);
    }
  }

  public interface OnPhotoSelectedListener {
    void onPhotoSelected(Photo photo, int position);
  }

  public interface OnPhotoUnSelectedListener {
    void onPhotoUnSelected(Photo photo, int position);
  }

  public interface OnSelectedMaxListener {
    void onSelectedMax();
  }
}
