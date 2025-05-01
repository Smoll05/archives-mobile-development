package com.android.archives.ui.fragment.main

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.archives.R
import com.android.archives.databinding.FragmentEmojiPickerDialogueBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmojiPickerDialogueFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentEmojiPickerDialogueBinding? = null
    private val binding get() = _binding!!

    private var emojiSelected = false

    companion object {
        const val EMOJI_PICKER_RESULT_KEY = "emoji_picker_result"
        const val EMOJI_PICKED_BUNDLE_KEY = "emoji"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmojiPickerDialogueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emojiPickerView = binding.emojiPickerLayout
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

        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.isDraggable = false
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.dialog_animation_enter_up)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        if (!emojiSelected && parentFragmentManager.isStateSaved.not()) {
            parentFragmentManager.setFragmentResult(
                EMOJI_PICKER_RESULT_KEY,
                Bundle()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
