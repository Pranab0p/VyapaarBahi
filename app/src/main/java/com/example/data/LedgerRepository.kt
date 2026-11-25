package com.example.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.Executors
import org.json.JSONArray
import org.json.JSONObject

enum class TransactionType {
    CREDIT, // YOU WILL GET (Positive)
    DEBIT,  // YOU WILL GIVE (Negative)
    SETTLED // Neutral Settlement / self transfer
}

data class Transaction(
    val id: String,
    val customerId: String?,
    val customerName: String,
    val amount: Double,
    val type: TransactionType,
    val timestamp: String,
    val description: String,
    val tag: String = "General"
)

data class Customer(
    val id: String,
    val name: String,
    val phone: String,
    val avatarUrl: String?,
    val initials: String,
    val balance: Double
)

object LedgerRepository {
    val customers = mutableStateListOf<Customer>()
    val transactions = mutableStateListOf<Transaction>()

    var onDataChanged: (() -> Unit)? = null
    private val ioExecutor = Executors.newSingleThreadExecutor()
    private var activeStoreName: String = "VyapaarBahi (Apna Khata)"

    private fun fileNameForStore(storeName: String): String {
        if (storeName == "VyapaarBahi (Apna Khata)") return "ledger_data.json"
        val sanitized = storeName.lowercase(Locale.US).replace(Regex("[^a-z0-9]"), "_").take(32)
        return "ledger_store_$sanitized.json"
    }

