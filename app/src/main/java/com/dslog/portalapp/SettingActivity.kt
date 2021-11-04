package com.dslog.portalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    private var snapshotListener: ListenerRegistration? = null
    var mQuestionArrayList = ArrayList<Question>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

                 // 一つ前のリスナーを消す
                snapshotListener?.remove()

        // ログイン済みのユーザーを取得する
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            editButton.visibility = View.GONE
        } else {

            // 選択したジャンルにリスナーを登録する
            snapshotListener = FirebaseFirestore.getInstance()
                .collection(ContentsPATH)
                .whereEqualTo("uid", user.uid)
                .whereEqualTo("Authority", "admin")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        // 取得エラー
                        return@addSnapshotListener
                        Log.d("Setting", "error")
                    }
                    var questions = listOf<Question>()
                    val results = querySnapshot?.toObjects(FireStoreAuth::class.java)
                    results?.also {
                        questions = it.map { FireStoreAuth ->
                            Question(FireStoreAuth.Authority, FireStoreAuth.uid)
                        }

                        mQuestionArrayList.clear()
                        mQuestionArrayList.addAll(questions)

                        Log.d("MainActivity2", user.uid)
                    }

                    if (mQuestionArrayList.size > 0) {
                        Log.d("Setting", mQuestionArrayList.toString())
                        editButton.visibility = View.VISIBLE
                    }else{
                        Log.d("Setting", "NULL")
                        editButton.visibility = View.GONE
                    }



                }
        }

        // UIの初期設定
        title = getString(R.string.settings_titile)

        logoutButton.setOnClickListener { v ->
            FirebaseAuth.getInstance().signOut()
            Snackbar.make(v, getString(R.string.logout_complete_message), Snackbar.LENGTH_LONG).show()

            // ログイン済みのユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser

            // ログインしていなければログイン画面に遷移させる
            if (user == null) {
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
