package com.lk.lankabell.fault.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lk.lankabell.fault.R;

import com.lk.lankabell.fault.model.IssuedMaterialDetails;

import java.text.DecimalFormat;
import java.util.List;

public class IssuedMaterialAdapter extends RecyclerView.Adapter<IssuedMaterialAdapter.MyViewHolder> {

    public List<IssuedMaterialDetails> list;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvItemCode,tvItemType, tvesn_imei, tvnoOfUnits,tvSeqNo, tvdesc;


        public MyViewHolder(View view) {
            super(view);
            tvSeqNo = (TextView) view.findViewById(R.id.seq_no);
            tvItemCode = (TextView) view.findViewById(R.id.ItemCode);
            tvItemType = (TextView) view.findViewById(R.id.tvDesc);
            tvesn_imei = (TextView) view.findViewById(R.id.esn_imei);
            tvnoOfUnits = (TextView) view.findViewById(R.id.tvType);
            tvdesc = (TextView) view.findViewById(R.id.tv_esn_imei);


        }
    }


    public IssuedMaterialAdapter(List<IssuedMaterialDetails> details) {
        this.list = details;
    }

    @Override
    public IssuedMaterialAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_dialog_issue_material_list, parent, false);
        return new IssuedMaterialAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(IssuedMaterialAdapter.MyViewHolder holder, int position) {

        IssuedMaterialDetails details = list.get(position);

        holder.tvSeqNo.setText(new DecimalFormat("00").format(position+1));
        holder.tvItemCode.setText(details.getItemCode());
        holder.tvItemType.setText(details.getItemType());
        holder.tvdesc.setText(details.getItemDescription());

        if(details.getSerial().equals("null") ||details.getSerial().equals("") || details.getSerial().equals(null))
            holder.tvesn_imei.setText("-");
        else
            holder.tvesn_imei.setText(details.getSerial());

        holder.tvnoOfUnits.setText("Units - "+new DecimalFormat("00").format(Integer.parseInt(details.getIssuedQty())));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}