/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.ui.widget

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView

import com.wingjay.jianshi.R
import com.wingjay.jianshi.ui.theme.TraditionalColorNamer

/**
 * Created by wingjay on 10/11/15.
 */
class BgColorPickDialogFragment : DialogFragment() {

  private var colorListView: ListView? = null

  lateinit var onBackgroundColorChangedListener: (newColorRes: Int) -> Unit

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val root = inflater.inflate(R.layout.fragment_bg_color_pick, container, false)
    dialog.setTitle(resources.getString(R.string.please_choose_bg_color))
    colorListView = root.findViewById<View>(R.id.bg_color_list_view) as ListView
    colorListView!!.adapter = BgColorPickAdapter(context!!)

    return root
  }

  private inner class BgColorPickAdapter(private val context: Context) : BaseAdapter() {

    private val colorNamerList: List<TraditionalColorNamer> = TraditionalColorNamer.allColorNamer

    private inner class ViewHolder {
      internal var colorHint: TextPointView? = null
      internal var colorName: TextView? = null
    }

    override fun getCount(): Int {
      return colorNamerList.size
    }

    override fun getItem(position: Int): Any {
      return colorNamerList[position]
    }

    override fun getItemId(position: Int): Long {
      return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
      var container = convertView
      val viewHolder: ViewHolder
      if (container == null) {
        container = LayoutInflater.from(context)
            .inflate(R.layout.view_bg_color_pick, parent, false)
        viewHolder = ViewHolder()
        viewHolder.colorHint = container!!.findViewById<View>(R.id.bg_color_hint) as TextPointView
        viewHolder.colorName = container.findViewById<View>(R.id.bg_color_name) as TextView
        container.tag = viewHolder
      } else {
        viewHolder = container.tag as ViewHolder
      }

      val colorNamer = colorNamerList[position]
      viewHolder.colorName!!.text = colorNamer.colorName
      viewHolder.colorHint!!.setCircleBackgroundColor(colorNamer.colorRes)
      container.setOnClickListener {
        dismiss()
        onBackgroundColorChangedListener(colorNamer.colorRes)
      }
      return container
    }

  }

}
