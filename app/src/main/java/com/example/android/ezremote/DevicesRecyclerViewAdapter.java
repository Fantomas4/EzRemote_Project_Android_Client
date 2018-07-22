package com.example.android.ezremote;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class DevicesRecyclerViewAdapter extends RecyclerView.Adapter<DevicesRecyclerViewAdapter.MyViewHolder> {

    private List<DetectedDevice> devicesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView computerIcon;
        public TextView deviceId;
        public TextView deviceIp;
        public TextView deviceStatus;

        public MyViewHolder(View view) {
            super(view);
            computerIcon = (ImageView) view.findViewById(R.id.computer_icon);
            deviceId = (TextView) view.findViewById(R.id.computer_id);
            deviceIp = (TextView) view.findViewById(R.id.computer_ip);
            deviceStatus = (TextView) view.findViewById(R.id.computer_status);
        }
    }

    public DevicesRecyclerViewAdapter(List<DetectedDevice> devicesList) {
        this.devicesList = devicesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detected_device_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.computerIcon.setImageResource(R.drawable.ic_computer);
        DetectedDevice device = devicesList.get(position);
        holder.deviceId.setText(device.getDevId());
        holder.deviceIp.setText(device.getDevIp());
        holder.deviceStatus.setText(device.getDevStatus());
    }
}
