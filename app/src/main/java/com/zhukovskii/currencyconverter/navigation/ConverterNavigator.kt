package com.zhukovskii.currencyconverter.navigation

import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import com.zhukovskii.currencyconverter.R

/**
 * Class which encapsulates navigation logic
 */
object ConverterNavigator {

    private fun getNavHostFragment(supportFragmentManager: FragmentManager): NavHostFragment {
        return supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
    }

    /**
     * Navigate from `SplashFragment` to `ConverterFragment`
     */
    fun fromSplashToConverter(supportFragmentManager: FragmentManager) {
        getNavHostFragment(supportFragmentManager)
            .navController
            .navigate(R.id.action_splashFragment_to_converterFragment)
    }

    /**
     * Navigate from `ConverterFragment` to `ResultFragment`
     *
     * This navigation must be executed only if the result of the conversion
     * is successful
     */
    fun fromConverterToResult(supportFragmentManager: FragmentManager) {
        getNavHostFragment(supportFragmentManager)
            .navController
            .navigate(R.id.action_converterFragment_to_resultFragment)
    }

    /**
     * Navigate to the previous fragment
     */
    fun back(supportFragmentManager: FragmentManager) {
        getNavHostFragment(supportFragmentManager)
            .navController
            .navigateUp()
    }
}