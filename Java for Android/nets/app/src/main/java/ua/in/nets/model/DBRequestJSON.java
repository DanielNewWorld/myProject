package ua.in.nets.model;

/**
 * Created by root on 30.07.15.
 */

public class DBRequestJSON {
      public boolean boolCheked;
      public String userLogin;
      public String userGRP;
      public String userChekError;
      public String userID;
      public String userFIO;
      public String userIP;
      public String userContract;
      public String userContractDate;
      public String userSTATE;
      public float userBALANCE;
      public String userLimitBalance;
      public String userModifyTime;
      public String userPaket;
      public String userPaketPrice;
      public String userNextPaket;
      public String userDiscount;
      public String userComment;
      public int userGRP_id;

      public String paysID;
      public String paysDates;
      public String paysNumDoc;
      public String paysType;
      public String paysAmount;
      public String paysDescription;

      public DBRequestJSON(String muserLogin, String muserGRP, String muserChekError, boolean boolCHEK,
                           String muserID, String muserFIO, String muserIP, String muserContract,
                           String muserContractDate, String muserState, float muserBalance, String muserLimitBalance,
                           String muserModifyTime, String muserPaket, String muserPaketPrice, String muserNextPaket,
                           String muserDiscount, String muserComment, int muser_GRP_id,
                           String mpaysID, String mpaysDates, String mpaysNumDoc, String mpaysType, String mpaysAmount,
                           String mpaysDescription) {
            userLogin = muserLogin;
            userGRP = muserGRP;
            userChekError = muserChekError;
            boolCheked = boolCHEK;
            userID = muserID;
            userFIO = muserFIO;
            userIP = muserIP;
            userContract = muserContract;
            userContractDate = muserContractDate;
            userSTATE = muserState;
            userBALANCE = muserBalance;
            userLimitBalance = muserLimitBalance;
            userModifyTime = muserModifyTime;
            userPaket = muserPaket;
            userPaketPrice = muserPaketPrice;
            userNextPaket = muserNextPaket;
            userDiscount = muserDiscount;
            userComment = muserComment;
            userGRP_id = muser_GRP_id;

            paysID = mpaysID;
            paysDates = mpaysDates;
            paysNumDoc = mpaysNumDoc;
            paysType = mpaysType;
            paysAmount = mpaysAmount;
            paysDescription = mpaysDescription;
      }
}