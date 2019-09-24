package com.lk.lankabell.fault.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.lk.lankabell.fault.R;
import com.lk.lankabell.fault.adapter.MyStockAdapter;
import com.lk.lankabell.fault.control.ClickListenerMaterial;
import com.lk.lankabell.fault.control.Data.PendingIssuedMaterialDAO;
import com.lk.lankabell.fault.model.IssuedMaterialDetails;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

public class MyStockActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {

    private RecyclerView recyclerView;

    ArrayList<IssuedMaterialDetails> list;
    MyStockAdapter mrDetailsAdapter;
    private CoordinatorLayout coordinatorLayout;
    private List<String> lastSearches;
    private MaterialSearchBar searchBar;
    String searchText;
    //public static PendingFaults fault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_request_details);
        //getSupportActionBar().setTitle("MY STOCK - ( NEW , REFURBISH )");
        getSupportActionBar().setTitle(Html.fromHtml("<small>MY STOCK -( NEW , REFURBISH )</small>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //coordinator_layout
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        //Pending Faults - Lists code
        recyclerView = (RecyclerView) findViewById(R.id.rvView);
        searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        searchBar.setCardViewElevation(10);
        //enable searchbar callbacks
        searchBar.setOnSearchActionListener(this);


        setRecyclerViewDate("LTEU","");
        final Button b4GLTE = (Button) findViewById(R.id.b4GLTE);
        final Button bCDMA = (Button) findViewById(R.id.bCDMA);
        final Button bSIM = (Button) findViewById(R.id.bSIM);
        final Button bOther = (Button) findViewById(R.id.bOther);


        Searching();

        try {
            //sendStock.putExtra("type","OTHER");
            Intent myIntent = getIntent(); // gets the previously created intent
            String type = myIntent.getStringExtra("type");

            switch (type){
                case "OTHER":{
                    list.clear();
                    searchBar.setText("");
                    searchBar.setPlaceHolder("Search By ItemCode");
                    searchBar.setHint("CDHWCU010111");
                    searchBar.clearFocus();
                    recyclerView.getAdapter().notifyDataSetChanged();
                    Searching();

                    b4GLTE.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                    bCDMA.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                    bSIM.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                    bOther.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                    setRecyclerViewDate("OTHER",searchText);
                }break;
                case "CDMA":{
                    searchBar.setPlaceHolder("Search By ESN Number");
                    searchBar.setHint("69E0E06");
                    searchBar.setText("");
                    list.clear();
                    recyclerView.getAdapter().notifyDataSetChanged();
                    Searching();

                    b4GLTE.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                    bCDMA.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    bSIM.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                    bOther.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));

                    setRecyclerViewDate("CPHN",searchText);
                }break;
                case "SIM":{
                    searchBar.setText("");
                    searchBar.setPlaceHolder("Search By IMSI Number");
                    searchBar.setHint("413040116074476");
                    list.clear();
                    recyclerView.getAdapter().notifyDataSetChanged();
                    Searching();

                    b4GLTE.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                    bCDMA.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                    bSIM.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    bOther.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));

                    setRecyclerViewDate("LTES",searchText);
                }break;
                case "LTE":{
                    searchBar.setText("");
                    searchBar.setPlaceHolder("Search By IMEI Number");
                    searchBar.setHint("866855027643758");
                    list.clear();
                    recyclerView.getAdapter().notifyDataSetChanged();
                    Searching();

                    b4GLTE.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    bCDMA.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                    bSIM.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                    bOther.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));

                    setRecyclerViewDate("LTEU",searchText);
                }break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }



        b4GLTE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBar.setPlaceHolder("Search By IMEI Number");
                searchBar.setHint("866855027643758");
                searchBar.setText("");
                list.clear();
                searchBar.clearFocus();
                recyclerView.getAdapter().notifyDataSetChanged();
                Searching();

                b4GLTE.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                bCDMA.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bSIM.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bOther.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));

                setRecyclerViewDate("LTEU",searchText);
            }
        });



        bCDMA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBar.setPlaceHolder("Search By ESN Number");
                searchBar.setHint("69E0E06");
                searchBar.setText("");
                searchBar.clearFocus();
                // Clear collection..
                list.clear();
                recyclerView.getAdapter().notifyDataSetChanged();
                Searching();

                b4GLTE.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bCDMA.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                bSIM.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bOther.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));

                setRecyclerViewDate("CPHN",searchText);
            }
        });



        bSIM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBar.setPlaceHolder("Search By IMSI Number");
                searchBar.setHint("413040116074476");
                searchBar.clearFocus();
                searchBar.setText("");
                list.clear();
                recyclerView.getAdapter().notifyDataSetChanged();
                Searching();

                b4GLTE.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bCDMA.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bSIM.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                bOther.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));

                setRecyclerViewDate("LTES",searchText);
            }
        });


        bOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBar.setPlaceHolder("Search By ItemCode");
                searchBar.setHint("CDHWCU010111");
                searchBar.clearFocus();
                searchBar.setText("");
                list.clear();
                recyclerView.getAdapter().notifyDataSetChanged();
                Searching();

                b4GLTE.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bCDMA.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bSIM.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                bOther.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                setRecyclerViewDate("OTHER",searchText);
            }
        });
    }

    private void Searching() {
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                System.out.println("* searching onchange ... "+s.toString());
                searchText = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private  void setRecyclerViewDate(final String type,String searchtext){

        System.out.println("* searchtext "+searchtext);
        if(searchtext.equals("")){
            list = new PendingIssuedMaterialDAO(this).getAllAcceptedMaterial(type);
        }else{
            list = new PendingIssuedMaterialDAO(this).searchMaterial(type,searchtext);
        }

        mrDetailsAdapter = new MyStockAdapter(MyStockActivity.this,list,new ClickListenerMaterial() {
            @Override
            public void onPositionClicked(int position, View v, CheckBox checkBox) { }});

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mrDetailsAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        //set Data and refrese adapter
        mrDetailsAdapter.notifyDataSetChanged();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //save last queries to disk
        //saveSearchSuggestionToDisk(searchBar.getLastSuggestions());
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        String s = enabled ? "enabled" : "disabled";
        Toast.makeText(MyStockActivity.this, "Search " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        System.out.println("* Finished "+text);
        startSearch(text.toString(), true, null, true);
    }

    @Override
    public void onButtonClicked(int buttonCode) {
        System.out.println("* buttonCode "+buttonCode);

    }
}