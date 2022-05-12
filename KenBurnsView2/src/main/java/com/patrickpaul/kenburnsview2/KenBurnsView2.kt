package com.patrickpaul.kenburnsview2

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.min

/**
 * {@link ImageView} extension that animates its image with the
 * <a href="http://en.wikipedia.org/wiki/Ken_Burns_effect">Ken Burns Effect</a>.
 * @author Flavio Faria
 * @see Transition
 * @see TransitionGenerator
 */
@SuppressLint("AppCompatCustomView")
class KenBurnsView2(context: Context, attrs: AttributeSet?, defStyle: Int)
    : AppCompatImageView(context, attrs, defStyle) {

    /** Matrix used to perform all the necessary transition transformations. */
    private var _mMatrix: Matrix? = null
    private val mMatrix get() = _mMatrix!!

    /** The [TransitionGenerator] implementation used to perform the transitions between
     *  rects. The default [TransitionGenerator] is [RandomTransitionGenerator]. */
    private var mTransGen: RandomTransitionGenerator? = null
    /** A {@link KenBurnsView.TransitionListener} to be notified when
     *  a transition starts or ends. */
    private var mTransitionListener: TransitionListener? = null
    /** The ongoing transition. */
    private var _mCurrentTrans: Transition? = null
    private val mCurrentTrans get() = _mCurrentTrans!!

    /** The rect that holds the bounds of this view. */
    private val mViewPortRect: RectF
    /** The rect that holds the bounds of the current [Drawable]. */
    private var _mDrawableRect: RectF? = null
    private val mDrawableRect get() = _mDrawableRect!!

    /** The progress of the animation, in milliseconds. */
    private var mElapsedTime: Long = 0
    /** The time, in milliseconds, of the last animation frame.
     * This is useful to increment [mElapsedTime] regardless
     * of the amount of time the animation has been paused. */
    private var mLastFrameTime: Long = 0

    /** Controls whether the the animation is running. */
    private var mPaused: Boolean = false

    /** Indicates whether the parent constructor was already called.
     * This is needed to distinguish if the image is being set before
     * or after the super class constructor returns. */
    private var mInitialized: Boolean


    constructor(context: Context)
            : this(context, null)
    constructor(context: Context, attrs: AttributeSet?)
            : this(context, attrs, 0)

    init {
        mInitialized = true

        mViewPortRect = RectF()

        // Attention to the super call here!
        super.setScaleType(ScaleType.MATRIX)
    }

    override fun setScaleType(scaleType: ScaleType?) {
        // No-op, because always center-cropped by default
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        when (visibility) {
            VISIBLE -> {
                resume()
            }
            else -> {
                pause()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        val d: Drawable? = drawable

        if (!mPaused && d != null) {
            if (mDrawableRect.isEmpty) {
                updateDrawableBounds()
            } else if (hasBounds()) {
                if (_mCurrentTrans == null) {
                    // Starting the first transition
                    startNewTransition()
                }
                if (mCurrentTrans.destinyRect != null) {
                    // If it's null, it's supposed to stop
                    mElapsedTime += System.currentTimeMillis() - mLastFrameTime
                    val currentRect: RectF = mCurrentTrans.getInterpolatedRect(mElapsedTime)

                    val widthScale = mDrawableRect.width() / currentRect.width()
                    val heightScale = mDrawableRect.height() / currentRect.height()
                    // Scale to make the current rect match the smallest drawable dimension.
                    val currRectToDrawScale = min(widthScale, heightScale)
                    // Scale to make the current rect match the viewport bounds.
                    val vpWidthScale: Float = mViewPortRect.width() / currentRect.width()
                    val vpHeightScale: Float = mViewPortRect.height() / currentRect.height()
                    val currRectToVpScale = min(vpWidthScale, vpHeightScale)
                    // Combines the two scales to fill the viewport with the current rect.
                    val totalScale: Float = currRectToDrawScale * currRectToVpScale

                    val translX: Float = totalScale * (mDrawableRect.centerX() - currentRect.left)
                    val translY: Float = totalScale * (mDrawableRect.centerY() - currentRect.top)

                    /*
                    Performs matrix transformations to fit the content of the current
                    rect into the entire view.
                     */
                    mMatrix.run {
                        reset()
                        postTranslate(-mDrawableRect.width()/2, -mDrawableRect.height()/2)
                        postScale(totalScale, totalScale)
                        postTranslate(translX, translY)
                    }

                    imageMatrix = mMatrix

                    // Current Transition is over. It's time to start a new one.
                    if (mElapsedTime >= mCurrentTrans.duration) {
                        fireTransitionEnd(mCurrentTrans)
                        startNewTransition()
                    }
                } else {
                    // Stopping? A stop event has to be fired.
                    fireTransitionEnd(mCurrentTrans)
                }
            }
            mLastFrameTime = System.currentTimeMillis()
            postInvalidateDelayed(FRAME_DELAY)
        }
        super.onDraw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        restart()
    }

    private fun startNewTransition() {}

    /**
     * Pauses the Ken Burns Effect animation.
     */
    fun pause() {}

    /**
     * Resumes the Ken Burns Effect animation.
     */
    fun resume() {}

    private fun restart() {}

    private fun hasBounds() : Boolean {
        return false;
    }

    private fun fireTransitionEnd(transition: Transition) {}

    private fun updateViewPort() {}

    private fun updateDrawableBounds() {}

    private fun handleImageChange() {}

    /**
     * A transition listener receives notifications when a transition starts or ends.
     */
    interface TransitionListener {
        /**
         * Notifies the start of a transition.
         * @param transition the transition that just started.
         */
        fun onTransitionStart(transition: Transition)
        /**
         * Notifies the end of a transition.
         * @param transition the transition that just ended.
         */
        fun onTransitionEnd(transition: Transition)
    }

    companion object {
        /** Delay between a pair of frames at a 60 FPS frame rate. */
        private val FRAME_DELAY = (1000 / 60).toLong()
    }
}