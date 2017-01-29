package com.courtside.demo.utility;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by MELHAGEH on 10/30/2016.
 */
//[
//       {
//        _id: "5823df849ac8a4212b4e8be2",
//        loc: [
//        -83.24999593,
//        42.26914561
//        ],
//        title: "fgh",
//        playerscount: 1,
//        startdate: "2016-11-09T21:45:00.000Z",
//        enddate: "2016-11-09T21:45:00.000Z",
//        players: [ ],
//        creator: {
//        id: "580a1fc3b61c870703fa725e",
//        username: "mhage82@gmail.com"
//        },
//        __v: 0
//        }
//        ]
public class JSONUtility {

    private static String extractTimeFromDate(String date){
        String finalTime = null;

//        Log.i("extractTimeFromDate", date.split("T")[0]);
//        Log.i("extractTimeFromDate", date.split("T")[1]);

        int hours = Integer.parseInt(date.split("T")[1].substring(0, 2));
        if(hours>12)
            hours = hours -12;

        finalTime = hours + ":" + date.split("T")[1].substring(3, 5);

        return finalTime;
    }
    public static ArrayList<JsonElement> getListFromJsonString(String s){

        ArrayList<JsonElement> list = new ArrayList<JsonElement>();

        try {
            JSONArray jsonArray = new JSONArray(s);

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject element = jsonArray.getJSONObject(i);
                String username = element.getJSONObject("creator").getString("username");
                String title = element.getString("title");
                float longitude = (float)element.getJSONArray("loc").getDouble(0);
                float latitude = (float)element.getJSONArray("loc").getDouble(1);
                String gameType = element.getString("type");
                int remainingSeats = Integer.parseInt(element.getString("playerscount"));// - element.getJSONArray("players").length();
                boolean isGamePublic = true;
                String id = element.getString("_id");
                String players = element.getJSONArray("players").toString();

                String gameTime = extractTimeFromDate(element.getString("startdate")) + " to " + extractTimeFromDate(element.getString("enddate"));

                list.add(i, new JsonElement(latitude, longitude, username, title, gameType, gameTime, id, players,
                        remainingSeats, isGamePublic));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<String> getPlayersListFromJsonString(String s){
        ArrayList<String> list = new ArrayList<String>();

        try{
            JSONArray jsonArray = new JSONArray(s);

            for(int i=0; i<jsonArray.length(); i++) {

            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return list;
    }

    public static void setUserInfo(String s){

        UserInfo userInfo = new UserInfo();
        if(s != null){
            try{
                JSONObject jsonObject = new JSONObject(s);
                Log.i("setUserInfo", "username is "+ jsonObject.getString("username"));
                userInfo.set_userId(jsonObject.getString("username"));
                userInfo.set_displayname(jsonObject.getString("displayname"));
                userInfo.set_id(jsonObject.getString("_id"));
                userInfo.set_phonenumber(jsonObject.getString("phone"));
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
    public static JsonElement getGameFromJsonString(String s){
        JsonElement jsonElement = null;

        if(s != null){
            try {
                JSONObject jsonObject= new JSONObject(s);

                String username = jsonObject.getJSONObject("creator").getString("username");
                String title = jsonObject.getString("title");
                float longitude = (float)jsonObject.getJSONArray("loc").getDouble(0);
                float latitude = (float)jsonObject.getJSONArray("loc").getDouble(1);
                String gameType = jsonObject.getString("type");
                int remainingSeats = Integer.parseInt(jsonObject.getString("playerscount"));// - element.getJSONArray("players").length();
                boolean isGamePublic = true;
                String id = jsonObject.getString("_id");

                String gameTime = extractTimeFromDate(jsonObject.getString("startdate")) + " to " + extractTimeFromDate(jsonObject.getString("enddate"));

                String players = jsonObject.getJSONArray("players").toString();

                jsonElement =new JsonElement(latitude, longitude, username, title, gameType, gameTime, id, players,
                        remainingSeats, isGamePublic);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else{
            Log.i("getGameFromJsonString", "string is null");
        }
        return jsonElement;
    }

    //[{"_id":"5866f6865dcc1b06b9336d1b","displayname":"mike","username":"mhage82@gmail.com"}]
    public static ArrayList<JoinListElement> getPlayerFromGame(String s){
        ArrayList<JoinListElement>joinElementList = new ArrayList<JoinListElement>();

        try{
            JSONArray jsonArray = new JSONArray(s);

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Log.i("getPlayerFromGame", jsonObject.getString("displayname"));
                String displayName = jsonObject.getString("displayname");
                String username = jsonObject.getString("username");
                String id = jsonObject.getString("_id");

                joinElementList.add(i, new JoinListElement(username, id, displayName));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.i("getPlayerFromGame", joinElementList.get(0).getDisplayName());
        Log.i("getPlayerFromGame", joinElementList.get(1).getDisplayName());
        return joinElementList;
    }

}
