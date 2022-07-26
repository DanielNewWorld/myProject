package ua.in.handycontacts2021.model;

/**
 * Created by root on 30.07.15.
 */

public class RequestJSON_CityList {
      public int cityID;
      public int cityIDCountry;
      public String cityName;
      public boolean boolCheked;
      public boolean boolSelect;

      public RequestJSON_CityList(int mcityID, String mcityNAME, boolean boolCHEK, int mcityIDCountry,
                                  boolean mboolSelect) {
            cityID = mcityID;
            cityIDCountry = mcityIDCountry;
            cityName = mcityNAME;
            boolCheked = boolCHEK;
            boolSelect = mboolSelect;
      }
}