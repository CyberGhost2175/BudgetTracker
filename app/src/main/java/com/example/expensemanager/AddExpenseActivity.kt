package com.example.expensemanager

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanager.databinding.ActivityAddExpenseBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import model.ExpenseModel
import java.util.Calendar
import java.util.UUID

class AddExpenseActivity() : AppCompatActivity() {
    var binding: ActivityAddExpenseBinding? = null
    private var type: String? = null
    private var expenseModel: ExpenseModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        type = intent.getStringExtra("type")
        expenseModel = intent.getSerializableExtra("model") as ExpenseModel?
        if (type == null) {
            type = expenseModel!!.type
            binding!!.amount.setText(expenseModel!!.amount.toString())
            binding!!.category.setText(expenseModel!!.category)
            binding!!.note.setText(expenseModel!!.note)
        }
        if ((type == "Income")) {
            binding!!.incomeRadio.isChecked = true
        } else {
            binding!!.expenseRadio.isChecked = true
        }
        binding!!.incomeRadio.setOnClickListener(View.OnClickListener { type = "Income" })
        binding!!.expenseRadio.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                type = "Expense"
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        if (expenseModel == null) {
            menuInflater.inflate(R.menu.add_menu, menu)
        } else {
            menuInflater.inflate(R.menu.update_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.saveExpense) {
            if (type != null) {
                createExpense()
            } else {
                updateExpense()
            }
            return true
        }
        if (id == R.id.deleteExpense) {
            deleteExpense()
        }
        return false
    }

    private fun deleteExpense() {
        FirebaseFirestore
            .getInstance()
            .collection("expenses")
            .document(expenseModel!!.expenseId.toString())
            .delete()
        finish()
    }

    private fun createExpense() {
        val colorsList: MutableList<Int> = ArrayList()
        val expenseId = UUID.randomUUID().toString()
        val amount = binding!!.amount.text.toString()
        val note = binding!!.note.text.toString()
        val category = binding!!.category.text.toString()
        val incomeChecked = binding!!.incomeRadio.isChecked
        if (incomeChecked) {
            type = "Income"

        } else {
            type = "Expense"
            colorsList.add(resources.getColor(R.color.red))
        }
        if (amount.trim { it <= ' ' }.length == 0) {
            binding!!.amount.error = "Empty"
            return
        }
        val expenseModel = ExpenseModel(
            expenseId, note, category, type, amount.toLong(), Calendar.getInstance().timeInMillis,
            FirebaseAuth.getInstance().uid
        )
        FirebaseFirestore
            .getInstance()
            .collection("expenses")
            .document(expenseId)
            .set(expenseModel)
        finish()
    }

    private fun updateExpense() {
        val expenseId = expenseModel!!.expenseId
        val amount = binding!!.amount.text.toString()
        val note = binding!!.note.text.toString()
        val category = binding!!.category.text.toString()
        val incomeChecked = binding!!.incomeRadio.isChecked
        if (incomeChecked) {
            type = "Income"
        } else {
            type = "Expense"
        }
        if (amount.trim { it <= ' ' }.length == 0) {
            binding!!.amount.error = "Empty"
            return
        }
        val model = ExpenseModel(
            expenseId, note, category, type, amount.toLong(), expenseModel!!.time,
            FirebaseAuth.getInstance().uid
        )
        if (expenseId != null) {
            FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .document(expenseId)
                .set(model)
        }
        finish()
    }
}