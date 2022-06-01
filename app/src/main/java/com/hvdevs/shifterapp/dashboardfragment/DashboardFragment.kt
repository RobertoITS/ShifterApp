package com.hvdevs.shifterapp.dashboardfragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hvdevs.shifterapp.DashboardUserActivity
import com.hvdevs.shifterapp.ExpandableListAdapter
import com.hvdevs.shifterapp.RecyclerItemTouchHelper
import com.hvdevs.shifterapp.databinding.FragmentDashboardBinding
import com.hvdevs.shifterapp.newappointment.Professional
import com.hvdevs.shifterapp.newappointment.Shifts

class DashboardFragment : Fragment(), RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private var list: ArrayList<String> = arrayListOf()
    private var parentItem: LinkedHashMap<String, Parent> = LinkedHashMap()
    private var itemList: ArrayList<Parent> = arrayListOf()
    private lateinit var mAdapter: ProfessionAdapter
    private lateinit var eAdapter: ExpandableListAdapter
    private lateinit var uid: String
    private lateinit var auth: FirebaseAuth
    private lateinit var sAdapter: ShiftAdapter
    private var shiftList: ArrayList<Shifts> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser!!.uid
        getDataShift()

        getData()

        val simpleCallback = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.rvShift)

        binding.rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rv.setHasFixedSize(true)

        checkItems()

        return binding.root
    }

    private fun checkItems(){
        binding.noShift.visibility = if (shiftList.size == 0) View.VISIBLE
                    else View.GONE
    }

    private fun getDataShift(){
        val db = FirebaseDatabase.getInstance().getReference("users/$uid/shifts")
        db.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(day: DataSnapshot) {
                for (dayKey in day.children){
                    val dKey = dayKey.key.toString() //Obtenemos las key
                    Log.d("ASD", dKey)
                    val dbShift = FirebaseDatabase.getInstance().getReference("users/$uid/shifts/$dKey")
                    dbShift.addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(shifts: DataSnapshot) {
                            for (shift in shifts.children){ //Aca podemos obtener los datos por separado
                                val image = shift.child("image").value.toString()
                                val profession = shift.child("profession").value.toString()
                                val professionalUid = shift.child("professional").value.toString()
                                val time = shift.child("time").value.toString()
                                val date = shift.child("date").value.toString()
                                val shiftKey = shift.key.toString()
                                getProfessional(professionalUid, image, profession, time, date, dKey, shiftKey)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Accion cancelada!", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getProfessional(
        professionalUid: String,
        image: String,
        profession: String,
        time: String,
        date: String,
        dKey: String,
        shiftKey: String
    ) {
        var professional = ""
        val db = FirebaseDatabase.getInstance().getReference("professional/$professionalUid").get()
        db.addOnSuccessListener { prof ->
            val profData: Professional? = prof.getValue(Professional::class.java)
            if (profData != null) {
                professional = profData.name
            }
            val data = Shifts(date, dKey, image, profession, professional, professionalUid, shiftKey, time)
            shiftList.add(data)
            Log.d("ASD", "$image, $profession, $time, $professional")
            sAdapter = ShiftAdapter(shiftList)
            binding.rvShift.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.rvShift.adapter = sAdapter
            checkItems()
            sAdapter.setOnItemClickListener(object : ShiftAdapter.OnItemClickListener{
                override fun onItemClick(position: Int) {
//                    deleteEntry(position)
                }
            })
        }
    }

    private fun getData() {
        binding.rvShimmer.startShimmer()
        val db = FirebaseDatabase.getInstance().getReference("profession").get()
        db.addOnSuccessListener {
            for (t in it.children){
                val item = t.key.toString()
                list.add(item)
            }
            Log.d("FIREBASE", list.toString())
            mAdapter = ProfessionAdapter(list)
            binding.rv.adapter = mAdapter//Instanciamos el adapter aca, vemos que pasa
            binding.rvShimmer.stopShimmer()
            binding.rvShimmer.visibility = View.GONE
            mAdapter.setOnItemClickListener(object : ProfessionAdapter.OnItemClickListener{
                override fun onItemClick(position: Int) {
                    binding.dl.openDrawer(GravityCompat.START)
                    binding.text3.text = list[position]
                    dataList(list[position])
                }
            })
        }
    }

    private fun dataList(item: String) {
        val db = FirebaseDatabase.getInstance().getReference("profession/$item").get()
        db.addOnSuccessListener {
            for(t in it.children){
                val parent = t.key.toString()
                val child = t.value.toString()
                addItem(parent, child)
                eAdapter = ExpandableListAdapter()
                eAdapter.getContext(requireContext(), binding.elv)
                eAdapter.getList(itemList)
                eAdapter.notifyDataSetChanged()
                binding.elv.setAdapter(eAdapter)
            }
            Log.d("FIREBASE", parentItem.toString())
        }

    }

    private fun addItem(parent: String, child: String): Int {
        val parentPosition: Int

        var parentInfo: Parent? = parentItem[parent]

        if (parentInfo == null){
            parentInfo = Parent()
            parentInfo.name = parent
            parentItem[parent] = parentInfo
            itemList.add(parentInfo)
        }

        val childItemList: ArrayList<String> = parentInfo.itemList
        var listSize = childItemList.size
        listSize +=1

        childItemList.add(child)
        parentInfo.itemList = childItemList

        parentPosition = childItemList.indexOf(child)

        return parentPosition
    }

    data class Parent(var name: String? = "", var itemList: ArrayList<String> = arrayListOf())

    override fun onSwipe(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (viewHolder is ShiftAdapter.ShiftViewHolder){
            sAdapter.removeItem(viewHolder.adapterPosition)
//            deleteEntry(position)
            checkItems()
        }
    }
}