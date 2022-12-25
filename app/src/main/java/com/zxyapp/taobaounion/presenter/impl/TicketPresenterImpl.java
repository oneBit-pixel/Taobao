package com.zxyapp.taobaounion.presenter.impl;

import com.zxyapp.taobaounion.model.Api;
import com.zxyapp.taobaounion.model.domain.TicketParams;
import com.zxyapp.taobaounion.model.domain.TicketResult;
import com.zxyapp.taobaounion.presenter.ITickPresenter;
import com.zxyapp.taobaounion.utils.LogUtils;
import com.zxyapp.taobaounion.utils.RetrofitManager;
import com.zxyapp.taobaounion.utils.UrlUtils;
import com.zxyapp.taobaounion.view.ITicketPagerCallback;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TicketPresenterImpl implements ITickPresenter {

    private ITicketPagerCallback mViewCallback = null;
    private String mCover=null;
    private TicketResult mTicketResult;

    enum LoadState {
        LOADING, SUCCESS, ERROR, NONE
    }

    private LoadState mCurrentState = LoadState.NONE;

    @Override
    public void getTicket(String title, String url, String cover) {
        //设置状态的目的，是防止数据还没加载出来时，程序运行太快，初始化没准备好
        this.onTicketLoading();
        this.mCover=cover;
        LogUtils.d(this, "title----->" + title);
        LogUtils.d(this, "url----->" + url);
        LogUtils.d(this, "cover----->" + cover);
        String ticketUrl = UrlUtils.getTicketUrl(url);
        LogUtils.d(this, "ticketUrl----->" + ticketUrl);
        //去获取淘口令
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        TicketParams ticketParams = new TicketParams(ticketUrl, title);
        Call<TicketResult> task = api.getTicket(ticketParams);
        task.enqueue(new Callback<TicketResult>() {
            @Override
            public void onResponse(Call<TicketResult> call, Response<TicketResult> response) {
                int code = response.code();
                LogUtils.d(this, "code------>" + code);
                if (code == HttpsURLConnection.HTTP_OK) {
                    //请求成功
                    mTicketResult = response.body();
                    LogUtils.d(TicketPresenterImpl.this, "result----->" + mTicketResult);
                    //通知UI更新
                    onTicketLoadedSuccess();
                } else {
                    //请求失败
                    onLoadedTicketError();
                    mCurrentState = LoadState.ERROR;
                }
            }

            @Override
            public void onFailure(Call<TicketResult> call, Throwable t) {
                //失败
                onLoadedTicketError();
            }
        });

    }

    private void onTicketLoadedSuccess() {
        if (mViewCallback != null) {
            mViewCallback.onTicketLoaded(mCover, mTicketResult);
        }else {
            mCurrentState = LoadState.SUCCESS;
        }
    }

    private void onLoadedTicketError() {
        if (mViewCallback != null) {
            mViewCallback.onError();
        }else {
            mCurrentState = LoadState.ERROR;
        }
    }

    @Override
    public void registerViewCallback(ITicketPagerCallback callback) {
        this.mViewCallback = callback;
        if (mCurrentState != LoadState.NONE) {
            //说明状态已经改变了
            //更新UI
            if (mCurrentState==LoadState.SUCCESS){
                onTicketLoadedSuccess();
            }else if (mCurrentState==LoadState.ERROR){
                onLoadedTicketError();
            }else if (mCurrentState==LoadState.LOADING){
                onTicketLoading();
            }
        }
    }

    private void onTicketLoading() {
        if (mViewCallback != null) {
            mViewCallback.onLoading();
        }else {
            mCurrentState = LoadState.LOADING;
        }
    }

    @Override
    public void unregisterViewCallback(ITicketPagerCallback callback) {
        this.mViewCallback = null;
    }

}
