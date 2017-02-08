package com.sean.firebase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity {

    //广告控件
    private AdView adv_banner;
    private NativeExpressAdView neadv_native;
    private Button bt_insert_page;

    //广告请求
    private AdRequest adRequest;

    //插页广告
    private InterstitialAd interstitialAd;
    private VideoController mVideoController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        adv_banner = (AdView) findViewById(R.id.main_adv_banner);
        bt_insert_page = (Button) findViewById(R.id.main_bt_insert_page);
        neadv_native = (NativeExpressAdView) findViewById(R.id.main_neadv_native);
    }

    private void initEvent() {
        bt_insert_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果加载完毕就显示广告
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    Toast.makeText(getApplicationContext(), "load not finish", Toast.LENGTH_SHORT).show();
                    //广告没加载，这里请求一个新的广告
                    requestNewInterstitial();
                }
            }
        });
    }

    private void initData() {
        //初始化firebase 统计
        FirebaseAnalytics.getInstance(getApplicationContext());
        //创建广告请求
        adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)//所有的模拟器
                .addTestDevice("74BAABC1A0E77EB8C34896404447DBEC")//nexus 5
                .addTestDevice("E3A8BB93EE8D9CF7C0B5351AB456C4C5")//我的锤子M1L
                .build();
        //横幅广告
        adv_banner.loadAd(adRequest);

        //插页广告
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.insert_ad_unit_id));//设置单元id
        //增加监听
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                //关闭了这个广告先加载下一个广告
                requestNewInterstitial();
            }
        });
        //加载插页广告
        requestNewInterstitial();

        neadv_native.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                Log.d("AdListener", "onAdFailedToLoad i:" + i);
                super.onAdFailedToLoad(i);
            }
        });
        // Set its video options.
        neadv_native.setVideoOptions(new VideoOptions.Builder()
                .setStartMuted(true)
                .build());

        // The VideoController can be used to get lifecycle events and info about an ad's video
        // asset. One will always be returned by getVideoController, even if the ad has no video
        // asset.
        mVideoController = neadv_native.getVideoController();
        mVideoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            @Override
            public void onVideoEnd() {
                Log.d("onVideoEnd", "Video playback is finished.");
                super.onVideoEnd();
            }
        });

        // Set an AdListener for the AdView, so the Activity can take action when an ad has finished
        // loading.
        neadv_native.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (mVideoController.hasVideoContent()) {
                    Log.d("onAdLoaded", "Received an ad that contains a video asset.");
                } else {
                    Log.d("onAdLoaded", "Received an ad that does not contain a video asset.");
                }
            }
        });

        //创建原生广告请求
        AdRequest adRequestNative = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)//所有的模拟器
                .addTestDevice("74BAABC1A0E77EB8C34896404447DBEC")//nexus 5
                .addTestDevice("E3A8BB93EE8D9CF7C0B5351AB456C4C5")//我的锤子M1L
                .build();
        //加载原生广告
        neadv_native.loadAd(adRequestNative);
    }

    /**
     * 请求一个新的插页广告
     */
    private void requestNewInterstitial() {
        //插页广告
        interstitialAd.loadAd(adRequest);
    }
}
