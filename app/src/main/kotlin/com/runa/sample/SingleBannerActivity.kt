package com.runa.sample

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.rakuten.android.ads.runa.AdStateListener
import com.rakuten.android.ads.runa.ErrorState
import com.runa.sample.databinding.ActivitySingleBannerBinding

/**
 * The most basic implementation pattern.
 */
class SingleBannerActivity : AppCompatActivity() {

    private val ADSPOT_ID = "16261"

    private val binding: ActivitySingleBannerBinding by lazy {
        ActivitySingleBannerBinding.inflate(LayoutInflater.from(this), null, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = "Single Banner Ad"

        binding.bannerAd.apply {
            // [Required] Please specify the ad ID issued on the dashboard
            adSpotId = ADSPOT_ID

            adStateListener = object: AdStateListener() {
                /**
                 * Called when the ad loads successfully
                 */
                override fun onLoadSuccess(view: View?) {
                }

                /**
                 * Called when the ad fails to load
                 */
                override fun onLoadFailure(view: View?, errorState: ErrorState) {
                    visibility = View.GONE
                }

                /**
                 * Called when the ad load is clicked
                 */
                override fun onClick(view: View?, errorState: ErrorState?) {
                }
            }
        }.show() // Start loading ads.

    }

    companion object {
        internal fun launch(context: Context) =
            context.startActivity(Intent(context, SingleBannerActivity::class.java))
    }
}