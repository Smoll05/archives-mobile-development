package com.android.archives.ui.fragment.auth

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.android.archives.data.event.UserEvent
import com.android.archives.databinding.FragmentOnboardingBinding
import com.android.archives.ui.activity.MainActivity
import com.android.archives.ui.viewmodel.UserViewModel
import com.android.archives.utils.DateConverter
import com.android.archives.utils.getContent
import com.android.archives.utils.isFieldEmptyOrNull
import com.android.archives.utils.smoothTextChangeAnimation
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class OnboardingFragment : Fragment() {
    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var onEvent: (UserEvent) -> Unit

    private lateinit var datePicker: MaterialDatePicker<Long>
    private var existingPicker: Fragment? = null
    private val pickerTag = "DATE PICKER"
    private lateinit var cachedDestinationUr: Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentOnboardingBinding.inflate(inflater).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onEvent = userViewModel::onEvent
        onEvent(UserEvent.ShowForm)

        binding.tbProfile.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.editName.addTextChangedListener {
            binding.tvName.smoothTextChangeAnimation(it.toString())
            binding.tilName.error = null
        }

        binding.editProgram.addTextChangedListener {
            binding.tvProgram.smoothTextChangeAnimation(it.toString())
            binding.tilProgram.error = null
        }

        binding.editSchool.addTextChangedListener {
            binding.tvSchool.smoothTextChangeAnimation(it.toString())
            binding.tilSchool.error = null
        }

        binding.editBirthday.addTextChangedListener {
            binding.tilBirthday.error = null
        }

        binding.ivProfileImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val constraintBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())
            .setEnd(today)

        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Birthday Date")
            .setCalendarConstraints(constraintBuilder.build())
            .build()

        binding.editBirthday.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            if(areFieldsEmpty()) return@setOnClickListener

            onEvent(UserEvent.SetFullName(binding.editName.getContent()))
            onEvent(UserEvent.SetProgram(binding.editProgram.getContent()))
            onEvent(UserEvent.SetSchool(binding.editSchool.getContent()))

            // SAVE USER TO DATABASE
            onEvent(UserEvent.SaveUser)

            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)

        }
    }

    private fun showDatePicker() {
        if (existingPicker == null) {
            datePicker.show(parentFragmentManager, pickerTag)
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = DateConverter.convertMillisToDateString(selection)
            binding.editBirthday.setText(selectedDate)
            binding.tvBirthday.text = selectedDate
            onEvent(UserEvent.SetBirthday(selection))
        }

        datePicker.addOnNegativeButtonClickListener {
            // Handle negative button click (optional)
        }

        datePicker.addOnCancelListener {
            // Handle cancel event (optional)
        }

        datePicker.addOnDismissListener {
            existingPicker = null
        }

        existingPicker = datePicker
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            startUCrop(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    private val cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val resultUri = data?.let { UCrop.getOutput(it) }
            resultUri?.let {
                binding.ivProfileImage.setImageURI(it)
                savePhotoToInternalStorage(it) // Save the cropped image
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(result.data!!)
            Log.e("Crop Error", "Cropping Image Failed: $cropError")
        }
    }

    private fun startUCrop(sourceUri: Uri) {
        cachedDestinationUr = Uri.fromFile(File(requireContext().cacheDir, "profile_picture_${System.currentTimeMillis()}.png"))

        val options = UCrop.Options().apply {
            setToolbarTitle("Crop Image")
            setFreeStyleCropEnabled(false)
            setAspectRatioOptions(0, com.yalantis.ucrop.model.AspectRatio("4:5", 4f, 5f))
            setShowCropGrid(true)
            setCompressionFormat(Bitmap.CompressFormat.PNG)
        }

        val intent = UCrop.of(sourceUri, cachedDestinationUr)
            .withAspectRatio(4f, 5f)
            .withOptions(options)
            .getIntent(requireContext())

        cropImageLauncher.launch(intent)
    }

    private fun savePhotoToInternalStorage(uri: Uri): Boolean {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            val filename = "profile_${System.currentTimeMillis()}.png"
            val file = File(requireContext().filesDir, filename)

            file.outputStream().use { stream ->
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
                    throw IOException("Couldn't save bitmap.")
                }

                val deleted = cachedDestinationUr.path?.let { File(it).delete() }

                if (deleted == true) {
                    Log.d("SaveImage", "Temporary cropped image deleted.")
                } else {
                    Log.e("SaveImage", "Failed to delete temporary cropped image.")
                }

                onEvent(UserEvent.SetPictureFilePath(file.absolutePath))

                Log.d("SaveImage", "Saved image path: ${file.absolutePath}")
            }
            bitmap.recycle()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun areFieldsEmpty() : Boolean {
        var isEmpty = false
        val errorMsg = "This Field Is Required"

        if(binding.editName.isFieldEmptyOrNull()) {
            binding.tilName.error = errorMsg
            isEmpty = true
        }

        if(binding.editBirthday.isFieldEmptyOrNull()) {
            binding.tilBirthday.error = errorMsg
            isEmpty = true
        } else {
            binding.tilBirthday.error = null
        }

        if(binding.editProgram.isFieldEmptyOrNull()) {
            binding.tilProgram.error = errorMsg
            isEmpty = true
        }

        if(binding.editSchool.isFieldEmptyOrNull()) {
            binding.tilSchool.error = errorMsg
            isEmpty = true
        }

        return isEmpty
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}