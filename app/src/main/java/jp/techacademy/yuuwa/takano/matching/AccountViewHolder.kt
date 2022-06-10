package jp.techacademy.yuuwa.takano.matching

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//    val iconView: TextView = itemView.findViewById(R.id.icon)
    val nameView: TextView = itemView.findViewById(R.id.nameTextView)
    val addressView: TextView = itemView.findViewById(R.id.addressTextView)
    val genreView: TextView = itemView.findViewById(R.id.genreTextView)
    val skillView: TextView = itemView.findViewById(R.id.skillTextView)
}