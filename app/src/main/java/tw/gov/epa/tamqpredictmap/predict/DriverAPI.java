package tw.gov.epa.tamqpredictmap.predict;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import tw.gov.epa.tamqpredictmap.predict.model.PredictData;

/**
 * Created by user on 2017/3/2.
 */

public interface DriverAPI {
    @GET("uc")
    Call<PredictData> getPredictData(
            @Query("id") String id,
            @Query("export") String export
    );
}
