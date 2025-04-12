package com.android.archives.ui.activity

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.archives.R
import com.android.archives.data.application.ArchivesApplication
import com.android.archives.data.model.Task
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TaskDetailViewActivity : AppCompatActivity() {
    lateinit var task: Task
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail_view)

        val app = application as ArchivesApplication

        intent?.let { it ->
            it.getIntExtra("task_id", 0).let {id ->
                task = app.taskList.find { it.taskId == id }!!
            }
        }

        val toolBar = findViewById<MaterialToolbar>(R.id.task_detail_toolbar)
        val tvTitle = findViewById<TextView>(R.id.task_detail_title)
        val tvDescription = findViewById<TextView>(R.id.task_detail_description)

        val btnEdit = findViewById<Button>(R.id.task_detail_edit)
        val btnDelete = findViewById<Button>(R.id.task_detail_delete)
        val tvMark = findViewById<TextView>(R.id.task_detail_mark)

        toolBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        tvTitle.text = task.title
        tvDescription.text = task.title

        btnEdit.setOnClickListener {
            Toast.makeText(this, "Edit", Toast.LENGTH_SHORT).show()
        }

        btnDelete.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setNeutralButton("Cancel") { _, _ -> }
                .setNegativeButton("Delete") { _, _ ->
                }
                .show()
                .apply {
                    getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                        ContextCompat.getColor(context, R.color.error)
                    )
                }
        }

        val spannableString = SpannableString("Mark As Complete")
        tvMark.text = spannableString

        tvMark.setOnClickListener {
            val spans = spannableString.getSpans(0, spannableString.length, StrikethroughSpan::class.java)

            if (spans.isEmpty()) {
                spannableString.setSpan(StrikethroughSpan(), 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                spannableString.removeSpan(spans[0])
            }

            tvMark.text = spannableString
        }
    }
}