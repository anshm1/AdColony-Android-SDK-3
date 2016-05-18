package com.adcolony.rewardedinterstitialdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.adcolony.ads.AdColony;
import com.adcolony.ads.AdColonyAdOptions;
import com.adcolony.ads.AdColonyAppOptions;
import com.adcolony.ads.AdColonyInterstitial;
import com.adcolony.ads.AdColonyInterstitialListener;
import com.adcolony.ads.AdColonyReward;
import com.adcolony.ads.AdColonyRewardListener;
import com.adcolony.ads.AdColonyUserMetadata;
import com.adcolony.ads.AdColonyZone;

public class RewardedInterstitialActivity extends Activity
{
    final private String APP_ID = "app185a7e71e1714831a49ec7";
    final private String ZONE_ID = "vz1fd5a8b2bf6841a0a4b826";
    final private String TAG = "AdColonyDemo";

    private Button show_button;
    private ProgressBar progress;
    private AdColonyInterstitial ad;
    private AdColonyInterstitialListener listener;
    private AdColonyAdOptions ad_options;

    @Override
    protected void onCreate( Bundle bundle )
    {
        super.onCreate( bundle );
        setContentView( R.layout.activity_rewarded_interstitial );
        progress = (ProgressBar) findViewById( R.id.progress );

        /** Construct optional app options object to be sent with configure */
        AdColonyAppOptions app_options = new AdColonyAppOptions()
                .setUserID( "unique_user_id" )
                .setAppVersion( "3.0.1.0" );

        /**
         * Configure AdColony in your launching Activity's onCreate() method so that cached ads can
         * be available as soon as possible.
         */
        AdColony.configure( this, app_options, APP_ID, ZONE_ID );

        /** Optional user metadata sent with the ad options in each request */
        AdColonyUserMetadata metadata = new AdColonyUserMetadata()
                .setUserAge( 26 )
                .setUserEducation( AdColonyUserMetadata.USER_EDUCATION_BACHELORS_DEGREE )
                .setUserGender( AdColonyUserMetadata.USER_MALE );

        /** Ad specific options to be sent with request */
        ad_options = new AdColonyAdOptions()
                .enableConfirmationDialog( true )
                .enableResultsDialog( true )
                .setUserMetadata( metadata );

        /** Create and set a reward listener */
        AdColony.setRewardListener( new AdColonyRewardListener()
        {
            @Override
            public void onReward( AdColonyReward reward )
            {
                /** Query reward object for info here */
                Log.d( TAG, "onReward" );
            }
        } );

        /**
         * Set up listener for interstitial ad callbacks. You only need to implement the callbacks
         * that you care about. The only required callback is onRequestFilled, as this is the only
         * way to get an ad object.
         */
        listener = new AdColonyInterstitialListener()
        {
            /** Ad passed back in request filled callback, ad can now be shown */
            @Override
            public void onRequestFilled( AdColonyInterstitial ad )
            {
                RewardedInterstitialActivity.this.ad = ad;
                show_button.setEnabled( true );
                progress.setVisibility( View.INVISIBLE );
                Log.d( TAG, "onRequestFilled" );
            }

            /** Ad request was not filled */
            @Override
            public void onRequestNotFilled( AdColonyZone zone )
            {
                progress.setVisibility( View.INVISIBLE );
                Log.d( TAG, "onRequestNotFilled");
            }

            /** Ad opened, reset UI to reflect state change */
            @Override
            public void onOpened( AdColonyInterstitial ad )
            {
                show_button.setEnabled( false );
                progress.setVisibility( View.VISIBLE );
                Log.d( TAG, "onOpened" );
            }

            /** Request a new ad if ad is expiring */
            @Override
            public void onExpiring( AdColonyInterstitial ad )
            {
                show_button.setEnabled( false );
                progress.setVisibility( View.VISIBLE );
                AdColony.requestInterstitial( ZONE_ID, this, ad_options );
                Log.d( TAG, "onExpiring" );
            }
        };

        /** Set up button to show an ad when clicked */
        show_button = (Button) findViewById( R.id.showbutton );
        show_button.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View view )
            {
                ad.show();
            }
        } );
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        /**
         * It's somewhat arbitrary when your ad request should be made. Here we are simply making
         * a request if there is no valid ad available onResume, but really this can be done at any
         * reasonable time before you plan on showing an ad.
         */
        if (ad == null || ad.isExpired())
        {
            /**
             * Optionally update location info in the ad options for each request:
             * LocationManager location_manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
             * Location location = location_manager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
             * ad_options.setUserMetadata( ad_options.getUserMetadata().setUserLocation( location ) );
             */
            progress.setVisibility( View.VISIBLE );
            AdColony.requestInterstitial( ZONE_ID, listener, ad_options );
        }

    }
}
