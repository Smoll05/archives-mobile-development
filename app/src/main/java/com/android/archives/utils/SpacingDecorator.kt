package com.android.archives.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class SpacingDecorator : RecyclerView.ItemDecoration {
    protected val top: Int
    protected val start: Int
    protected val end: Int
    protected val bottom: Int

    constructor(
        top: Int,
        start: Int,
        end: Int,
        bottom: Int
    ) : super() {
        this.top = top
        this.start = start
        this.end = end
        this.bottom = bottom
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            top = this@SpacingDecorator.top
            left = this@SpacingDecorator.start
            right = this@SpacingDecorator.end
            bottom = this@SpacingDecorator.bottom
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SpacingDecorator) return false
        return other.top == top && other.start == start && other.end == end && other.bottom == bottom
    }

    fun top(spaceSize: Int) = SpacingDecorator(spaceSize, start, end, bottom)
    fun start(spaceSize: Int) = SpacingDecorator(top, spaceSize, end, bottom)
    fun end(spaceSize: Int) = SpacingDecorator(top, start, spaceSize, bottom)
    fun bottom(spaceSize: Int) = SpacingDecorator(top, start, end, spaceSize)
    fun vertical(spaceSize: Int) = SpacingDecorator(spaceSize, start, end, spaceSize)
    fun horizontal(spaceSize: Int) = SpacingDecorator(top, spaceSize, spaceSize, bottom)

    companion object {
        fun top(spaceSize: Int) = SpacingDecorator(spaceSize, 0, 0, 0)
        fun start(spaceSize: Int) = SpacingDecorator(0, spaceSize, 0, 0)
        fun end(spaceSize: Int) = SpacingDecorator(0, 0, spaceSize, 0)
        fun bottom(spaceSize: Int) = SpacingDecorator(0, 0, 0, spaceSize)
        fun vertical(spaceSize: Int) = SpacingDecorator(spaceSize, 0, 0, spaceSize)
        fun horizontal(spaceSize: Int) = SpacingDecorator(0, spaceSize, spaceSize, 0)
    }

    open class Builder {
        var top: Int = 0
        var start: Int = 0
        var end: Int = 0
        var bottom: Int = 0

        fun top(value: Number) {
            this.top = value.toInt()
        }

        fun start(value: Number) {
            this.start = value.toInt()
        }

        fun end(value: Number) {
            this.end = value.toInt()
        }

        fun bottom(value: Number) {
            this.bottom = value.toInt()
        }

        open fun build() = SpacingDecorator(top, start, end, bottom)
    }
}