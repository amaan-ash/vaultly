package com.example.vaultly

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var prefsManager: PrefsManager
    private var currentProduct: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        prefsManager = PrefsManager(this)

        findViewById<ImageButton>(R.id.btnBackDetails).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnEditProduct).setOnClickListener {
            currentProduct?.let {
                val intent = Intent(this, EditProductActivity::class.java)
                intent.putExtra("product_id", it.id)
                startActivity(intent)
            }
        }

        findViewById<Button>(R.id.btnDeleteProduct).setOnClickListener {
            confirmDelete()
        }
    }

    override fun onResume() {
        super.onResume()
        loadProduct()
    }

    private fun loadProduct() {
        val productId = intent.getStringExtra("product_id") ?: return
        val product = prefsManager.getProductById(productId)
        if (product == null) {
            finish()
            return
        }
        currentProduct = product
        bindProduct(product)
    }

    private fun bindProduct(product: Product) {
        findViewById<TextView>(R.id.tvDetailName).text = product.name
        findViewById<TextView>(R.id.tvDetailCategory).text = product.category

        val status = prefsManager.getWarrantyStatus(product.warrantyExpiry)
        val tvStatus = findViewById<TextView>(R.id.tvDetailStatus)
        tvStatus.text = status

        val bgColor: Int
        val textColor: Int
        when (status) {
            "Expired" -> {
                bgColor = ContextCompat.getColor(this, R.color.status_expired_bg)
                textColor = ContextCompat.getColor(this, R.color.status_expired_text)
            }
            "Expiring Soon" -> {
                bgColor = ContextCompat.getColor(this, R.color.status_expiring_bg)
                textColor = ContextCompat.getColor(this, R.color.status_expiring_text)
            }
            else -> {
                bgColor = ContextCompat.getColor(this, R.color.status_active_bg)
                textColor = ContextCompat.getColor(this, R.color.status_active_text)
            }
        }
        tvStatus.backgroundTintList = ColorStateList.valueOf(bgColor)
        tvStatus.setTextColor(textColor)

        setRow(R.id.rowPurchaseDate, R.string.label_purchase_date, product.purchaseDate)
        setRow(R.id.rowWarrantyExpiry, R.string.label_warranty_expiry, product.warrantyExpiry)
        setRow(R.id.rowStoreName, R.string.label_store_name, product.storeName)
        setRow(R.id.rowInvoiceNumber, R.string.label_invoice_number, product.invoiceNumber)
        setRow(
            R.id.rowNotes,
            R.string.label_notes,
            if (product.notes.isBlank()) "—" else product.notes
        )
    }

    private fun setRow(rowId: Int, labelRes: Int, value: String) {
        val row = findViewById<android.view.View>(rowId)
        row.findViewById<TextView>(R.id.tvRowLabel).text = getString(labelRes)
        row.findViewById<TextView>(R.id.tvRowValue).text = value
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_delete_title)
            .setMessage(R.string.dialog_delete_message)
            .setPositiveButton(R.string.delete) { _, _ ->
                currentProduct?.let {
                    prefsManager.deleteProduct(it.id)
                    Toast.makeText(this, R.string.msg_product_deleted, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
