package com.lk.lankabell.fault.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.lk.lankabell.fault.adapter.MySingleton;
import com.lk.lankabell.fault.config.AppConfig;
import com.lk.lankabell.fault.config.AppContoller;
import com.lk.lankabell.fault.control.TaskType;
import com.lk.lankabell.fault.control.VolleyCallback;
import com.lk.lankabell.fault.model.PendingFaults;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SyncData {

    Context context;
    String Tag = "SyncData";

    private ProgressDialog pDialog;

    public SyncData(Context context) {
        this.context = context;
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    //
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    // ------------------------- PENDING FAULT -----------------------------------
    public void getPendingFaults(final VolleyCallback callback, final TaskType type, String PF_type) {

        String empNo = new AppContoller().getEMPNO(context);
//        Log.v("empNo", empNo);

        //PF_type 1->pending
        //PF_type 2->Accepted

        String url = AppConfig.path + "fetchPendingFaults?empNo=" + empNo + "&type=" + PF_type;

        Log.v("URL", url.replaceAll(" ", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response); //JsonObject.parse(response).asObject();
                    int id = Integer.parseInt(object.get("ID").toString());
                    String Data = object.get("Data").toString();

                    System.out.println("* id " + id);

                    if (id == 1) {

                        String newUrl = response.replace("{\"Data\":\"", "{\"Data\": ");
                        String new2Url = newUrl.replace("\",\"ID\":\"1\"}", "}");
                        String new3Url = new2Url.replace("\\", "");
                        //String payload = "{\"message\":\"" + message.replace("\"", "\\\"") + "\"}";
                       // String new4Url = new3Url.replace("\"", "\\\"");
                        new3Url = new3Url.trim();

                        //hideDialog();
                        callback.onSuccess(new3Url + "", type);

                    } else {
//                        new ToastManager(context).error("No data.");
                        //hideDialog();
                        callback.onError(Data, type);
                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    Log.e(Tag, error.getMessage());
                    //hideDialog();
                    callback.onError(error.toString(), type);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //hideDialog();
                        error.printStackTrace();

                    }
                }
        );
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    // ------------------------- CHECK QUOTA STATUS -----------------------------------
    public void CheckQuotaStatus(final VolleyCallback callback, final TaskType type, final String fault_id) {

        String url = AppConfig.path + "checkQuotaStatus?faultId=" + fault_id;

        Log.v("URL", url.replaceAll(" ", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response); //JsonObject.parse(response).asObject();
                    int id = Integer.parseInt(object.get("ID").toString());
                    String Data = object.get("Data").toString();

                    System.out.println("* id " + id);
                    System.out.println("* Data  " + Data);
                    if (id == 200) {
                        callback.onSuccess(Data, type, fault_id);
                    } else {
                        callback.onError("ERROR " + " fault_id " + fault_id + "Data " + Data, type);
                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    Log.e(Tag, error.getMessage());
                    callback.onError(error.toString(), type);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        callback.onError(error.toString(), type);
                    }
                }
        );

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                error.printStackTrace();
                callback.onError(error.toString(), type);
                hideDialog();
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    //------------------------- FETCH PENDING ISSUE DETAILS ----------------------
    public void fetchPendingIssueDetails(final VolleyCallback callback, final TaskType type) {

        // pDialog.setMessage("Loading...");
        // showDialog();

        String empNo = new AppContoller().getEMPNO(context);
        Log.v("empNo", empNo);

        String url = AppConfig.path + "fetchPendingIssueDetails?empNo=" + empNo;

        Log.v("URL", url.replaceAll(" ", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response); //JsonObject.parse(response).asObject();
                    int id = Integer.parseInt(object.get("ID").toString());
                    String Data = object.get("Data").toString();

                    if (id == 1) {

                        String newUrl = response.replace("{\"Data\":\"", "{\"Data\": ");
                        String new2Url = newUrl.replace("\",\"ID\":\"1\"}", "}");
                        String new3Url = new2Url.replace("\\", "");
                        new3Url = new3Url.trim();

                        // hideDialog();
                        callback.onSuccess(new3Url + "", type);

                    } else {
                        // Toast.makeText(context, "No data available!",Toast.LENGTH_LONG).show();

                        //new ToastManager(context).error("No data.");

                        //hideDialog();
                        callback.onError(Data, type);
                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    Log.e(Tag, error.getMessage());
                    //hideDialog();
                    callback.onError(error.toString(), type);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //hideDialog();
                        error.printStackTrace();
                        callback.onError(error.toString(), type);

                    }
                }
        );
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    //------------------------- FETCH ACTION DETAILS ---------------------------
    public void fetchActionTaken(final VolleyCallback callback, final TaskType type) {

        //pDialog.setMessage("Loading...");
        //showDialog();


        String url = AppConfig.path + "fetchActionTakenDetails";

        Log.v("URL", url.replaceAll(" ", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response); //JsonObject.parse(response).asObject();
                    int id = Integer.parseInt(object.get("ID").toString());

                    if (id == 1) {

                        String newUrl = response.replace("{\"Data\":\"", "{\"Data\": ");
                        String new2Url = newUrl.replace("\",\"ID\":\"1\"}", "}");
                        String new3Url = new2Url.replace("\\", "");
                        new3Url = new3Url.trim();

                        //hideDialog();
                        callback.onSuccess(new3Url + "", type);

                    } else {
                        //hideDialog();
                        callback.onError("ERROR", type);
                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    Log.e(Tag, error.getMessage());
                    //hideDialog();
                    callback.onError(error.toString(), type);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //hideDialog();


                    }
                }
        );
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }
    //------------------------- FETCH BI CODE DETAILS ---------------------------
    public void fetchBiCode(final VolleyCallback callback, final TaskType type) {

        //pDialog.setMessage("Loading...");
        //showDialog();

        String url = AppConfig.path + "getBIItems";

        Log.v("URL", url.replaceAll(" ", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response); //JsonObject.parse(response).asObject();
                    int id = Integer.parseInt(object.get("ID").toString());

                    if (id == 1) {

                        String newUrl = response.replace("{\"Data\":\"", "{\"Data\": ");
                        String new2Url = newUrl.replace("\",\"ID\":\"1\"}", "}");
                        String new3Url = new2Url.replace("\\", "");
                        new3Url = new3Url.trim();

                        //hideDialog();
                        callback.onSuccess(new3Url + "", type);

                    } else {
                        //hideDialog();
                        callback.onError("ERROR", type);
                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    Log.e(Tag, error.getMessage());
                    //hideDialog();
                    callback.onError(error.toString(), type);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //hideDialog();


                    }
                }
        );
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void FetchExistingLTEImsi(final VolleyCallback callback, final TaskType type, final String Serial, PendingFaults faults) {

        //pDialog.setMessage("Loading...");
        //showDialog();

        String empNo = new AppContoller().getEMPNO(context);
        Log.v("empNo", empNo);

        String url = AppConfig.path + "FetchExistingLTEImsi?faultId=" + faults.getPF_REQUESTID();

        Log.v("URL", url.replaceAll(" ", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response); //JsonObject.parse(response).asObject();
                    int id = Integer.parseInt(object.get("ID").toString());

                    if (id == 1) {

                        String newUrl = response.replace("{\"Data\":\"", "{\"Data\": ");
                        String new2Url = newUrl.replace("\",\"ID\":\"1\"}", "}");
                        String new3Url = new2Url.replace("\\", "");
                        new3Url = new3Url.trim();

                        //hideDialog();
                        callback.onSuccess(new3Url + "", type, Serial);
                        Log.v("Json >>> ", new3Url);

                    } else {
                        //hideDialog();
                        callback.onError("ERROR", type);
                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    Log.e(Tag, error.getMessage());
                    //hideDialog();
                    callback.onError(error.toString(), type);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //hideDialog();


                    }
                }
        );
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }


    public void FetchExisitingCDMA(final VolleyCallback callback, final TaskType type, final String Serial, PendingFaults faults) {

        //pDialog.setMessage("Loading...");
        //showDialog();

        String empNo = new AppContoller().getEMPNO(context);
        Log.v("empNo", empNo);

        String url = AppConfig.path + "FetchExisitingCDMA?faultId=" + faults.getPF_REQUESTID();

        Log.v("URL", url.replaceAll(" ", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response); //JsonObject.parse(response).asObject();
                    int id = Integer.parseInt(object.get("ID").toString());

                    if (id == 1) {

                        String newUrl = response.replace("{\"Data\":\"", "{\"Data\": ");
                        String new2Url = newUrl.replace("\",\"ID\":\"1\"}", "}");
                        String new3Url = new2Url.replace("\\", "");
                        new3Url = new3Url.trim();

                        //hideDialog();
                        callback.onSuccess(new3Url + "", type, Serial);
                        Log.v("Json >>> ", new3Url);

                    } else {
                        //hideDialog();
                        callback.onError("ERROR", type);
                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    Log.e(Tag, error.getMessage());
                    //hideDialog();
                    callback.onError(error.toString(), type);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //hideDialog();


                    }
                }
        );
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }


    public void FetchExisitingLTEEmei(final VolleyCallback callback, final TaskType type, final String Serial, PendingFaults faults) {

        //pDialog.setMessage("Loading...");
        //showDialog();

        String empNo = new AppContoller().getEMPNO(context);
        Log.v("empNo", empNo);

        String url = AppConfig.path + "FetchExisitingLTEEmei?faultId=" + faults.getPF_REQUESTID();

        Log.v("URL", url.replaceAll(" ", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response); //JsonObject.parse(response).asObject();
                    int id = Integer.parseInt(object.get("ID").toString());

                    if (id == 1) {

                        String newUrl = response.replace("{\"Data\":\"", "{\"Data\": ");
                        String new2Url = newUrl.replace("\",\"ID\":\"1\"}", "}");
                        String new3Url = new2Url.replace("\\", "");
                        new3Url = new3Url.trim();

                        //hideDialog();
                        callback.onSuccess(new3Url + "", type, Serial);
                        Log.v("Json >>> ", new3Url);

                    } else {
                        //hideDialog();
                        callback.onError("ERROR", type);
                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    Log.e(Tag, error.getMessage());
                    //hideDialog();
                    callback.onError(error.toString(), type);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //hideDialog();


                    }
                }
        );
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void syncAcceptedData(final VolleyCallback callback, final TaskType type, final JSONObject json) {

        //pDialog.setMessage("Loading...");
        //showDialog();

        String url = AppConfig.path + "updateAcceptedFaults";

        Log.v("URL", url.toString());


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    //   Log.i("RESPONSE : ", response.toString());
                    JSONObject object = response;
                    int id = Integer.parseInt(object.get("ID").toString());

                    if (id == 200) {
                        //hideDialog();
                        callback.onSuccess(response.toString(), type);
                    } else {
                        //hideDialog();
                        callback.onError(response.toString(), type);
                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    //hideDialog();
                    callback.onError(error.toString(), type);
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        callback.onError(error.toString(), type);
                        // hideDialog();
                        //new AlertDialog(context).showWarningAlert("Something went wrong. Try again later!");
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void syncRejectedData(final VolleyCallback callback, final TaskType type, final JSONObject json) {

        pDialog.setMessage("Loading...");
        showDialog();

        String url = AppConfig.path + "updateRejectedFaults";

        Log.v("URL", url.toString());


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.i("RESPONSE : ", response.toString());
                    JSONObject object = response;
                    int id = Integer.parseInt(object.get("ID").toString());

                    hideDialog();
                    callback.onSuccess(response.toString(), type);

                } catch (Exception error) {
                    error.printStackTrace();
                    //hideDialog();
                    callback.onError(error.toString(), type);
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  hideDialog();
                        //new AlertDialog(context).showWarningAlert("Something went wrong. Try again later!");
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                error.printStackTrace();
                callback.onError(error.toString(), type);
                hideDialog();
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void syncAcceptedUnitsData(final VolleyCallback callback, final TaskType type, final JSONObject json) {

        pDialog.setMessage("Loading...");
        showDialog();

        String url = AppConfig.path + "updateAcceptedIssuedItems";

        Log.v("URL", url.toString());


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.i("RESPONSE : ", response.toString());
                    JSONObject object = response;
                    int id = Integer.parseInt(object.get("ID").toString());

                    if (id == 200) {
                        hideDialog();
                        callback.onSuccess(response.toString(), type);
                    } else {
                        hideDialog();
                        callback.onError(response.toString(), type);
                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    hideDialog();
                    callback.onError(error.toString(), type);
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideDialog();
                        //new AlertDialog(context).showWarningAlert("Something went wrong. Try again later!");
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void syncRejectedUnitsData(final VolleyCallback callback, final TaskType type, final JSONObject json) {

        //pDialog.setMessage("Loading...");
        //showDialog();

        String url = AppConfig.path + "updateRejectedIssuedItems";

        Log.v("URL", url.toString());


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.i("RESPONSE : ", response.toString());
                    JSONObject object = response;
                    int id = Integer.parseInt(object.get("ID").toString());

                    if (id == 200) {
                        // hideDialog();
                        callback.onSuccess(response.toString(), type);
                    } else {
                        //hideDialog();
                        callback.onError(response.toString(), type);
                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    // hideDialog();
                    callback.onError(error.toString(), type);
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // hideDialog();
                        //new AlertDialog(context).showWarningAlert("Something went wrong. Try again later!");
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void syncCompletedJobs(final VolleyCallback callback, final TaskType type, final JSONObject json) {

        //   pDialog.setMessage("Loading...");
        //    showDialog();

      // String url = AppConfig.path + "updateAcceptedIssuedItems";
        String url = AppConfig.path + "updateClosedFault";


        Log.v("URL", url.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.i("RESPONSE : ", response.toString());
                    JSONObject object = response;
                    int id = Integer.parseInt(object.get("ID").toString());

                    //   hideDialog();
                    callback.onSuccess(response.toString(), type);


                } catch (Exception error) {
                    error.printStackTrace();
                    //    hideDialog();
                    System.out.println("* error " + error.getMessage());
                    callback.onError(error.toString(), type);
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        callback.onError(error.toString(), type);
                        //      hideDialog();
                        //new AlertDialog(context).showWarningAlert("Something went wrong. Try again later!");
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                error.printStackTrace();
                callback.onError(error.toString(), type);
                //  hideDialog();
            }
        });


        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void fetchCustomerPriorityDetails(final VolleyCallback callback, final TaskType type) {

        // pDialog.setMessage("Loading...");
        // showDialog();
        String empNo = new AppContoller().getEMPNO(context);

        String url = AppConfig.path + "fetchCustomerStatDetails";

        Log.v("URL", url.replaceAll(" ", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response); //JsonObject.parse(response).asObject();
                    int id = Integer.parseInt(object.get("ID").toString());

                    if (id == 1) {

                        String newUrl = response.replace("{\"Data\":\"", "{\"Data\": ");
                        String new2Url = newUrl.replace("\",\"ID\":\"1\"}", "}");
                        String new3Url = new2Url.replace("\\", "");
                        new3Url = new3Url.trim();

                        // hideDialog();
                        callback.onSuccess(new3Url + "", type);

                    } else {
                        // Toast.makeText(context, "No data available!",Toast.LENGTH_LONG).show();

                        //new ToastManager(context).error("No data.");

                        //hideDialog();
                        callback.onError("ERROR", type);
                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    Log.e(Tag, error.getMessage());
                    //hideDialog();
                    callback.onError(error.toString(), type);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //hideDialog();


                    }
                }
        );
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void fetchCoordinator(final VolleyCallback callback, final TaskType type) {

        // pDialog.setMessage("Loading...");
        // showDialog();
        String empNo = new AppContoller().getEMPNO(context);
        String url = AppConfig.path + "GetCoordinatorTelNo?empNo=" + empNo;

        Log.v("URL", url.replaceAll(" ", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response); //JsonObject.parse(response).asObject();
                    int id = Integer.parseInt(object.get("ID").toString());
                    String data = object.get("Data").toString();

                    if (id == 200) {
                        callback.onSuccess(data + "", type);
                    } else {
                        callback.onError(data, type);
                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    callback.onError(error.toString(), type);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //hideDialog();
                        callback.onError(error.toString(), type);
                    }
                }
        );
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void fetchHistoryReport(final VolleyCallback callback, final TaskType type, final String fromDate, final String toDate, final String status) {

        //status = NEW , ACCEPTED , COMPLETED
        String url = AppConfig.path
                + "FetchReportFaultByDate?empNo=" + AppContoller.getEMPNO(context) + "&fromDate=" + fromDate + "&toDate=" + toDate + "&type=" + status;

        Log.v("URL", url.replaceAll(" ", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response); //JsonObject.parse(response).asObject();
                    int id = Integer.parseInt(object.get("ID").toString());
                    String data = object.get("Data").toString();

                    if (id == 1) {

                        String newUrl = response.replace("{\"Data\":\"", "{\"Data\": ");
                        String new2Url = newUrl.replace("\",\"ID\":\"1\"}", "}");
                        String new3Url = new2Url.replace("\\", "");
                        new3Url = new3Url.trim();

                        // hideDialog();
                        callback.onSuccess(new3Url + "", type);

                    } else {
                        // Toast.makeText(context, "No data available!",Toast.LENGTH_LONG).show();

                        //new ToastManager(context).error("No data.");

                        //hideDialog();
                        callback.onError(data, type);
                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    Log.e(Tag, error.getMessage());
                    //hideDialog();
                    callback.onError(error.toString(), type);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //hideDialog();


                    }
                }
        );
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    //------------------------- FETCH PENDING ISSUE DETAILS ----------------------
    public void fetchCDMAUnits(final VolleyCallback callback, final TaskType type) {

        pDialog.setMessage("Loading...");
        showDialog();

        String empNo = new AppContoller().getEMPNO(context);
        Log.v("empNo", empNo);

        String url = AppConfig.path + "fetchCDMAUnits?empNo=" + empNo;

        Log.v("URL", url.replaceAll(" ", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response); //JsonObject.parse(response).asObject();
                    int id = Integer.parseInt(object.get("ID").toString());

                    if (id == 200) {

                        String newUrl = response.replace("{\"Data\":\"", "{\"Data\": ");
                        String new2Url = newUrl.replace("\",\"ID\":\"1\"}", "}");
                        String new3Url = new2Url.replace("\\", "");
                        new3Url = new3Url.trim();

                        hideDialog();
                        callback.onSuccess(new3Url + "", type);

                    } else {
                        // Toast.makeText(context, "No data available!",Toast.LENGTH_LONG).show();

                        //new ToastManager(context).error("No data.");

                        hideDialog();
                        callback.onError("ERROR", type);
                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    Log.e(Tag, error.getMessage());
                    hideDialog();
                    callback.onError(error.toString(), type);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideDialog();


                    }
                }
        );
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    //------------------------- FETCH PENDING ISSUE DETAILS ----------------------
    public void checkAccStatus(final VolleyCallback callback, final TaskType type, String faultID) {

        pDialog.setMessage("Loading...");
        showDialog();


        String url = AppConfig.path + "checkAccStatus?faultId=" + faultID;

        Log.v("URL", url.replaceAll(" ", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response); //JsonObject.parse(response).asObject();
                    int id = Integer.parseInt(object.get("ID").toString());

                    if (id == 200) {

                        String newUrl = response.replace("{\"Data\":\"", "{\"Data\": ");
                        String new2Url = newUrl.replace("\",\"ID\":\"1\"}", "}");
                        String new3Url = new2Url.replace("\\", "");
                        new3Url = new3Url.trim();

                        hideDialog();
                        callback.onSuccess(new3Url + "", type);

                    } else {
                        // Toast.makeText(context, "No data available!",Toast.LENGTH_LONG).show();

                        //new ToastManager(context).error("No data.");

                        hideDialog();
                        callback.onError("ERROR", type);
                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    Log.e(Tag, error.getMessage());
                    hideDialog();
                    callback.onError(error.toString(), type);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideDialog();


                    }
                }
        );
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }


    // ------------------------- FETCH CDMA ISSUE DETAILS-----------------------------------
    public void fetchCDMAIssueDetails(final VolleyCallback callback, final TaskType type) {
        pDialog.setMessage("Please Wait ...");
        showDialog();
        String empNo = new AppContoller().getEMPNO(context);
        Log.v("empNo", empNo);

        String url = AppConfig.path + "FetchCDMAIssueDetails?empNo=" + empNo;

        Log.v("URL", url.replaceAll(" ", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response); //JsonObject.parse(response).asObject();
                    int id = Integer.parseInt(object.get("ID").toString());
                    String Data = object.get("Data").toString();
                    if (id == 1) {
                        hideDialog();
                        String newUrl = response.replace("{\"Data\":\"", "{\"Data\": ");
                        String new2Url = newUrl.replace("\",\"ID\":\"1\"}", "}");
                        String new3Url = new2Url.replace("\\", "");
                        new3Url = new3Url.trim();

                        callback.onSuccess(new3Url + "", type);

                    } else {
                        hideDialog();
                        callback.onError(Data, type);
                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    Log.e(Tag, error.getMessage());
                    //hideDialog();
                    callback.onError(error.toString(), type);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        callback.onError(error.toString(), type);
                        //hideDialog();


                    }
                }
        );

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                error.printStackTrace();
                callback.onError(error.toString(), type);
                hideDialog();
            }
        });

        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }


    // ------------------------- CHANGE TO REPUN-----------------------------------
    public void ChangeToRepun(final VolleyCallback callback, final TaskType type, final String faultId, final String remark, final String faultType) {

        showDialog();
        String url = AppConfig.path + "changeToRepun?faultId=" + faultId + "&remark=" + remark;

        Log.v("URL", url.replaceAll(" ", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response); //JsonObject.parse(response).asObject();
                    hideDialog();
                    callback.onSuccess(object.get("ID").toString() + "", type, faultType);

                } catch (Exception error) {
                    error.printStackTrace();
                    Log.e(Tag, error.getMessage());
                    hideDialog();
                    callback.onError(error.toString(), type);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideDialog();


                    }
                }
        );
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    // ------------------------- ITEM ISSUE VALIDATION-----------------------------------
    public void itemIssueValidation(final VolleyCallback callback, final TaskType type, final String faultId, final String selectedType) {

        String url = AppConfig.path + "itemIssueValidation?faultId=" + faultId + "&selectedType=" + selectedType;

        Log.v("URL", url.replaceAll(" ", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response); //JsonObject.parse(response).asObject();
                    String id = object.get("ID").toString();
                    String data = object.get("Data").toString();

                    System.out.println("* id " + id + " validation " + type + " selectedType " + selectedType);

                    callback.onSuccess(id + "", type, data);

                } catch (Exception error) {
                    error.printStackTrace();
                    Log.e(Tag, error.getMessage());
                    //hideDialog();
                    callback.onError(error.toString(), type);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //hideDialog();


                    }
                }
        );
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    // ------------------------- APP VERSION UPLOAD -----------------------------------
    public void updateVersion(final VolleyCallback callback, final TaskType type, final String epfNo, final String appversion) {

        String url = AppConfig.CorePath + "UpdateAppVersion?epfNo=" + epfNo + "&appId=7&appVersion=" + appversion;

        Log.v("URL", url.replaceAll(" ", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response); //JsonObject.parse(response).asObject();
                    String id = object.get("ID").toString();
                    String data = object.get("Data").toString();

                    System.out.println("* id " + id + " validation " + type + " selectedType " + data);

                    callback.onSuccess(id + "", type, data);

                } catch (Exception error) {
                    error.printStackTrace();
                    Log.e(Tag, error.getMessage());
                    //hideDialog();
                    callback.onError(error.toString(), type);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //hideDialog();


                    }
                }
        );
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    // ------------------------- GET UNIT IN HAND  -----------------------------------
    public void getUnitInHand(final VolleyCallback callback, final TaskType type) {
        pDialog.setMessage("Please Wait ...");
        showDialog();
        String empNo = new AppContoller().getEMPNO(context);
        Log.v("epfNo", empNo);
        String url = AppConfig.path + "fetchTestUnits?empNo=" + empNo;

        System.out.println("* url " + url.replaceAll(" ", "%20"));
        Log.v("URL", url.replaceAll(" ", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response); //JsonObject.parse(response).asObject();
                    String id = object.get("ID").toString();
                    String data = object.get("Data").toString();

                    if (id.equals("1")) {
                        String newUrl = response.replace("{\"Data\":\"", "{\"Data\": ");
                        String new2Url = newUrl.replace("\",\"ID\":\"1\"}", "}");
                        String new3Url = new2Url.replace("\\", "");
                        new3Url = new3Url.trim();
                        hideDialog();
                        callback.onSuccess(new3Url + "", type);
                    } else {
                        hideDialog();
                        callback.onError(data, type);
                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    Log.e(Tag, error.getMessage());
                    //hideDialog();
                    callback.onError(error.toString(), type);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //hideDialog();
                        //  callback.onError(error.toString(),type);

                    }
                }
        );

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                error.printStackTrace();
                callback.onError(error.toString(), type);
                hideDialog();
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }


    // -------------------------update Visit Location-----------------------------------
    public void updateVisitLocation(final VolleyCallback callback, final TaskType type, final JSONObject json) {

        //  pDialog.setMessage("Loading...");
        //   showDialog();

        String url = AppConfig.path + "updateVisitLocation";

        Log.v("URL", url.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.i("RESPONSE : ", response.toString());
                    JSONObject object = response;
                    int id = Integer.parseInt(object.get("ID").toString());

                    System.out.println("* id visitLog  " + id);
                    if (id == 200) {
                        //   hideDialog();
                        callback.onSuccess(response.toString(), type);
                    } else {
                        //new ToastManager(context).error("Uploading Error.. Please try again.");
                        //    hideDialog();
                        callback.onError(response.toString(), type);

                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    // hideDialog();
                    callback.onError(error.toString(), type);
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error.toString(), type);
                        error.printStackTrace();
                        //    hideDialog();
                        //new AlertDialog(context).showWarningAlert("Something went wrong. Try again later!");
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    // -------------------------ASR CREATE-----------------------------------
    public void InsertASR(final VolleyCallback callback, final TaskType type, final JSONObject json) {

        //  pDialog.setMessage("Loading...");
        //   showDialog();

        String url = AppConfig.path + "CreateASREntry";

        Log.v("URL", url.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.i("RESPONSE : ", response.toString());
                    JSONObject object = response;
                    int id = Integer.parseInt(object.get("ID").toString());

                    System.out.println("* id InsertASR  " + id);
                    if (id == 200) {
                        //   hideDialog();
                        callback.onSuccess(response.toString(), type);
                    } else {
                        //new ToastManager(context).error("Uploading Error.. Please try again.");
                        //    hideDialog();
                        callback.onError(response.toString(), type);

                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    // hideDialog();
                    callback.onError(error.toString(), type);
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error.toString(), type);
                        error.printStackTrace();
                        //    hideDialog();
                        //new AlertDialog(context).showWarningAlert("Something went wrong. Try again later!");
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    // -------------------------TSR DETAILS-----------------------------------
    public void getTSR(final VolleyCallback callback, final TaskType type, final String username) {

        //  pDialog.setMessage("Loading...");
        //   showDialog();

        String url = AppConfig.CorePath + "TSRAccDetails?companyId=" + username;


        Log.v("URL", url.replaceAll(" ", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response); //JsonObject.parse(response).asObject();

                    callback.onSuccess(object.getString("Data"), type);

                } catch (Exception error) {
                    error.printStackTrace();
                    Log.e(Tag, error.getMessage());
                    callback.onError(error.toString(), type);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error.toString(), type);
                    }
                }
        );

        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    //------------------------- FETCH ACCEPT ISSUE DETAILS ----------------------
    public void fetchAcceptedIssueDetails(final VolleyCallback callback, final TaskType type) {

        pDialog.setMessage("Please Wait ...");
        //     showDialog();

        String empNo = new AppContoller().getEMPNO(context);
        Log.v("empNo", empNo);

        String url = AppConfig.path + "fetchAcceptedIssueDetails?empNo=" + empNo;

        Log.v("URL", url.replaceAll(" ", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response); //JsonObject.parse(response).asObject();
                    int id = Integer.parseInt(object.get("ID").toString());
                    String Data = object.get("Data").toString();

                    System.out.println("* id acc issue " + id);
                    if (id == 1) {

                        String newUrl = response.replace("{\"Data\":\"", "{\"Data\": ");
                        String new2Url = newUrl.replace("\",\"ID\":\"1\"}", "}");
                        String new3Url = new2Url.replace("\\", "");
                        new3Url = new3Url.trim();

                        hideDialog();
                        callback.onSuccess(new3Url + "", type);

                    } else {
                        // Toast.makeText(context, "No data available!",Toast.LENGTH_LONG).show();

                        //new ToastManager(context).error("No data.");

                        hideDialog();
                        callback.onError(Data, type);
                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    Log.e(Tag, error.getMessage());
                    hideDialog();
                    callback.onError(error.toString(), type);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideDialog();
                        error.printStackTrace();
                        callback.onError(error.toString(), type);

                    }
                }
        );
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                error.printStackTrace();
                callback.onError(error.toString(), type);
                hideDialog();
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    // -------------------------CHANGE TO REFURBISH-----------------------------------
    public void ChangeRefurbish(final VolleyCallback callback, final TaskType type, final JSONObject json) {

        pDialog.setMessage("Please Wait ...");
        showDialog();

        String url = AppConfig.path + "changeUnitStatusDetails";

        Log.v("URL", url.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.i("RESPONSE : ", response.toString());
                    JSONObject object = response;
                    int id = Integer.parseInt(object.get("ID").toString());

                    if (id == 200) {
                        hideDialog();
                        callback.onSuccess(response.toString(), type);
                    } else {
                        //new ToastManager(context).error("Uploading Error.. Please try again.");
                        hideDialog();
                        callback.onError(response.toString(), type);

                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    hideDialog();
                    callback.onError(error.toString(), type);
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error.toString(), type);
                        error.printStackTrace();
                        hideDialog();
                        //new AlertDialog(context).showWarningAlert("Something went wrong. Try again later!");
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }


}