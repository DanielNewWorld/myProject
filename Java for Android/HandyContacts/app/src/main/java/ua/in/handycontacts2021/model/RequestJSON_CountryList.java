package ua.in.handycontacts2021.model;

/**
 * Created by root on 30.07.15.
 */

public class RequestJSON_CountryList {
      public int countryID;
      public String countryName;
      public String cityList;
      public boolean boolCheked;

      public RequestJSON_CountryList(int mcountryID, String mcountryNAME, boolean boolCHEK, String mcityList) {
            countryID = mcountryID;
            countryName = mcountryNAME;
            cityList = mcityList;
            boolCheked = boolCHEK;
      }
}