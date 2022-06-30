package jp.techacademy.yuuwa.takano.matching

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_login.view.*


class AccountViewAdapter(
    private val list: List<Account>,
    private val listener: ListListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface ListListener {
        fun onClickItem(tappedView: View, itemModel: Account)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_account_list, parent, false)
        return AccountViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.nameTextView).text = list[position].name
        holder.itemView.findViewById<TextView>(R.id.addressTextView).text = list[position].address
        holder.itemView.findViewById<TextView>(R.id.genreTextView).text = list[position].genre
        holder.itemView.findViewById<TextView>(R.id.skillTextView).text = list[position].skill
        val bytes = list[position].imageBytes
        if (bytes.isNotEmpty()) {
            val image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                .copy(Bitmap.Config.ARGB_8888, true)
            val imageView = holder.itemView.findViewById<View>(R.id.iconImageView) as ImageView
            imageView.setImageBitmap(image)
        }
        holder.itemView.setOnClickListener {
            listener.onClickItem(it, list[position])
        }
    }

    override fun getItemCount(): Int = list.size
}