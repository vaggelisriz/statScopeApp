package com.example.frontend;

import java.util.ArrayList;
import java.util.List;

public class ChampionshipList{

    private ArrayList<Championship> chlist= new ArrayList<>();

    public ChampionshipList() {
        String url= Config.BASE_URL+"/getChampionships.php";
        try {
            OkHttpHandler okHttpHandler = new OkHttpHandler();
            chlist = okHttpHandler.populateChampionshipSpinner(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getChampionships() {
        List<String> temp = new ArrayList<String>();
        for (int i=0; i<chlist.size(); i++) {
            temp.add(chlist.get(i).getName());
        }
        return temp;
    }

    public int getChampionshipId(int position) {
        return chlist.get(position).getId();
    }

}

