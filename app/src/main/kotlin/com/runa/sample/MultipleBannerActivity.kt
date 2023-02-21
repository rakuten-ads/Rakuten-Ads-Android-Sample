package com.runa.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.rakuten.android.ads.runa.AdStateListener
import com.rakuten.android.ads.runa.AdView
import com.rakuten.android.ads.runa.ErrorState
import com.runa.sample.databinding.ActivityMultipleBannerBinding

class MultipleBannerActivity: AppCompatActivity() {

    private val ADSPOT_ID = "18263"

    private val binding: ActivityMultipleBannerBinding by lazy {
        ActivityMultipleBannerBinding.inflate(LayoutInflater.from(this), null, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = "Multiple Banner Ads"

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

        AdView(this@MultipleBannerActivity).apply {
            // [Required] Please specify the ad ID issued on the dashboard
            adSpotId = ""
            adStateListener = object: AdStateListener() {
                override fun onLoadSuccess(view: View?) {
                }

                override fun onLoadFailure(view: View?, errorState: ErrorState) {
                    visibility = View.GONE
                }

                override fun onClick(view: View?, errorState: ErrorState?) {
                }
            }
            binding.bannerAdContainer.addView(this, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        }.show()
    }

    companion object {
        internal fun launch(context: Context) =
            context.startActivity(Intent(context, MultipleBannerActivity::class.java))
    }
}