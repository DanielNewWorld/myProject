package ua.in.handycontacts2021.model;

import java.io.Serializable;

/**
 * Created by root on 30.07.15.
 */

public class Filtre_class implements Serializable {
      public String jsonCategory;
      public String jsonCountry;
      public boolean boolCheked;

      public Filtre_class(boolean boolCHEK, String jsonCategory, String jsonCountry) {
            this.boolCheked = boolCHEK;
            this.jsonCategory = jsonCategory;
            this.jsonCountry = jsonCountry;
      }

      /*public int getCategoryID() {
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
*/
      public boolean getBoolCheked() {
            return boolCheked;
      }

      public void setBoolCheked(boolean boolCheked) {
            this.boolCheked = boolCheked;
      }
}