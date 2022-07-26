package ua.in.handycontacts2021.model;

/**
 * Created by root on 30.07.15.
 */

public class RequestJSON_CatalogList {
      public int catalogID;
      public int categoryID;
      public String catalogTerritory;
      public String catalogName;
      public int userRating;
      public String catalogDescription;
      public String catalogCategory;
      public String userLogin;
      public int catalogPublish;
      public boolean boolCheked;

      public RequestJSON_CatalogList(String catalogNAME, String catalogDesc, String mUserLogin, int catalogIDIn,
                                     boolean boolCHEK, int muserRating, int mcategoryID, String mcatalogCategory,
                                     String mcatalogTerritory, int mCatalogPublish) {
            catalogName = catalogNAME;
            catalogDescription = catalogDesc;
            catalogID = catalogIDIn;
            categoryID = mcategoryID;
            boolCheked = boolCHEK;
            userRating = muserRating;
            catalogCategory = mcatalogCategory;
            catalogTerritory = mcatalogTerritory;
            catalogPublish = mCatalogPublish;
            userLogin = mUserLogin;
      }
}