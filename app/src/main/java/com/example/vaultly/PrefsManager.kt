package com.example.vaultly

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Handles all local storage for Vaultly using SharedPreferences only.
 * The full product list is stored as a single JSON array (via Gson) under one key,
 * exactly as described in the SLA report (SharedPreferences / Key-Value storage).
 */
class PrefsManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "vaultly_prefs"
        private const val KEY_PRODUCTS = "vaultly_products"
        const val DATE_PATTERN = "dd/MM/yyyy"
        private const val EXPIRING_SOON_DAYS = 30
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getAllProducts(): MutableList<Product> {
        val json = prefs.getString(KEY_PRODUCTS, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Product>>() {}.type
        return try {
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    private fun saveAllProducts(products: List<Product>) {
        val json = gson.toJson(products)
        prefs.edit().putString(KEY_PRODUCTS, json).apply()
    }

    fun addProduct(product: Product) {
        val products = getAllProducts()
        product.id = UUID.randomUUID().toString()
        products.add(0, product)
        saveAllProducts(products)
    }

    fun updateProduct(updated: Product) {
        val products = getAllProducts()
        val index = products.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            products[index] = updated
            saveAllProducts(products)
        }
    }

    fun deleteProduct(productId: String) {
        val products = getAllProducts()
        products.removeAll { it.id == productId }
        saveAllProducts(products)
    }

    fun getProductById(productId: String): Product? {
        return getAllProducts().find { it.id == productId }
    }

    /** Returns "Active", "Expiring Soon" or "Expired" based on the warranty expiry date. */
    fun getWarrantyStatus(warrantyExpiry: String): String {
        val expiryDate = parseDate(warrantyExpiry) ?: return "Active"
        val today = Date()
        val diffDays = ((expiryDate.time - today.time) / (1000 * 60 * 60 * 24)).toInt()
        return when {
            diffDays < 0 -> "Expired"
            diffDays <= EXPIRING_SOON_DAYS -> "Expiring Soon"
            else -> "Active"
        }
    }

    fun countExpiringSoon(products: List<Product>): Int {
        return products.count { getWarrantyStatus(it.warrantyExpiry) == "Expiring Soon" }
    }

    private fun parseDate(dateStr: String): Date? {
        return try {
            val sdf = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
            sdf.isLenient = false
            sdf.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }
}
