package com.hvdevs.shifterapp.dashboardfragment

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hvdevs.shifterapp.databinding.AppointmentCardBinding
import com.hvdevs.shifterapp.newappointment.Shifts

class ShiftAdapter(private var list: ArrayList<Shifts>): RecyclerView.Adapter<ShiftAdapter.ShiftViewHolder>() {
    class ShiftViewHolder(val binding: AppointmentCardBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShiftViewHolder {
        val binding = AppointmentCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShiftViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShiftViewHolder, position: Int) {
        val binding = holder.binding
        binding.time.text = list[position].time
        binding.profession.text = list[position].profession
        binding.expCard.setOnClickListener {
            if(binding.gone.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(binding.gone, AutoTransition())
                binding.gone.visibility = View.VISIBLE
            }
            else {
                TransitionManager.beginDelayedTransition(binding.gone, AutoTransition())
                binding.gone.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}