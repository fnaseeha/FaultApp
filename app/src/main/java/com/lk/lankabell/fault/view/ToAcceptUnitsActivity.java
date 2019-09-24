package com.lk.lankabell.fault.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.lk.lankabell.fault.R;
import com.lk.lankabell.fault.adapter.ConnectionDetector;
import com.lk.lankabell.fault.adapter.IssuedMaterialAdapter;
import com.lk.lankabell.fault.adapter.RecyclerItemClickListener;
import com.lk.lankabell.fault.adapter.ToAcceptUnitAdapter;
import com.lk.lankabell.fault.adapter.ToastManager;
import com.lk.lankabell.fault.control.Data.AcceptedIssuedMatrlDAO;
import com.lk.lankabell.fault.control.Data.IssuedDetailsDAO;
import com.lk.lankabell.fault.control.Data.PendingIssuedMaterialDAO;
import com.lk.lankabell.fault.control.Data.RejectedIssuedMatrlDAO;
import com.lk.lankabell.fault.control.SwipeController;
import com.lk.lankabell.fault.control.TaskType;
import com.lk.lankabell.fault.control.VolleyCallback;
import com.lk.lankabell.fault.model.IssuedDetails;
import com.lk.lankabell.fault.model.IssuedMaterialDetails;
import com.lk.lankabell.fault.service.SyncData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ToAcceptUnitsActivity extends AppCompatActivity implements VolleyCallback {

    private RecyclerView recyclerView;

    List<IssuedDetails> list;
    ToAcceptUnitAdapter unitAdapter;
    private CoordinatorLayout coordinatorLayout;

    SwipeController swipeController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_unit);
        getSupportActionBar().setTitle("ACCEPT UNITS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        //coordinator_layout
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvView);

        list = new IssuedDetailsDAO(this).getAllIssuedDetailsByNotAccepted();
        System.out.println("* list " + list.size());
        for (IssuedDetails g : list) {
            System.out.println("* g : " + g);
        }
        unitAdapter = new ToAcceptUnitAdapter(list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(unitAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        final IssuedDetails details = list.get(position);

                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Issue No "
                                + details.getPID_ITEMISSUE_NO().toString(), Snackbar.LENGTH_LONG);

                        snackbar.show();

                        showIssueMaterialByIssueCode(details, position);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever

                    }
                })
        );


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //recyclerView.setAdapter(unitAdapter);

//        swipeController = new SwipeController(new SwipeControllerActions() {
//            @Override
//            public void onRightClicked(int position) {
//
//                if(new AcceptedIssuedMatrlDAO(ToAcceptUnitsActivity.this).insertORupdate(list.get(position)) > 0) {
//
//                    Snackbar snackbar = Snackbar.make(coordinatorLayout,
//                            "Issue No " + list.get(position).getPID_ITEMISSUE_NO() + " accepted!", Snackbar.LENGTH_INDEFINITE);
//                    View sbView = snackbar.getView();
//                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
//                    textView.setTextColor(Color.GREEN);
//                    snackbar.show();
//
//                    unitAdapter.list.remove(position);
//                    unitAdapter.notifyItemRemoved(position);
//                    unitAdapter.notifyItemRangeChanged(position, unitAdapter.getItemCount());
//
//                }
//
//            }
//
//            @Override
//            public void onLeftClicked(int position) {
//
//                if(new RejectedIssuedMatrlDAO(ToAcceptUnitsActivity.this).insertORupdate(list.get(position)) > 0) {
//
//                    Snackbar snackbar = Snackbar.make(coordinatorLayout,
//                            "Issue No " + list.get(position).getPID_ITEMISSUE_NO() + " Rejected!", Snackbar.LENGTH_INDEFINITE);
//                    View sbView = snackbar.getView();
//                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
//                    textView.setTextColor(Color.RED);
//                    snackbar.show();
//
//                    unitAdapter.list.remove(position);
//                    unitAdapter.notifyItemRemoved(position);
//                    unitAdapter.notifyItemRangeChanged(position, unitAdapter.getItemCount());
//                }
////                mAdapter.players.remove(position);
////                mAdapter.notifyItemRemoved(position);
////                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
//            }
//        });

