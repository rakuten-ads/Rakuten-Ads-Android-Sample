package com.runa.sample

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rakuten.android.ads.runa.extension.AdViewHelper
import com.runa.sample.databinding.ActivityNestedscrollviewBannerBinding
import com.runa.sample.databinding.ListRowContentBinding
import com.squareup.picasso.Picasso

/**
 * Overview:
 *  NestedScrollView interferes with ad scrolling when a horizontally scrollable ad is displayed in NestedScrollView.
 * The implementation sample of the function to solve the problem is described below.
 *
 * Requirement SDK version:
 *  - 'com.rakuten.android.ads:runa:1.8.1'
 *  - 'com.rakuten.android.ads:runa-extension:1.8.1'
 *
 *
 * AdViewHelper.ScrollableListener is for detecting scrolling in ads.
 * And this class does the processing to stop the vertical scrolling of the NestedScrollView while scrolling within the ad.
 *
 */
class NestedScrollViewSampleActivity : AppCompatActivity() {

    private val binding: ActivityNestedscrollviewBannerBinding by lazy {
        ActivityNestedscrollviewBannerBinding.inflate(LayoutInflater.from(this), null, false)
    }

    private var isScrollable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "NestedScrollView Sample"
        setContentView(binding.root)

        binding.adView.apply {
            adSpotId = "21881"
            AdViewHelper.RecyclerViewController(adView = this)
                .execute(listener = object : AdViewHelper.ScrollableListener {
                    override fun onScrollable(isScrollEnabled: Boolean) {
                        isScrollable = isScrollEnabled
                        binding.nestedScrollView.isScrollEnabled = isScrollEnabled
                    }
                })
        }.show()

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@NestedScrollViewSampleActivity)
            adapter = RecyclerViewAdapter(generateListData())
        }

    }

    companion object {
        internal fun launch(context: Context) =
            context.startActivity(Intent(context, NestedScrollViewSampleActivity::class.java))
    }


    private class RecyclerViewAdapter(
        private val items: List<ContentData>
    ) : RecyclerView.Adapter<RecyclerViewAdapter.RowHolder>() {

        override fun getItemCount(): Int = items.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder =
            RowHolder.generate(parent = parent)
        override fun onBindViewHolder(holder: RowHolder, position: Int) {
            holder.bind(items[position])
        }

        /**
         * ViewHolder
         */
        class RowHolder(
            view: View,
        ) : RecyclerView.ViewHolder(view) {

            private val bind = DataBindingUtil.bind<ViewDataBinding>(view)!!

            fun bind(data: ContentData) = (bind as ListRowContentBinding).apply {
                contentName.text = data.name
                Picasso.get()
                    .load(data.url)
                    .into(contentImg)
            }

            companion object {
                fun generate(
                    parent: ViewGroup
                ): RowHolder =
                    RowHolder(
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.list_row_content, parent, false)
                    )
            }
        }
    }
}

class CustomNestedScrollView : NestedScrollView {

    var isScrollEnabled: Boolean = true

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean =
        if (isScrollEnabled) super.onInterceptTouchEvent(ev) else false

    override fun onTouchEvent(ev: MotionEvent): Boolean =
        if (isScrollEnabled) super.onTouchEvent(ev) else false
}

