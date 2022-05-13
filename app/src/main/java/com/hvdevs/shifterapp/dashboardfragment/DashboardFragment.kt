package com.hvdevs.shifterapp.dashboardfragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.hvdevs.shifterapp.ExpandableListAdapter
import com.hvdevs.shifterapp.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private var list: ArrayList<String> = arrayListOf()
    private var parentItem: LinkedHashMap<String, Parent> = LinkedHashMap()
    private var itemList: ArrayList<Parent> = arrayListOf()
    private lateinit var mAdapter: ProfessionAdapter
    private lateinit var eAdapter: ExpandableListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        getData()
        binding.rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rv.setHasFixedSize(true)


        return binding.root
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
}