//        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
//        itemTouchhelper.attachToRecyclerView(recyclerView);
//
//        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
//                swipeController.onDraw(c);
//            }
//        });
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


    private void showIssueMaterialByIssueCode(final IssuedDetails faults, final int pos) {

        final View dialogView = View.inflate(this, R.layout.show_issued_material, null);
        final Dialog dialog = new Dialog(this, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.rvView);

//        Button bReject = (Button) dialog.findViewById(R.id.bReject);
//        Button bSubmit = (Button) dialog.findViewById(R.id.bSubmit);


        TextView issuedNumber = (TextView) dialog.findViewById(R.id.tvRequestId);
        issuedNumber.setText("Issue Number : " + faults.getPID_ITEMISSUE_NO());

        TextView tvIssuedDate = (TextView) dialog.findViewById(R.id.tvIssuedDate);
        tvIssuedDate.setText("Issue Date : " + faults.getPID_ITEM_ISSUED_DATE());

        final ArrayList<IssuedMaterialDetails> lists = new PendingIssuedMaterialDAO(ToAcceptUnitsActivity.this)
                .getAlItemIssuedMaterialsByIssueCode(faults.getPID_ITEMISSUE_NO());

        IssuedMaterialAdapter faultAdapter = new IssuedMaterialAdapter(lists);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(faultAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        new ToastManager(ToAcceptUnitsActivity.this).info("" + lists.get(position).getItemCode());
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );

        //set Data and refrese adapter
        faultAdapter.notifyDataSetChanged();

        ImageView imageView = (ImageView) dialog.findViewById(R.id.closeDialogImg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                revealShow(dialogView, false, dialog);
            }
        });

        TextView bSubmit = (TextView) dialog.findViewById(R.id.bSubmit);
        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<IssuedDetails> issuedDetails = new ArrayList<>();
                issuedDetails.add(list.get(pos));
                new AcceptedIssuedMatrlDAO(ToAcceptUnitsActivity.this).insertORupdate(list.get(pos), "0");
                if (new ConnectionDetector(getApplication()).isConnectingToInternet()) {

                    //JSONArray rejectedList = new RejectedIssuedMatrlDAO(SyncActivity.this).getRejectedUnits();
                    JSONArray accetedUnitList = new AcceptedIssuedMatrlDAO(ToAcceptUnitsActivity.this).getAcceptedUnitArray(issuedDetails);

                    JSONObject object = new JSONObject();

                    try {
                        object.put("data", accetedUnitList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.v("Json ", object.toString().replace("\\", ""));

                    new SyncData(ToAcceptUnitsActivity.this)
                            .syncAcceptedUnitsData(ToAcceptUnitsActivity.this, TaskType.UPLOAD_ACCEPTED_UNITS, object);

                }
                unitAdapter.list.remove(pos);
                unitAdapter.notifyItemRemoved(pos);
                unitAdapter.notifyItemRangeChanged(pos, unitAdapter.getItemCount());

                revealShow(dialogView, false, dialog);
            }
        });

        TextView bReject = (TextView) dialog.findViewById(R.id.bReject);
        bReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                showRejectWarning(faults,dialogView,dialog);

                if (new RejectedIssuedMatrlDAO(ToAcceptUnitsActivity.this).insertORupdate(list.get(pos)) > 0) {
                    unitAdapter.list.remove(pos);
                    unitAdapter.notifyItemRemoved(pos);
                    unitAdapter.notifyItemRangeChanged(pos, unitAdapter.getItemCount());
                }
                revealShow(dialogView, false, dialog);
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @SuppressLint("NewApi")
            @Override
            public void onShow(DialogInterface dialogInterface) {
                revealShow(dialogView, true, null);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    revealShow(dialogView, false, dialog);
                    return true;
                }
                return false;
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }


    @SuppressLint("NewApi")
    private void revealShow(View dialogView, boolean b, final Dialog dialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final View view = dialogView.findViewById(R.id.dialog);

            int w = view.getWidth();
            int h = view.getHeight();

            int endRadius = (int) Math.hypot(w, h);

            int cx = 0;
            int cy = 0;

            if (b) {
                Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, endRadius);
                view.setVisibility(View.VISIBLE);
                revealAnimator.setDuration(700);
                revealAnimator.start();

            } else {

                Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        dialog.dismiss();
                        view.setVisibility(View.INVISIBLE);

                    }
                });
                anim.setDuration(700);
                anim.start();
            }

        } else {
            if (dialog != null) {
                if (b) {

                    dialog.show();
                } else {
                    dialog.dismiss();
                }
            }
        }
    }


    @Override
    public void onSuccess(String result, TaskType type) {
        if (type.equals(TaskType.UPLOAD_ACCEPTED_UNITS)) {
            Log.v("UPLOAD ACCEPTED UNITS", result);

            try {

                JSONObject jsonObject = new JSONObject(result.toString());

                final JSONArray jsonArray = jsonObject.getJSONArray("Data");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject data = (JSONObject) jsonArray.get(i);

                    int iStatus = data.getInt("status");

                    if (iStatus >= 1) {

                        int iUpdated = new AcceptedIssuedMatrlDAO(ToAcceptUnitsActivity.this)
                                .updateIsSynced(data.getString("requestId"));

                    }

                }

            } catch (JSONException e) {
                Log.e("Error", e.toString());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String message, TaskType type) {
        if (type.equals(TaskType.UPLOAD_ACCEPTED_UNITS)) {
            new ToastManager(ToAcceptUnitsActivity.this).error(message);
        }
    }

    @Override
    public void onSuccess(String id, TaskType type, String faultType) {

    }
}
