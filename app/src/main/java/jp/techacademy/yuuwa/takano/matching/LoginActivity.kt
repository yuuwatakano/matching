package jp.techacademy.yuuwa.takano.matching

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mCreateAccountListener: OnCompleteListener<AuthResult>
    private lateinit var mLoginListener: OnCompleteListener<AuthResult>
    private lateinit var mDataBaseReference: DatabaseReference

    // アカウント作成時にフラグを立て、ログイン処理後に名前をFirebaseに保存する
    private var mIsCreateAccount = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        var addresstext = "未選択"
        var genretext   = "未選択"
        var skilltext   = "未選択"


        //所在地・ジャンル・スキルの選択-ここから-
        val addressspinner = findViewById<Spinner>(R.id.address_spinner)
        val genrespinner = findViewById<Spinner>(R.id.genre_spinner)
        val skillspinner = findViewById<Spinner>(R.id.skill_spinner)

        val addressadapter = ArrayAdapter.createFromResource(this, R.array.address_spinnerItems, android.R.layout.simple_spinner_item)
        val genreadapter = ArrayAdapter.createFromResource(this, R.array.genre_spinnerItems, android.R.layout.simple_spinner_item)
        val skilladapter = ArrayAdapter.createFromResource(this, R.array.skill_spinnerItems, android.R.layout.simple_spinner_item)

        addressadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genreadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        skilladapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        addressspinner.adapter = addressadapter
        genrespinner.adapter = genreadapter
        skillspinner.adapter = skilladapter

        addressspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // 項目が選択された時に呼ばれる
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                addresstext = parent?.selectedItem as String //所在地データ
                Log.v("test_address", addresstext)

            }

            // 基本的には呼ばれないが、何らかの理由で選択されることなく項目が閉じられたら呼ばれる
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        genrespinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // 項目が選択された時に呼ばれる
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                genretext = parent?.selectedItem as String //ジャンルデータ
                Log.v("test_genre", genretext)
            }

            // 基本的には呼ばれないが、何らかの理由で選択されることなく項目が閉じられたら呼ばれる
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        skillspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // 項目が選択された時に呼ばれる
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                skilltext = parent?.selectedItem as String //スキルデータ
                Log.v("test_skill", skilltext)

            }

            // 基本的には呼ばれないが、何らかの理由で選択されることなく項目が閉じられたら呼ばれる
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //所在地・ジャンル・スキルの選択-ここまで-





        //ログイン・アカウント作成の処理-ここから-

        mDataBaseReference = FirebaseDatabase.getInstance().reference

        // FirebaseAuthのオブジェクトを取得する
        mAuth = FirebaseAuth.getInstance()

        // アカウント作成処理のリスナー
        mCreateAccountListener = OnCompleteListener { task ->
            if (task.isSuccessful) {
                // 成功した場合
                // ログインを行う
                val email = emailText.text.toString()
                val password = passwordText.text.toString()
                login(email, password)
            } else {

                // 失敗した場合
                // エラーを表示する
                val view = findViewById<View>(android.R.id.content)
                Snackbar.make(
                    view,
                    getString(R.string.create_account_failure_message),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        // ログイン処理のリスナー
        mLoginListener = OnCompleteListener { task ->
            if (task.isSuccessful) {
                // 成功した場合
                val user = mAuth.currentUser
                val userRef = mDataBaseReference.child(AccountPATH).child(user!!.uid)

                if (mIsCreateAccount) {
                    val name = nameText.text.toString()
                    val lineid = lineid_Text.text.toString()
                    val data = HashMap<String, String>()
                    data["name"] = name
                    data["lineid"] = lineid
                    data["address"] = addresstext
                    data["genre"] = genretext
                    data["skill"] = skilltext

                    userRef.setValue(data)
                }
                // Activityを閉じる
                finish()

            } else {
                // 失敗した場合
                // エラーを表示する
                val view = findViewById<View>(android.R.id.content)
                Snackbar.make(view, getString(R.string.login_failure_message), Snackbar.LENGTH_LONG)
                    .show()

            }
        }

        // タイトルの設定
        title = getString(R.string.login_title)

        createButton.setOnClickListener { v ->
            // キーボードが出てたら閉じる
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            val email = emailText.text.toString()
            val password = passwordText.text.toString()
            val name = nameText.text.toString()
            val lineid = lineid_Text.text.toString()


            if (email.length != 0 && password.length >= 6 && name.length != 0 && lineid.length != 0) {
                // ログイン時に表示名を保存するようにフラグを立てる
                mIsCreateAccount = true
                createAccount(email, password)
            } else {
                // エラーを表示する
                Snackbar.make(v, getString(R.string.login_error_message), Snackbar.LENGTH_LONG)
                    .show()
            }
        }

        loginButton.setOnClickListener { v ->
            // キーボードが出てたら閉じる
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            val email = emailText.text.toString()
            val password = passwordText.text.toString()

            if (email.length != 0 && password.length >= 6) {
                // フラグを落としておく
                mIsCreateAccount = false

                login(email, password)
            } else {
                // エラーを表示する
                Snackbar.make(v, getString(R.string.login_error_message), Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun createAccount(email: String, password: String) {
        // アカウントを作成する
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(mCreateAccountListener)
    }

    private fun login(email: String, password: String) {
        // ログインする
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(mLoginListener)

    }
//ログイン・アカウント作成の処理-ここまで-

}