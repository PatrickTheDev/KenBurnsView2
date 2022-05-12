package com.patrickpaul.kenburnsview2

import android.graphics.RectF
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import java.util.*

class RandomTransitionGenerator @JvmOverloads constructor(
    transitionDuration: Long = DEFAULT_TRANSITION_DURATION.toLong(),
    transitionInterpolator: Interpolator? = AccelerateDecelerateInterpolator()
) : TransitionGenerator {

    /** Random object used to generate arbitrary rects.  */
    private val mRandom: Random = Random(System.currentTimeMillis())

    /** The duration, in milliseconds, of each transition.  */
    private var mTransitionDuration: Long = 0

    /** The [Interpolator] to be used to create transitions.  */
    private var mTransitionInterpolator: Interpolator? = null

    /** The last generated transition.  */
    private var mLastGenTrans: Transition? = null

    /** The bounds of the drawable when the last transition was generated.  */
    private var mLastDrawableBounds: RectF? = null

    override fun generateNextTransition(drawableBounds: RectF, viewport: RectF): Transition {
        val firstTransition = mLastGenTrans == null
        var drawableBoundsChanged = true
        var viewportRatioChanged = true
        var srcRect: RectF? = null
        var dstRect: RectF? = null
        if (!firstTransition) {
            dstRect = mLastGenTrans!!.destinyRect
            drawableBoundsChanged = drawableBounds != mLastDrawableBounds
            viewportRatioChanged = !haveSameAspectRatio(dstRect, viewport)
        }
        srcRect = if (dstRect == null || drawableBoundsChanged || viewportRatioChanged) {
            generateRandomRect(drawableBounds, viewport)
        } else {
            /* Sets the destiny rect of the last transition as the source one
                    if the current drawable has the same dimensions as the one of
                    the last transition. */
            dstRect
        }
        dstRect = generateRandomRect(drawableBounds, viewport)
        mLastGenTrans = mTransitionInterpolator?.let {
            Transition(
                srcRect, dstRect, mTransitionDuration,
                it
            )
        }
        mLastDrawableBounds = RectF(drawableBounds)
        return mLastGenTrans!!
    }

    /**
     * Generates a random rect that can be fully contained within `drawableBounds` and
     * has the same aspect ratio of `viewportRect`. The dimensions of this random rect
     * won't be higher than the largest rect with the same aspect ratio of `viewportRect`
     * that `drawableBounds` can contain. They also won't be lower than the dimensions
     * of this upper rect limit weighted by `MIN_RECT_FACTOR`.
     * @param drawableBounds the bounds of the drawable that will be zoomed and panned.
     * @param viewportRect the bounds of the view that the drawable will be shown.
     * @return an arbitrary generated rect with the same aspect ratio of `viewportRect`
     * that will be contained within `drawableBounds`.
     */
    private fun generateRandomRect(drawableBounds: RectF, viewportRect: RectF): RectF {
        val drawableRatio: Float = getRectRatio(drawableBounds)
        val viewportRectRatio: Float = getRectRatio(viewportRect)
        val maxCrop: RectF
        if (drawableRatio > viewportRectRatio) {
            val r: Float = drawableBounds.height() / viewportRect.height() * viewportRect.width()
            val b: Float = drawableBounds.height()
            maxCrop = RectF(0F, 0F, r, b)
        } else {
            val r: Float = drawableBounds.width()
            val b: Float = drawableBounds.width() / viewportRect.width() * viewportRect.height()
            maxCrop = RectF(0F, 0F, r, b)
        }
        val randomFloat: Float = truncate(mRandom.nextFloat(), 2)
        val factor = MIN_RECT_FACTOR + (1 - MIN_RECT_FACTOR) * randomFloat
        val width: Float = factor * maxCrop.width()
        val height: Float = factor * maxCrop.height()
        val widthDiff = (drawableBounds.width() - width).toInt()
        val heightDiff = (drawableBounds.height() - height).toInt()
        val left = if (widthDiff > 0) mRandom.nextInt(widthDiff) else 0
        val top = if (heightDiff > 0) mRandom.nextInt(heightDiff) else 0
        return RectF(left.toFloat(), top.toFloat(), left + width, top + height)
    }

    /**
     * Sets the duration, in milliseconds, for each transition generated.
     * @param transitionDuration the transition duration.
     */
    fun setTransitionDuration(transitionDuration: Long) {
        mTransitionDuration = transitionDuration
    }

    /**
     * Sets the [Interpolator] for each transition generated.
     * @param interpolator the transition interpolator.
     */
    fun setTransitionInterpolator(interpolator: Interpolator) {
        mTransitionInterpolator = interpolator
    }

    companion object {
        /** Default value for the transition duration in milliseconds.  */
        const val DEFAULT_TRANSITION_DURATION = 10000

        /** Minimum rect dimension factor, according to the maximum one.  */
        private const val MIN_RECT_FACTOR = 0.75f
    }

    init {
        setTransitionDuration(transitionDuration)
        if (transitionInterpolator != null) {
            setTransitionInterpolator(transitionInterpolator)
        }
    }
}