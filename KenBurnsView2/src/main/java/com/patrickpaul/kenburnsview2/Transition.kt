package com.patrickpaul.kenburnsview2

import android.graphics.RectF
import android.view.animation.Interpolator
import kotlin.math.min

class Transition(srcRect: RectF, dstRect: RectF, duration: Long, interpolator: Interpolator) {

    /** The rect the transition will start from.  */
    private val mSrcRect: RectF

    /** The rect the transition will end at.  */
    private val mDstRect: RectF

    /** An intermediary rect that changes in every frame according to the transition progress.  */
    private val mCurrentRect: RectF = RectF()

    /** Precomputed width difference between [.mSrcRect] and [.mDstRect].  */
    private val mWidthDiff: Float

    /** Precomputed height difference between [.mSrcRect] and [.mDstRect].  */
    private val mHeightDiff: Float

    /** Precomputed X offset between the center points of
     * [.mSrcRect] and [.mDstRect].  */
    private val mCenterXDiff: Float

    /** Precomputed Y offset between the center points of
     * [.mSrcRect] and [.mDstRect].  */
    private val mCenterYDiff: Float
    /** The duration of the transition in milliseconds. The default duration is 5000 ms.  */
    val duration: Long

    /** The [Interpolator] used to perform the transitions between rects.  */
    private val mInterpolator: Interpolator

    /**
     * Gets the rect that will take the scene when a Ken Burns transition starts.
     * @return the rect that starts the transition.
     */
    val sourceRect: RectF get() = mSrcRect

    /**
     * Gets the rect that will take the scene when a Ken Burns transition ends.
     * @return the rect that ends the transition.
     */
    val destinyRect: RectF get() = mDstRect

    /**
     * Gets the current rect that represents the part of the image to take the scene
     * in the current frame.
     * @param elapsedTime the elapsed time since this transition started.
     */
    fun getInterpolatedRect(elapsedTime: Long): RectF {
        val elapsedTimeFraction = elapsedTime / duration.toFloat()
        val interpolationProgress = min(elapsedTimeFraction, 1f)
        val interpolation: Float = mInterpolator.getInterpolation(interpolationProgress)
        val currentWidth: Float = mSrcRect.width() + interpolation * mWidthDiff
        val currentHeight: Float = mSrcRect.height() + interpolation * mHeightDiff
        val currentCenterX: Float = mSrcRect.centerX() + interpolation * mCenterXDiff
        val currentCenterY: Float = mSrcRect.centerY() + interpolation * mCenterYDiff
        val left = currentCenterX - currentWidth / 2
        val top = currentCenterY - currentHeight / 2
        val right = left + currentWidth
        val bottom = top + currentHeight
        mCurrentRect.set(left, top, right, bottom)
        return mCurrentRect
    }

    init {
        if (!haveSameAspectRatio(srcRect, dstRect)) {
            throw IncompatibleRatioException()
        }
        mSrcRect = srcRect
        mDstRect = dstRect
        this.duration = duration
        mInterpolator = interpolator

        // Precomputes a few variables to avoid doing it in onDraw().
        mWidthDiff = dstRect.width() - srcRect.width()
        mHeightDiff = dstRect.height() - srcRect.height()
        mCenterXDiff = dstRect.centerX() - srcRect.centerX()
        mCenterYDiff = dstRect.centerY() - srcRect.centerY()
    }
}