package com.android.archives.ui.fragment

import android.content.Context
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
    interface EmojiPickerListener {
        fun onEmojiPicked(emoji: String)
    }

    private var listener: EmojiPickerListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EmojiPickerListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement EmojiPickerListener")
        }
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

        emojiPickerView.setOnEmojiPickedListener {emojiItem ->
            listener?.onEmojiPicked(emojiItem.emoji)
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

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}