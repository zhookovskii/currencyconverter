package com.zhukovskii.currencyconverter.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import com.zhukovskii.currencyconverter.R
import com.zhukovskii.currencyconverter.navigation.ConverterNavigator

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val splashImage = view.findViewById<ImageView>(R.id.splash_image)

        fun scaleDimension(dimension: String) =
            ObjectAnimator.ofFloat(
                splashImage,
                "scale$dimension",
                2.0f
            ).apply {
                interpolator = OvershootInterpolator(2f)
                duration = 1000
            }

        val stretchX = scaleDimension("X")
        val stretchY = scaleDimension("Y")

        AnimatorSet().apply {
            play(stretchX).with(stretchY)
            doOnEnd {
                activity?.let {
                    ConverterNavigator.fromSplashToConverter(it.supportFragmentManager)
                }
            }
            start()
        }
    }
}