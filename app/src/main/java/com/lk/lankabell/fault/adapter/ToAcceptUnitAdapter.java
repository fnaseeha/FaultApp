package com.lk.lankabell.fault.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lk.lankabell.fault.R;
import com.lk.lankabell.fault.model.IssuedDetails;

import java.text.DecimalFormat;
import java.util.List;

public class ToAcceptUnitAdapter extends RecyclerView.Adapter<ToAcceptUnitAdapter.MyViewHolder> {

    public List<IssuedDetails> list;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvIssueNo,tvFromLocation, tvIssuedDate, tvnoOfUnits,tvSeqNo;


        public MyViewHolder(View view) {
            super(view);
            tvSeqNo = (TextView) view.findViewById(R.id.seq_no);
            tvIssueNo = (TextView) view.findViewById(R.id.ItemCode);
            tvFromLocation = (TextView) view.findViewById(R.id.tvDesc);
            tvIssuedDate = (TextView) view.findViewById(R.id.tv_esn_imei);
            tvnoOfUnits = (TextView) view.findViewById(R.id.tvType);


        }
    }


    public ToAcceptUnitAdapter(List<IssuedDetails> details) {
        this.list = details;
    }

    @Override
    public ToAcceptUnitAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_custom_to_accept_units_list, parent, false);
        return new ToAcceptUnitAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ToAcceptUnitAdapter.MyViewHolder holder, int position) {

        IssuedDetails details = list.get(position);

        holder.tvSeqNo.setText(new DecimalFormat("00").format(position+1));
        holder.tvIssueNo.setText(details.getPID_ITEMISSUE_NO());
        holder.tvFromLocation.setText(details.getPID_FROM_LOCATION_TYPE());
        holder.tvIssuedDate.setText(details.getPID_ITEM_ISSUED_DATE());
        holder.tvnoOfUnits.setText("Units - "+new DecimalFormat("00").format(Integer.parseInt(details.getPID_TOTAL_UNITS())));

        //holder.tvCustomerRatings.setText(faults.getPF_CUSTOMERRATINGS());



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}