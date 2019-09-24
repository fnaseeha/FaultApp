package com.lk.lankabell.fault.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lk.lankabell.fault.R;
import com.lk.lankabell.fault.model.PendingFaults;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PendingFaultAdapter extends RecyclerView.Adapter<PendingFaultAdapter.MyViewHolder> {

    private List<PendingFaults> list;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvSeq,tvRequestToRefId, tvRequestToName, tvRequestToLocation,tvCustomerRatings,tvRequestAssignedDate,tvRequestType;

        public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View view) {
            super(view);

            tvRequestToRefId = (TextView) view.findViewById(R.id.ItemCode);
            tvSeq = (TextView) view.findViewById(R.id.seq_no);
            tvRequestToName = (TextView) view.findViewById(R.id.tvDesc);
            tvRequestToLocation = (TextView) view.findViewById(R.id.tv_esn_imei);
            tvCustomerRatings = (TextView) view.findViewById(R.id.CustomerRatings);
            tvRequestAssignedDate = (TextView) view.findViewById(R.id.tvType);
            tvRequestType = (TextView) view.findViewById(R.id.RequestType);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);

        }
    }


    public PendingFaultAdapter(ArrayList<PendingFaults> orderList) {
        this.list = orderList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_custom_pending, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        PendingFaults faults = list.get(position);

        holder.tvSeq.setText(new DecimalFormat("00").format(position+1));
        holder.tvRequestToRefId.setText(faults.getPF_REQUESTTOREFID());
        holder.tvRequestToName.setText(faults.getPF_REQUESTTONAME());
        holder.tvRequestToLocation.setText(faults.getPF_REQUESTTOLOCATION());
        holder.tvCustomerRatings.setText(faults.getPF_CUSTOMERRATINGS());
        holder.tvRequestAssignedDate.setText(faults.getPF_REQUESTASSIGNEDDATE());

        if(faults.getPF_REQUESTTYPE().toUpperCase().equals("L")){
            holder.tvRequestType.setText("LTE");
        }else if(faults.getPF_REQUESTTYPE().toUpperCase().equals("C")){
            holder.tvRequestType.setText("CDMA");
        }




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeItem(int position) {
        list.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(PendingFaults faults, int position) {
        list.add(position, faults);
        // notify item added by position
        notifyItemInserted(position);
    }
}