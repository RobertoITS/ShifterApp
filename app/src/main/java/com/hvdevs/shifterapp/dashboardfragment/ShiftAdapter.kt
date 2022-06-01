package com.hvdevs.shifterapp.dashboardfragment

import android.annotation.SuppressLint
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hvdevs.shifterapp.databinding.AppointmentCardBinding
import com.hvdevs.shifterapp.newappointment.Shifts

class ShiftAdapter(var list: ArrayList<Shifts>): RecyclerView.Adapter<ShiftAdapter.ShiftViewHolder>() {

    lateinit var mListener: OnItemClickListener
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    class ShiftViewHolder(val binding: AppointmentCardBinding, listener: OnItemClickListener): RecyclerView.ViewHolder(binding.root){
        init {
            binding.delete.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShiftViewHolder {
        val binding = AppointmentCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShiftViewHolder(binding, mListener)
    }

    override fun onBindViewHolder(holder: ShiftViewHolder, position: Int) {
        val binding = holder.binding
        binding.time.text = list[position].date
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

    fun removeItem(position: Int){
        deleteEntry(position)
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteEntry(position: Int) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val node = list[position].dayKey
        val key = list[position].shiftKey
        val prof = list[position].professionalNode
        val profession = list[position].profession
        val mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("users/$uid/shifts/$node").child(key)
            .removeValue()
        mDatabase.child("professional/$prof/takenShift/$profession/$node").child(key)
            .removeValue()
    }
}