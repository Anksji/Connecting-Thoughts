package com.rsons.application.connecting_thoughts.NavigationDrawer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by ankit on 9/15/2017.
 */

public class ExpandableListDataSource {

    /**
     * Returns
     * @return navbar List
     */
    public static LinkedHashMap<String, List<String>> getData(final String[] lang,  final String[] sections) {
        LinkedHashMap<String, List<String>> expandableListData = new LinkedHashMap<>();

        List<String> mChildList = new ArrayList<String>();
        for (int i = 0; i < sections.length; ++i) {
            mChildList.add(i, sections[i]);
        }


        List<String> language_ = Arrays.asList(lang);


        expandableListData.put(mChildList.get(0), language_);
        expandableListData.put(mChildList.get(1),new ArrayList<String>());
        expandableListData.put(mChildList.get(2), new ArrayList<String>());
        expandableListData.put(mChildList.get(3), new ArrayList<String>());
        expandableListData.put(mChildList.get(4), new ArrayList<String>());
        expandableListData.put(mChildList.get(5), new ArrayList<String>());
        expandableListData.put(mChildList.get(6), new ArrayList<String>());
        expandableListData.put(mChildList.get(7), new ArrayList<String>());
        expandableListData.put(mChildList.get(8), new ArrayList<String>());
        /*expandableListData.put(mChildList.get(9), new ArrayList<String>());*/
        return expandableListData;
    }
}
