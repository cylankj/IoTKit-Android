package com.cylan.jfgappdemo.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cylan.entity.jniCall.JFGDevice;
import com.cylan.jfgapp.jni.JfgAppCmd;
import com.cylan.jfgappdemo.R;
import com.cylan.jfgappdemo.databinding.DevItemBinding;
import com.superlog.SLog;

/**
 * Created by lxh on 16-7-23.
 */
public class DevsAdapter extends RecyclerView.Adapter<DevViewHolder> {

    private JFGDevice[] device;

    public DevsAdapter(JFGDevice[] device) {
        this.device = device;
    }

    public void setDevice(JFGDevice[] device) {
        this.device = device;
    }

    public int getPositionBySn(String sn) {
        for (int i = 0; i < device.length; i++) {
            if (device[i].uuid.equals(sn)) {
                return i;
            }
        }
        return -1;
    }


    public JFGDevice[] getDevice() {
        return device;
    }

    Context ctx ;

    @Override
    public DevViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        DevItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.dev_item, parent, false);
        return new DevViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(DevViewHolder holder, final int position) {
        final JFGDevice dev = device[position];
        holder.getBinding().tvDevAlias.setText(dev.alias);
        if (dev.base != null) {
            holder.getBinding().tvSsid.setText(dev.base.netName);
        }
        setImageResourceByType(dev.pid, holder.getBinding().ivDevIcon);
        if (simpleListener != null) {
            holder.getBinding().getRoot().setTag(position);
            holder.getBinding().getRoot().setOnClickListener(simpleListener);
        }
        holder.getBinding().executePendingBindings();
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);

    }

    @Override
    public int getItemCount() {
        return device.length;
    }


    private void setImageResourceByType(int type, ImageView imageView) {
        switch (type) {
            case 4:
            case 5:
            case 7:
            case 13:
                imageView.setImageResource(R.drawable.icon_camera);
                break;
            case 86:
            case 18:
                imageView.setImageResource(R.drawable.icon_camera_1);
                break;
            case 6:
            case 14:
            case 15:
                imageView.setImageResource(R.drawable.icon_doorbell);
                break;
            case 11:
                imageView.setImageResource(R.drawable.icon_magnetic);
                break;
            case 8:
                imageView.setImageResource(R.drawable.icon_album);
            default:
                break;
        }
    }

    SimpleListener simpleListener;

    public void setSimpleListener(SimpleListener simpleListener) {
        this.simpleListener = simpleListener;
    }

    public static class SimpleListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

        }
    }
}


class DevViewHolder extends RecyclerView.ViewHolder {

    private DevItemBinding binding;

    public DevViewHolder(DevItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public DevItemBinding getBinding() {
        return binding;
    }
}