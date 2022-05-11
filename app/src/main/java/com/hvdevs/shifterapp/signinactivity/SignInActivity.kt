package com.hvdevs.shifterapp.signinactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.hvdevs.shifterapp.R
import com.hvdevs.shifterapp.signinactivity.adapter.SignInFragmentAdapter
import com.hvdevs.shifterapp.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    //Instanciamos el adapter
    private lateinit var mAdapter: SignInFragmentAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignInBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // ## Animamos los botones y el tabLayout
        startAnimY(binding.imageButtonGoogle, -200f)
        startAnimY(binding.signInTabLayout, 100f)
        startAnimX(binding.imageButtonFacebook, -800f)
        startAnimX(binding.imageButtonInstagram, 800f)

        mAdapter = SignInFragmentAdapter(supportFragmentManager, lifecycle)
        binding.signInViewPager2.adapter = mAdapter
        with(binding.signInTabLayout){
            this.addTab(this.newTab().setText("INGRESA"))
            this.addTab(this.newTab().setText("REGISTRATE"))
            this.tabGravity = TabLayout.GRAVITY_FILL
            this.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    binding.signInViewPager2.currentItem = tab!!.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
            binding.signInViewPager2.registerOnPageChangeCallback(
                object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.signInTabLayout.selectTab(binding.signInTabLayout.getTabAt(position))
                }
            })
        }
    }

    override fun onStop() {
        finish()
        super.onStop()
    }

    //Animaciones de transicion
    private fun translationAnimY(view: View){
        view.animate().translationY(0f).alpha(1f).setDuration(1200).setStartDelay(600).start()
    }
    private fun translationAnimX(view: View){
        view.animate().translationX(0f).alpha(1f).setDuration(1200).setStartDelay(600).start()
    }
    private fun startAnimY(view: View, t: Float){
        view.translationY = t
        view.alpha = 0f
        translationAnimY(view)
    }
    private fun startAnimX(view: View, t: Float){
        view.translationX = t
        view.alpha = 0f
        translationAnimX(view)
    }
}