package com.runa.sample

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rakuten.android.ads.runa.AdLoaderStateListener
import com.rakuten.android.ads.runa.AdSize
import com.rakuten.android.ads.runa.ErrorState
import com.runa.sample.databinding.ActivityMultipleBannerRecyclerviewBinding
import com.runa.sample.databinding.ListRowAdsBinding
import com.runa.sample.databinding.ListRowContentBinding
import com.squareup.picasso.Picasso

internal class PointClubSampleActivity : AppCompatActivity() {

    private val ADSPOT_IDS = arrayOf("21881")
    private val INTERVAL = 10

    private val binding: ActivityMultipleBannerRecyclerviewBinding by lazy {
        ActivityMultipleBannerRecyclerviewBinding.inflate(LayoutInflater.from(this), null, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = "PointClub Sample Ads in RecyclerView"

        binding.recyclerView.apply {
            val data = combineData(generateListData(), ADSPOT_IDS.toAdsDataList(), INTERVAL)
            adapter = RecyclerViewAdapter(data)
        }
    }

    companion object {
        internal fun launch(context: Context) =
            context.startActivity(Intent(context, PointClubSampleActivity::class.java))
    }

    private class RecyclerViewAdapter(
        private val items: List<ListData>
    ) : RecyclerView.Adapter<RecyclerViewAdapter.RowHolder>() {

        override fun getItemCount(): Int = items.size
        override fun getItemViewType(position: Int): Int = items[position].type.value
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder =
            RowHolder.generate(parent, viewType)

        override fun onBindViewHolder(holder: RowHolder, position: Int) {
            holder.bind(items[position])
        }

        /**
         * ViewHolder
         */
        private class RowHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val bind = DataBindingUtil.bind<ViewDataBinding>(view)!!

            fun bind(data: ListData) = when (data.type) {
                ContentType.CONTENT -> (bind as ListRowContentBinding).apply {
                    data.contentData?.let { content ->
                        contentName.text = content.name
                        Picasso.get()
                            .load(content.url)
                            .into(contentImg)
                    }
                }
                ContentType.AD -> (bind as ListRowAdsBinding).apply {
                    label.visibility = View.GONE
                    title.text = "Checked Productes on Rakuten Ichiba"
                    data.adsData?.let { ad ->
                        adview.apply {
                            adSpotId = ad.adSpotId
                            adViewSize = AdSize.ASPECT_FIT
                            adStateListener = object : AdLoaderStateListener() {

                                override fun onLoadSuccess(view: View?) {
                                    progress.visibility = View.GONE
                                    title.visibility = View.VISIBLE
                                    adFrame.setBackgroundColor(Color.parseColor("#FFFFFF"))
                                }

                                override fun onLoadFailure(view: View?, errorState: ErrorState) {
                                    adArea.visibility = View.GONE
                                    title.visibility = View.GONE
//                                    errorInfo.text = errorState.toString()
                                }
                            }
                            show()
                            progress.visibility = View.VISIBLE
                        }
                    }
                }
            }

            companion object {
                fun generate(parent: ViewGroup, type: Int): RowHolder = when (type) {
                    ContentType.CONTENT.value -> R.layout.list_row_content
                    ContentType.AD.value -> R.layout.list_row_ads
                    else -> R.layout.list_row_content
                }.run {
                    LayoutInflater.from(parent.context).inflate(this, parent, false)
                }.run {
                    RowHolder(this)
                }
            }
        }
    }
}