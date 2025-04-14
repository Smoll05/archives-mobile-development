package com.android.archives.utils

import android.animation.ValueAnimator
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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


fun EditText.isFieldEmptyOrNull() : Boolean = this.text.toString().isEmpty()


fun EditText.getContent() : String = this.text.toString().trim()


fun Button.getContent() : String = this.text.toString().trim()


// Fragment extension function to collect the latest state of a ViewModel
fun <T> Fragment.collectLatestOnViewLifecycle(
    flow: Flow<T>,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    collector: suspend (T) -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(state) {
            flow.collectLatest { collector(it) }
        }
    }
}