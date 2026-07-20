package com.example.vaultly

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {

    private lateinit var prefsManager: PrefsManager
    private lateinit var rvRecentProducts: RecyclerView
    private lateinit var tvTotalProducts: TextView
    private lateinit var tvExpiringSoon: TextView
    private lateinit var tvEmptyHome: TextView
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        prefsManager = PrefsManager(this)

        tvTotalProducts = findViewById(R.id.tvTotalProducts)
        tvExpiringSoon = findViewById(R.id.tvExpiringSoon)
        tvEmptyHome = findViewById(R.id.tvEmptyHome)
        rvRecentProducts = findViewById(R.id.rvRecentProducts)

        rvRecentProducts.layoutManager = LinearLayoutManager(this)
        adapter = ProductAdapter(mutableListOf(), prefsManager) { product ->
            openProductDetails(product.id)
        }
        rvRecentProducts.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAddProduct).setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        setupBottomNav()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        val allProducts = prefsManager.getAllProducts()
        tvTotalProducts.text = allProducts.size.toString()
        tvExpiringSoon.text = prefsManager.countExpiringSoon(allProducts).toString()

        val recentProducts = allProducts.take(5)
        adapter.updateData(recentProducts)

        tvEmptyHome.visibility = if (allProducts.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        rvRecentProducts.visibility = if (allProducts.isEmpty()) android.view.View.GONE else android.view.View.VISIBLE
    }

    private fun openProductDetails(productId: String) {
        val intent = Intent(this, ProductDetailsActivity::class.java)
        intent.putExtra("product_id", productId)
        startActivity(intent)
    }

    private fun setupBottomNav() {
        findViewById<android.widget.LinearLayout>(R.id.navSaved).setOnClickListener {
            startActivity(Intent(this, SavedProductsActivity::class.java))
        }
        findViewById<android.widget.LinearLayout>(R.id.navAbout).setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }
}
