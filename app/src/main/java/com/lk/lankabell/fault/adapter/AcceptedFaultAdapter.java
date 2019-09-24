package com.lk.lankabell.fault.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lk.lankabell.fault.R;
import com.lk.lankabell.fault.control.ClickListener;
import com.lk.lankabell.fault.model.PendingFaults;
import com.lk.lankabell.fault.view.FaultClosingActivity;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class AcceptedFaultAdapter extends RecyclerView.Adapter<AcceptedFaultAdapter.MyViewHolder> {

    private List<PendingFaults> list;
    private Context mContext;
    private ClickListener listener;
    private Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        public TextView tvSeq,tvRequestToRefId, tvRequestToName, tvRequestToLocation,tvRequestAssignedDate,
                tvRequestSubType,tvMore,time;
        public Button brequestMaterial;
        private RelativeLayout reListClick;
        private WeakReference<ClickListener> listenerRef;
        //public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View view, ClickListener listener) {
            super(view);

            tvRequestToRefId = (TextView) view.findViewById(R.id.ItemCode);
            tvSeq = (TextView) view.findViewById(R.id.seq_no);
            tvRequestToName = (TextView) view.findViewById(R.id.tvDesc);
            tvRequestToLocation = (TextView) view.findViewById(R.id.tv_esn_imei);
            tvRequestAssignedDate = (TextView) view.findViewById(R.id.tvType);
            tvRequestSubType = (TextView) view.findViewById(R.id.desc1);
            time = (TextView) view.findViewById(R.id.time);

            tvMore= (TextView) view.findViewById(R.id.tvMore);
            tvMore.setOnClickListener(this);

            listenerRef = new WeakReference<>(listener);

            reListClick = (RelativeLayout) view.findViewById(R.id.view_foreground);
            reListClick.setOnClickListener(this);

            reListClick.setOnLongClickListener(this);

            //Button
            brequestMaterial = (Button) view.findViewById(R.id.btnCheck);
            brequestMaterial.setOnClickListener(this);
            System.out.println("* context "+ getContext());

        }

        @Override
        public void onClick(View v) {
                listenerRef.get().onPositionClicked(getAdapterPosition(),v);
        }

        @Override
        public boolean onLongClick(View v) {
            listenerRef.get().onPositionClickedLong(getAdapterPosition(),v);
            return false;
        }
    }


    public AcceptedFaultAdapter( Context context, ArrayList<PendingFaults> orderList, Activity activity,ClickListener listener) {
        this.list = orderList;
        this.listener=listener;
        this.mContext=context;
        this.activity = activity;
    }

    @Override
    public AcceptedFaultAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_custom_accepted_list, parent, false);
        return new AcceptedFaultAdapter.MyViewHolder(itemView,listener);
    }

    @Override
    public void onBindViewHolder(AcceptedFaultAdapter.MyViewHolder holder, int position) {

        PendingFaults faults = list.get(position);

        holder.tvSeq.setText(new DecimalFormat("00").format(position+1));
        holder.tvRequestToRefId.setText(faults.getPF_REQUESTTOREFID());
        holder.tvRequestToName.setText(faults.getPF_REQUESTTONAME());
        holder.tvRequestToLocation.setText(faults.getPF_REQUESTTOLOCATION() +" \n" +faults.getPF_REQUESTTOCONTACT());
        holder.tvRequestSubType.setText(faults.getPF_REQUESTSUBTYPE());
        holder.tvRequestAssignedDate.setText(faults.getAF_ACCEPTEDO_DATE());

        if(!(faults.getPF_RESP_TIME()== 0)){
            double pendingHrs = faults.getPF_RESP_TIME();
            int hour = (int)pendingHrs/60;
            int min =(int) pendingHrs%60;
            if(min<0){
                min = min * -1;
            }
            holder.time.setText(hour+" Hrs "+min+" min");
            holder.time.setVisibility(View.VISIBLE);
         //   holder.tvMore.setVisibility(View.GONE);

        }


        if(faults.getACTIVE_SCREEN() !=null) {
            if (faults.getACTIVE_SCREEN().equals("NEW")) {
                holder.brequestMaterial.setText("PENDING");
                holder.brequestMaterial.setBackgroundColor(Color.parseColor("#A90000"));
                holder.tvMore.setVisibility(View.GONE);
            }
            if (faults.getACTIVE_SCREEN().equals("ACCEPTED")) {
                holder.brequestMaterial.setText("ACCEPTED");
                holder.brequestMaterial.setBackgroundColor(Color.parseColor("#BBC400"));
                holder.tvMore.setVisibility(View.GONE);
            }
            if (faults.getACTIVE_SCREEN().equals("COMPLETED")) {
                holder.brequestMaterial.setText("COMPLETED");
                holder.brequestMaterial.setBackgroundColor(Color.parseColor("#229E00"));
                holder.tvMore.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}