package ua.in.handycontacts2021.model;

/**
 * Created by root on 30.07.15.
 */

public class RequestJSON_UsersList {
      public int statusID;
      public int admin;
      //public String catalogTerritory;
      //public String catalogName;
      //public int userRating;
      //public String catalogDescription;
      //public String catalogCategory;
      //public int catalogPublish;
      public boolean boolCheked;

      public RequestJSON_UsersList(int mstatusID, int madmin, boolean boolCHEK) {
            statusID = mstatusID;
            admin = madmin;
            //catalogID = catalogIDIn;
            //categoryID = mcategoryID;
            boolCheked = boolCHEK;
            //userRating = muserRating;
            //catalogCategory = mcatalogCategory;
            //catalogTerritory = mcatalogTerritory;
            //catalogPublish = mCatalogPublish;
      }
}