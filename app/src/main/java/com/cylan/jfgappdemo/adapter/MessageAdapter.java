package com.cylan.jfgappdemo.adapter;

import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cylan.jfgappdemo.R;
import com.cylan.jfgappdemo.databinding.MessageItemBinding;
import com.cylan.jfgappdemo.datamodel.MessageBean;
import com.superlog.SLog;

import java.util.ArrayList;

/**
 * Created by lxh on 16-8-9.
 */
public class MessageAdapter extends RecyclerView.Adapter<MsgViewHolder> {

    ArrayList<MessageBean> list;
    Fragment fragment;
    OnItemLongClickListener listener = new OnItemLongClickListener() {
        @Override
        public boolean OnLongClick(View view, int position) {
            return false;
        }
    };

    public MessageAdapter(Fragment fragment, ArrayList<MessageBean> list) {
        this.list = list;
        this.fragment = fragment;
    }

    public void setListener(OnItemLongClickListener listener) {
        this.listener = listener;
    }

    @Override
    public MsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SLog.i("onCreateViewHolder");
        MessageItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.message_item, parent, false);
        return new MsgViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MsgViewHolder holder, final int position) {
        MessageBean bean = list.get(position);
        holder.binding.tvInfo.setText(bean.date);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return listener.OnLongClick(v, position);
            }
        });
        for (int i = 0; i < bean.urls.size(); i++) {
            String url = bean.urls.get(i);
            Glide.with(fragment).load(url).into((ImageView) holder.binding.llPicLayout.getChildAt(i));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public interface OnItemLongClickListener {
        boolean OnLongClick(View view, int position);
    }
}


class MsgViewHolder extends RecyclerView.ViewHolder {

    public MessageItemBinding binding;

    public MsgViewHolder(MessageItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }


}
