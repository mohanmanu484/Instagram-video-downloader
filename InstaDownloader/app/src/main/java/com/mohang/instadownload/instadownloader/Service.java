package com.mohang.instadownload.instadownloader;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Service {


    private static Service service;
    private Retrofit retrofit;

    public Service() {

         retrofit = new Retrofit.Builder()
                .baseUrl("http://139.59.27.50:3000/")
                 .addConverterFactory(GsonConverterFactory.create())
                .build();


    }

    public static Service getService(){



        if(service ==null){
            service =new Service();
        }
        return service;
    }


    public DetailService getDetailPost() {
        return retrofit.create(DetailService.class);
    }


}
