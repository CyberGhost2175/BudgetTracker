package model

import java.io.Serializable

class ExpenseModel : Serializable {
    var expenseId: String? = null
    var note: String? = null
    var category: String? = null
    var type: String? = null
    var amount: Long = 0
    var time: Long = 0
    var uid: String? = null

    constructor() {}
    constructor(
        expenseId: String?,
        note: String?,
        category: String?,
        type: String?,
        amount: Long,
        time: Long,
        uid: String?
    ) {
        this.expenseId = expenseId
        this.note = note
        this.category = category
        this.type = type
        this.amount = amount
        this.time = time
        this.uid = uid
    }
}