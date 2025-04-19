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
import androidx.lifecycle.lifecycleScope
import com.android.archives.R
import com.android.archives.databinding.FragmentProfileBinding
import com.android.archives.ui.event.UserEvent
import com.android.archives.ui.viewmodel.UserViewModel
import com.android.archives.utils.DateConverter
import com.android.archives.utils.collectLatestOnViewLifecycle
import com.android.archives.utils.isFieldEmptyOrNull
import com.android.archives.utils.smoothTextChangeAnimation
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import kotlin.properties.Delegates

@AndroidEntryPoint
class ProfileFragment : DialogFragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var onEvent: (UserEvent) -> Unit

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

    private var selectedDate by Delegates.notNull<Long>()
    private lateinit var pathFileLocation: String
    private var selectedUri: Uri? = null

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

        onEvent = userViewModel::onEvent

        binding.profileToolbar.setNavigationOnClickListener {
            dismiss()
        }

        return binding.root
    }

    // DIRI ANG ANIMATION : SUGOD & END
    override fun onStart() {
        super.onStart()

        dialog?.window?.setWindowAnimations(
            R.style.dialog_animation_enter_up);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.loadStateFromCurrentUser()
        loadProfile()

        val toolBar = view.findViewById<MaterialToolbar>(R.id.profile_toolbar)

        val tvName = view.findViewById<TextView>(R.id.profile_name)
        tvBirthday = view.findViewById(R.id.profile_birthday)
        val tvProgram = view.findViewById<TextView>(R.id.profile_program)
        val tvSchool = view.findViewById<TextView>(R.id.profile_school)
        ivImage = view.findViewById(R.id.profile_card_img)

        tilName = view.findViewById(R.id.layout_name)
        tilDate = view.findViewById(R.id.layout_birthday)
        tilProgram = view.findViewById(R.id.layout_program)
        tilSchool = view.findViewById(R.id.layout_school)

        etName = view.findViewById(R.id.edit_name)
        etDate = view.findViewById(R.id.edit_birthday)
        etProgram = view.findViewById(R.id.edit_program)
        etSchool = view.findViewById(R.id.edit_school)

        val btnSave = view.findViewById<Button>(R.id.profile_save_btn)

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
            val name = it.toString()
            tvName.smoothTextChangeAnimation(name)
            onEvent(UserEvent.SetUserName(name))
            tilName.error = null
        }

        etProgram.addTextChangedListener {
            val program = it.toString()
            tvProgram.smoothTextChangeAnimation(program)
            onEvent(UserEvent.SetProgram(program))
            tilProgram.error = null
        }

        etSchool.addTextChangedListener {
            val school = it.toString()
            tvSchool.smoothTextChangeAnimation(school)
            onEvent(UserEvent.SetSchool(school))
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
            if(areFieldsEmpty()) return@setOnClickListener

            selectedUri?.let { uri -> savePhotoToInternalStorage(uri) }

            lifecycleScope.launch {
                if(userViewModel.updateUser()) {
                    Log.d("Profile", "Updated User")
                    Toast.makeText(requireContext(), "Updated user profile", Toast.LENGTH_SHORT).show()
                }
                dismiss()
            }
        }
    }

    private fun showDatePicker() {
        if (existingPicker == null) {
            datePicker.show(parentFragmentManager, pickerTag)
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = DateConverter.convertMillisToDateString(selection)
            binding.editBirthday.setText(selectedDate)
            binding.profileBirthday.text = selectedDate
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

    private val cropImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val resultUri = data?.let { UCrop.getOutput(it) }
            resultUri?.let { uri ->
                ivImage.setImageURI(uri)
                selectedUri = uri
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

                collectLatestOnViewLifecycle(userViewModel.state) { state ->
                    val picture = state.pictureFilePath?.let { File(requireContext().filesDir, it) }

                    if(picture?.exists() == true) {
                        if (picture.delete()) {
                            Log.d("PictureFile", "Deleted previous image successfully")
                        } else {
                            Log.d("PictureFile", "Failed to deleted previous image")
                        }
                    } else {
                        Log.d("PictureFile", "File does not exist")
                    }
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
    }

    private fun loadProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            val state = userViewModel.state.first()
            selectedDate = state.birthday

            val fullName = state.fullName
            val birthday = DateConverter.convertMillisToDateString(state.birthday)
            val program = state.program
            val school = state.school

            binding.profileName.text = fullName
            binding.profileBirthday.text = birthday
            binding.profileProgram.text = program
            binding.profileSchool.text = school

            val imgFile = state.pictureFilePath?.let { File(it) }

            if (imgFile != null) {
                if (imgFile.exists()) {
                    Glide.with(requireContext())
                        .load(imgFile)
                        .into(binding.profileCardImg)
                }
            }

            binding.editName.setText(fullName)
            binding.editBirthday.setText(birthday)
            binding.editProgram.setText(program)
            binding.editSchool.setText(school)
        }
//
//        collectLatestOnViewLifecycle(userViewModel.state) { state ->
//            selectedDate = state.birthday
//
//            val fullName = state.fullName
//            val birthday = DateConverter.convertMillisToDateString(state.birthday)
//            val program = state.program
//            val school = state.school
//
//            binding.profileName.text = fullName
//            binding.profileBirthday.text = birthday
//            binding.profileProgram.text = program
//            binding.profileSchool.text = school
//
//            val imgFile = state.pictureFilePath?.let { File(it) }
//
//            if (imgFile != null) {
//                if (imgFile.exists()) {
//                    Glide.with(this)
//                        .load(imgFile)
//                        .into(binding.profileCardImg)
//                }
//            }
//
//            binding.editName.setText(fullName)
//            binding.editBirthday.setText(birthday)
//            binding.editProgram.setText(program)
//            binding.editSchool.setText(school)
//        }
    }
}