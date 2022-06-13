package com.hvdevs.shifterapp.dashboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hvdevs.shifterapp.BlankFragment
import com.hvdevs.shifterapp.dashboard.dashboardfragment.DashboardFragment
import com.hvdevs.shifterapp.dashboard.myaccountfragment.MyAccountFragment
import com.hvdevs.shifterapp.dashboard.newappointment.NewAppointmentFragment

class ViewPager2Adapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0->{
                DashboardFragment()
            }
            1->{
                NewAppointmentFragment()
            }
            2->{
                BlankFragment()
            }
            3->{
                MyAccountFragment()
            }
            else -> {
                DashboardFragment()
            }
        }
    }
}