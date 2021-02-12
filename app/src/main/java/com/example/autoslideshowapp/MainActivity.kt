package com.example.autoslideshowapp

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    var mCursor: Cursor? = null

    private var mTimer: Timer? = null

    private var mHandler = Handler()


    private val PERMISSIOM_REQUEST_CODE = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIOM_REQUEST_CODE
                )
            }
        } else {
            getContentsInfo()
        }

        start_button.setOnClickListener(this)
        next_button.setOnClickListener(this)
        return_button.setOnClickListener(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permission: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIOM_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                } else {
                start_button.isClickable = false
                return_button.isClickable = false
                next_button.isClickable = false
            }
        }
    }

    private fun getContentsInfo() {

        val resolver = contentResolver
        mCursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if (mCursor!!.moveToFirst()) {
            val fieldIndex = mCursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id = mCursor!!.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            imageView.setImageURI(imageUri)
        }
    }

    override fun onClick(v: View) {

        when(v.id) {
            R.id.return_button -> {
                if (mCursor!!.moveToPrevious()) {
                    val fieldIndex = mCursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = mCursor!!.getLong(fieldIndex)
                    val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    imageView.setImageURI(imageUri)
                } else {
                    mCursor!!.moveToLast()
                    val fieldIndex = mCursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = mCursor!!.getLong(fieldIndex)
                    val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    imageView.setImageURI(imageUri)
                }
            }

            R.id.start_button -> {
                if (mTimer == null) {
                    start_button.text = "停止"
                    return_button.isClickable = false
                    next_button.isClickable = false
                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            mHandler.post {
                                if (mCursor!!.moveToNext()) {
                                    val fieldIndex = mCursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                                    val id = mCursor!!.getLong(fieldIndex)
                                    val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                                    imageView.setImageURI(imageUri)
                                } else {
                                    mCursor!!.moveToFirst()
                                    val fieldIndex = mCursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                                    val id = mCursor!!.getLong(fieldIndex)
                                    val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                                    imageView.setImageURI(imageUri)
                                }
                            }
                        }
                    }, 2000, 2000)
                } else if(mTimer != null) {
                    mTimer!!.cancel()
                    mTimer = null
                    start_button.text = "再生"
                    return_button.isClickable = true
                    next_button.isClickable = true
                }

                if (mTimer != null) {

                } else if (mTimer == null) {

                }
            }

            R.id.next_button -> {
                if (mCursor!!.moveToNext()) {
                    val fieldIndex = mCursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = mCursor!!.getLong(fieldIndex)
                    val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    imageView.setImageURI(imageUri)
                } else {
                    mCursor!!.moveToFirst()
                    val fieldIndex = mCursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = mCursor!!.getLong(fieldIndex)
                    val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    imageView.setImageURI(imageUri)
                }
            }
        }
        return
    }
}