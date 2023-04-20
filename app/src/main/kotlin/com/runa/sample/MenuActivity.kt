package com.runa.sample

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.rakuten.android.ads.runa.Runa
import com.runa.sample.databinding.ActivityMenuBinding

class SampleAppApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Runa.init(this)
    }
}

class MenuActivity : AppCompatActivity() {

    private val binding: ActivityMenuBinding by lazy {
        ActivityMenuBinding.inflate(LayoutInflater.from(this), null, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.singleBanner.setOnClickListener {
            SingleBannerActivity.launch(this)
        }

        binding.nestedScrollViewSample.setOnClickListener {
            NestedScrollViewSampleActivity.launch(this)
        }

        binding.multipleBanner.setOnClickListener {
            MultipleBannerActivity.launch(this)
        }

        binding.scrollableBanner.setOnClickListener {
            RecyclerViewSampleActivity.launch(this)
        }

        binding.scrollableBannerAvoidDuplication.setOnClickListener {
            AvoidDuplicationRecyclerViewActivity.launch(this)
        }

        binding.recyclerWithScrollableHorizontalBanner.setOnClickListener {
            RecyclerViewSample2Activity.launch(this)
        }
    }

}