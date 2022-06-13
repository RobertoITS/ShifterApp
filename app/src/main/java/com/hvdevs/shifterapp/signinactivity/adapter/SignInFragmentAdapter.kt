package com.hvdevs.shifterapp.signinactivity.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hvdevs.shifterapp.signinactivity.fragments.TabForgotPasswordFragment
import com.hvdevs.shifterapp.signinactivity.fragments.initfragment.TabInitFragment
import com.hvdevs.shifterapp.signinactivity.fragments.loginfragment.TabLoginFragment
import com.hvdevs.shifterapp.signinactivity.fragments.registerfragment.TabProfileFragment
import com.hvdevs.shifterapp.signinactivity.fragments.registerfragment.TabRegisterFragment

class SignInFragmentAdapter(fragmentManager: FragmentManager?, lifecycle: Lifecycle) :
    FragmentStateAdapter(
        fragmentManager!!, lifecycle
    ) {
    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> { TabInitFragment() }
            1 -> { TabLoginFragment() }
            2 -> { TabRegisterFragment() }
            3 -> { TabProfileFragment() }
            else -> { TabForgotPasswordFragment() }
        }
//        creamos un switch para pasar entre tabs
//        return if (position == 1) {
//            TabRegisterFragment()
//        } else TabLoginFragment()
    }

    override fun getItemCount(): Int {
        return 5
    }
}