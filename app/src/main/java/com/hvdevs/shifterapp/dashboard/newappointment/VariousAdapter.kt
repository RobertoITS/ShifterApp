package com.hvdevs.shifterapp.dashboard.newappointment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.hvdevs.shifterapp.R
import com.hvdevs.shifterapp.dashboard.dashboardfragment.ProfessionAdapter

class VariousAdapter(private var list: ArrayList<String>): RecyclerView.Adapter<VariousAdapter.VariousViewHolder>() {

    /**Creamos la funcion del clickListener*/
    private lateinit var mListener: OnItemClickListener
    interface OnItemClickListener{
        fun onItemClick(position: Int, button: View)
    }
    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    class VariousViewHolder(itemView: View, listener: OnItemClickListener): RecyclerView.ViewHolder(itemView) {
        val name: MaterialButton = itemView.findViewById(R.id.name)
        /**Inicializamos el click y el check*/
        init {
            name.setOnClickListener{
                listener.onItemClick(adapterPosition, name)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariousViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.professional, parent, false)
        return VariousViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: VariousViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item
        //Le pasamos un tag al elemento.
        holder.name.tag = position
    }

    override fun getItemCount(): Int {
        return list.size
    }
}