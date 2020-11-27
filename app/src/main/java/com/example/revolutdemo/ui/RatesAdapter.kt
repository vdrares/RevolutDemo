package com.example.revolutdemo.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.revolutdemo.R
import com.example.revolutdemo.model.Rate
import com.example.revolutdemo.presenter.RatesContract
import kotlinx.android.synthetic.main.currency_item.view.*
import java.math.BigDecimal
import java.math.BigDecimal.ROUND_CEILING
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList

class RatesAdapter(private var view: RatesContract.View, private val context: Context) : RecyclerView.Adapter<RatesAdapter.ViewHolder>() {

    private var ratesList: ArrayList<Rate> = ArrayList()
    private var currentCurrencyInput: Double = 1.0
    private var currentSelectedCurrency: String = "EUR"

    private val currencyAmountEditTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(currentAmountValueText: CharSequence?, start: Int, before: Int, count: Int) {
            val newCurrentCurrencyInput = if (currentAmountValueText.isNullOrEmpty()) 0.0 else currentAmountValueText.toString().toDouble()

            val updatedList = ArrayList<Rate>()
            for (rate in ratesList) {
                updatedList.add(
                    if (ratesList.indexOf(rate) == 0) Rate(rate.currency, rate.value)
                    else Rate(rate.currency, rate.value
                        .divide(BigDecimal.valueOf(currentCurrencyInput), 2, ROUND_CEILING)
                        .multiply(BigDecimal.valueOf(newCurrentCurrencyInput))))
            }
            updateRates(updatedList)
            currentCurrencyInput = newCurrentCurrencyInput
            view.onValueChanged(currentCurrencyInput)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.currency_item, parent, false))
        viewHolder.inputValue.apply {
            setOnFocusChangeListener { _, focused ->
                when (focused) {
                    true -> {
                        bringRateToTop(viewHolder.bindingAdapterPosition, viewHolder.inputValue, ratesList[viewHolder.bindingAdapterPosition])
                        addTextChangedListener(currencyAmountEditTextWatcher)}
                    false -> removeTextChangedListener(currencyAmountEditTextWatcher)
                }
            }
        }
        return viewHolder
    }

    override fun getItemCount(): Int = ratesList.size

    override fun getItemId(position: Int): Long = ratesList[position].currency.hashCode().toLong()

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        when (payloads.isEmpty()) {
            true -> onBindViewHolder(holder, position)
            false -> with(holder.inputValue) { if (!isFocused) setText((payloads[0] as BigDecimal).setScale(2, RoundingMode.DOWN).toPlainString()) }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rate = ratesList[position]
        holder.title.text = rate.currency.currencyCode
        holder.subtitle.text = rate.currency.displayName
        Glide.with(context)
            .load(context.resources.getIdentifier("${rate.currency.currencyCode.toLowerCase(Locale.getDefault())}_flag", "drawable", context.packageName))
            .into(holder.itemView.flag)

        val convertedValue = (rate.value * ratesList[0].value)
        with(holder.inputValue) {
            if (!isFocused) {
                holder.inputValue.setText(
                    if (rate.currency.currencyCode == currentSelectedCurrency)
                        currentCurrencyInput.toString() else
                        convertedValue.setScale(2, RoundingMode.DOWN).toPlainString()
                )
            }
        }
        holder.itemView.setOnClickListener { bringRateToTop(position, holder.inputValue, rate) }
    }

    private fun bringRateToTop(position: Int, inputValue: EditText, selectedRate: Rate) {
        if (position != 0) {
            val updatedList = ArrayList<Rate>()

            for (rate in ratesList) {
                if (rate.currency.currencyCode != selectedRate.currency.currencyCode) updatedList.add(rate)
            }
            updatedList.apply {
                sortBy { it.currency.currencyCode }
                add(0, selectedRate)
            }
            inputValue.requestFocus()
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(inputValue, InputMethodManager.SHOW_IMPLICIT)
            currentCurrencyInput = inputValue.text.toString().toDouble()
            currentSelectedCurrency = selectedRate.currency.currencyCode
            view.onRateClicked(selectedRate, currentCurrencyInput)
            updateRates(updatedList)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.currencyTitle
        val subtitle: TextView = itemView.currencySubtitle
        val inputValue: EditText = itemView.currencyInput
    }

    fun updateRates(newRatesList: ArrayList<Rate>) {
        val diffCallback = RateDiffCallback(ratesList, newRatesList)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        ratesList.apply {
            clear()
            addAll(newRatesList)
        }
        diffResult.dispatchUpdatesTo(this@RatesAdapter)
    }

}
