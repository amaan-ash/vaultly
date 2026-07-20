package com.example.vaultly

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SavedProductsActivity : AppCompatActivity() {

    private lateinit var prefsManager: PrefsManager
    private lateinit var rvSavedProducts: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var tvEmptySaved: TextView
    private lateinit var adapter: ProductAdapter
    private var allProducts: List<Product> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_products)

        prefsManager = PrefsManager(this)

        rvSavedProducts = findViewById(R.id.rvSavedProducts)
        etSearch = findViewById(R.id.etSearch)
        tvEmptySaved = findViewById(R.id.tvEmptySaved)

        rvSavedProducts.layoutManager = LinearLayoutManager(this)
        adapter = ProductAdapter(mutableListOf(), prefsManager) { product ->
            val intent = Intent(this, ProductDetailsActivity::class.java)
            intent.putExtra("product_id", product.id)
            startActivity(intent)
        }
        rvSavedProducts.adapter = adapter

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        setupBottomNav()
    }

    override fun onResume() {
        super.onResume()
        allProducts = prefsManager.getAllProducts()
        filterProducts(etSearch.text.toString())
    }

    private fun filterProducts(query: String) {
        val filtered = if (query.isBlank()) {
            allProducts
        } else {
            allProducts.filter {
                it.name.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true) ||
                    it.storeName.contains(query, ignoreCase = true)
            }
        }
        adapter.updateData(filtered)
        tvEmptySaved.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        rvSavedProducts.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun setupBottomNav() {
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        findViewById<LinearLayout>(R.id.navAbout).setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }
}
