package com.lk.lankabell.fault.helper;

import android.content.Context;

import com.lk.lankabell.fault.control.Data.LocationDAO;
import com.lk.lankabell.fault.model.CompetedJob;
import com.lk.lankabell.fault.model.LocationDetails;

public class Other {

    private Context context;

    public Other(Context context) {
        this.context = context;
    }

    public void setLatLong(CompetedJob job) {
        LocationDetails lastLocation = new LocationDAO(context).getLastLocation();
        job.setCOMPLETED_JOB_LATITUDE(String.valueOf(lastLocation.getLATITUDE()));
        job.setCOMPLETED_JOB_LONGITUTE(String.valueOf(lastLocation.getLONGITUDE()));
    }

}
