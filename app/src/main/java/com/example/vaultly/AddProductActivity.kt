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

class AddProductActivity : AppCompatActivity() {

    private lateinit var prefsManager: PrefsManager

    private lateinit var etProductName: EditText
    private lateinit var spCategory: Spinner
    private lateinit var etPurchaseDate: EditText
    private lateinit var etWarrantyExpiry: EditText
    private lateinit var etStoreName: EditText
    private lateinit var etInvoiceNumber: EditText
    private lateinit var etNotes: EditText

    private val categories = listOf("Electronics", "Home Appliance", "Kitchen Appliance", "Furniture", "Others")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        prefsManager = PrefsManager(this)

        etProductName = findViewById(R.id.etProductName)
        spCategory = findViewById(R.id.spCategory)
        etPurchaseDate = findViewById(R.id.etPurchaseDate)
        etWarrantyExpiry = findViewById(R.id.etWarrantyExpiry)
        etStoreName = findViewById(R.id.etStoreName)
        etInvoiceNumber = findViewById(R.id.etInvoiceNumber)
        etNotes = findViewById(R.id.etNotes)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategory.adapter = adapter

        etPurchaseDate.setOnClickListener { showDatePicker(etPurchaseDate) }
        etWarrantyExpiry.setOnClickListener { showDatePicker(etWarrantyExpiry) }

        findViewById<ImageButton>(R.id.btnBackAdd).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnSaveProduct).setOnClickListener { saveProduct() }
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

    private fun saveProduct() {
        val name = etProductName.text.toString().trim()
        val category = spCategory.selectedItem?.toString() ?: ""
        val purchaseDate = etPurchaseDate.text.toString().trim()
        val warrantyExpiry = etWarrantyExpiry.text.toString().trim()
        val storeName = etStoreName.text.toString().trim()
        val invoiceNumber = etInvoiceNumber.text.toString().trim()
        val notes = etNotes.text.toString().trim()

        if (name.isEmpty()) {
            etProductName.error = getString(R.string.error_field_required)
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

        val product = Product(
            name = name,
            category = category,
            purchaseDate = purchaseDate,
            warrantyExpiry = warrantyExpiry,
            storeName = storeName,
            invoiceNumber = invoiceNumber,
            notes = notes
        )

        prefsManager.addProduct(product)
        Toast.makeText(this, R.string.msg_product_saved, Toast.LENGTH_SHORT).show()
        finish()
    }
}
