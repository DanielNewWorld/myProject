package ua.in.nets.network;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

public class DataService extends RetrofitGsonSpiceService {
    private final static String BASE_URL = "http://www.nets.in.ua/script/nets.app";

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected String getServerUrl() {
        return BASE_URL;
    }

}