package com.patrickpaul.kenburnsview2

import android.graphics.RectF
import kotlin.math.pow
import kotlin.math.abs
import kotlin.math.roundToInt


/**
 * Truncates a float number {@code f} to {@code decimalPlaces}.
 * @param f the number to be truncated.
 * @param decimalPlaces the amount of decimals that {@code f}
 * will be truncated to.
 * @return a truncated representation of {@code f}.
 */
fun truncate(f: Float, decimalPlaces: Int) : Float {
    val ten = 10.0
    val decimalShift = ten.pow(decimalPlaces.toDouble()).toFloat()
    return (f * decimalShift).roundToInt() / decimalShift
}

/**
 * Checks whether two {@link RectF} have the same aspect ratio.
 * @param r1 the first rect.
 * @param r2  the second rect.
 * @return {@code true} if both rectangles have the same aspect ratio,
 * {@code false} otherwise.
 */
fun haveSameAspectRatio(r1: RectF, r2: RectF) : Boolean {
    // Reduces precision to avoid problems when comparing aspect ratios.
    val srcRectRatio: Float = truncate(getRectRatio(r1), 3)
    val dstRectRatio: Float = truncate(getRectRatio(r2), 3)
    // Compares aspect ratios that allows for a tolerance range of [0, 0.01]
    return abs(srcRectRatio - dstRectRatio) <= 0.01f
}

/**
 * Computes the aspect ratio of a given rect.
 * @param rect the rect to have its aspect ratio computed.
 * @return the rect aspect ratio.
 */
fun getRectRatio(rectF: RectF) : Float {
    return rectF.width()/rectF.height()
}