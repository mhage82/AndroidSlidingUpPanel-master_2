package com.courtside.demo.utility;

/**
 * Created by MELHAGEH on 11/10/2016.
 */

public class JsonElement {
    double longitude;
    double latitude;
    String username;
    String title;
    String gameType;
    String gameTime;
    String _id;
    String players;
    int openSeatsNum;
    boolean isPublicGame;


    public JsonElement(double lat, double lng,
                       String un, String title, String gameType, String gameTime, String id,String players,
                       int openSeatsNum,
                       boolean isPublicGame){
        this.latitude = lat;
        this.longitude = lng;
        this.username = un;
        this.title = title;
        this.gameType = gameType;
        this.openSeatsNum = openSeatsNum;
        this.isPublicGame = isPublicGame;
        this.gameTime = gameTime;
        this._id = id;
        this.players = players;
    }

    public void setOpenSeatsNum( int num){ this.openSeatsNum = num;}
    public int getOpenSeatsNum(){ return  this.openSeatsNum;}

    public void gameIsPublic(boolean isPublic){this.isPublicGame = isPublic;}
    public boolean isGamePublic(){return this.isPublicGame;}

    public void setGameType(String gametype){ this.gameType = gametype;}
    public String getGameType(){return this.gameType;}

    public void setLatitude(double lat){ this.latitude = lat;}
    public double getLatitude(){return this.latitude;}

    public void setLongitude(double lng){ this.longitude = lng;}
    public double getLongitude(){return this.longitude;}

    public void setUsername(String un){ this.username = un;}
    public String getUsername(){return this.username;}

    public void setGameTitle(String un){ this.title = un;}
    public String getGameTitle(){return this.title;}

    public void setGameTime(String gameTime){ this.gameTime = gameTime;}
    public String getGameTime(){return this.gameTime;}

    public void setGameId(String id){ this._id = id;}
    public String getGameId(){ return this._id;}

    public void setPlayers(String value){ players = value;}
    public String getPlayers(){return players;}
}
