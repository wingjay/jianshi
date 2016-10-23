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

    View diaryItem;
    TextView title, content, date;

    DiaryListViewHolder(View itemView) {
      super(itemView);
      diaryItem = itemView.findViewById(R.id.diary_item);
      title = (TextView) itemView.findViewById(R.id.item_diary_title);
      content = (TextView) itemView.findViewById(R.id.item_diary_content);
      date = (TextView) itemView.findViewById(R.id.item_diary_date);
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
  public void onBindViewHolder(DiaryListViewHolder diaryListViewHolder, final int i) {
    final Diary d = diaryList.get(i);
    diaryListViewHolder.title.setText(d.getTitle());
    diaryListViewHolder.content.setText(d.getContent());
    diaryListViewHolder.date.setText(d.getChineseCreatedTime());
    diaryListViewHolder.diaryItem.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (listener != null) {
          listener.onItemClick(i);
        }
      }
    });
    diaryListViewHolder.diaryItem.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        if (listener != null) {
          listener.onItemLongClick(i);
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
    void onItemClick(int position);
    void onItemLongClick(int position);
  }

}
