package com.example.meme;

import android.content.Context;
import android.widget.Toast;

import com.example.meme.Structure.Api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class RequestManager {

    public Context context;

    public Retrofit retrofit = new Retrofit.Builder().baseUrl("https://meme-api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public RequestManager(Context context) {
        this.context = context;
    }

    public void getData(OnFetchDataListener listener){

        callApi callApi = retrofit.create(RequestManager.callApi.class);
        Call<Api> call = callApi.callApi();

        try {

            call.enqueue(new Callback<Api>() {
                @Override
                public void onResponse(Call<Api> call, Response<Api> response) {

                    if (!response.isSuccessful())
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();

                    listener.OnFetchData(response.body().postLink, response.body()
                                    .subreddit, response.body().title, response.body().url
                            , response.body().nsfw, response.body().spoiler, response.body().author
                            , response.body().ups, response.body().preview, response.message());


                }

                @Override
                public void onFailure(Call<Api> call, Throwable t) {

                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();

                }
            });

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    interface callApi{

        //String s = "AdviceAnimals";

        @GET("gimme")

        Call<Api> callApi();

    }

}

