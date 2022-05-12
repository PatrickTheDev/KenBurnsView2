package com.patrickpaul.kenburnsview2

import java.lang.RuntimeException

class IncompatibleRatioException
    : RuntimeException("Can't perform Ken Burns effect on rects with distinct aspect ratios!")