package com.lk.lankabell.fault.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;


import com.lk.lankabell.fault.R;
import com.lk.lankabell.fault.adapter.AcceptedFaultAdapter;
import com.lk.lankabell.fault.adapter.DateManager;
import com.lk.lankabell.fault.control.ClickListener;
import com.lk.lankabell.fault.control.Data.AcceptFaultDAO;
import com.lk.lankabell.fault.control.Data.CustomerPriorityDAO;
import com.lk.lankabell.fault.model.PendingFaults;

import java.util.ArrayList;
import java.util.Collections;

public class PendingPriorityFaults extends AppCompatActivity {


    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_priority_faults);
        getSupportActionBar().setTitle("PENDING PRIORITY FAULTS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.rvView);

        ArrayList<PendingFaults>  acceptedList =  new AcceptFaultDAO(this).getAllAccepted();

        for(PendingFaults faults: acceptedList) {

            double pendingHrs = Double.parseDouble(new CustomerPriorityDAO(this)
                    .getMinmumRespTime(faults.getPF_CUSTOMERRATINGS()))*60 - new DateManager().getHrsGivenDate(faults.getAF_ACCEPTEDO_DATE());


            faults.setPF_RESP_TIME(pendingHrs);
        }

        Collections.sort(acceptedList);

            AcceptedFaultAdapter  acceptedFaultAdapter = new AcceptedFaultAdapter(this, acceptedList,PendingPriorityFaults.this, new ClickListener() {
            @Override
            public void onPositionClicked(int position,View v) {

            }

            @Override
            public void onPositionClickedLong(int position, View view) {

            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(acceptedFaultAdapter);
        recyclerView.setNestedScrollingEnabled(false);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }


}
