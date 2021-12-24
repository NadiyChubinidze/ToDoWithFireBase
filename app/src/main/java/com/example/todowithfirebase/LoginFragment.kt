package com.example.todowithfirebase

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var logButton: Button
    private lateinit var regButton: Button
    private lateinit var login: EditText
    private lateinit var password: EditText
    private lateinit var alertDialog: AlertDialog.Builder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth

        val view = inflater.inflate(R.layout.fragment_login, container, false)
        alertDialog = AlertDialog.Builder(view.context)

        logButton = view.findViewById(R.id.fragment_loginregister_login)
        regButton = view.findViewById(R.id.fragment_loginregister_register)
        login = view.findViewById(R.id.fragment_loginregister_email)
        password = view.findViewById(R.id.fragment_loginregister_password)
        addListeners(view)
        return view
    }

    private fun addListeners(view: View){
        logButton.setOnClickListener {
            if(login.text.toString().isEmpty() or password.text.toString().isEmpty()) {
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage("Поля незаполнены")
                alertDialog.setPositiveButton("ОК") { dialog, _ ->
                    dialog.dismiss()
                }
                alertDialog.show()
            }
            else {
                val email = login.text.toString()
                val pswrd = password.text.toString()
                if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    signIn(email,pswrd,view)
                }
                else {
                    alertDialog.setTitle("Ошибка")
                    alertDialog.setMessage("Некорректный Email")
                    alertDialog.setPositiveButton("ОК"){dialog, _->
                        dialog.dismiss()
                    }
                    alertDialog.show()
                }
            }
        }
        regButton.setOnClickListener{
            if(login.text.toString().isEmpty() or password.text.toString().isEmpty()) {
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage("Поля незаполнены")
                alertDialog.setPositiveButton("ОК"){dialog, _->
                    dialog.dismiss()
                }
                alertDialog.show()
            }
            else {
                val email = login.text.toString()
                val pswrd = password.text.toString()
                if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    createAccount(email,pswrd)
                }
                else {
                    alertDialog.setTitle("Ошибка")
                    alertDialog.setMessage("Некорректный Email")
                    alertDialog.setPositiveButton("ОК"){dialog, _->
                        dialog.dismiss()
                    }
                    alertDialog.show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload();
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    successRegistration(requireActivity(),user!!)
                } else {
                    successRegistration(requireActivity(),null)
                }
            }
    }

    private fun signIn(email: String, password: String, view: View) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail:success")
                        view.findNavController().navigate(R.id.action_loginFragment_to_listFragment)
                    } else {
                        alertDialog.setTitle("Ошибка")
                        alertDialog.setMessage("Данный пользователь еще не зарегестрирован " +
                                                "или введеный пароль неверен")
                        alertDialog.setPositiveButton("ОК"){dialog, _->
                            dialog.dismiss()
                        }
                        alertDialog.show()
                    }
                }
        }

    private fun successRegistration(activity: FragmentActivity, user: FirebaseUser?) {
        if(user != null){
            alertDialog.setTitle("Успешно")
            alertDialog.setMessage("Пользователь зарегестрирован")
            alertDialog.setPositiveButton("ОК"){dialog, _->
                dialog.dismiss()
            }
            alertDialog.show()
        }
        else {
            alertDialog.setTitle("Ошибка")
            alertDialog.setMessage("Данный пользователь уже зарегестрирован")
            alertDialog.setPositiveButton("ОК"){dialog, _->
                dialog.dismiss()
            }
            alertDialog.show()
        }
    }


    private fun reload() {
        //.findNavController().navigate(R.id.action_loginFragment_to_listFragment)
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}

