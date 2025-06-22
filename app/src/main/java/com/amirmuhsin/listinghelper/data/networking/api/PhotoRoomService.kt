package com.amirmuhsin.listinghelper.data.networking.api

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PhotoRoomService {

    @Multipart
    @POST("v1/segment")
    suspend fun cleanBackground(
        @Part image_file: MultipartBody.Part,
        @Part("format") format: String? = null,
        @Part("bg_color") bgColor: String? = null,
        @Part("size") size: String? = null,
        @Part("crop") crop: Boolean? = null
    ): Response<ResponseBody>

    @Multipart
    @POST("v2/edit")
    suspend fun editImage(
        @Part imageFile: MultipartBody.Part,
        @Part("background.color") bgColor: String = "FFFFFFFF",
        @Part("outputSize") outputSize: String = "1600x1600",
        @Part("marginTop") marginTop: String = "2%",
        @Part("marginBottom") marginBottom: String = "2%",
        @Part("marginLeft") marginLeft: String = "2%",
        @Part("marginRight") marginRight: String = "2%",
        @Part("paddingTop") paddingTop: String = "5%",
        @Part("paddingBottom") paddingBottom: String = "5%",
        @Part("paddingLeft") paddingLeft: String = "5%",
        @Part("paddingRight") paddingRight: String = "5%"
    ): Response<ResponseBody>

}
