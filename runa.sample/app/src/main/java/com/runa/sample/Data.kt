package com.runa.sample

import android.content.Context
import com.rakuten.android.ads.runa.AdView
import com.runa.sample.R

internal fun Context.generateListData(): List<ContentData> =
    resources.getStringArray(R.array.octocat_array).map {
        it.split(",").run {
            ContentData(this[0], this[1])
        }
    }

internal fun Array<String>.toAdsDataList(): List<AdsData> = map { AdsData(it) }

internal fun combineData(
    contentDataList: List<ContentData>,
    adsDataList: List<AdsData>,
    interval: Int
): List<ListData> = ArrayList<ListData>().apply {
    var j = 0
    contentDataList.forEachIndexed { i, contentData ->
        if (((i + 1) % interval) != 0) add(ListData(ContentType.CONTENT, contentData))
        else if (j <= adsDataList.size - 1) {
            add(ListData(ContentType.AD, null, adsDataList[j])); j++
        } else add(ListData(ContentType.CONTENT, contentData))
    }
}

internal fun Array<String>.toAdViewArray(context: Context): Array<AdView> = map { id ->
    AdView(context).apply {
        adSpotId = id
    }
}.toTypedArray()

internal fun Array<AdView>.toAdsDataList(): List<AdsData> = map {
    AdsData(it.adSpotId, it)
}

internal data class ListData(
    val type: ContentType,
    val contentData: ContentData? = null,
    val adsData: AdsData? = null
)

internal data class ContentData(
    val name: String,
    val url: String
)

internal data class AdsData(
    val adSpotId: String,
    var adView: AdView? = null
)

internal enum class ContentType(val value: Int) {
    CONTENT(0), AD(1)
}