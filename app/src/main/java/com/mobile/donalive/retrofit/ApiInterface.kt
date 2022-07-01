package com.mobile.donalive.retrofit

import com.mobile.donalive.agora.firebase.GiftsListModel
import com.mobile.donalive.modelclass.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

//    @FormUrlEncoded
//    @POST("quick-Registration")
//    fun checkPhoneNumberApi(@FieldMap data: HashMap<String, String>): Call<UserInfoModel>
//
//    @FormUrlEncoded
//    @POST("select-gender")
//    fun selectGenderApi(@FieldMap data: HashMap<String, String>): Call<UserInfoModel>
//
//    @FormUrlEncoded
//    @POST("select-dob")
//    fun selectDateOfBirth(@FieldMap data: HashMap<String, String>): Call<UserInfoModel>
//
//    @GET("select-purpose")
//    fun purposeList(): Call<PurposeModel>
//
//    @FormUrlEncoded
//    @POST("select-purposeList")
//    fun selectPurposeList(@FieldMap data: HashMap<String, String>): Call<UserInfoModel>
//
//    @FormUrlEncoded
//    @POST("set-username")
//    fun setUsername(@FieldMap data: HashMap<String, String>): Call<UserInfoModel>
//
//    @FormUrlEncoded
//    @POST("user-stage")
//    fun userStage(@Field("userId") userId: String): Call<StageModel>
//
//    @FormUrlEncoded
//    @POST("user-final-stage")
//    fun userFinalStage(@Field("userId") userId: String): Call<StageModel>
//
//    @FormUrlEncoded
//    @POST("user-logout")
//    fun userLogout(@Field("userId") userId: String): Call<StageModel>
//
//    @FormUrlEncoded
//    @POST("user-get-Otp")
//    fun userGetOtp(@FieldMap data: HashMap<String, String>): Call<GetOtpModel>
//
//    @FormUrlEncoded
//    @POST("user-match-Otp")
//    fun userMatchOtp(@FieldMap data: HashMap<String, String>): Call<UserInfoModel>
//
//    @FormUrlEncoded
//    @POST("social-login")
//    fun socialLogin(@FieldMap data: HashMap<String, String>): Call<UserInfoModel>
//
//
//    @Multipart
//    @POST("user-upload-images")
//    fun userUploadImages(
//        @Part("userId") userId: RequestBody,
//        @Part imagesList: ArrayList<MultipartBody.Part>,
//        @Part image: MultipartBody.Part
//    ): Call<UserInfoModel>
//
//    @FormUrlEncoded
//    @POST("user-get-profile")
//    fun userGetProfile(@Field("userId") userId: String): Call<UserInfoModel>
//
//
//    @Multipart
//    @POST("user-update-profile")
//    fun userUpdateProfile(
//        @Part("name") name: RequestBody,
//        @Part("bio") bio: RequestBody,
//        @Part("userId") userId: RequestBody,
//        @Part image: MultipartBody.Part
//    ): Call<UserInfoModel>
//
//
//    @Multipart
//    @POST("upload-media")
//    fun uploadMedia(
//        @Part("modeType") modeType: RequestBody,
//        @Part("userId") userId: RequestBody,
//        @Part("mediaType") mediaType: RequestBody,
//        @Part image: MultipartBody.Part
//    ): Call<UserInfoModel>
//
//    @FormUrlEncoded
//    @POST("get-user-media")
//    fun getUserMedia(
//        @Field("modeType") modeType: String, @Field("userId") userId: String
//    ): Call<MediaModel>


    @FormUrlEncoded
    @POST("home-screen")
    fun homeScreen(
        @Field("userId") userId: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String
    ): Call<HomeModel>

    @FormUrlEncoded
    @POST("nearBy-users")
    fun nearByUsers(
        @Field("userId") userId: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String
    ): Call<NearByModel>

    @FormUrlEncoded
    @POST("add-matches")
    fun addMatches(
        @Field("userId") userId: String
    ): Call<HomeModel>

    @FormUrlEncoded
    @POST("get-otherUserDetails")
    fun getOtherUserDetails(
        @Field("otherUserId") otherUserId: String,
        @Field("userId") userId: String
    ): Call<OtherUserDetailsModel>


    @GET("get-country")
    fun getCountry(): Call<GetCountrySliderModel>


    @FormUrlEncoded
    @POST("like-dislike")
    fun likeDislike(
        @Field("userId") userId: String,
        @Field("otherUserId") otherUserId: String,
    ): Call<LikeDislikeModel>

    @FormUrlEncoded
    @POST("get-liked-list")
    fun getLikedList(
        @Field("userId") userId: String
    ): Call<LikeLikedByModel>

    @FormUrlEncoded
    @POST("get-likedBy-list")
    fun getLikedByList(
        @Field("userId") userId: String
    ): Call<LikeLikedByModel>

    @FormUrlEncoded
    @POST("get-User-ByCountry")
    fun getUserByCountry(
        @Field("country") country: String,
        @Field("userId") userId: String
    ): Call<NearByModel>


    @FormUrlEncoded
    @POST("unlock-Profile")
    fun unlockProfile(
        @Field("diamond") diamond: String,
        @Field("userId") userId: String,
        @Field("otherUserId") otherUserId: String
    ): Call<UserInfoModel>

    @FormUrlEncoded
    @POST("post-delete")
    fun postDelete(
        @Field("id") diamond: String
    ): Call<UserInfoModel>


    @FormUrlEncoded
    @POST("spin-list")
    fun spinList(
        @Field("userId") userId: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("gender") gender: String
    ): Call<SpinModel>


    @FormUrlEncoded
    @POST("live-Multi-Live-Token")
    fun liveMultiLiveAgora(@FieldMap data: HashMap<String, String>): Call<LiveMultiliveTokemModel>

    @FormUrlEncoded
    @POST("generateTokenForRTMlogin")
    fun generateTokenForRTMlogin(@Field("channelName") channelName: String): Call<GenerateTokenForRTMLoginModel>

    @GET("get-Live-Multi-Live-Users")
    fun getLiveMultiLiveUsers(): Call<GetLiveMultiLiveModel>

    @FormUrlEncoded
    @POST("liveHistory")
    fun createLiveHistory(@FieldMap data: HashMap<String, String>): Call<CreateLiveHistoryModel>

    @FormUrlEncoded
    @POST("endLive")
    fun endLive(@FieldMap data: HashMap<String, String>): Call<CreateLiveHistoryModel>

    @FormUrlEncoded
    @POST("getGifts")
    fun getGifts(@FieldMap data: HashMap<String, String>): Call<GiftsListModel>

    @FormUrlEncoded
    @POST("getPkLiveList")
    fun getPkLiveList(@Field("userId") userId: String): Call<PKLiveUserModel>


    @FormUrlEncoded
    @POST("receivedGiftCoin")
    fun receivedGiftCoin(@FieldMap data: HashMap<String, String>): Call<GiftsListModel>


    @FormUrlEncoded
    @POST("getCoins")
    fun getCurrentCoin(@Field("userId") userId: String): Call<GetCoinModel>

}
