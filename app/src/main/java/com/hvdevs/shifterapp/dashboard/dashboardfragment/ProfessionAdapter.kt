package com.hvdevs.shifterapp.dashboard.dashboardfragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hvdevs.shifterapp.R

class ProfessionAdapter(private val list: ArrayList<String>): RecyclerView.Adapter<ProfessionAdapter.ProfessionalViewHolder>(){
    /**Creamos la funcion del clickListener*/
    private lateinit var mListener: OnItemClickListener
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    class ProfessionalViewHolder(itemView: View, listener: OnItemClickListener): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.speciality_name)

        /**Inicializamos el click y el check*/
        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfessionalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profession_card, parent, false)
        return ProfessionalViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: ProfessionalViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item
    }

    override fun getItemCount(): Int {
        return list.size
    }
}