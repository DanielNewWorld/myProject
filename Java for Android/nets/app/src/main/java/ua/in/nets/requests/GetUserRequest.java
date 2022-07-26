package ua.in.nets.requests;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import ua.in.nets.model.ArrayListUser;
import ua.in.nets.network.UserNetwork;


/**
 * Created by root on 19.10.15.
 */
public class GetUserRequest extends RetrofitSpiceRequest<ArrayListUser, UserNetwork> {
    private String mName;

    public GetUserRequest(String adminName) {
        super(ArrayListUser.class, UserNetwork.class);
        mName = adminName;
    }

    @Override
    public ArrayListUser loadDataFromNetwork() throws Exception {
        return getService().GetUser(mName);
    }
}
