package com.mohang.instadownload.instadownloader.instadownloader;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface DetailService {


    @GET
    Call<Media> detail(@Url String url);

}
