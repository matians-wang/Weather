package com.test.carweather.network;

import java.util.HashMap;

import com.test.carweather.entity.ActiveResponseEntity;
import com.test.carweather.entity.DNSResponseEntity;
import com.test.carweather.entity.GeocodeResponseEntity;
import com.test.carweather.entity.GetTokenResponseEntity;
import com.test.carweather.entity.ReplaceTokenResponseEntity;
import com.test.carweather.entity.SearchResponseEntity;
import com.test.carweather.entity.WeatherInfoResponseEntity;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface WeatherApi {

    String BASE_URL = "http://54.223.255.108:48091/UniTSSPbackend/weather/";

    @GET
    Call<DNSResponseEntity> getDNS(@Header("Cookie") String tuidAndToken, @Url String url,
            @QueryMap HashMap<String, String> dns);

    @POST
    Call<ActiveResponseEntity> postActive(@Header("Cookie") String tuidAndToken, @Url String url,
            @QueryMap HashMap<String, String> dns);

    @POST
    Call<GetTokenResponseEntity> postGetToken(@Header("Cookie") String tuidAndToken, @Url String url,
            @QueryMap HashMap<String, String> dns);

    @POST
    @FormUrlEncoded
    Call<ReplaceTokenResponseEntity> postReplaceToken(@Header("Cookie") String tuidAndToken, @Url String url,
            @QueryMap HashMap<String, String> dns, @Field("version") String version,
            @Field("sys_version") String sysVersion);

    @GET
    Call<WeatherInfoResponseEntity> getWeather(@Header("Cookie") String tuidAndToken, @Url String url,
            @QueryMap HashMap<String, String> weather);

    @GET
    Call<GeocodeResponseEntity> getGeocode(@Header("Cookie") String tuidAndToken, @Url String url,
            @QueryMap HashMap<String, String> geocode);

    @GET
    Call<SearchResponseEntity> getSearch(@Header("Cookie") String tuidAndToken, @Url String url,
            @QueryMap HashMap<String, String> search);

}
