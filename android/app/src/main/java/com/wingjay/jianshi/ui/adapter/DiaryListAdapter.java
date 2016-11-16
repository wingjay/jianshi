/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.db.model.Diary;
import com.wingjay.jianshi.ui.base.BaseActivity;

import java.util.List;


public class DiaryListAdapter extends RecyclerView.Adapter<DiaryListAdapter.DiaryListViewHolder> {

  private final BaseActivity context;
  private final List<Diary> diaryList;
  private RecyclerClickListener listener;

  class DiaryListViewHolder extends RecyclerView.ViewHolder {
    TextView title, year;
    View contentView;

    DiaryListViewHolder(View itemView) {
      super(itemView);
      contentView = itemView;
      title = (TextView) itemView.findViewById(R.id.diary_title);
      year = (TextView) itemView.findViewById(R.id.year);
    }
  }

  public void setRecyclerClickListener(RecyclerClickListener listener) {
    this.listener = listener;
  }

  public DiaryListAdapter(BaseActivity context, List<Diary> diaryList) {
    this.context = context;
    this.diaryList = diaryList;
  }

  @Override
  public DiaryListAdapter.DiaryListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View v = LayoutInflater.from(context).inflate(R.layout.item_diary_list, viewGroup, false);
    return new DiaryListViewHolder(v);
  }

  @Override
  public void onBindViewHolder(DiaryListViewHolder diaryListViewHolder, int position) {
    final Diary d = diaryList.get(position);
    if (position == 0) {
      diaryListViewHolder.year.setVisibility(View.VISIBLE);
    } else {
      if (diaryList.get(position).getYearCN()
          .equals(diaryList.get(position - 1).getYearCN())) {
        diaryListViewHolder.year.setVisibility(View.GONE);
      } else {
        diaryListViewHolder.year.setVisibility(View.VISIBLE);
      }
    }
    diaryListViewHolder.title.setText(d.getCatalogueTitle());
    diaryListViewHolder.year.setText(d.getYearCN());
    diaryListViewHolder.title.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (listener != null) {
          listener.onItemClick(d);
        }
      }
    });
    diaryListViewHolder.title.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        if (listener != null) {
          listener.onItemLongClick(d);
        }
        return true;
      }
    });
  }

  @Override
  public int getItemCount() {
    return diaryList.size();
  }

  public interface RecyclerClickListener {
    void onItemClick(Diary diary);

    void onItemLongClick(Diary diary);
  }

}
