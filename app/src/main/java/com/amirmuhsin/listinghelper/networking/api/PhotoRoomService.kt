package com.amirmuhsin.listinghelper.networking.api

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
}
