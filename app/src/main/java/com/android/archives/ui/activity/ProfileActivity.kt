package com.android.archives.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.android.archives.R
import com.android.archives.utils.isFieldEmptyOrNull
import com.android.archives.utils.smoothTextChangeAnimation
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.yalantis.ucrop.UCrop
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileActivity : AppCompatActivity() {
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


    private val pickerTag = "DATE PICKER"
    private var existingPicker: Fragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

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

        toolBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        etDate.setOnClickListener {
            showDatePicker()
        }

        btnSave.setOnClickListener {
            if(areFieldsEmpty()) return@setOnClickListener
            Toast.makeText(this, "Fields Input Correct", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePicker() {
        if (existingPicker == null) {
            datePicker.show(supportFragmentManager, pickerTag)
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = convertMillisToDateString(selection)
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

    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            resultUri?.let {
                ivImage.setImageURI(it)
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Log.e("Crop Error", "Cropping Image Failed: $cropError")
        }
    }

    private fun startUCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "profile_picture_${System.currentTimeMillis()}.jpg"))

        val options = UCrop.Options().apply {
            setToolbarTitle("Crop Image")
            setFreeStyleCropEnabled(false)
            setAspectRatioOptions(0, com.yalantis.ucrop.model.AspectRatio("4:5", 4f, 5f))
            setShowCropGrid(true)
            setCompressionFormat(Bitmap.CompressFormat.PNG)
        }

        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(4f, 5f)
            .withOptions(options)
            .start(this)
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