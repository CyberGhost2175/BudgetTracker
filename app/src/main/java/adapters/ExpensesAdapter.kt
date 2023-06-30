package adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import adapters.ExpensesAdapter.MyViewHolder
import model.OnItemsCLick
import com.example.expensemanager.R
import model.ExpenseModel

class ExpensesAdapter(private val context: Context, onItemsCLick: OnItemsCLick) :
    RecyclerView.Adapter<MyViewHolder>() {
    private val onItemsCLick: OnItemsCLick
    private val expenseModelList: MutableList<ExpenseModel>

    init {
        expenseModelList = ArrayList()
        this.onItemsCLick = onItemsCLick
    }

    fun add(expenseModel: ExpenseModel) {
        expenseModelList.add(expenseModel)
        notifyDataSetChanged()
    }

    fun clear() {
        expenseModelList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expense_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val expenseModel = expenseModelList[position]
        holder.note.text = expenseModel.note
        holder.category.text = expenseModel.category
        holder.amount.text = expenseModel.amount.toString()
        holder.itemView.setOnClickListener { onItemsCLick.onClick(expenseModel) }
    }

    override fun getItemCount(): Int {
        return expenseModelList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val note: TextView
         val category: TextView
         val amount: TextView
//         val date: TextView

        init {
            note = itemView.findViewById(R.id.note)
            category = itemView.findViewById(R.id.category)
            amount = itemView.findViewById(R.id.amount)
//            date = itemView.findViewById(R.id.date)
        }
    }
}