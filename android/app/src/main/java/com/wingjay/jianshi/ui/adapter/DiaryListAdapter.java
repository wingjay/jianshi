package com.wingjay.jianshi.ui.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.bean.Diary;
import com.wingjay.jianshi.db.DbUtil;
import com.wingjay.jianshi.ui.ViewActivity;
import com.wingjay.jianshi.ui.base.BaseActivity;
import com.wingjay.jianshi.util.ConstantUtil;

import java.util.List;

/**
 * Created by wingjay on 9/30/15.
 */
public class DiaryListAdapter extends RecyclerView.Adapter<DiaryListAdapter.DiaryListViewHolder> {

    private final BaseActivity context;
    private final List<Diary> diaryList;

    public class DiaryListViewHolder extends RecyclerView.ViewHolder {

        public View diaryItem;
        public TextView title, content, date;
        public DiaryListViewHolder(View itemView) {
            super(itemView);
            diaryItem = itemView.findViewById(R.id.diary_item);
            title = (TextView) itemView.findViewById(R.id.item_diary_title);
            content = (TextView) itemView.findViewById(R.id.item_diary_content);
            date = (TextView) itemView.findViewById(R.id.item_diary_date);
        }
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
    public void onBindViewHolder(DiaryListViewHolder diaryListViewHolder, int i) {
        final Diary d = diaryList.get(i);
        diaryListViewHolder.title.setText(d.getTitle());
        diaryListViewHolder.content.setText(d.getContent());
        diaryListViewHolder.date.setText(d.getChineseCreatedTime());
        diaryListViewHolder.diaryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = ViewActivity.createIntent(context, d.getId());
                context.startActivityForResult(i, ConstantUtil.REQUEST_CODE_VIEW_DIARY_FROM_LIST);
            }
        });
        final int index = i;
        diaryListViewHolder.diaryItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.do_you_want_to_delete_diary)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (DbUtil.deleteDiary(d.getId()) == 1) {
                                    diaryList.remove(index);
                                    //notifyDataSetChanged();
                                    notifyItemRemoved(index);
                                    notifyItemRangeChanged(index, diaryList.size());
                                } else {
                                    Toast.makeText(context, R.string.delete_diary_failure,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .create()
                        .show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }

}
