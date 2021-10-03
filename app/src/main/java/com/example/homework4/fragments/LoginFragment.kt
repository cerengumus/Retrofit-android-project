package com.example.homework4.fragments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.homework4.R
import com.example.homework4.base.BaseCallback
import com.example.homework4.model.User
import com.example.homework4.service.Request
import com.example.homework4.service.Response
import com.example.homework4.service.ServiceConnector
import com.patikadev.deneme1.utils.USER_TOKEN
import com.patikadev.deneme1.utils.changeStatusBarColor
import kotlinx.android.synthetic.main.fragment_login.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        changeStatusBarColor(R.color.zircon)

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        login.setOnClickListener{
            val emailToSend = email.text.toString()
            val passwordToSend = password.text.toString()

            if(wrongAdressImplementation(emailToSend, passwordToSend)){

                val loginRequest = Request(emailToSend, passwordToSend)

                ServiceConnector.restInterface.login(loginRequest).enqueue(object: BaseCallback<Response>(){

                    override fun onSuccess(loginResponse: Response) {
                        super.onSuccess(loginResponse)
                        val token: String? = loginResponse.token
                        User.getCurrentInstance().token = token
                        saveToken(token!!)
                        Toast.makeText(requireContext(), "Giriş Başarılı", Toast.LENGTH_LONG).show()
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)

                    }

                    override fun onFailure() {
                        super.onFailure()
                        Toast.makeText(requireContext(), "Giriş basarısız", Toast.LENGTH_LONG).show()
                    }

                })

            }
            else{
                Toast.makeText(requireContext(), "Yanlış girdiniz Lütfen kontrol ediniz !!", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun wrongAdressImplementation(
        email: String,
        password: String
    ): Boolean {

        var allFieldsAreValid = true

        if (email.isEmpty() || !isValidEmail(email)) allFieldsAreValid = false

        if (password.isEmpty() || password.length < 3) allFieldsAreValid = false

        return allFieldsAreValid
    }


    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun saveToken(token: String){
        val sharedPreferences = this.activity?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.apply {
            putString(USER_TOKEN, token)
        }?.apply()

    }



}
