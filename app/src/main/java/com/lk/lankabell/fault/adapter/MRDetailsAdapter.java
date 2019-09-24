package com.lk.lankabell.fault.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lk.lankabell.fault.R;
import com.lk.lankabell.fault.control.ClickListenerMaterial;
import com.lk.lankabell.fault.control.Data.MaterialIssuedDAO;
import com.lk.lankabell.fault.model.IssuedMaterialDetails;
import com.lk.lankabell.fault.model.MaterialIssued;
import com.lk.lankabell.fault.model.PendingFaults;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MRDetailsAdapter extends RecyclerView.Adapter<MRDetailsAdapter.MyViewHolder> {

    private List<IssuedMaterialDetails> list;
    private Context mContext;
    private ClickListenerMaterial listener;
    private PendingFaults fault;

    private ArrayList<MaterialIssued> givenList;
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView tvSeq,tvItemCode, tvItemType, tvdesc1,tvQty;
        private CheckBox btnCheck;

        private WeakReference<ClickListenerMaterial> listenerRef;
        //public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View view, ClickListenerMaterial listener) {
            super(view);

            tvItemCode = (TextView) view.findViewById(R.id.ItemCode);
            tvSeq = (TextView) view.findViewById(R.id.seq_no);
            tvItemType = (TextView) view.findViewById(R.id.tvDesc);
            tvdesc1 = (TextView) view.findViewById(R.id.desc1);
            tvQty= (TextView) view.findViewById(R.id.tvQty);
            btnCheck = (CheckBox) view.findViewById(R.id.btnCheck);
            btnCheck.setOnClickListener(this);

            listenerRef = new WeakReference<>(listener);

        }

        @Override
        public void onClick(View v) {
            listenerRef.get().onPositionClicked(getAdapterPosition(),v,btnCheck);
        }
    }


    public MRDetailsAdapter( Context context, ArrayList<IssuedMaterialDetails> orderList,PendingFaults fault, ClickListenerMaterial listener) {
        this.list = orderList;
        this.listener=listener;
        this.mContext=context;
        this.fault = fault;
        this.givenList = new MaterialIssuedDAO(mContext)
                .getAllIssuedMaterialByRequestID(fault.getPF_REQUESTID());
    }

    @Override
    public MRDetailsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_custom_material_request_details, parent, false);
        return new MRDetailsAdapter.MyViewHolder(itemView,listener);
    }

    @Override
    public void onBindViewHolder(MRDetailsAdapter.MyViewHolder holder, int position) {


        IssuedMaterialDetails materialDetails = list.get(position);
        holder.btnCheck.setChecked(false);


        for (MaterialIssued issued: givenList) {

            if(!issued.getMATERIAL_ISSUED_TYPE().equals("OTHER")) {
                if (materialDetails.getItemCode().equals(issued.getMATERIAL_ISSUED_ITEM_NO()) && materialDetails.getsID().equals(issued.getMATERIAL_ISSUED_PIM_ID())) {

                    holder.btnCheck.setChecked(true);
//                Log.v("Pos",position +"");
                }
//                else if (materialDetails.getItemCode().equals(issued.getMATERIAL_ISSUED_ITEM_NO()) && )
            }else{
                if (materialDetails.getItemCode().equals(issued.getMATERIAL_ISSUED_ITEM_NO())) {
                    holder.btnCheck.setChecked(true);
//                Log.v("Pos",position +"");
                }
            }
        }


        holder.tvSeq.setText(new DecimalFormat("00").format(position+1));
        holder.tvItemCode.setText(materialDetails.getItemCode());
        holder.tvItemType.setText(materialDetails.getItemDescription());
        holder.tvdesc1.setText(materialDetails.getSerial());
        holder.tvQty.setText("Qty - "+new DecimalFormat("00").format(Integer.parseInt(materialDetails.getIssuedQty())));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}