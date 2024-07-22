package com.zhukovskii.currencyconverter.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zhukovskii.currencyconverter.R
import com.zhukovskii.currencyconverter.databinding.FragmentConverterBinding
import com.zhukovskii.currencyconverter.model.repository.ApiException
import com.zhukovskii.currencyconverter.navigation.ConverterNavigator
import com.zhukovskii.currencyconverter.viewmodel.ConversionResult
import com.zhukovskii.currencyconverter.viewmodel.ConverterViewModel
import com.zhukovskii.currencyconverter.viewmodel.NoExchangeRateException
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ConverterFragment : Fragment() {

    private val viewModel: ConverterViewModel by activityViewModels()

    private var _binding: FragmentConverterBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentConverterBinding.inflate(inflater, container, false)
        val view = binding.root

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.currency_array,
            android.R.layout.simple_spinner_dropdown_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        with (binding) {
            toCurrencyList.adapter = adapter
            fromCurrencyList.adapter = adapter

            convertButton.setOnClickListener {

                val amount = enteredAmount.text.toString().toBigDecimalOrNull()
                val fromCurrency = fromCurrencyList.selectedItem.toString()
                val toCurrency = toCurrencyList.selectedItem.toString()

                if (amount != null) {
                    viewModel.makeConversion(fromCurrency, toCurrency, amount)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {

                viewModel.invalidateResult()

                launch {
                    viewModel.conversionResultFlow.collectLatest { result ->
                        when (result) {

                            ConversionResult.Empty -> {}

                            is ConversionResult.Success -> {
                                activity?.let {
                                    ConverterNavigator.fromConverterToResult(
                                        it.supportFragmentManager
                                    )
                                }
                            }
                        }
                    }
                }

                launch {
                    viewModel.errorFlow.collectLatest { exception ->
                        val message = when (exception) {

                            is NoExchangeRateException -> {
                                getString(
                                    R.string.no_exchange_rate_exception_message,
                                    binding.fromCurrencyList.selectedItem.toString(),
                                    binding.toCurrencyList.selectedItem.toString()
                                )
                            }

                            is ApiException -> {
                                exception.message
                                    ?: getString(R.string.api_exception_message)
                            }

                            is HttpException -> {
                                getString(R.string.http_exception_format, exception.code())
                            }

                            is IOException -> {
                                getString(R.string.io_exception_message)
                            }

                            else -> {
                                getString(R.string.unexpected_exception_message)
                            }
                        }

                        context?.let {
                            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
                        }
                    }
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