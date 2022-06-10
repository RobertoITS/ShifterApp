package com.hvdevs.shifterapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.hvdevs.shifterapp.databinding.ActivityTutorialBinding
import com.hvdevs.shifterapp.signinactivity.SignInActivity

class TutorialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTutorialBinding
    private lateinit var adapter: ViewPagerInitAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val images = listOf(R.mipmap.body_care, R.mipmap.appointment_book, R.mipmap.final_image)
        val title = listOf(
            "Encuentra nuestros mejores especialistas para el cuidado de tu cuerpo",
            "Usa una interfaz amigable para sacar turnos en la comodidad del hogar",
            "Todo lo hacemos para tu felicidad!"
        )

        binding.button.setOnClickListener {
            if (binding.button.text == "Siguiente"){
                binding.vp.currentItem = binding.vp.currentItem + 1
            }
            else{
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
                this.overridePendingTransition(R.anim.enter, R.anim.leave)
            }
        }

        adapter = ViewPagerInitAdapter(images, title)
        binding.vp.adapter = adapter

        binding.vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                changeColor()
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageScrollStateChanged(state: Int) {
                changeColor()
                super.onPageScrollStateChanged(state)
            }
        })
    }

    private fun changeColor() {
        when(binding.vp.currentItem){
            0->{
                binding.iv1.setBackgroundColor(applicationContext.resources.getColor(R.color.new_color))
                binding.iv2.setBackgroundColor(applicationContext.resources.getColor(R.color.white))
                binding.iv3.setBackgroundColor(applicationContext.resources.getColor(R.color.white))
                binding.button.text = "Siguiente"
            }
            1->{
                binding.iv1.setBackgroundColor(applicationContext.resources.getColor(R.color.white))
                binding.iv2.setBackgroundColor(applicationContext.resources.getColor(R.color.new_color))
                binding.iv3.setBackgroundColor(applicationContext.resources.getColor(R.color.white))
                binding.button.text = "Siguiente"
            }
            2->{
                binding.iv1.setBackgroundColor(applicationContext.resources.getColor(R.color.white))
                binding.iv2.setBackgroundColor(applicationContext.resources.getColor(R.color.white))
                binding.iv3.setBackgroundColor(applicationContext.resources.getColor(R.color.new_color))
                binding.button.text = "Continuar"
            }
        }
    }
}