import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.archives.R
import com.android.archives.data.model.Upload

class FileRecyclerAdapter(
    private val files: MutableList<Upload>,
    private val onDelete: (Upload) -> Unit
) : RecyclerView.Adapter<FileRecyclerAdapter.FileViewHolder>() {

    inner class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fileName: TextView = view.findViewById(R.id.fileName)
        val fileIcon: ImageView = view.findViewById(R.id.fileIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.listview_file_item, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]
        holder.fileName.text = file.name

        val lowerName = file.name.lowercase()
        val iconRes = when {
            lowerName.endsWith(".pdf") -> R.drawable.ic_pdf
            lowerName.endsWith(".doc") || lowerName.endsWith(".docx") -> R.drawable.ic_word
            lowerName.endsWith(".ppt") || lowerName.endsWith(".pptx") -> R.drawable.ic_ppt
            else -> R.drawable.ic_file
        }
        holder.fileIcon.setImageResource(iconRes)
    }

    override fun getItemCount(): Int = files.size

    fun deleteItem(position: Int) {
        onDelete(files[position])
        notifyItemRemoved(position)
    }
}
