package ua.in.handycontacts2021.model;

/**
 * Created by root on 30.07.15.
 */

public class RequestJSON_PhoneList {
      public int phoneID;
      public String phoneDescription;
      public String phoneName;
      public boolean boolChekedDEL;

      public RequestJSON_PhoneList(String mphoneDesc, int mphoneID, String mphoneNAME, boolean boolCHEK) {
            phoneID = mphoneID;
            phoneDescription = mphoneDesc;
            phoneName = mphoneNAME;
            boolChekedDEL = boolCHEK;
      }
}