package com.lk.lankabell.fault.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lk.lankabell.fault.R;
import com.lk.lankabell.fault.control.ClickListenerMaterial;
import com.lk.lankabell.fault.model.IssuedMaterialDetails;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MyStockAdapter extends RecyclerView.Adapter<MyStockAdapter.MyViewHolder> {

    private List<IssuedMaterialDetails> list;
    private Context mContext;
    private ClickListenerMaterial listener;


   // private ArrayList<MaterialIssued> givenList;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView tvSeq,tvItemCode, tvItemType, tvQty,tvType,tvSerial;
        //private CheckBox btnCheck;

        private WeakReference<ClickListenerMaterial> listenerRef;
        //public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View view, ClickListenerMaterial listener) {
            super(view);

            tvItemCode = (TextView) view.findViewById(R.id.ItemCode);
            tvSeq = (TextView) view.findViewById(R.id.seq_no);
            tvItemType = (TextView) view.findViewById(R.id.tvDesc);
            //tvdesc1 = (TextView) view.findViewById(R.id.desc1);
            tvQty= (TextView) view.findViewById(R.id.tvQty);
            tvType= (TextView) view.findViewById(R.id.tvType);
            tvSerial= (TextView) view.findViewById(R.id.tvSerial);


            listenerRef = new WeakReference<>(listener);

        }

        @Override
        public void onClick(View v) {
            //listenerRef.get().onPositionClicked(getAdapterPosition(),v,btnCheck);
        }
    }


    public MyStockAdapter(Context context, ArrayList<IssuedMaterialDetails> orderList, ClickListenerMaterial listener) {
        this.list = orderList;
        this.listener=listener;
        this.mContext=context;
        //this.fault = fault;


    }

    @Override
    public MyStockAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_custom_sotck, parent, false);
        return new MyStockAdapter.MyViewHolder(itemView,listener);
    }

    @Override
    public void onBindViewHolder(MyStockAdapter.MyViewHolder holder, int position) {


        IssuedMaterialDetails materialDetails = list.get(position);
        //holder.btnCheck.setVisibility(View.GONE);

        holder.tvSeq.setText(new DecimalFormat("00").format(position+1));
        holder.tvItemCode.setText(materialDetails.getItemCode());
        holder.tvItemType.setText(materialDetails.getItemDescription());
        holder.tvType.setText(materialDetails.getItemType());
        if(materialDetails.getItemType().equals("RB")){
            holder.tvType.setText("REFURBISH");
        }else if(materialDetails.getItemType().equals("NW")){
            holder.tvType.setText("NEW");
        }

        //holder.tvdesc1.setText(materialDetails.getSerial());
        holder.tvQty.setText("Qty - "+new DecimalFormat("00").format(Integer.parseInt(materialDetails.getIssuedQty())));

        if(materialDetails.getSerial().equals(null) || materialDetails.getSerial().equalsIgnoreCase("null")){
            holder.tvSerial.setVisibility(View.INVISIBLE);
        }else{
            holder.tvSerial.setVisibility(View.VISIBLE);
        }
        holder.tvSerial.setText(materialDetails.getSerial());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}