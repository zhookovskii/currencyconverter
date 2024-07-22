package com.zhukovskii.currencyconverter.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.zhukovskii.currencyconverter.R
import com.zhukovskii.currencyconverter.databinding.FragmentResultBinding
import com.zhukovskii.currencyconverter.navigation.ConverterNavigator
import com.zhukovskii.currencyconverter.view.util.LargeCurrencyFormatter
import com.zhukovskii.currencyconverter.viewmodel.ConversionResult
import com.zhukovskii.currencyconverter.viewmodel.ConverterViewModel

class ResultFragment : Fragment() {

    private val viewModel: ConverterViewModel by activityViewModels()

    private var _binding: FragmentResultBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentResultBinding.inflate(inflater, container, false)
        val view = binding.root

        val result = viewModel.conversionResultFlow.value as ConversionResult.Success

        with (binding) {
            fromCurrencyAmount.text = getString(
                R.string.currency_amount_format,
                LargeCurrencyFormatter.format(result.amount),
                result.baseCode
            )
            fromCurrencyName.text = result.baseName
            toCurrencyAmount.text = getString(
                R.string.currency_amount_format,
                LargeCurrencyFormatter.format(result.resultAmount),
                result.targetCode
            )
            toCurrencyName.text = result.targetName
            exchangeRateText.text = getString(
                R.string.exchange_rate_format,
                result.baseCode,
                result.exchangeRate,
                result.targetCode
            )

            backButton.setOnClickListener {
                activity?.let {
                    ConverterNavigator.back(it.supportFragmentManager)
                }
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}