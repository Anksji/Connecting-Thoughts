package com.rsons.application.connecting_thoughts.Fetch_data;

import java.util.ArrayList;

/**
 * Created by ankit on 2/13/2018.
 */
public class SectionDataModel {



    private String headerTitle;
    private ArrayList<SingleStoryModel> allItemsInSection;


    public SectionDataModel() {

    }
    public SectionDataModel(String headerTitle, ArrayList<SingleStoryModel> allItemsInSection) {
        this.headerTitle = headerTitle;
        this.allItemsInSection = allItemsInSection;
    }



    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public ArrayList<SingleStoryModel> getAllItemsInSection() {
        return allItemsInSection;
    }

    public void setAllItemsInSection(ArrayList<SingleStoryModel> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }


}