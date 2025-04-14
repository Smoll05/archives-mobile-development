package com.android.archives.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.android.archives.R
import com.android.archives.application.ArchivesApplication
import com.android.archives.data.model.Task
import com.android.archives.ui.fragment.EmojiPickerDialogueFragment
import com.android.archives.utils.getContent
import com.android.archives.utils.isFieldEmptyOrNull
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditTaskActivity : AppCompatActivity(), EmojiPickerDialogueFragment.EmojiPickerListener {
    lateinit var etTaskTitle : EditText
    lateinit var etTaskDescription : EditText
    lateinit var btnEmoji : Button
    lateinit var tilTitle : TextInputLayout
    lateinit var tilDesc : TextInputLayout
    lateinit var errorEmoji : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        val btnAdd = findViewById<Button>(R.id.edit_task_button)
        etTaskTitle = findViewById(R.id.edit_task_title)
        etTaskDescription = findViewById(R.id.edit_task_desc)
        btnEmoji = findViewById(R.id.edit_task_emoji_button)

        tilTitle = findViewById(R.id.edit_task_layout)
        tilDesc = findViewById(R.id.edit_task_desc_layout)

        errorEmoji = findViewById(R.id.error_emoji_btn)
        errorEmoji.visibility = TextView.INVISIBLE

        val app = application as ArchivesApplication

        val toolBar = findViewById<MaterialToolbar>(R.id.edit_task_dialog_toolbar)
        toolBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        btnEmoji.setOnClickListener {
            val emojiPickerDialogFragment = EmojiPickerDialogueFragment()
            emojiPickerDialogFragment.show(supportFragmentManager, "emoji_picker_dialog")
        }

        btnAdd.setOnClickListener {
            if(areFieldsEmpty()) return@setOnClickListener
            Toast.makeText(this, "Fields Input Correct", Toast.LENGTH_SHORT).show()

            app.taskList.add(
                Task(
                99,
                etTaskTitle.getContent(),
                etTaskDescription.getContent(),
                btnEmoji.getContent(),
                false
            )
            )

            startActivity(
                Intent(this, MainActivity::class.java)
            )

            finish()
        }

        etTaskTitle.addTextChangedListener {
            tilTitle.error = null
        }

        etTaskDescription.addTextChangedListener {
            tilDesc.error = null
        }
    }

    private fun areFieldsEmpty() : Boolean {
        var isEmpty = false

        val errorMsg = "This Field Is Required"

        if(etTaskTitle.isFieldEmptyOrNull()) {
            tilTitle.error = errorMsg
            isEmpty = true
        }

        if(etTaskDescription.isFieldEmptyOrNull()) {
            tilDesc.error = errorMsg
            isEmpty = true
        }

        if(btnEmoji.text.isNullOrEmpty()) {
            errorEmoji.visibility = TextView.VISIBLE
            isEmpty = true
        }

        return isEmpty
    }

    override fun onEmojiPicked(emoji: String) {
        btnEmoji.text = emoji
        btnEmoji.textSize = 40f
        errorEmoji.visibility = TextView.INVISIBLE
    }
}