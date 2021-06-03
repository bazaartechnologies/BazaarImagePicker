package com.example.imagepickerlibrary.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import com.example.imagepickerlibrary.R
import kotlinx.android.synthetic.main.dialog_progress.view.*

object D {
    fun showProgressDialog(
        context: Context,
        msg: String,
        isCancelable: Boolean = false
    ): Dialog {

        val v = View.inflate(context, R.layout.dialog_progress, null)

        val d = Dialog(context)
        d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        d.setContentView(v)
        d.setCancelable(isCancelable)

        v.tvMsg.text = msg

        return d
    }
}