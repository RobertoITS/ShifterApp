package com.hvdevs.shifterapp.dashboard.dashboardfragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.snap
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.hvdevs.shifterapp.databinding.FragmentDashboardBinding
import com.hvdevs.shifterapp.dashboard.newappointment.Professional
import com.hvdevs.shifterapp.dashboard.newappointment.Shifts
import com.hvdevs.shifterapp.R

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

    //El viewPager2 (pasa las imagenes)
    private lateinit var viewPager2: ViewPager2
    private val sliderHandler = Handler()
    //Lista de imagenes y su adaptador
    private var imagesList: ArrayList<String> = arrayListOf()
    private lateinit var iAdapter: SliderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser!!.uid
        Log.d("ASD", uid)
        getName()

        getDataShift()

        getData()

        val simpleCallback = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.rvShift)

        binding.rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rv.setHasFixedSize(true)

        checkItems()

        return binding.root
    }

    private fun getName() { //Obtenemos el nombre del cliente
        val db = FirebaseDatabase.getInstance().getReference("users/$uid/data/name").get()
        db.addOnSuccessListener {
            val name = it.value.toString()
            Log.d("ASD", name)
            binding.name.text = name
        }
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
            if (professionalUid != "null" && image != "null" && profession != "null" && time != "null" && date != "null" && dKey != "null"
                && shiftKey != "null") {
                if (!shiftList.contains(data)) {
                    shiftList.add(data)
                    sAdapter = ShiftAdapter(shiftList, requireContext())
                    binding.rvShift.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    binding.rvShift.adapter = sAdapter
                }
            }
            Log.d("ASD", "$image, $profession, $time, $professional")
            checkItems()
//            sAdapter.setOnItemClickListener(object : ShiftAdapter.OnItemClickListener{
//                override fun onItemClick(position: Int) {
////                    deleteEntry(position)
//                }
//            })
        }
//        return professional
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
                    itemList.clear()
                    parentItem.clear()
                    imagesList.clear()
                    binding.imageSliderViewPager.adapter = null
                    binding.dl.openDrawer(GravityCompat.START)
                    binding.text3.text = list[position]
                    dataList(list[position])
                    getImages(list[position]) //Obtenemos las imagenes
                }
            })
        }
    }

    private fun dataList(item: String) {
//        val db2 = FirebaseDatabase.getInstance().getReference("profession/$item/professional").get()
//        db2.addOnSuccessListener { professionalId ->
//            for (prof in professionalId.children){
//                val id = prof.value.toString()
//                Log.d("FIREBASE", id)
//                val dbProf = FirebaseDatabase.getInstance().getReference("professional/$id")
//                dbProf.addValueEventListener(object : ValueEventListener{
//                    override fun onDataChange(it: DataSnapshot) {
//                        if (it.exists()) {
//                            val professional = it.child("name").value.toString()
//                            val pro = "Profesional"
//                            addItem(pro, professional)
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//
//                    }
//
//                })
//            }
//        }
        val db = FirebaseDatabase.getInstance().getReference("profession/$item") //.get()
        db.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val description = snapshot.child("description").value.toString()
                    val desc = "Descripción"
                    addItem(desc, description)
                    val duration = snapshot.child("duration").value.toString()
                    val dur = "Duración"
                    addItem(dur, duration)
                    val zones = snapshot.child("zones").value.toString()
                    val zon = "Zonas"
                    addItem(zon, zones)
                }
                eAdapter = ExpandableListAdapter()
                eAdapter.getContext(requireContext(), binding.elv)
                eAdapter.getList(itemList)
                eAdapter.notifyDataSetChanged()
                binding.elv.setAdapter(eAdapter)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
//        db.addOnSuccessListener {
//            for(t in it.children){
//                val parent = t.key.toString()
//                val child = t.value.toString()
//                addItem(parent, child)
//                eAdapter = ExpandableListAdapter()
//                eAdapter.getContext(requireContext(), binding.elv)
//                eAdapter.getList(itemList)
//                eAdapter.notifyDataSetChanged()
//                binding.elv.setAdapter(eAdapter)
//            }
//            Log.d("FIREBASE", parentItem.toString())
//        }

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

    /**De aca en adelante, la logica para el imageSlider:*/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Se tiene que colocar una vez que se crea la vista
        viewPager2 = binding.imageSliderViewPager

        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.offscreenPageLimit = 3
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(30))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - kotlin.math.abs(position)
            page.scaleY = 0.85f + r * 0.25f
        }

        viewPager2.setPageTransformer(compositePageTransformer)
        //Hasta aca, ver las animaciones

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 3000)
            }
        })
    }

    //Variable tipo Runnable para ejecutar el codigo siempre y cuando este activo
    private val sliderRunnable = Runnable {
        viewPager2.currentItem = viewPager2.currentItem + 1
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.postDelayed(sliderRunnable, 3000)
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 3000)
    }

    //Obtenemos las imagenes
    private fun getImages(path: String){
        val db = FirebaseDatabase.getInstance().getReference("profession/$path/image").get()
        db.addOnSuccessListener { snapshot->
            if (!snapshot.exists()){
                Log.e("FIREBASE", "Lista vacía")
            } else {
                for (dc in snapshot.children){
                    val image = dc.value.toString()
                    Log.d("FIREBASE", image)
                    image.let { imagesList.add(it) }
                }
                updateImages(imagesList)
            }
        }
    }
    //Actualizamos las listas de imagen
    @SuppressLint("NotifyDataSetChanged")
    private fun updateImages(imagesList: ArrayList<String>) {
        //El adaptador
        iAdapter = SliderAdapter(imagesList, viewPager2)
        viewPager2.adapter = iAdapter
        iAdapter.notifyDataSetChanged()
    }
}