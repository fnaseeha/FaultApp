package com.lk.lankabell.fault.helper;

import com.lk.lankabell.fault.model.ActionTaken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Sort {

    public static  List<ActionTaken> sortTheList(List<ActionTaken> actionTakenList) {

        List<ActionTaken> sortedList = new ArrayList<>();

        Collections.sort(actionTakenList, new Comparator<ActionTaken>() {
            @Override
            public int compare(ActionTaken item, ActionTaken t1) {
                String s1 = item.getACTION_DESCRIPTION();
                String s2 = t1.getACTION_DESCRIPTION();
                return s1.compareToIgnoreCase(s2);
            }

        });

            return sortedList;

    }
}
