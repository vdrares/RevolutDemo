package com.example.revolutdemo.presenter

import com.example.revolutdemo.model.Rate
import com.example.revolutdemo.model.RatesResponse
import com.example.revolutdemo.networking.RatesService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class RatesPresenter : RatesContract.Presenter {

    private val api = RatesService.create()
    private val subscriptions = CompositeDisposable()
    private lateinit var view: RatesContract.View

    var selectedCurrency = "EUR"
    var currentCurrencyInput = 1.0

    override fun getRatesEachSecond() {
        subscriptions.add(
            Observable.interval(1, TimeUnit.SECONDS)
                .flatMap { api.getRates(selectedCurrency).takeUntil(Observable.timer(5, TimeUnit.SECONDS)) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response: RatesResponse? ->
                    val rates: ArrayList<Rate> = ArrayList()
                    if (response != null) {
                        rates.add(Rate(Currency.getInstance(selectedCurrency), BigDecimal.ONE))
                        val sortedMap: MutableMap<String, Float> = LinkedHashMap()
                        response.rates.entries.sortedBy { it.key }.forEach { sortedMap[it.key] = it.value }

                        for ((currencyCode, value) in sortedMap) {
                            rates.add(Rate(Currency.getInstance(currencyCode), BigDecimal.valueOf(value * currentCurrencyInput)))
                        }
                    }
                    view.onRatesReceived(rates)
                }, { error -> view.onError(error) })
        )
    }

    override fun attach(view: RatesContract.View) {
        this.view = view
    }

    override fun onValueChanged(newValue: Double) {
        currentCurrencyInput = newValue
    }

    override fun onBaseRateChanged(rate: Rate, currentCurrencyInput: Double) {
        selectedCurrency = rate.currency.currencyCode
        this.currentCurrencyInput = currentCurrencyInput
    }
}
