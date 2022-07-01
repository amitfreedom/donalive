package com.mobile.donalive.viewmodels

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobile.donalive.agora.firebase.GiftsListModel
import com.mobile.donalive.modelclass.CreateLiveHistoryModel
import com.mobile.donalive.modelclass.GetCoinModel
import com.mobile.donalive.modelclass.LiveMultiliveTokemModel
import com.mobile.donalive.modelclass.PKLiveUserModel
import com.mobile.donalive.retrofit.ApiInterface
import com.mobile.donalive.retrofit.BaseUrl
import com.mobile.donalive.utils.CommonUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VM : ViewModel() {

    var apiInterface: ApiInterface? = BaseUrl().getClient()?.create(ApiInterface::class.java)

    private var liveMultiliveTokemModel: MutableLiveData<LiveMultiliveTokemModel> =
        MutableLiveData()

    fun liveMultiLiveToken(
        activity: Activity,
        data: HashMap<String, String>
    ): LiveData<LiveMultiliveTokemModel> {

        if (CommonUtils.isNetworkConnected(activity)) {
            CommonUtils.showDialog(activity)
            apiInterface?.liveMultiLiveAgora(data)
                ?.enqueue(object : Callback<LiveMultiliveTokemModel> {
                    override fun onResponse(
                        call: Call<LiveMultiliveTokemModel>,
                        response: Response<LiveMultiliveTokemModel>
                    ) {
                        CommonUtils.dismissDialog()
                        if (response.body() != null) {
                            liveMultiliveTokemModel.postValue(response.body())
                        } else {
                            CommonUtils.toastBodyNullOrError(activity, "body null")
                        }
                    }

                    override fun onFailure(call: Call<LiveMultiliveTokemModel>, t: Throwable) {
                        CommonUtils.dismissDialog()
                        CommonUtils.toastBodyNullOrError(activity, t.message.toString())
                        Log.i("logi", "onFailure: ======="+t.message.toString())
                    }

                })
        } else {
            Toast.makeText(activity, "No Internet connection", Toast.LENGTH_SHORT).show()

        }

        return liveMultiliveTokemModel
    }

    private var getCoinMutable = MutableLiveData<GetCoinModel>()

    fun getCurrentCoin(activity: Activity, userId: String): LiveData<GetCoinModel> {

        if (CommonUtils.isNetworkConnected(activity)) {
            apiInterface?.getCurrentCoin(userId)?.enqueue(object : Callback<GetCoinModel> {
                override fun onResponse(
                    call: Call<GetCoinModel>,
                    response: Response<GetCoinModel>
                ) {

                    if (response.body() != null) {
                        getCoinMutable.postValue(response.body())
                    } else {
                        Toast.makeText(activity, "Body Null", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GetCoinModel>, t: Throwable) {
                    Toast.makeText(activity, "" + t.message, Toast.LENGTH_SHORT).show()
                }

            })

        } else {
            Toast.makeText(activity, "No Internet connection", Toast.LENGTH_SHORT).show()

        }


        return getCoinMutable
    }

    private var createLiveHistory: MutableLiveData<CreateLiveHistoryModel> =
        MutableLiveData()

    fun createLiveHistory(
        activity: Activity, data: HashMap<String, String>
    ): LiveData<CreateLiveHistoryModel> {

        if (CommonUtils.isNetworkConnected(activity)) {
            apiInterface?.createLiveHistory(data)
                ?.enqueue(object : Callback<CreateLiveHistoryModel> {
                    override fun onResponse(
                        call: Call<CreateLiveHistoryModel>,
                        response: Response<CreateLiveHistoryModel>
                    ) {
                        if (response.body() != null) {
                            createLiveHistory.postValue(response.body())
                        } else {
                            CommonUtils.toastBodyNullOrError(activity, "body null")
                        }
                    }

                    override fun onFailure(call: Call<CreateLiveHistoryModel>, t: Throwable) {
                        CommonUtils.toastBodyNullOrError(activity, t.message.toString())
                    }

                })
        } else {
            Toast.makeText(activity, "No Internet connection", Toast.LENGTH_SHORT).show()

        }

        return createLiveHistory
    }

    fun endLive(
        activity: Activity, data: HashMap<String, String>
    ): LiveData<CreateLiveHistoryModel> {

        if (CommonUtils.isNetworkConnected(activity)) {
            apiInterface?.endLive(data)
                ?.enqueue(object : Callback<CreateLiveHistoryModel> {
                    override fun onResponse(
                        call: Call<CreateLiveHistoryModel>,
                        response: Response<CreateLiveHistoryModel>
                    ) {
                        if (response.body() != null) {
                            createLiveHistory.postValue(response.body())
                        } else {
                            CommonUtils.toastBodyNullOrError(activity, "body null")
                        }
                    }

                    override fun onFailure(call: Call<CreateLiveHistoryModel>, t: Throwable) {
                        CommonUtils.toastBodyNullOrError(activity, t.message.toString())
                    }

                })
        } else {
            Toast.makeText(activity, "No Internet connection", Toast.LENGTH_SHORT).show()

        }

        return createLiveHistory
    }

    private var getGifts: MutableLiveData<GiftsListModel> =
        MutableLiveData()

    fun getGifts(
        activity: Activity, data: HashMap<String, String>
    ): LiveData<GiftsListModel> {

        if (CommonUtils.isNetworkConnected(activity)) {
            apiInterface?.getGifts(data)
                ?.enqueue(object : Callback<GiftsListModel> {
                    override fun onResponse(
                        call: Call<GiftsListModel>,
                        response: Response<GiftsListModel>
                    ) {
                        if (response.body() != null) {
                            getGifts.postValue(response.body())
                        } else {
                            CommonUtils.toastBodyNullOrError(activity, "body null")
                        }
                    }

                    override fun onFailure(call: Call<GiftsListModel>, t: Throwable) {
                        CommonUtils.toastBodyNullOrError(activity, t.message.toString())
                    }

                })
        } else {
            Toast.makeText(activity, "No Internet connection", Toast.LENGTH_SHORT).show()

        }

        return getGifts
    }


    private var getPkLiveUser: MutableLiveData<PKLiveUserModel> =
        MutableLiveData()

    fun getPkLiveUser(
        activity: Activity, userId: String
    ): LiveData<PKLiveUserModel> {

        if (CommonUtils.isNetworkConnected(activity)) {
            apiInterface?.getPkLiveList(userId)
                ?.enqueue(object : Callback<PKLiveUserModel> {
                    override fun onResponse(
                        call: Call<PKLiveUserModel>,
                        response: Response<PKLiveUserModel>
                    ) {
                        if (response.body() != null) {
                            getPkLiveUser.postValue(response.body())
                        } else {
                            CommonUtils.toastBodyNullOrError(activity, "body null")
                        }
                    }

                    override fun onFailure(call: Call<PKLiveUserModel>, t: Throwable) {
                        CommonUtils.toastBodyNullOrError(activity, t.message.toString())
                    }

                })
        } else {
            Toast.makeText(activity, "No Internet connection", Toast.LENGTH_SHORT).show()

        }

        return getPkLiveUser
    }


    fun sendGiftApi(
        activity: Activity, data: HashMap<String, String>
    ): LiveData<GiftsListModel> {

        if (CommonUtils.isNetworkConnected(activity)) {
            apiInterface?.receivedGiftCoin(data)
                ?.enqueue(object : Callback<GiftsListModel> {
                    override fun onResponse(
                        call: Call<GiftsListModel>,
                        response: Response<GiftsListModel>
                    ) {
                        if (response.body() != null) {
                            getGifts.postValue(response.body())
                        } else {
                            CommonUtils.toastBodyNullOrError(activity, "body null")
                        }
                    }

                    override fun onFailure(call: Call<GiftsListModel>, t: Throwable) {
                        CommonUtils.toastBodyNullOrError(activity, t.message.toString())
                    }

                })
        } else {
            Toast.makeText(activity, "No Internet connection", Toast.LENGTH_SHORT).show()

        }

        return getGifts
    }

}