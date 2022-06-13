package com.hvdevs.shifterapp.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.hvdevs.shifterapp.R
import com.hvdevs.shifterapp.databinding.ActivityDashboardUserBinding

class DashboardUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.viewPager2.isUserInputEnabled = false

        val adapter = ViewPager2Adapter(this)
        binding.viewPager2.adapter = adapter
        binding.viewPager2.setPageTransformer(DepthPageTransformer())
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when(position){
                    0->{binding.bottomNav.setItemSelected(R.id.home_page)}
                    1->{binding.bottomNav.setItemSelected(R.id.myAppointment)}
                    2->{binding.bottomNav.setItemSelected(R.id.esp)}
                    3->{binding.bottomNav.setItemSelected(R.id.myAccount)}
                }
            }
        })

        binding.bottomNav.setOnItemSelectedListener { item->
            when (item){
                R.id.home_page ->{ binding.viewPager2.currentItem = 0 }
                R.id.myAppointment ->{ binding.viewPager2.currentItem = 1 }
                R.id.esp ->{ binding.viewPager2.currentItem = 2 }
                R.id.myAccount ->{ binding.viewPager2.currentItem = 3 }
            }
        }
    }
}