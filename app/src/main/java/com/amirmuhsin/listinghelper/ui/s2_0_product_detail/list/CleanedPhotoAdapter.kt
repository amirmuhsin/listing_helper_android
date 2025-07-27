import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amirmuhsin.listinghelper.domain.model.AddPhotoItemButton
import com.amirmuhsin.listinghelper.domain.model.PhotoItem
import com.amirmuhsin.listinghelper.domain.model.PhotoPair
import com.amirmuhsin.listinghelper.ui.s2_0_product_detail.list.AddPhotoItemLayout
import com.amirmuhsin.listinghelper.ui.s2_0_product_detail.list.CleanedPhotoItemLayout

class CleanedPhotoAdapter(
    private val context: Context,
    private val onPhotoClick: (PhotoPair) -> Unit,
    private val onAddClick: () -> Unit
): ListAdapter<PhotoItem, RecyclerView.ViewHolder>(DIFF) {

    companion object {

        private val DIFF = object: DiffUtil.ItemCallback<PhotoItem>() {
            override fun areItemsTheSame(a: PhotoItem, b: PhotoItem): Boolean {
                return when {
                    a is PhotoPair && b is PhotoPair -> a.internalId == b.internalId
                    a is AddPhotoItemButton && b is AddPhotoItemButton -> true
                    else -> false
                }
            }

            override fun areContentsTheSame(a: PhotoItem, b: PhotoItem): Boolean {
                return a == b
            }
        }

        private const val TYPE_PHOTO = 0
        private const val TYPE_ADD = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PhotoPair -> TYPE_PHOTO
            is AddPhotoItemButton -> TYPE_ADD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_PHOTO -> {
                val layout = CleanedPhotoItemLayout(context, onPhotoClick)
                return CleanedPhotoViewHolder(layout)
            }

            TYPE_ADD -> {
                val layout = AddPhotoItemLayout(context, onAddClick)
                return AddCleanedPhotoViewHolder(layout)
            }

            else -> error("Unknown view type")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is PhotoPair -> ((holder as CleanedPhotoViewHolder).layout as CleanedPhotoItemLayout).fillContent(item)
            is AddPhotoItemButton -> Unit
        }
    }

    inner class CleanedPhotoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val layout = itemView
    }

    inner class AddCleanedPhotoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val layout = itemView
    }
}