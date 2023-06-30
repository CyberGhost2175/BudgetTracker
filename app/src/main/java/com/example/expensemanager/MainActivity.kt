package com.example.expensemanager

import adapters.ExpensesAdapter
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensemanager.databinding.ActivityMainBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import model.ExpenseModel
import model.OnItemsCLick

class MainActivity : AppCompatActivity(), OnItemsCLick {
    var binding: ActivityMainBinding? = null
    private var expensesAdapter: ExpensesAdapter? = null

    //    Intent intent;
    private var income: Long = 0
    private var expense: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        expensesAdapter = ExpensesAdapter(this, this)
        binding!!.recycler.adapter = expensesAdapter
        binding!!.recycler.layoutManager = LinearLayoutManager(this)
        binding!!.addIncome.setOnClickListener {
            val intent = Intent(this@MainActivity, AddExpenseActivity::class.java)
            intent.putExtra("type", "Income")
            startActivity(intent)
        }
        binding!!.addExpense.setOnClickListener {
            val intent = Intent(this@MainActivity, AddExpenseActivity::class.java)
            intent.putExtra("type", "Expense")
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please")
        progressDialog.setMessage("Wait")
        progressDialog.setCancelable(false)
        if (FirebaseAuth.getInstance().currentUser == null) {
            progressDialog.show()
            FirebaseAuth.getInstance()
                .signInAnonymously()
                .addOnSuccessListener { progressDialog.cancel() }
                .addOnFailureListener { e ->
                    progressDialog.cancel()
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onResume() {
        super.onResume()
        income = 0
        expense = 0
        data
    }

    private val data: Unit
        private get() {
            FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .whereEqualTo("uid", FirebaseAuth.getInstance().uid)
                .get()
                .addOnSuccessListener { queryDocumentSnapshots ->
                    expensesAdapter!!.clear()
                    val dsList = queryDocumentSnapshots.documents
                    for (ds in dsList) {
                        val expenseModel = ds.toObject(ExpenseModel::class.java)
                        if (expenseModel!!.type == "Income") {
                            income += expenseModel.amount
                        } else {
                            expense += expenseModel.amount
                        }
                        expensesAdapter!!.add(expenseModel)
                    }
                    setUpGraph()
                }
        }

    private fun setUpGraph() {
        val pieEntryList: MutableList<PieEntry> = ArrayList()
        val colorsList: MutableList<Int> = ArrayList()
        if (income != 0L) {
            pieEntryList.add(PieEntry(income.toFloat(), "Income"))
            colorsList.add(resources.getColor(R.color.teal_700))
        }
        if (expense != 0L) {
            pieEntryList.add(PieEntry(expense.toFloat(), "Expense"))
            colorsList.add(resources.getColor(R.color.red))
        }
        val pieDataSet = PieDataSet(pieEntryList, (income - expense).toString())
        pieDataSet.colors = colorsList
        pieDataSet.valueTextColor = resources.getColor(R.color.white)
        val pieDat = PieData(pieDataSet)
        binding!!.pieChart.data = pieDat
        binding!!.pieChart.invalidate()
    }

    override fun onClick(expenseModel: ExpenseModel?) {
        val intent = Intent(this@MainActivity, AddExpenseActivity::class.java)
        intent.putExtra("model", expenseModel)
        startActivity(intent)
    }
}