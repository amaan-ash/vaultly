package com.example.vaultly

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditProductActivity : AppCompatActivity() {

    private lateinit var prefsManager: PrefsManager
    private var currentProduct: Product? = null

    private lateinit var etName: EditText
    private lateinit var spCategory: Spinner
    private lateinit var etPurchaseDate: EditText
    private lateinit var etWarrantyExpiry: EditText
    private lateinit var etStoreName: EditText
    private lateinit var etInvoiceNumber: EditText
    private lateinit var etNotes: EditText

    private val categories = listOf("Electronics", "Home Appliance", "Kitchen Appliance", "Furniture", "Others")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        prefsManager = PrefsManager(this)

        etName = findViewById(R.id.etEditProductName)
        spCategory = findViewById(R.id.spEditCategory)
        etPurchaseDate = findViewById(R.id.etEditPurchaseDate)
        etWarrantyExpiry = findViewById(R.id.etEditWarrantyExpiry)
        etStoreName = findViewById(R.id.etEditStoreName)
        etInvoiceNumber = findViewById(R.id.etEditInvoiceNumber)
        etNotes = findViewById(R.id.etEditNotes)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategory.adapter = adapter

        etPurchaseDate.setOnClickListener { showDatePicker(etPurchaseDate) }
        etWarrantyExpiry.setOnClickListener { showDatePicker(etWarrantyExpiry) }

        findViewById<ImageButton>(R.id.btnBackEdit).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnUpdateProduct).setOnClickListener { updateProduct() }

        loadProduct()
    }

    private fun loadProduct() {
        val productId = intent.getStringExtra("product_id") ?: run { finish(); return }
        val product = prefsManager.getProductById(productId) ?: run { finish(); return }
        currentProduct = product

        etName.setText(product.name)
        etPurchaseDate.setText(product.purchaseDate)
        etWarrantyExpiry.setText(product.warrantyExpiry)
        etStoreName.setText(product.storeName)
        etInvoiceNumber.setText(product.invoiceNumber)
        etNotes.setText(product.notes)

        val categoryIndex = categories.indexOf(product.category)
        if (categoryIndex >= 0) {
            spCategory.setSelection(categoryIndex)
        }
    }

    private fun showDatePicker(target: EditText) {
        val calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selected = Calendar.getInstance()
                selected.set(year, month, dayOfMonth)
                val sdf = SimpleDateFormat(PrefsManager.DATE_PATTERN, Locale.getDefault())
                target.setText(sdf.format(selected.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    private fun updateProduct() {
        val product = currentProduct ?: return

        val name = etName.text.toString().trim()
        val purchaseDate = etPurchaseDate.text.toString().trim()
        val warrantyExpiry = etWarrantyExpiry.text.toString().trim()

        if (name.isEmpty()) {
            etName.error = getString(R.string.error_field_required)
            return
        }
        if (purchaseDate.isEmpty()) {
            etPurchaseDate.error = getString(R.string.error_field_required)
            return
        }
        if (warrantyExpiry.isEmpty()) {
            etWarrantyExpiry.error = getString(R.string.error_field_required)
            return
        }

        product.name = name
        product.category = spCategory.selectedItem?.toString() ?: product.category
        product.purchaseDate = purchaseDate
        product.warrantyExpiry = warrantyExpiry
        product.storeName = etStoreName.text.toString().trim()
        product.invoiceNumber = etInvoiceNumber.text.toString().trim()
        product.notes = etNotes.text.toString().trim()

        prefsManager.updateProduct(product)
        Toast.makeText(this, R.string.msg_product_updated, Toast.LENGTH_SHORT).show()
        finish()
    }
}
