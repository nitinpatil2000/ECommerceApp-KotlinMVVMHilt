package com.courses.ecommerceapp.dialog

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.courses.ecommerceapp.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

//we can used this dialog in multiple frag so we used Fragment
fun Fragment.setUpBottomSheetDialog(
    sendOnClick: (String) -> Unit
) {


    val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogStyle)
    val view = layoutInflater.inflate(R.layout.forgot_password_dialog, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val edtEmail = view.findViewById<EditText>(R.id.edtResetPassword)
    val btnSend = view.findViewById<Button>(R.id.btnResetPassword)
    val btnCancel = view.findViewById<Button>(R.id.btnCancel)

    btnSend.setOnClickListener {
        val forgotEmail = edtEmail.text.toString().trim()
        sendOnClick(forgotEmail)
        dialog.dismiss()
    }

    btnCancel.setOnClickListener {
        dialog.dismiss()
    }
}