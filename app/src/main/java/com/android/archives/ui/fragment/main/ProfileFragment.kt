package com.android.archives.ui.fragment.main

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.archives.R
import com.android.archives.databinding.FragmentProfileBinding
import com.android.archives.ui.viewmodel.UserViewModel
import com.android.archives.utils.DateConverter
import com.android.archives.utils.isFieldEmptyOrNull
import com.android.archives.utils.smoothTextChangeAnimation
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class ProfileFragment : DialogFragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserViewModel by activityViewModels()

    private lateinit var tilName: TextInputLayout
    private lateinit var tilDate: TextInputLayout
    private lateinit var tilProgram: TextInputLayout
    private lateinit var tilSchool: TextInputLayout

    private lateinit var etName: EditText
    private lateinit var etDate: EditText
    private lateinit var etProgram: EditText
    private lateinit var etSchool: EditText

    private lateinit var tvBirthday: TextView
    private lateinit var ivImage: ImageView

    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var cachedDestinationUr: Uri
    private val pickerTag = "DATE PICKER"
    private var existingPicker: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.profileToolbar.setNavigationOnClickListener {
            dismiss()
        }

        return binding.root
    }

    // DIRI ANG ANIMATION : SUGOD & END
    override fun onStart() {
        super.onStart()

        dialog?.window?.setWindowAnimations(
            R.style.dialog_animation_enter_right);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolBar = binding.profileToolbar
        val tvName = binding.profileName
        tvBirthday = binding.profileBirthday
        val tvProgram = binding.profileProgram
        val tvSchool = binding.profileSchool
        ivImage = binding.profileCardImg

        tilName = binding.layoutName
        tilDate = binding.layoutBirthday
        tilProgram = binding.layoutProgram
        tilSchool = binding.layoutSchool

        etName = binding.editName
        etDate = binding.editBirthday
        etProgram = binding.editProgram
        etSchool = binding.editSchool

        val btnSave = binding.profileSaveBtn

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: $uri")
                    startUCrop(uri)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        etName.addTextChangedListener {
            tvName.smoothTextChangeAnimation(it.toString())
            tilName.error = null
        }

        etProgram.addTextChangedListener {
            tvProgram.smoothTextChangeAnimation(it.toString())
            tilProgram.error = null
        }

        etSchool.addTextChangedListener {
            tvSchool.smoothTextChangeAnimation(it.toString())
            tilSchool.error = null
        }

        ivImage.setOnClickListener {
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

        etDate.setOnClickListener {
            showDatePicker()
        }

        btnSave.setOnClickListener {
            if (areFieldsEmpty()) return@setOnClickListener
            Toast.makeText(context, "Fields Input Correct", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePicker() {
        if (existingPicker == null) {
            datePicker.show(parentFragmentManager, pickerTag)
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = DateConverter.convertMillisToDateString(selection)
            etDate.setText(selectedDate)
            tvBirthday.smoothTextChangeAnimation(selectedDate)
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

//    @Deprecated("Deprecated in Java")
//    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
//            val resultUri = UCrop.getOutput(data!!)
//            resultUri?.let {
//                ivImage.setImageURI(it)
//            }
//        } else if (resultCode == UCrop.RESULT_ERROR) {
//            val cropError = UCrop.getError(data!!)
//            Log.e("Crop Error", "Cropping Image Failed: $cropError")
//        }
//    }


//    private fun startUCrop(sourceUri: Uri) {
//        val destinationUri = Uri.fromFile(File(cacheDir, "profile_picture_${System.currentTimeMillis()}.png"))
//
//        val options = UCrop.Options().apply {
//            setToolbarTitle("Crop Image")
//            setFreeStyleCropEnabled(false)
//            setAspectRatioOptions(0, com.yalantis.ucrop.model.AspectRatio("4:5", 4f, 5f))
//            setShowCropGrid(true)
//        }
//
//        UCrop.of(sourceUri, destinationUri)
//            .withAspectRatio(4f, 5f)
//            .withOptions(options)
//            .start(this)
//    }

    private val cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val resultUri = data?.let { UCrop.getOutput(it) }
            resultUri?.let {
                ivImage.setImageURI(it)
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

                Log.d("SaveImage", "Saved image path: ${file.absolutePath}")
            }
            bitmap.recycle()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

//    private fun savePhotoToInternalStorage(uri: Uri): Boolean {
//        return try {
//            val inputStream = requireContext().contentResolver.openInputStream(uri)
//            val bitmap = BitmapFactory.decodeStream(inputStream)
//
//            val filename = "profile_${System.currentTimeMillis()}.png"
//
//
//            openFileOutput("$filename.jpg", MODE_PRIVATE).use { stream ->
//                if(!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
//                    throw IOException("Couldn't save bitmap.")
//                }
//
//                Log.d("SaveImage", "Saved image path: ${stream.absolutePath}")
//            }
//
//            true
//        } catch (e: Exception) {
//            e.printStackTrace()
//            false
//        }
//    }

    private fun areFieldsEmpty() : Boolean {
        var isEmpty = false
        val errorMsg = "This Field Is Required"

        if(etName.isFieldEmptyOrNull()) {
            tilName.error = errorMsg
            isEmpty = true
        }

        if(etDate.isFieldEmptyOrNull()) {
            tilDate.error = errorMsg
            isEmpty = true
        } else {
            tilDate.error = null
        }

        if(etProgram.isFieldEmptyOrNull()) {
            tilProgram.error = errorMsg
            isEmpty = true
        }

        if(etSchool.isFieldEmptyOrNull()) {
            tilSchool.error = errorMsg
            isEmpty = true
        }

        return isEmpty
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val container = requireActivity().findViewById<View>(R.id.fullscreen_overlay_container)
        container.visibility = View.GONE
        _binding = null

        Log.d("Profile", "I am destroyed")
    }
}