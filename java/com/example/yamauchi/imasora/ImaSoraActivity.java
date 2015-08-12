package com.example.yamauchi.imasora;

import com.example.yamauchi.imasora.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class ImaSoraActivity extends Activity
        implements LoaderManager.LoaderCallbacks<String>
{
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ima_sora);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        // ボタンクリックのイベントリスナーを追加する
        findViewById(R.id.dummy_button).setOnClickListener(clickListener);

        // 通信開始
        execStart();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    View.OnClickListener clickListener  = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // 更新処理開始
            execStart();
        }
    };


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    // GPSの緯度経度をパラメータにして、住所を取得するWebAPIを呼び出す
    public void execStart() {

        //　GPSから現在地の情報を取得する
        LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        Location myLocate = locationManager.getLastKnownLocation("gps");

        Bundle bundle = new Bundle();
        if (myLocate != null) {
            // 緯度
            bundle.putString("lat", Double.toString(myLocate.getLatitude()));
            // 経度
            bundle.putString("lon", Double.toString(myLocate.getLongitude()));
        }
        else {
            // GPSが使用できない場合、皇居の緯度経度を設定する
            bundle.putString("lat", "35.6853264");
            bundle.putString("lon", "139.7530997");
        }
        bundle.putString("geo", "http://www.finds.jp/ws/rgeocode.php?");

        getLoaderManager().initLoader(0, bundle, this);

    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle bundle) {
        HttpAsyncLoader loader = null;

        switch ( id ) {

            // 緯度経度から都道府県コードを取得する
            case 0:
                String url = bundle.getString("geo") +
                        "lat=" + bundle.getString("lat") +
                        "&" +
                        "lon=" + bundle.getString("lon") +
                        "&json";

                Log.d("url", url);
                loader  = new HttpAsyncLoader(this, url);
                loader.forceLoad();
                break;

            // 天気予報を取得する
            case 1:
                Log.d("url", bundle.getString("url"));

                loader = new HttpAsyncLoader(this, bundle.getString("url"));
                loader.forceLoad();
                break;
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String body) {
        if (body == null) return;

        ParseJson analyze = null;
        switch ( loader.getId() ) {

            // 都道府県コードから、該当の天気予報の取得を開始する
            case 0:
                analyze = new ParseFindsjp();
                analyze.loadJson(body);

                Bundle bundle = new Bundle();
                bundle.putString("url", "http://www.drk7.jp/weather/json/" + analyze.getContent() + ".js");
                getLoaderManager().initLoader(1, bundle, this);
                break;

            // 天気予報情報を、ビューにセットする
            case 1:
                analyze = new ParseDrk7jpweather();
                analyze.loadJson(body);

                TextView tv = (TextView)findViewById(R.id.fullscreen_content);

                tv.setText( analyze.getContent() );

                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        // 今回は何も処理しない
    }
}
