package com.example.revolutdemo.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.revolutdemo.R
import com.example.revolutdemo.di.component.DaggerActivityComponent
import com.example.revolutdemo.di.module.ActivityModule
import com.example.revolutdemo.model.Rate
import com.example.revolutdemo.presenter.RatesContract
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class RatesActivity : AppCompatActivity(), RatesContract.View {

    @Inject
    lateinit var presenter: RatesContract.Presenter
    private val ratesAdapter = RatesAdapter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DaggerActivityComponent.builder().activityModule(ActivityModule((this))).build().inject(this)
    }

    override fun onStart() {
        super.onStart()
        presenter.apply {
            attach(this@RatesActivity)
            getRatesEachSecond()
        }
        setupUI()
    }

    private fun setupUI() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        ratesAdapter.apply {
            setHasStableIds(false)
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        ratesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RatesActivity)
            adapter = ratesAdapter
            setOnTouchListener { v, _ ->
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(v.windowToken, 0)
                false
            }
        }
    }

    override fun onRatesReceived(rates: ArrayList<Rate>) {
        progressBar.visibility = GONE
        ratesRecyclerView.visibility = VISIBLE
        errorMsg.visibility = GONE
        ratesAdapter.updateRates(rates)
    }

    override fun onError(throwable: Throwable) {
        progressBar.visibility = GONE
        ratesRecyclerView.visibility = GONE
        errorMsg.visibility = VISIBLE
    }

    override fun onRateClicked(rate: Rate, currentCurrencyInput: Double) {
        presenter.onBaseRateChanged(rate, currentCurrencyInput)
    }

    override fun onValueChanged(value: Double) {
        presenter.onValueChanged(value)
    }
}
