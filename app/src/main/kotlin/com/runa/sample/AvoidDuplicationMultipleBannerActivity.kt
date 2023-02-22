package com.runa.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rakuten.android.ads.runa.AdLoader
import com.rakuten.android.ads.runa.AdLoaderStateListener
import com.rakuten.android.ads.runa.AdView
import com.rakuten.android.ads.runa.ErrorState
import com.runa.sample.R
import com.runa.sample.databinding.ActivityMultipleBannerRecyclerviewBinding
import com.runa.sample.databinding.ListRowAds2Binding
import com.runa.sample.databinding.ListRowContentBinding
import com.runa.sample.ContentType.AD
import com.runa.sample.ContentType.CONTENT
import com.squareup.picasso.Picasso

/**
 *
 */
internal class AvoidDuplicationRecycerViewActivity : AppCompatActivity() {

    private val ADSPOT_IDS = arrayOf("18261", "18262", "18269")
    private val INTERVAL = 10

    private val binding: ActivityMultipleBannerRecyclerviewBinding by lazy {
        ActivityMultipleBannerRecyclerviewBinding.inflate(LayoutInflater.from(this), null, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = "Multiple Banner Ads in RecyclerView"

        val adViewArray = ADSPOT_IDS.toAdViewArray(this)
        val adLoaderBuilder = AdLoader.Builder(this)
            .add(*adViewArray)


        binding.recyclerView.apply {
            val data = combineData(generateListData(), adViewArray.toAdsDataList(), INTERVAL)
            adapter = ADRecyclerViewAdapter(data, adLoaderBuilder)
        }
    }

    companion object {
        internal fun launch(context: Context) =
            context.startActivity(Intent(context, AvoidDuplicationRecycerViewActivity::class.java))
    }
}

internal class ADRecyclerViewAdapter(
    private val items: List<ListData>,
    adLoaderBuilder: AdLoader.Builder
) : RecyclerView.Adapter<ADRecyclerViewAdapter.ADRowHolder>() {

    private val adLoaderStateListener = object : AdLoaderStateListener() {

        override fun onLoadFailure(view: View?, errorState: ErrorState) {
            view?.visibility = View.GONE
        }

        override fun onAllLoadsFinished(adLoader: AdLoader, loadedAdViews: List<AdView>) {
            loadedAdViews?.let {
                it.forEach { adView ->
                    items.forEach { data ->
                        if (data.adsData != null && data.adsData.adSpotId == adView.adSpotId) {
                            data.adsData.adView = adView
                        }
                    }
                }
            }
        }

        override fun onClick(view: View?, errorState: ErrorState?) {
            // Detect a click
        }
    }

    init {
        adLoaderBuilder
            .with(adLoaderStateListener)
            .build()
            .execute()
    }

    override fun getItemCount(): Int = items.size
    override fun getItemViewType(position: Int): Int = items[position].type.value
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ADRowHolder =
        ADRowHolder.factory(parent, viewType)

    override fun onBindViewHolder(holder: ADRowHolder, position: Int) {
        holder.bind(items[position])
    }

    /**
     * Avoid Duplication ViewHolder
     */
    class ADRowHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val bind = DataBindingUtil.bind<ViewDataBinding>(view)!!

        fun bind(data: ListData) = when (data.type) {
            CONTENT -> (bind as ListRowContentBinding).apply {
                data.contentData?.let { content ->
                    contentName.text = content.name
                    Picasso.get()
                        .load(content.url)
                        .into(contentImg)
                }
            }
            AD -> (bind as ListRowAds2Binding).apply {
                data.adsData?.let { ad ->
                    adViewContainer.removeAllViews()
                    adViewContainer.addView(ad.adView, FrameLayout.LayoutParams(-2, -2))
                    adViewContainer.requestLayout()
                }
            }
        }

        companion object {
            fun factory(parent: ViewGroup, type: Int): ADRowHolder = when (type) {
                CONTENT.value -> R.layout.list_row_content
                AD.value -> R.layout.list_row_ads_2
                else -> R.layout.list_row_content
            }.run {
                LayoutInflater.from(parent.context).inflate(this, parent, false)
            }.run {
                ADRowHolder(this)
            }
        }
    }
}