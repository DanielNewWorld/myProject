package ua.in.nets.network;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import ua.in.nets.model.ArrayListUser;

/**
 * Created by root on 30.07.15.
 */

public interface UserNetwork {
    @GET("/nets.php")
    ArrayListUser GetUser(@Query("act") String adminNAME
    );

    @POST("/nets.php")
    Void PostWallpapers(@Query("param1") double quantity, @Query("param2") String name, @Body ArrayListUser data);
}
