package com.android.archives.utils

import android.animation.ValueAnimator
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

fun TextView.smoothTextChangeAnimation(newText: String) {
    val oldText = this.text

    if (newText.length > oldText.length) {
        val addedCharIndex = oldText.commonPrefixWith(newText).length
        animateAddedChar(this, newText, addedCharIndex)
    } else {
        this.text = newText
    }
}

private fun animateAddedChar(textView: TextView, fullText: String, index: Int) {
    val spannable = SpannableString(fullText)

    val alphaSpan = AlphaSpan()
    spannable.setSpan(alphaSpan, index, index + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    textView.text = spannable

    ValueAnimator.ofInt(0, 255).apply {
        duration = 300
        addUpdateListener {
            alphaSpan.alpha = it.animatedValue as Int
            textView.text = spannable
        }
        start()
    }
}

class AlphaSpan(var alpha: Int = 0) : CharacterStyle() {
    override fun updateDrawState(tp: TextPaint) {
        tp.alpha = alpha
    }
}

fun EditText.isFieldEmptyOrNull() : Boolean = this.text.isNullOrEmpty()

fun EditText.getContent() : String = this.text.toString().trim()

fun Button.getContent() : String = this.text.toString().trim()