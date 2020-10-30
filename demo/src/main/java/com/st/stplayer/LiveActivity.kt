package com.st.stplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.st.stplayer.adapter.LivePagerAdapter
import com.st.stplayer.data.TestData
import com.st.stplayer.util.ActivityUtil
import com.st.stplayer.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_live.*

class LiveActivity : AppCompatActivity() {
    private lateinit var mPagerAdapter: LivePagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityUtil.setFullScreen(this)
        ActivityUtil.hideTitleBar(this)
        ActivityUtil.setScreenHorizontal(this)
        StatusBarUtil.hideSysBar(this, window.decorView)
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_live)
        initView()
    }

    private fun initView() {
        mPagerAdapter = LivePagerAdapter(this, TestData.getLiveTestData())
        mViewPager.adapter = mPagerAdapter
        mViewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mPagerAdapter.setCurrentPosition(position)
                autoStartPlay(position)
            }

        })
    }

    private fun autoStartPlay(position: Int) {
        val holder: LivePagerAdapter.ViewHolder? = mPagerAdapter.getHolder(position)
        holder?.startPlay()
    }

    override fun onBackPressed() {
        ActivityUtil.setScreenVertical(this)
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        mPagerAdapter.onResume()
    }

    override fun onPause() {
        super.onPause()
        mPagerAdapter.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPagerAdapter.onDestroy()
    }
}