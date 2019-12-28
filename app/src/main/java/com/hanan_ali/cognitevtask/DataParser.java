package com.hanan_ali.cognitevtask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser
{
    private HashMap<String,String> getSingleNearbyPlace(JSONObject googlePlaceJASON)
    {
        HashMap<String,String> googlePlaceMap=new HashMap<>();
        String nameOfPace="-NA-";
        String vicinity="-NA-";
        String latitude="";
        String logitude="";
        String reference="";
        try {
            if (!googlePlaceJASON.isNull("name"))
            {
                nameOfPace=googlePlaceJASON.getString("name");
            }
            if (!googlePlaceJASON.isNull("vicinity"))
            {
                vicinity=googlePlaceJASON.getString("vicinity");
            }
            latitude=googlePlaceJASON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            logitude=googlePlaceJASON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference=googlePlaceJASON.getString("reference");
            googlePlaceMap.put("place_name",nameOfPace);
            googlePlaceMap.put("vicinity",vicinity);
            googlePlaceMap.put("lat",latitude);
            googlePlaceMap.put("lng",logitude);
            googlePlaceMap.put("reference",reference);



        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return googlePlaceMap;

    }
    private List<HashMap<String,String>> getAllNearbyPlaces(JSONArray jsonArray)
    {
        int counter=jsonArray.length();
        List<HashMap<String,String>> NearbyPlacesList=new ArrayList<>();
        HashMap<String,String> NearbyPlaceMap=null;
        for (int i=0;i<counter;i++)
        {
            try {
                NearbyPlaceMap=getSingleNearbyPlace((JSONObject)jsonArray.get(i));
                NearbyPlacesList.add(NearbyPlaceMap);

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return NearbyPlacesList;
    }
    public List<HashMap<String,String>> Parse(String JSONdata)
    {
        JSONArray jsonArray=null;
        JSONObject jsonObject;
        try {
            jsonObject=new JSONObject(JSONdata);

            jsonArray=jsonObject.getJSONArray("results");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return getAllNearbyPlaces(jsonArray);

    }

}
