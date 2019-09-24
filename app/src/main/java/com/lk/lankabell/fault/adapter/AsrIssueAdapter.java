package com.lk.lankabell.fault.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lk.lankabell.fault.R;
import com.lk.lankabell.fault.control.ClickListenerMaterial;
import com.lk.lankabell.fault.control.Data.MaterialIssuedDAO;
import com.lk.lankabell.fault.model.MaterialIssued;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class AsrIssueAdapter extends RecyclerView.Adapter<AsrIssueAdapter.MyViewHolder>{

    ArrayList<MaterialIssued> materialIssuedList = new ArrayList<>();
    private ClickListenerMaterial listener;
    Context context;

    public AsrIssueAdapter(ArrayList<MaterialIssued> materialIssuedList,Context context, ClickListenerMaterial listener) {
        this.materialIssuedList = materialIssuedList;
        this.listener=listener;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_asr_item, parent, false);
        return new AsrIssueAdapter.MyViewHolder(itemView,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        MaterialIssued materialIssued = materialIssuedList.get(position);

        holder.tvSerial.setText(new MaterialIssuedDAO(context).getDescription(materialIssued.getMATERIAL_ISSUED_ITEM_NO()));

        holder.seq_no.setText(materialIssued.getMATERIAL_ISSUED_ID());

        String type = materialIssued.getMATERIAL_ISSUED_TYPE();

        if(type.equals("NW")){
            holder.tvModel.setText("NEW");
            holder.tvModel.setVisibility(View.VISIBLE);
        }else if(type.equals("RB")){
            holder.tvModel.setText("REFURBISH");
            holder.tvModel.setVisibility(View.VISIBLE);

        }else if(type.equals("LTES") ||type.equals("LTEU") || type.equals("CPHN") ){
            holder.tvModel.setVisibility(View.GONE);
        }else{
            holder.tvModel.setText(materialIssued.getMATERIAL_ISSUED_TYPE());
            holder.tvModel.setVisibility(View.VISIBLE);
        }

        holder.ItemCode.setText(materialIssued.getMATERIAL_ISSUED_ITEM_NO());
        holder.tvIssue_no.setText(materialIssued.getMATERIAL_ISSUED_GIVEN_IMEI_ESN());
    }

    @Override
    public int getItemCount() {
        return materialIssuedList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView ItemCode,tvSerial,tvModel,tvIssue_no,seq_no;
        CheckBox btnCheck;
        private WeakReference<ClickListenerMaterial> listenerRef;

       public MyViewHolder(View view,ClickListenerMaterial listener){
           super(view);
           seq_no = view.findViewById(R.id.seq_no);
           ItemCode = view.findViewById(R.id.ItemCode);
           tvSerial = view.findViewById(R.id.tvSerial);
           tvModel = view.findViewById(R.id.tvModel);
           btnCheck = view.findViewById(R.id.btnCheck);
           tvIssue_no = view.findViewById(R.id.tvIssue_no);

           btnCheck.setOnClickListener(this);

           listenerRef = new WeakReference<>(listener);
       }


        @Override
        public void onClick(View v) {
            listenerRef.get().onPositionClicked(getAdapterPosition(),v,btnCheck);
        }
    }
}
