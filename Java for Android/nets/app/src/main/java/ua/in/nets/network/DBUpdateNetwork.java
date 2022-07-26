package ua.in.nets.network;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import ua.in.nets.model.DBRequest;

/**
 * Created by root on 30.07.15.
 */

public interface DBUpdateNetwork {
    @GET("/nets.php")
    DBRequest GetUser(@Query("service") String service, @Query("data") String data);

    @POST("/nets.php")
    Void PostWallpapers(@Query("param1") double quantity, @Query("param2") String name, @Body DBRequest data);
}
