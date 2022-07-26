package ua.in.handycontacts2021.requests;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import ua.in.handycontacts2021.model.DBRequest;
import ua.in.handycontacts2021.network.DBUpdateNetwork;

/**
 * Created by root on 19.10.15.
 */
public class GetRequest extends RetrofitSpiceRequest<DBRequest, DBUpdateNetwork> {
    private String mSERVICE;
    private String mDATA;

    public GetRequest(String service, String data) {
        super(DBRequest.class, DBUpdateNetwork.class);
        mSERVICE = service;
        mDATA = data;
    }

    @Override
    public DBRequest loadDataFromNetwork() throws Exception {
        return getService().GetUser(mSERVICE, mDATA);
    }
}
