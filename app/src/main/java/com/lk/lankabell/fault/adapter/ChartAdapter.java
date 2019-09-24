package com.lk.lankabell.fault.adapter;


import android.content.Context;
import android.graphics.Color;

//import com.bellvatage.sanasa.leadsmonitoring.control.data.Count.CountDAO;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ChartAdapter {

    Context context;

    public ChartAdapter(Context context){
        this.context = context;
    }

    public LineData generateDataLine(int cnt) {
        //lead count


        ArrayList<Entry> e1 = new ArrayList<Entry>();
        for (int i = 0; i < 31; i++) {
            e1.add(new Entry(i, (int) (Math.random() * 10) + 5));
            //e1.add(new Entry(i, new CountDAO(context).getCount(DateTimeManager.getLastMonthDate(i))));
            // System.out.println("Leed Count New "+new CountDAO(context).getCount(DateTimeManager.getLastMonthDate(i)));

        }

        LineDataSet d1 = new LineDataSet(e1, "Month completed faults");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(2 , 0, 0));
        d1.setColor(Color.rgb(250, 50, 100));
        d1.setCircleColor(Color.rgb(0, 0, 0));
        d1.setDrawValues(false);

        ArrayList<Entry> e2 = new ArrayList<Entry>();
        for (int i = 0; i < 31; i++) {
            //e2.add(new Entry(i, new CountDAO(context).getCount(DateTimeManager.getThisMonthDate(i))));
            e2.add(new Entry(i, (int) (Math.random() * 10) + 5));
        }

        LineDataSet d2 = new LineDataSet(e2, "This Month fault");
        d2.setLineWidth(2.5f);
        d2.setCircleRadius(4.5f);
        d2.setHighLightColor(Color.rgb(0, 200, 0));
        d2.setColor(Color.rgb(0, 200, 0));
        d2.setCircleColor(Color.rgb(0, 200, 0));
        d2.setDrawValues(false);
        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        sets.add(d1);
        //sets.add(d2);

        LineData cd = new LineData(sets);
        return cd;
    }


    public PieData generateDataPie(int cnt) {

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        for (int i = 0; i < 4; i++) {
            entries.add(new PieEntry((float) ((Math.random() * 10) + 10), "Quarter " + (i+1)));
        }

        PieDataSet d = new PieDataSet(entries, "");

        // space between slices
        d.setSliceSpace(2f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);

        PieData cd = new PieData(d);
        return cd;
    }
}
