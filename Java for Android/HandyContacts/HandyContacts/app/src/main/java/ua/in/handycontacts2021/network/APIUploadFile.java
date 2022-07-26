package ua.in.handycontacts2021.network;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import ua.in.handycontacts2021.model.DBRequest;

//sftp://91.194.250.154
//hcontacts
//redoHCONTACTS

public interface APIUploadFile {
    @Multipart
    @POST("/script/hcontacts/php_upload.php")

    Call<DBRequest> uploadImage(
            @Part MultipartBody.Part file
            //@Query("service") String service,
            //@Query("data") String data
    );

    @GET("/script/hcontacts/hcontacts.php")
    Call<DBRequest> serviceGET(
            @Query("service") String service,
            @Query("data") String data
    );
}
