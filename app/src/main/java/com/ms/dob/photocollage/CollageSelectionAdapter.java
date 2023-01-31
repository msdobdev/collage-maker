package com.ms.dob.photocollage;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.ms.dob.collage.CollageLayout;
import com.ms.dob.collage.SquareCollageView;
import com.ms.dob.photocollage.layout.custom.CollageNumberLayout;
import com.ms.dob.photocollage.layout.skew.NumberSkewLayout;
import com.ms.dob.photocollage.R;

import java.util.ArrayList;
import java.util.List;


public class CollageSelectionAdapter extends RecyclerView.Adapter<CollageSelectionAdapter.CollageViewHolder> {

  private List<CollageLayout> layoutData = new ArrayList<>();
  private List<Bitmap> bitmapData = new ArrayList<>();
  private OnItemClickListener onItemClickListener;

  @Override public CollageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collage_selection, parent, false);
    return new CollageViewHolder(itemView);
  }

  @Override public void onBindViewHolder(CollageViewHolder holder, int position) {
    final CollageLayout collageLayout = layoutData.get(position);

    holder.squareCollageView.setNeedDrawLine(true);
    holder.squareCollageView.setNeedDrawOuterLine(true);
    holder.squareCollageView.setTouchEnable(false);

    holder.squareCollageView.setPuzzleLayout(collageLayout);

    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (onItemClickListener != null) {
          int theme = 0;
          if (collageLayout instanceof NumberSkewLayout) {
            theme = ((NumberSkewLayout) collageLayout).getTheme();
          } else if (collageLayout instanceof CollageNumberLayout) {
            theme = ((CollageNumberLayout) collageLayout).getTheme();
          }
          onItemClickListener.onItemClick(collageLayout, theme);
        }
      }
    });

    if (bitmapData == null) return;

    final int bitmapSize = bitmapData.size();

    if (collageLayout.getAreaCount() > bitmapSize) {
      for (int i = 0; i < collageLayout.getAreaCount(); i++) {
        holder.squareCollageView.addPiece(bitmapData.get(i % bitmapSize));
      }
    } else {
      holder.squareCollageView.addPieces(bitmapData);
    }
  }

  @Override public int getItemCount() {
    return layoutData == null ? 0 : layoutData.size();
  }

  public void refreshData(List<CollageLayout> layoutData, List<Bitmap> bitmapData) {
    this.layoutData = layoutData;
    this.bitmapData = bitmapData;

    notifyDataSetChanged();
  }

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  public static class CollageViewHolder extends RecyclerView.ViewHolder {

    SquareCollageView squareCollageView;

    public CollageViewHolder(View itemView) {
      super(itemView);
      squareCollageView = (SquareCollageView) itemView.findViewById(R.id.puzzle);
    }
  }

  public interface OnItemClickListener {
    void onItemClick(CollageLayout collageLayout, int themeId);
  }
}
