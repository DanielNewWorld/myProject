package ua.in.handycontacts2021.model;

/**
 * Created by root on 30.07.15.
 */

public class RequestJSON_CategoryList {
      public int categoryID;
      //public int catalogIDName;
      public String categoryName;
      public boolean boolCheked;

      public RequestJSON_CategoryList(String categoryNAME, int categoryIDIn, boolean boolCHEK) {
            categoryName = categoryNAME;
            categoryID = categoryIDIn;
            //catalogIDName = mCIDName;
            boolCheked = boolCHEK;
      }

      public int getCategoryID() {
            return categoryID;
      }

      public void setCategoryID(int categoryID) {
            this.categoryID = categoryID;
      }

      public String getCategoryName() {
            return categoryName;
      }

      public void setCategoryName(String categoryNANE) {
            this.categoryName = categoryName;
      }

      public boolean getBoolCheked() {
            return boolCheked;
      }

      public void setBoolCheked(boolean boolCheked) {
            this.boolCheked = boolCheked;
      }
}