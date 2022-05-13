package com.hvdevs.shifterapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import com.hvdevs.shifterapp.dashboardfragment.DashboardFragment

class ExpandableListAdapter: BaseExpandableListAdapter() {

    private lateinit var childList: ArrayList<DashboardFragment.Parent>
    private lateinit var context: Context
    private lateinit var expandableListView: ExpandableListView

    fun getList(childList: ArrayList<DashboardFragment.Parent>){
        this.childList = childList
    }
    //Obtenemos los contextos
    fun getContext(context: Context, expandableListView: ExpandableListView){
        this.context = context
        this.expandableListView = expandableListView
    }

    override fun getGroupCount(): Int {
        return childList.size
    }

    override fun getChildrenCount(p0: Int): Int {
        val itemList: ArrayList<String> = childList[p0].itemList
        return itemList.size
    }

    override fun getGroup(p0: Int): Any {
        return childList[p0]
    }

    override fun getChild(p0: Int, p1: Int): Any {
        val itemList: ArrayList<String> = childList[p0].itemList
        return itemList[p1]
    }

    override fun getGroupId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getChildId(p0: Int, p1: Int): Long {
        return p1.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(p0: Int, p1: Boolean, p2: View?, p3: ViewGroup?): View {
        val parentInfo: DashboardFragment.Parent = getGroup(p0) as DashboardFragment.Parent
        var currentView = p2
        if (currentView == null){
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            currentView = inflater.inflate(R.layout.layout_group, null)
        }
        val tv: TextView = currentView!!.findViewById(R.id.tv_title)
        tv.text = parentInfo.name
        return currentView
    }

    override fun getChildView(p0: Int, p1: Int, p2: Boolean, p3: View?, p4: ViewGroup?): View {
        val childInfo: String = getChild(p0, p1) as String
        var currentView = p3
        if (currentView == null){
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            currentView = inflater.inflate(R.layout.layout_child, null)
        }
        val name: TextView = currentView!!.findViewById(R.id.tv_title_child)
        name.text = childInfo
        return currentView
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
        TODO("Not yet implemented")
    }
}