package bosch.cpm.com.bosch.retrofit;

import com.squareup.okhttp.RequestBody;

import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by jeevanp on 5/29/2018.
 */

public interface PostApiForUpload {
    @POST("Uploadimageswithpath")
    retrofit.Call<String> getUploadImageRetrofitOne(@Body RequestBody body1);

    @POST("Uploadimages")
    retrofit.Call<String> getUploadImages(@Body RequestBody body1);
}
