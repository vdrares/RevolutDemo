package com.example.revolutdemo.presenter

import com.example.revolutdemo.model.Rate

interface RatesContract {

    interface View {
        fun onRatesReceived(rates: ArrayList<Rate>)
        fun onError(throwable: Throwable)
        fun onRateClicked(rate: Rate, currentCurrencyInput: Double)
        fun onValueChanged(value: Double)
    }

    interface Presenter {
        fun getRatesEachSecond()
        fun attach(view: View)
        fun onValueChanged(newValue: Double)
        fun onBaseRateChanged(rate: Rate, currentCurrencyInput: Double)
    }
}
