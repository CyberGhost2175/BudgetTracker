package model

import model.ExpenseModel

interface OnItemsCLick {
    fun onClick(expenseModel: ExpenseModel?)
}