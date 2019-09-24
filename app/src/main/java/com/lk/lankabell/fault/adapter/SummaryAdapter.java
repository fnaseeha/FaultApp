package com.lk.lankabell.fault.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lk.lankabell.fault.R;
import com.lk.lankabell.fault.model.MaterialIssued;

import java.text.DecimalFormat;
import java.util.List;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.MyViewHolder> {

    public List<MaterialIssued> list;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvItemCode,tvDesc, tvesn_imei, tvType,tvSeqNo, tvdesc;


        public MyViewHolder(View view) {
            super(view);

            tvSeqNo = (TextView) view.findViewById(R.id.seq_no);
            tvItemCode = (TextView) view.findViewById(R.id.ItemCode);
            tvDesc = (TextView) view.findViewById(R.id.tvDesc);
            tvesn_imei = (TextView) view.findViewById(R.id.tv_esn_imei);
            tvType = (TextView) view.findViewById(R.id.tvType);

        }
    }


    public SummaryAdapter(List<MaterialIssued> details) {
        this.list = details;
    }

    @Override
    public SummaryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_issued_material_summary, parent, false);
        return new SummaryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SummaryAdapter.MyViewHolder holder, int position) {

        MaterialIssued details = list.get(position);

        holder.tvSeqNo.setText(new DecimalFormat("00").format(position+1));

        holder.tvItemCode.setText(details.getMATERIAL_ISSUED_ITEM_NO());
        if(details.getMATERIAL_ISSUED_IS_ASR().equals("1")) {
            holder.tvDesc.setText("Create ASR");
        }else {
            holder.tvDesc.setText("No ASR");
        }

        if(details.getMATERIAL_ISSUED_GIVEN_IMEI_ESN().equals("") ||details.getMATERIAL_ISSUED_GIVEN_IMEI_ESN().equals("null") ) {
            holder.tvesn_imei.setText("-");
        } else{
            holder.tvesn_imei.setText(details.getMATERIAL_ISSUED_GIVEN_IMEI_ESN());
        }

        if(details.getMATERIAL_ISSUED_TYPE().equals("LTEU"))
            holder.tvType.setText("4G/LTE");
        else if(details.getMATERIAL_ISSUED_TYPE().equals("LTES")){
            holder.tvType.setText("SIM");
        }else if(details.getMATERIAL_ISSUED_TYPE().equals("CPHN")){
            holder.tvType.setText("CDMA");
        }else if (details.getMATERIAL_ISSUED_TYPE_OTHER().equals("OTHER")) {
            holder.tvType.setText("OTHER");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}