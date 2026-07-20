package com.example.vaultly

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView adapter used by both the Home screen (recent products)
 * and the Saved Products screen (full list with search).
 */
class ProductAdapter(
    private var products: MutableList<Product>,
    private val prefsManager: PrefsManager,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvItemProductName)
        val subtitle: TextView = itemView.findViewById(R.id.tvItemSubtitle)
        val status: TextView = itemView.findViewById(R.id.tvItemStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        val context = holder.itemView.context

        holder.name.text = product.name
        holder.subtitle.text = "${product.category} · ${product.storeName}"

        val status = prefsManager.getWarrantyStatus(product.warrantyExpiry)
        holder.status.text = status

        val bgColor: Int
        val textColor: Int
        when (status) {
            "Expired" -> {
                bgColor = ContextCompat.getColor(context, R.color.status_expired_bg)
                textColor = ContextCompat.getColor(context, R.color.status_expired_text)
            }
            "Expiring Soon" -> {
                bgColor = ContextCompat.getColor(context, R.color.status_expiring_bg)
                textColor = ContextCompat.getColor(context, R.color.status_expiring_text)
            }
            else -> {
                bgColor = ContextCompat.getColor(context, R.color.status_active_bg)
                textColor = ContextCompat.getColor(context, R.color.status_active_text)
            }
        }
        holder.status.backgroundTintList = ColorStateList.valueOf(bgColor)
        holder.status.setTextColor(textColor)

        holder.itemView.setOnClickListener { onItemClick(product) }
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newProducts: List<Product>) {
        products = newProducts.toMutableList()
        notifyDataSetChanged()
    }
}
