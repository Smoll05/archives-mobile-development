package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.emoji2.emojipicker.EmojiPickerView
import com.android.archives.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmojiPickerDialogueFragment : BottomSheetDialogFragment() {

    companion object {
        const val EMOJI_PICKER_RESULT_KEY = "emoji_picker_result"
        const val EMOJI_PICKED_BUNDLE_KEY = "emoji"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_emoji_picker_dialogue, container, false)

        val emojiPickerView = view.findViewById<EmojiPickerView>(R.id.emoji_picker_layout)
        emojiPickerView.emojiGridColumns = 8

        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels

        val heightInPixels = (screenHeight * 0.7).toInt()

        val layoutParams = emojiPickerView.layoutParams
        layoutParams.height = heightInPixels
        emojiPickerView.layoutParams = layoutParams

        emojiPickerView.setOnEmojiPickedListener { emojiItem ->
            val result = Bundle().apply {
                putString(EMOJI_PICKED_BUNDLE_KEY, emojiItem.emoji)
            }
            parentFragmentManager.setFragmentResult(EMOJI_PICKER_RESULT_KEY, result)
            dismiss()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.isDraggable = false
        }
    }
}