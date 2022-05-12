package com.patrickpaul.kenburnsview2

import android.graphics.RectF

interface TransitionGenerator {
    /**
     * Generates the next transition to be played by the [KenBurnsView2].
     * @param drawableBounds the bounds of the drawable to be shown in the [KenBurnsView2].
     * @param viewport the rect that represents the viewport where
     * the transition will be played in. This is usually the bounds of the
     * [KenBurnsView2].
     * @return a [Transition] object to be played by the [KenBurnsView2].
     */
    fun generateNextTransition(drawableBounds: RectF, viewport: RectF): Transition
}