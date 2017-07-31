package tw.gov.epa.tamqpredictmap.predict;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tw.gov.epa.tamqpredictmap.MapsActivity;
import tw.gov.epa.tamqpredictmap.basic.BasePresenter;
import tw.gov.epa.tamqpredictmap.predict.model.PredictData;
import tw.gov.epa.tamqpredictmap.predict.model.Result;

/**
 * Created by user on 2017/3/2.
 */

public class DriverService extends BasePresenter<DriverService.View> {
    public interface View{
        void refreshMap(List<Result> results);
    }

    private String url = "https://drive.google.com/"; //https://www.googleapis.com/
    //測試ID:"0B_TvZKObPCRCa1VSdVJaWkU2Q2s" 正式ID:"0B7Ld7OVhJc6HTDlya29QbEpRdEU"
    private String id = "0B7Ld7OVhJc6HTDlya29QbEpRdEU";//"0B_TvZKObPCRCa1VSdVJaWkU2Q2s";
    private String download = "download";
    private DriverAPI driveApi;

    public DriverService() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        driveApi = retrofit.create(DriverAPI.class);
    }

    public void getPredictData() {
        Call<PredictData> result = driveApi.getPredictData(id,download);
        result.enqueue(new Callback<PredictData>() {
            @Override
            public void onResponse(Call<PredictData> call, Response<PredictData> response) {
                //Log.d("predict response:",response.body().getResult());
                for(Result res:response.body().getResult()){
                    Log.d("Driverservice:",res.getSiteName()+":"+res.getHr1());
                }
                view.refreshMap(response.body().getResult());
            }

            @Override
            public void onFailure(Call<PredictData> call, Throwable t) {

            }
        });
    }
}
