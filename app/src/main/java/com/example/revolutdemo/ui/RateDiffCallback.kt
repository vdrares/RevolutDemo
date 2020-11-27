package com.example.revolutdemo.ui

import androidx.recyclerview.widget.DiffUtil
import com.example.revolutdemo.model.Rate
import java.math.BigDecimal

class RateDiffCallback(private val oldList: MutableList<Rate>, private val newList: MutableList<Rate>): DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldList[oldItemPosition].currency.currencyCode === newList[newItemPosition].currency.currencyCode

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldList[oldItemPosition].value == newList[newItemPosition].value

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? = newList[newItemPosition].value
}