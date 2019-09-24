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
import com.lk.lankabell.fault.control.ClickListenerUnitInHand;
import com.lk.lankabell.fault.model.UnitInHand;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class unitInHandAdapter extends RecyclerView.Adapter<unitInHandAdapter.MyViewHolder> {
    ArrayList<UnitInHand> AllUnitInHand = new ArrayList<>();
    Context context;
    private ClickListenerUnitInHand listener;

    public unitInHandAdapter(Context context,ArrayList<UnitInHand> AllUnitInHand,ClickListenerUnitInHand listener){
        this.context = context;
        this.AllUnitInHand = AllUnitInHand;
        this.listener = listener;
    }

    @NonNull
    @Override
    public unitInHandAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_unit_in_hand,parent,false);
        return new unitInHandAdapter.MyViewHolder(v,listener);
    }


    @Override
    public void onBindViewHolder(unitInHandAdapter.MyViewHolder holder, int position) {
       UnitInHand unitInHand = AllUnitInHand.get(position);
       holder.ItemCode.setText(unitInHand.getItemCode());
       holder.tvSerial.setText(unitInHand.getSerial());
       holder.IssueDescription.setText(unitInHand.getItemDescription());

       holder.btnCheck.setChecked(false);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView ItemCode,tvSerial,IssueDescription,IssuedBy,IssuedDate;
        CheckBox btnCheck;
        private WeakReference<ClickListenerUnitInHand> listenerRef;

        public MyViewHolder(View itemView, ClickListenerUnitInHand listener){
            super(itemView);
            ItemCode = (TextView)itemView.findViewById(R.id.ItemCode);
            tvSerial = (TextView)itemView.findViewById(R.id.tvSerial);
            IssueDescription = (TextView)itemView.findViewById(R.id.ItemDescription);

            btnCheck = (CheckBox)itemView.findViewById(R.id.btnCheck);
            btnCheck.setOnClickListener(this);
            listenerRef = new WeakReference<>(listener);

        }


        @Override
        public void onClick(View v) {
            listenerRef.get().onPositionClicked(getAdapterPosition(),v,btnCheck);
        }
    }

    @Override
    public int getItemCount() {
        return AllUnitInHand.size();
    }
}
