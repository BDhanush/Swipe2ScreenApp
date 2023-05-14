package com.example.swipe2screen.adapter
import android.content.Context
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.swipe2screen.R
import com.example.swipe2screen.model.Product
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.*

class ListingAdapter(val context: Context, var dataset:MutableList<Product>): RecyclerView.Adapter<ListingAdapter.ItemViewHolder>()
{
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img:ImageView=view.findViewById(R.id.img)
        var title:TextView=view.findViewById(R.id.title)
        val type:TextView=view.findViewById(R.id.type)
        val price:TextView?=view.findViewById(R.id.price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(context)
            .inflate(R.layout.listing_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    fun filterList(filterlist: MutableList<Product>) {
        // below line is to add our filtered
        // list in our course array list.
        dataset = filterlist
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        if(item.image!="")
            Picasso.get().load(item.image).into(holder.img);
        holder.title.text= item.product_name
        holder.type.text= "Type: ${item.product_type}"
        val priceWithTax =item.price*(1+item.tax)
        holder.price!!.text= NumberFormat.getCurrencyInstance(Locale("en","in")).format(priceWithTax)
    }
    override fun getItemCount() = dataset.size
}
