package com.cam.swipedismiss

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.cam.swipedismiss.adapter.SwipeDismissBaseAdapter
import com.cam.swipedismiss.widget.SwipeDismissListView
import com.cam.swipedismiss.widget.SwipeItem
import java.util.ArrayList

class MainActivity : AppCompatActivity(), SwipeDismissListView.OnDismissCallback {

    private var mDataList: ArrayList<String>? = null
    private var mListView: SwipeDismissListView? = null
    private var mAdapter: SwipeDismissAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mListView = findViewById(R.id.slv_list)

        val list = ArrayList<String>()
        for (i in 0..49) {
            list.add("item $i")
        }
        mDataList = list
        mListView?.let {
            mAdapter = SwipeDismissAdapter(it)
            it.adapter = mAdapter
            it.setOnDismissCallback(this)
        }
    }

    override fun onDismiss(dismissPosition: Int) {
        mDataList?.removeAt(dismissPosition)
        mAdapter?.notifyDataSetChanged()
    }

    private inner class SwipeDismissAdapter(mLvSwipeDismiss: SwipeDismissListView) :
        SwipeDismissBaseAdapter(mLvSwipeDismiss) {

        override fun getCount(): Int {
            return mDataList?.size?:0
        }

        override fun getItem(position: Int): Any {
            return mDataList?.let { it[position]}?:""
        }

        override fun getMemberHolder(): BaseMemberHolder {
            val view = layoutInflater.inflate(R.layout.item_swipe_dismiss, null) as SwipeItem
            return MemberHolder(view)
        }

        inner class MemberHolder(view: SwipeItem) : BaseMemberHolder(view) {

            private val mTvName: TextView = view.findViewById(R.id.tv_name)

            override fun setViewData(position: Int) {
                super.setViewData(position)
                mTvName.text = getItem(position).toString()
            }
        }
    }
}