    fun saveLocal(context: Context, storeName: String = activeStoreName) {
        val snapshotJson = exportToJsonString()
        val targetFile = fileNameForStore(storeName)
        val appContext = context.applicationContext
        ioExecutor.execute {
            try {
                appContext.openFileOutput(targetFile, Context.MODE_PRIVATE).use { fos ->
                    fos.write(snapshotJson.toByteArray(Charsets.UTF_8))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadLocal(context: Context, storeName: String = activeStoreName) {
        activeStoreName = storeName
        try {
            val file = context.getFileStreamPath(fileNameForStore(storeName))
            if (file.exists()) {
                val jsonStr = file.readText(Charsets.UTF_8)
                importFromJsonString(jsonStr, notify = false)
            } else {
                customers.clear()
                transactions.clear()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun switchStore(context: Context, newStoreName: String) {
        if (newStoreName == activeStoreName) return
        saveLocal(context, activeStoreName)
        loadLocal(context, newStoreName)
    }

    init {
        // Start VyapaarBahi as a clean, blank slate. No demo data.
    }

    fun addCustomer(name: String, phone: String, initialBalance: Double, type: TransactionType): Customer {
        val id = "c_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().take(6)
        val initials = name.trim().split(Regex("\\s+"))
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")
            .take(2)
            .ifEmpty { "VB" }
        
        val actualBalance = when (type) {
            TransactionType.CREDIT -> initialBalance
            TransactionType.DEBIT -> -initialBalance
            TransactionType.SETTLED -> 0.0
        }

        val newCustomer = Customer(id, name, phone, null, initials, actualBalance)
        customers.add(0, newCustomer)

        if (initialBalance != 0.0) {
            val description = if (type == TransactionType.CREDIT) "Opening Credit Balance" else "Opening Debit Balance"
            val transactionType = if (type == TransactionType.CREDIT) TransactionType.CREDIT else TransactionType.DEBIT
            addTransaction(id, name, initialBalance, transactionType, description, "Opening Balance")
        }
        onDataChanged?.invoke()
        return newCustomer
    }

    fun addTransaction(
        customerId: String?,
        customerName: String,
        amount: Double,
        type: TransactionType,
        description: String,
        tag: String = "General"
    ) {
        val id = "t_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().take(6)
        val timestamp = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        val newTx = Transaction(id, customerId, customerName, amount, type, timestamp, description, tag)
        transactions.add(0, newTx)

        // Update customer balance if registered
        if (customerId != null) {
            val index = customers.indexOfFirst { it.id == customerId }
            if (index != -1) {
                val customer = customers[index]
                val balanceShift = when (type) {
                    TransactionType.CREDIT -> -amount
                    TransactionType.DEBIT -> amount
                    TransactionType.SETTLED -> 0.0
                }
                customers[index] = customer.copy(balance = customer.balance + balanceShift)
            }
        }
        onDataChanged?.invoke()
    }

    fun settleCustomer(customerId: String, customerName: String, paymentMethod: String) {
        val index = customers.indexOfFirst { it.id == customerId }
        if (index != -1) {
            val customer = customers[index]
            val currentBalance = customer.balance
            if (currentBalance != 0.0) {
                val type = if (currentBalance > 0) TransactionType.CREDIT else TransactionType.DEBIT
                val description = "Settlement via $paymentMethod"
                addTransaction(customerId, customerName, Math.abs(currentBalance), type, description, "Cash")
                customers[index] = customer.copy(balance = 0.0)
            }
        }
        onDataChanged?.invoke()
    }

    // --- CRUD: Edit Transaction ---
    fun editTransaction(txId: String, newAmount: Double, newDescription: String, newTag: String) {
        val txIndex = transactions.indexOfFirst { it.id == txId }
        if (txIndex != -1) {
            val oldTx = transactions[txIndex]
            val amountDiff = newAmount - oldTx.amount

            // Only proceed if there is an actual change
            if (amountDiff != 0.0 || newDescription != oldTx.description || newTag != oldTx.tag) {
                val updatedTx = oldTx.copy(
                    amount = newAmount,
                    description = newDescription,
                    tag = newTag,
                    timestamp = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date()) + " (Edited)"
                )
                transactions[txIndex] = updatedTx

                // Re-adjust customer balance
                val customerId = oldTx.customerId
                if (customerId != null) {
                    val custIndex = customers.indexOfFirst { it.id == customerId }
                    if (custIndex != -1) {
                        val customer = customers[custIndex]
                        val balanceShift = when (oldTx.type) {
                            TransactionType.CREDIT -> -amountDiff
                            TransactionType.DEBIT -> amountDiff
                            TransactionType.SETTLED -> 0.0
                        }
                        customers[custIndex] = customer.copy(balance = customer.balance + balanceShift)
                    }
                }
            }
        }
        onDataChanged?.invoke()
    }

    // --- CRUD: Delete Transaction ---
    fun deleteTransaction(txId: String) {
        val txIndex = transactions.indexOfFirst { it.id == txId }
        if (txIndex != -1) {
            val oldTx = transactions[txIndex]
            transactions.removeAt(txIndex)

            // Revert customer balance change
            val customerId = oldTx.customerId
            if (customerId != null) {
                val custIndex = customers.indexOfFirst { it.id == customerId }
                if (custIndex != -1) {
                    val customer = customers[custIndex]
                    val balanceRevert = when (oldTx.type) {
                        TransactionType.CREDIT -> oldTx.amount
                        TransactionType.DEBIT -> -oldTx.amount
                        TransactionType.SETTLED -> 0.0
                    }
                    customers[custIndex] = customer.copy(balance = customer.balance + balanceRevert)
                }
            }
        }
        onDataChanged?.invoke()
    }

    // --- CRUD: Delete Customer Profile ---
    fun deleteCustomer(customerId: String) {
        // Remove customer profile
        customers.removeAll { it.id == customerId }
        // Clean up all related transaction records
        transactions.removeAll { it.customerId == customerId }
        onDataChanged?.invoke()
    }

    // --- LOCAL BACKUP & RESTORE ENGINES ---
    fun exportToJsonString(): String {
        val backupObj = JSONObject()
        backupObj.put("version", 1)
        backupObj.put("timestamp", SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date()))

        val customersArray = JSONArray()
        for (c in customers) {
            val cObj = JSONObject()
            cObj.put("id", c.id)
            cObj.put("name", c.name)
            cObj.put("phone", c.phone)
            cObj.put("avatarUrl", c.avatarUrl ?: JSONObject.NULL)
            cObj.put("initials", c.initials)
            cObj.put("balance", c.balance)
            customersArray.put(cObj)
        }
        backupObj.put("customers", customersArray)

        val transactionsArray = JSONArray()
        for (t in transactions) {
            val tObj = JSONObject()
            tObj.put("id", t.id)
            tObj.put("customerId", t.customerId ?: JSONObject.NULL)
            tObj.put("customerName", t.customerName)
            tObj.put("amount", t.amount)
            tObj.put("type", t.type.name)
            tObj.put("timestamp", t.timestamp)
            tObj.put("description", t.description)
            tObj.put("tag", t.tag)
            transactionsArray.put(tObj)
        }
        backupObj.put("transactions", transactionsArray)

        return backupObj.toString(4) // Pretty print with indent = 4
    }

    fun importFromJsonString(jsonStr: String, notify: Boolean = true): Boolean {
        return try {
            val backupObj = JSONObject(jsonStr)
            val customersArray = backupObj.getJSONArray("customers")
            val transactionsArray = backupObj.getJSONArray("transactions")

            // Clear current state in memory safely
            customers.clear()
            transactions.clear()

            for (i in 0 until customersArray.length()) {
                val cObj = customersArray.getJSONObject(i)
                val id = cObj.getString("id")
                val name = cObj.getString("name")
                val phone = cObj.getString("phone")
                val avatarUrl = if (cObj.isNull("avatarUrl")) null else cObj.getString("avatarUrl")
                val initials = cObj.getString("initials")
                val balance = cObj.getDouble("balance")
                customers.add(Customer(id, name, phone, avatarUrl, initials, balance))
            }

            for (i in 0 until transactionsArray.length()) {
                val tObj = transactionsArray.getJSONObject(i)
                val id = tObj.getString("id")
                val customerId = if (tObj.isNull("customerId")) null else tObj.getString("customerId")
                val customerName = tObj.getString("customerName")
                val amount = tObj.getDouble("amount")
                val type = TransactionType.valueOf(tObj.getString("type"))
                val timestamp = tObj.getString("timestamp")
                val description = tObj.getString("description")
                val tag = if (tObj.has("tag")) tObj.getString("tag") else "General"
                transactions.add(Transaction(id, customerId, customerName, amount, type, timestamp, description, tag))
            }
            if (notify) {
                onDataChanged?.invoke()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
