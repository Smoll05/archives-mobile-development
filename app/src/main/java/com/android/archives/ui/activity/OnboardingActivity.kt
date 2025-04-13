package com.android.archives.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.archives.R
import com.android.archives.data.db.ArchivesDatabase
import com.android.archives.data.event.UserEvent
import com.android.archives.ui.viewmodel.UserViewModel
import com.android.archives.utils.getContent
import com.android.archives.utils.isFieldEmptyOrNull
import com.android.archives.utils.smoothTextChangeAnimation
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OnboardingActivity : AppCompatActivity() {
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

    private var existingPicker: Fragment? = null
    private val pickerTag = "DATE PICKER"
    private lateinit var cachedDestinationUr: Uri

    private lateinit var userViewModel: UserViewModel
    private lateinit var onEvent: (UserEvent) -> Unit
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val userDao = ArchivesDatabase(this).userDao
        val viewModelProviderFactory = UserViewModel.UserViewModelProviderFactory(userDao)

        userViewModel = ViewModelProvider(this, viewModelProviderFactory)[UserViewModel::class.java]
        onEvent = userViewModel::onEvent

        onEvent(UserEvent.ShowForm)


        val toolBar = findViewById<MaterialToolbar>(R.id.profile_toolbar)

        val tvName = findViewById<TextView>(R.id.profile_name)
        tvBirthday = findViewById(R.id.profile_birthday)
        val tvProgram = findViewById<TextView>(R.id.profile_program)
        val tvSchool = findViewById<TextView>(R.id.profile_school)
        ivImage = findViewById(R.id.profile_card_img)

        tilName = findViewById(R.id.layout_name)
        tilDate = findViewById(R.id.layout_birthday)
        tilProgram = findViewById(R.id.layout_program)
        tilSchool = findViewById(R.id.layout_school)

        etName = findViewById(R.id.edit_name)
        etDate = findViewById(R.id.edit_birthday)
        etProgram = findViewById(R.id.edit_program)
        etSchool = findViewById(R.id.edit_school)

        val btnSave = findViewById<Button>(R.id.profile_save_btn)

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
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

        etDate.addTextChangedListener {
            tilDate.error = null
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

        toolBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        etDate.setOnClickListener {
            showDatePicker()
        }

        btnSave.setOnClickListener {
            if(areFieldsEmpty()) return@setOnClickListener

            onEvent(UserEvent.SetFullName(etName.getContent()))
            onEvent(UserEvent.SetProgram(etProgram.getContent()))
            onEvent(UserEvent.SetSchool(etSchool.getContent()))

            // SAVE USER TO DATABASE
            onEvent(UserEvent.SaveUser(this))

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun showDatePicker() {
        if (existingPicker == null) {
            datePicker.show(supportFragmentManager, pickerTag)
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = convertMillisToDateString(selection)
            etDate.setText(selectedDate)
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

    private fun convertMillisToDateString(millis: Long): String {
        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return sdf.format(Date(millis))
    }

    private fun convertDateStringToMillis(dateString: String): Long {
        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return try {
            val date: Date? = sdf.parse(dateString)
            date?.time ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

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
        cachedDestinationUr = Uri.fromFile(File(cacheDir, "profile_picture_${System.currentTimeMillis()}.png"))

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
            .getIntent(this)

        cropImageLauncher.launch(intent)
    }

    private fun savePhotoToInternalStorage(uri: Uri): Boolean {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            val filename = "profile_${System.currentTimeMillis()}.png"
            val file = File(filesDir, filename)

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
}