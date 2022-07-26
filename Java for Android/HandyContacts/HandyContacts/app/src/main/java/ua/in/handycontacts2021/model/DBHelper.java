package ua.in.handycontacts2021.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader12.db";
    public static final String tableName = "table1002";

    public static final String keyCatalogID = "catalogID";              //catalog ID
    public static final String keyCatalogMaster = "catalogMaster";      //Catalog Master
    public static final String keyCountryID = "countryID";
    public static final String keyCountryName = "countryName";
    //public static final String keyBack = "keyBack";
    public static final String keyLogin = "login";                      //login
    public static final String keyPass = "pass";                        //pass
    public static final String keyUserID = "userID";                    //user ID
    public static final String keyFiltrCategory = "FiltrCategory";      //filtr
    public static final String keyFiltrCountry = "keyFiltrCountry";     //filtr

        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //Log.d(TAG, "--- onCreate database ---");
            // создаем таблицу с полями
            db.execSQL("create table " + tableName + " ("
                    + "id integer primary key autoincrement,"
                    + keyCatalogID + " integer,"
                    + keyCountryID + " integer,"
                    + keyUserID + " integer,"
                    + keyCountryName + " text,"
                    + keyFiltrCategory + " text,"
                    + keyFiltrCountry + " text,"
                    + keyLogin + " text,"
                    + keyPass + " text,"
                    + keyCatalogMaster + " text"
                    + ");");

            db.execSQL("insert into " + tableName + " (" + keyLogin + ") values (\'anonym\');");
            //        + keyCatalogID + ", "
            //        + keyCountryID + ", "
            //        + keyUserID + ", "
            //        + keyCountryName + ", "
            //        + keyBack + ", "

            //        + keyPass + ", "
            //        + keyCatalogMaster


            //db.execSQL("insert into " + tableName + " ("
            //        + keyCatalogID + ", "
            //        + keyCountryID + ", "
            //        + keyUserID + ", "
            //        + keyCountryName + ", "
            //        + keyBack + ", "
            //        + keyLogin + ", "
            //        + keyPass + ", "
            //        + keyCatalogMaster + ") values (0, 0, 0, example, home, anonym, a, name);");
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName + ";");
            onCreate(db);
        }
}
