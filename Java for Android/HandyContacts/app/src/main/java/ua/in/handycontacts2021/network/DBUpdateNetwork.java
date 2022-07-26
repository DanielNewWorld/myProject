package ua.in.handycontacts2021.network;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import ua.in.handycontacts2021.model.DBRequest;

/**
 * Created by root on 30.07.15.
 */

public interface DBUpdateNetwork {
    @GET("/script/hcontacts/hcontacts.php")
    DBRequest GetUser(@Query("service") String service, @Query("data") String data);

    @POST("/script/hcontacts/hcontacts.php")
    Void PostWallpapers(@Query("param1") double quantity, @Query("param2") String name, @Body DBRequest data);
}
