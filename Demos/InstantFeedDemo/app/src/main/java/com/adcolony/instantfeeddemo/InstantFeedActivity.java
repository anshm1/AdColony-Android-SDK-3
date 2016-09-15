package com.adcolony.instantfeeddemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.adcolony.sdk.*;

public class InstantFeedActivity extends Activity
{
    private AdColonyNativeAdView ad_view;
    private LinearLayout ad_layout;
    private AdColonyNativeAdViewListener ad_listener;
    private AdColonyAdOptions ad_options;

    private final String APP_ID = "app185a7e71e1714831a49ec7";
    private final String ZONE_ID = "vze4675ec2638048a789";
    private final String TAG = "InstantFeed";

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.layout );

        ad_layout = (LinearLayout) findViewById( R.id.instant_feed_layout );

        /** Construct optional app options object to be sent with configure */
        AdColonyAppOptions app_options = new AdColonyAppOptions()
                .setUserID( "unique_user_id" );

        AdColony.configure( this, app_options, APP_ID, ZONE_ID );

        /** Instantiate ad specific listener for native ad view callbacks */
        ad_listener = new AdColonyNativeAdViewListener()
        {
            @Override
            public void onRequestFilled( AdColonyNativeAdView view )
            {
                ad_view = view;
                ad_layout.addView( ad_view );
                Toast.makeText( InstantFeedActivity.this,
                                "Instant-Feed ad added to layout. Scroll through your feed to find it.",
                                Toast.LENGTH_LONG).show();

                /** Add/style engagement button to your layout as appropriate */
                if (ad_view.isEngagementEnabled())
                {
                    /**
                     *  If you already have a button you want to use, you should use the engagement button's
                     *  getOnClickListener method to use as your button's click listener
                     */

                    AdColonyNativeAdView.EngagementButton engagement_button = ad_view.getEngagementButton();
                    ad_layout.addView( engagement_button );
                }
            }

            @Override
            public void onMuted( AdColonyNativeAdView view )
            {
                Log.d( TAG, "onMuted" );
            }

            @Override
            public void onUnmuted( AdColonyNativeAdView view )
            {
                Log.d( TAG, "onUnmuted" );
            }

            @Override
            public void onRequestNotFilled( AdColonyZone zone )
            {
                Log.d( TAG, "onRequestNotFilled" );
            }

            @Override
            public void onNativeVideoFinished( AdColonyNativeAdView view )
            {
                Log.d( TAG, "onNativeVideoFinished" );
            }
        };

        /** Ad specific options to be sent with the request */
        AdColonyUserMetadata metadata = new AdColonyUserMetadata()
                .setUserAge( 26 )
                .setUserEducation( AdColonyUserMetadata.USER_EDUCATION_BACHELORS_DEGREE )
                .setUserGender( AdColonyUserMetadata.USER_MALE );
        ad_options = new AdColonyAdOptions().setUserMetadata( metadata );

        /**
         * It's somewhat arbitrary when your ad request should be made. Here we are simply making
         * a request onCreate, but really this can be done at any reasonable time before you plan on
         * displaying the ad view.
         *
         * Optionally update location info in the ad options for each request:
         * LocationManager location_manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
         * Location location = location_manager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
         * ad_options.setUserMetadata( ad_options.getUserMetadata().setUserLocation( location ) );
         */
        AdColony.requestNativeAdView( ZONE_ID, ad_listener, AdColonyAdSize.MEDIUM_RECTANGLE, ad_options );
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (ad_view != null)
        {
            ad_view.destroy();
            ad_layout.removeView( ad_view );
            ad_view = null;
        }
    }
}
