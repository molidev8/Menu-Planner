package es.uam.eps.tfg.menuPlanner.util

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import es.uam.eps.tfg.menuPlanner.R

class LoadingDialog(val activity: Activity) {

    private lateinit var dialog: AlertDialog

    fun startLoadingDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

        val inflater: LayoutInflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.loading_dialog, null))
        builder.setCancelable(false)

        dialog = builder.create()
        dialog.show()

    }

    fun endLoadingDialog() {
        dialog.dismiss()
    }


}