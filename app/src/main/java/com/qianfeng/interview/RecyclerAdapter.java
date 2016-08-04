package com.qianfeng.interview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liu Jianping
 *
 * @date : 16/3/18.
 */
public class RecyclerAdapter extends
        RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>
{
    private LayoutInflater inflater;

    private List<User> list;

    // 选中项
    private List<Integer> checked = new ArrayList<>();

    private Context context;

    private IOnClickListener iOnClickListener;
    private IOnLongClickListener iOnLongClickListener;

    public RecyclerAdapter(Context context, List<User> userList)
    {
        this.context = context;
        inflater = LayoutInflater.from(context);
        list = userList;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = inflater.inflate(R.layout.adapter_recycler, parent,
                false);

        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder,
            final int position)
    {
        User user = list.get(position);

        // 选中项显示红色
        if (checked.contains(position))
        {
            holder.textView.setBackgroundColor(context.getResources().getColor(
                    R.color.color_current));
        }
        // 已面试的
        else if (user.getState() == UserHelper.State.INTERVIEWED)
        {
            holder.textView.setBackgroundColor(context.getResources().getColor(
                    R.color.color_interview));
        }
        // 默认的
        else
        {
            holder.textView.setBackgroundColor(context.getResources().getColor(
                    R.color.color_normal));
        }

        holder.textView.setText(user.getName());

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iOnClickListener != null) {
                    iOnClickListener.onClick(position, v);
                }
            }
        });

        holder.textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (iOnLongClickListener != null)
                {
                    iOnLongClickListener.onLongClick(position, v);
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView textView;

        public RecyclerViewHolder(View itemView)
        {
            super(itemView);

            textView = (TextView) itemView
                    .findViewById(R.id.adapter_recycler_tv);
        }
    }

    public void setChecked(List<Integer> checked)
    {
        this.checked = checked;
        notifyDataSetChanged();
    }

    public void setIOnClickListener(IOnClickListener onClickListener)
    {
        this.iOnClickListener = onClickListener;
    }

    public void setIOnLongClickListener(IOnLongClickListener onLongClickListener)
    {
        this.iOnLongClickListener = onLongClickListener;
    }

    public interface IOnClickListener
    {
        void onClick(int position, View view);
    }

    public interface IOnLongClickListener
    {
        void onLongClick(int position, View view);
    }



}
