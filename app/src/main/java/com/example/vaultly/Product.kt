package com.example.vaultly

import java.io.Serializable

/**
 * Simple data model representing one saved product entry.
 * Dates are stored as dd/MM/yyyy strings to keep parsing simple for this microproject.
 */
data class Product(
    var id: String = "",
    var name: String = "",
    var category: String = "",
    var purchaseDate: String = "",
    var warrantyExpiry: String = "",
    var storeName: String = "",
    var invoiceNumber: String = "",
    var notes: String = ""
) : Serializable
