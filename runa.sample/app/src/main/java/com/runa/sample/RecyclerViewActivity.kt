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
import androidx.recyclerview.widget.RecyclerView
import com.rakuten.android.ads.runa.AdLoaderStateListener
import com.rakuten.android.ads.runa.ErrorState
import com.runa.sample.R
import com.runa.sample.databinding.ActivityMultipleBannerRecyclerviewBinding
import com.runa.sample.databinding.ListRowAdsBinding
import com.runa.sample.databinding.ListRowContentBinding
import com.runa.sample.ContentType.AD
import com.runa.sample.ContentType.CONTENT
import com.squareup.picasso.Picasso

internal class RecyclerViewActivity : AppCompatActivity() {

    private val ADSPOT_IDS = arrayOf("", "", "")
    private val INTERVAL = 10

    private val binding: ActivityMultipleBannerRecyclerviewBinding by lazy {
        ActivityMultipleBannerRecyclerviewBinding.inflate(LayoutInflater.from(this), null, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = "Multiple Banner Ads in RecyclerView"

        binding.recyclerView.apply {
            val data = combineData(generateListData(), ADSPOT_IDS.toAdsDataList(), 8)
            adapter = RecyclerViewAdapter(data)
        }
    }

    companion object {
        internal fun launch(context: Context) =
            context.startActivity(Intent(context, RecyclerViewActivity::class.java))
    }
}

internal class RecyclerViewAdapter(
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
    class RowHolder(view: View) : RecyclerView.ViewHolder(view) {
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
                        show()
                        progress.visibility = View.VISIBLE
                    }
                }
            }
        }

        companion object {
            fun generate(parent: ViewGroup, type: Int): RowHolder = when (type) {
                CONTENT.value -> R.layout.list_row_content
                AD.value -> R.layout.list_row_ads
                else -> R.layout.list_row_content
            }.run {
                LayoutInflater.from(parent.context).inflate(this, parent, false)
            }.run {
                RowHolder(this)
            }
        }
    }
}