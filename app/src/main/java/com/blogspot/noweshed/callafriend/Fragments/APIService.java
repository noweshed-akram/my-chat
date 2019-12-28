package com.blogspot.noweshed.callafriend.Fragments;

import com.blogspot.noweshed.callafriend.Notification.MyResponse;
import com.blogspot.noweshed.callafriend.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key="
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
