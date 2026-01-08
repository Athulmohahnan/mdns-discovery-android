package com.example.mdnsdiscovery.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mdnsdiscovery.R
import com.example.mdnsdiscovery.auth.GoogleAuthManager
import com.example.mdnsdiscovery.data.local.AuthPreferences
import com.example.mdnsdiscovery.ui.home.HomeActivity
import com.example.mdnsdiscovery.util.NetworkUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class LoginActivity : AppCompatActivity() {

    private lateinit var authManager: GoogleAuthManager
    private lateinit var authPrefs: AuthPreferences
    private lateinit var btnLogin: Button
    private lateinit var loginLoader: ProgressBar

    private val signInLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        authManager = GoogleAuthManager(this)
        authPrefs = AuthPreferences(this)
        btnLogin = findViewById(R.id.btnLogin)
        loginLoader = findViewById(R.id.loginLoader)
        checkSilentLogin()
        btnLogin.setOnClickListener {
            showLoading()
            signInLauncher.launch(authManager.getSignInIntent())
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            authPrefs.saveToken(account.idToken)
            navigateToHome()
        } catch (e: ApiException) {
            hideLoading()
            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkSilentLogin() {
        val token = authPrefs.getToken()
        val account = authManager.getLastAccount()

        if (token != null && account != null) {
            // Token exists → try silent auth
            if (NetworkUtil.isNetworkAvailable(this)) {
                navigateToHome()
            } else {
                hideLoading()
                forceLogout()
            }
        } else {
            hideLoading()
        }
    }

    private fun forceLogout() {
        authManager.signOut {
            authPrefs.clear()
        }
    }

    private fun navigateToHome() {
        hideLoading()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoading() {
        btnLogin.isEnabled = false
        loginLoader.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        btnLogin.isEnabled = true
        loginLoader.visibility = View.GONE
    }
}
