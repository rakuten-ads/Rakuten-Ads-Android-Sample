package com.runa.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rakuten.android.ads.runa.AdLoaderStateListener
import com.rakuten.android.ads.runa.ErrorState
import com.rakuten.android.ads.runa.extension.AdViewHelper
import com.runa.sample.databinding.ActivityMultipleBannerRecyclerviewBinding
import com.runa.sample.databinding.ListRowAdsBinding
import com.runa.sample.databinding.ListRowContentBinding
import com.runa.sample.ContentType.AD
import com.runa.sample.ContentType.CONTENT
import com.squareup.picasso.Picasso


/**
 * Overview:
 *  RecyclerView interferes with ad scrolling when a horizontally scrollable ad is displayed in RecyclerView.
 * The implementation sample of the function to solve the problem is described below.
 *
 * Requirement SDK version:
 *  - 'com.rakuten.android.ads:runa:1.8.1'
 *  - 'com.rakuten.android.ads:runa-extension:1.8.1'
 *
 *
 * AdViewHelper.ScrollableListener is for detecting scrolling in ads.
 * And this class does the processing to stop the vertical scrolling of the RecyclerView while scrolling within the ad.
 *
 */
internal class RecyclerViewSample2Activity : AppCompatActivity() {

    private val ADSPOT_IDS = arrayOf("21881")
    private val INTERVAL = 10

    private val binding: ActivityMultipleBannerRecyclerviewBinding by lazy {
        ActivityMultipleBannerRecyclerviewBinding.inflate(LayoutInflater.from(this), null, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = "RecyclerView with a scrollable horizontal banner"

        val data = combineData(generateListData(), ADSPOT_IDS.toAdsDataList(), INTERVAL)
        val customManager = CustomLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val customAdapter = RecyclerViewAdapterSample2(
            items = data,

            /**
             * This ScrollableListener for detecting scrolling of ads that can be scrolled horizontally and instructing LayoutManager to scroll.
             */
            scrollableListener = object : AdViewHelper.ScrollableListener {
                override fun onScrollable(isScrollEnabled: Boolean) {
                    customManager.isScrollEnabled = isScrollEnabled
                }
            }
        )
        binding.recyclerView.apply {
            layoutManager = customManager
            adapter = customAdapter
        }
    }

    companion object {
        internal fun launch(context: Context) =
            context.startActivity(Intent(context, RecyclerViewSample2Activity::class.java))
    }
}

/**
 * This sample LayoutManager for controlling RecyclerView scrolling.
 */
internal class CustomLinearLayoutManager(
    context: Context,
    @RecyclerView.Orientation orientation: Int,
    reverseLayout: Boolean
) : LinearLayoutManager(context, orientation, reverseLayout) {

    var isScrollEnabled = true
    override fun canScrollHorizontally(): Boolean =
        isScrollEnabled && super.canScrollHorizontally()

    override fun canScrollVertically(): Boolean =
        isScrollEnabled && super.canScrollVertically()
}

internal class RecyclerViewAdapterSample2(
    private val items: List<ListData>,
    private val scrollableListener: AdViewHelper.ScrollableListener
) : RecyclerView.Adapter<RecyclerViewAdapterSample2.RowHolder>() {

    override fun getItemCount(): Int = items.size
    override fun getItemViewType(position: Int): Int = items[position].type.value
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder =
        RowHolder.generate(parent = parent, type = viewType, listener = scrollableListener)

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        holder.bind(items[position])
    }

    /**
     * ViewHolder
     */
    class RowHolder(
        view: View,
        private val listener: AdViewHelper.ScrollableListener? = null
    ) : RecyclerView.ViewHolder(view) {

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
            AD -> (bind as ListRowAdsBinding).apply {
                data.adsData?.let { ad ->
                    adview.apply {
                        adSpotId = ad.adSpotId
                        adStateListener = object : AdLoaderStateListener() {

                            override fun onLoadSuccess(view: View?) {
                                label.visibility = View.VISIBLE
                                progress.visibility = View.GONE
                            }

                            override fun onLoadFailure(view: View?, errorState: ErrorState) {
                                label.visibility = View.GONE
                                adview.visibility = View.GONE
                                progress.visibility = View.GONE
                                errorInfo.visibility = View.VISIBLE
                                errorInfo.text = errorState.toString()
                            }
                        }

                        /**
                         * Set a Listener to detect scrolling within an ad.
                         */
                        listener?.let {
                            AdViewHelper.RecyclerViewController(adView = this).execute(listener = it)
                        }
                        show()
                        progress.visibility = View.VISIBLE
                    }
                }
            }
        }

        companion object {
            fun generate(
                parent: ViewGroup,
                type: Int,
                listener: AdViewHelper.ScrollableListener? = null
            ): RowHolder = when (type) {
                CONTENT.value -> R.layout.list_row_content
                AD.value -> R.layout.list_row_ads
                else -> R.layout.list_row_content
            }.run {
                LayoutInflater.from(parent.context).inflate(this, parent, false)
            }.run {
                RowHolder(this, listener)
            }
        }
    }
}