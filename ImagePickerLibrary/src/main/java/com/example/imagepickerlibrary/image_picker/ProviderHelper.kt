package com.example.imagepickerlibrary.image_picker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.imagepickerlibrary.image_picker.model.Picker
import com.example.imagepickerlibrary.image_picker.ui.ImagePickerActivity
import com.example.imagepickerlibrary.util.ProgressDialog
import com.example.imagepickerlibrary.util.FileUriUtils
import com.example.imagepickerlibrary.util.FileUtil
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ProviderHelper(private val activity: AppCompatActivity) {

    /**
     * How many image user can pick
     * */
    private val isMultiSelection: Boolean
    private val isCropEnabled: Boolean
    private val isCropOvalEnabled: Boolean
    private val isToCompress: Boolean

    // Ucrop & compress
    private val mMaxWidth: Int
    private val mMaxHeight: Int
    private val mCropAspectX: Float
    private val mCropAspectY: Float
    private val mGalleryMimeTypes: Array<String>

    init {
        val bundle = activity.intent.extras!!

        isMultiSelection = bundle.getBoolean(ImagePicker.EXTRA_MULTI_SELECTION, false)

        // Cropping
        isCropEnabled = bundle.getBoolean(ImagePicker.EXTRA_CROP, false)
        isCropOvalEnabled = bundle.getBoolean(ImagePicker.EXTRA_CROP_OVAL, false)
        isToCompress = bundle.getBoolean(ImagePicker.EXTRA_IS_TO_COMPRESS, false)

        // Get Max Width/Height parameter from Intent
        mMaxWidth = bundle.getInt(ImagePicker.EXTRA_MAX_WIDTH, 0)
        mMaxHeight = bundle.getInt(ImagePicker.EXTRA_MAX_HEIGHT, 0)

        // Get Crop Aspect Ratio parameter from Intent
        mCropAspectX = bundle.getFloat(ImagePicker.EXTRA_CROP_X, 0f)
        mCropAspectY = bundle.getFloat(ImagePicker.EXTRA_CROP_Y, 0f)

        mGalleryMimeTypes = bundle.getStringArray(ImagePicker.EXTRA_MIME_TYPES) as Array<String>


    }

    fun isToCompress() = isToCompress

    fun getGalleryMimeTypes() = mGalleryMimeTypes

    fun getMultiSelection() = isMultiSelection

    fun setResultAndFinish(images: ArrayList<Picker>?) {
        val i = Intent().apply {
            putParcelableArrayListExtra(ImagePicker.EXTRA_SELECTED_IMAGES, images)
        }
        Log.d("CurrentDateTag Finish", Date().toString())
        activity.setResult(Activity.RESULT_OK, i)
        activity.finish()
    }

    private suspend fun prepareImage(uri: Uri) = withContext(Dispatchers.IO) {
        Log.d("CurrentDateTag", Date().toString())
        return@withContext if (isToCompress) {

            val file = FileUtil.compressImage(
                    activity.baseContext,
                    uri,
                    mMaxWidth.toFloat(),
                    mMaxHeight.toFloat()
            )

            val name = file.name
            Picker(name, Uri.fromFile(file), file)
        } else {
            val file = File(FileUriUtils.getRealPath(activity.baseContext, uri) ?: "")
            val name = file.name
            Picker(name, uri, file)
        }
    }

    suspend fun performGalleryOperationForSingleSelection(uri: Uri){
        when {
            isCropEnabled -> {
                val croppedFile = FileUtil.getImageOutputDirectory(activity.baseContext)
                startCrop(uri, Uri.fromFile(croppedFile))
            }
            else -> {
                val image = prepareImage(uri)
                val images = ArrayList<Picker>()
                images.add(image)

                // if compress is true then delete the saved image
                if (isToCompress) delete(uri)

                setResultAndFinish(images)
            }
        }
    }

    suspend fun performGalleryOperationForMultipleSelection(uris: List<Uri>): ArrayList<Picker> {
        val images = ArrayList<Picker>()

        uris.forEach { uri ->
            val image = prepareImage(uri)
            images.add(image)
        }
        //setResultAndFinish(images)
        return images
    }

    suspend fun performCameraOperation(savedUri: Uri) {
        when {
            isCropEnabled -> {
                val croppedFile = FileUtil.getImageOutputDirectory(activity.baseContext)
                startCrop(savedUri, Uri.fromFile(croppedFile))
            }
            else -> {
                val image = prepareImage(savedUri)
                val images = ArrayList<Picker>()
                images.add(image)

                // if compress is true then delete the saved image
                if (isToCompress) delete(savedUri)

                setResultAndFinish(images)
            }
        }
    }

    private fun startCrop(sourceUri: Uri, croppedUri: Uri) {
        val uCrop = UCrop.of(sourceUri, croppedUri)
        val options = UCrop.Options()
        options.setCircleDimmedLayer(isCropOvalEnabled)
        options.setShowCropGrid(false)
        options.setShowCropFrame(false)
        options.setHideBottomControls(true)
        options.withAspectRatio(1f, 1f)
        uCrop.withOptions(options)

        if (mCropAspectX > 0 && mCropAspectY > 0) {
            uCrop.withAspectRatio(mCropAspectX, mCropAspectY)
        }

        if (mMaxWidth > 0 && mMaxHeight > 0) {
            uCrop.withMaxResultSize(mMaxWidth, mMaxHeight)
        }

        uCrop.start(activity, UCrop.REQUEST_CROP)
    }

    private suspend fun delete(uri: Uri) {
        FileUtil.delete(File(uri.path))
    }

    suspend fun handleUCropResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?,
            captureImageUri: Uri?
    ) {

        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP && data != null) {
            // Deleting Captured image
            captureImageUri?.let {
                delete(it)
            }
            // Getting the cropped image
            val resultUri = UCrop.getOutput(data)
            val activity = activity as ImagePickerActivity
            val d = ProgressDialog.showProgressDialog(activity, "Processing")
            d.show()
            val image = prepareImage(resultUri!!)
            val images = ArrayList<Picker>()
            images.add(image)
            d.hide()
            setResultAndFinish (images)

        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            setResultAndFinish(null)
        }
    }
}