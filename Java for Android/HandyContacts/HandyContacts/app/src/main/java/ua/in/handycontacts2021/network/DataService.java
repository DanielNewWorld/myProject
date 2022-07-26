package ua.in.handycontacts2021.network;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

public class DataService extends RetrofitGsonSpiceService {
    public final static String BASE_URL = "https://www.hcontacts.shop";
    //public static final String BASE_URL_Retrofit = "https://www.hcontacts.shop";
    //private final static String BASE_URL = "https://hcontacts.litespeed-hosting.online/script";
    //private final static String BASE_URL = "http://www.nets.in.ua/script/hcontacts";

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected String getServerUrl() {
        return BASE_URL;
    }

}