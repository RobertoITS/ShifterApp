package com.hvdevs.shifterapp.signinactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.hvdevs.shifterapp.signinactivity.adapter.SignInFragmentAdapter
import com.hvdevs.shifterapp.databinding.ActivitySignInBinding
import com.hvdevs.shifterapp.signinactivity.utilities.ChangePage

class SignInActivity : AppCompatActivity(), ChangePage {
    private lateinit var binding: ActivitySignInBinding
    //Instanciamos el adapter
    private lateinit var mAdapter: SignInFragmentAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignInBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        mAdapter = SignInFragmentAdapter(supportFragmentManager, lifecycle)
        with(binding.vp){
            this.isUserInputEnabled = false
            this.adapter = mAdapter
        }
    }

    override fun pageSelect(position: Int) { //Cambiamos las paginas, pero desde los fragment
        binding.vp.currentItem = binding.vp.currentItem + position
    }

    override fun onBackPressed() {
        when (binding.vp.currentItem){
            0 -> { super.onBackPressed() }
            1 -> { binding.vp.currentItem = 0 }
            2 -> { binding.vp.currentItem = 0 }
            3 -> { Toast.makeText(this, "Complete los campos y de a Siguiente", Toast.LENGTH_SHORT).show() }
            4 -> { binding.vp.currentItem = 0 }
        }
    }
}