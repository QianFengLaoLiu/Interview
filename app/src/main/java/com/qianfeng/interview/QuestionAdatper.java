package com.qianfeng.interview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Liu Jianping
 *
 * @date : 16/7/29.
 */
public class QuestionAdatper extends BaseAdapter {
    private List<Question> list;
    private LayoutInflater inflater;


    public QuestionAdatper(Context context, List<Question> list) {
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null ? null : list
                .get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QuestionHolder holder = null;
        if (convertView == null)
        {
            holder = new QuestionHolder();

            convertView = inflater.inflate(R.layout.adapter_questions, null);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.item_title);
            holder.tvAnswer = (TextView) convertView.findViewById(R.id.item_answer);
            convertView.setTag(holder);
        }
        else
        {
            holder = (QuestionHolder) convertView.getTag();
        }

        Question question = list.get(position);
        holder.tvTitle.setText(question.getTitle());
        holder.tvAnswer.setText(question.getAnswer());

        return convertView;
    }

    class QuestionHolder
    {
        TextView tvTitle, tvAnswer;
    }

}
