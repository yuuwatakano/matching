package jp.techacademy.yuuwa.takano.matching

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
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
import java.io.ByteArrayOutputStream

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mCreateAccountListener: OnCompleteListener<AuthResult>
    private lateinit var mLoginListener: OnCompleteListener<AuthResult>
    private lateinit var mDataBaseReference: DatabaseReference
    companion object {
        private val PERMISSIONS_REQUEST_CODE = 100
        private val CHOOSER_REQUEST_CODE = 100
    }
    // アカウント作成時にフラグを立て、ログイン処理後に名前をFirebaseに保存する
    private var mIsCreateAccount = false
    private var mPictureUri: Uri? = null


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSER_REQUEST_CODE) {

            if (resultCode != Activity.RESULT_OK) {
                if (mPictureUri != null) {
                    contentResolver.delete(mPictureUri!!, null, null)
                    mPictureUri = null
                }
                return
            }

            // 画像を取得
            val uri = if (data == null || data.data == null) mPictureUri else data.data

            // URIからBitmapを取得する
            val image: Bitmap
            try {
                val contentResolver = contentResolver
                val inputStream = contentResolver.openInputStream(uri!!)
                image = BitmapFactory.decodeStream(inputStream)
                inputStream!!.close()
            } catch (e: Exception) {
                return
            }

            // 取得したBimapの長辺を500ピクセルにリサイズする
            val imageWidth = image.width
            val imageHeight = image.height
            val scale = Math.min(500.toFloat() / imageWidth, 500.toFloat() / imageHeight) // (1)

            val matrix = Matrix()
            matrix.postScale(scale, scale)

            val resizedImage = Bitmap.createBitmap(image, 0, 0, imageWidth, imageHeight, matrix, true)

            // BitmapをImageViewに設定する
            imageView.setImageBitmap(resizedImage)

            mPictureUri = null
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        var addresstext = "未選択"
        var genretext   = "未選択"
        var skilltext   = "未選択"
        var bitMap = ""


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
        imageView.setOnClickListener { v ->
            // パーミッションの許可状態を確認する
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // 許可されている
                    showChooser()
                } else {
                    // 許可されていないので許可ダイアログを表示する
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PERMISSIONS_REQUEST_CODE
                    )
                }
            } else {
                showChooser()
            }


        }
        // ログイン処理のリスナー
        mLoginListener = OnCompleteListener { task ->
            if (task.isSuccessful) {
                // 成功した場合
                val user = mAuth.currentUser
                val local = "Local"
                val localRef = mDataBaseReference.child(AccountPATH).child(local).child(addresstext).child(genretext).child(user!!.uid)
                val all = "all"
                val allRef = mDataBaseReference.child(AccountPATH).child(all).child(user!!.uid)




                if (mIsCreateAccount) {
                    val drawable = imageView.drawable as? BitmapDrawable

                    // 添付画像が設定されていれば画像を取り出してBASE64エンコードする
                    if (drawable != null) {
                        val bitmap = drawable.bitmap
                        val baos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
                        bitMap = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
                    }
                        val bit = bitMap
                        val name = nameText.text.toString().trim()
//                        val profiledata = profileText.toString().trim()
                        val twitterid = twitter_Text.text.toString().trim()
                        val soundcloud = soundcloudID_Text.text.toString().trim()
                        val instagram = instagram_Text.text.toString().trim()

                        val data = HashMap<String, String>()
                        data["name"] = name
//                        data["profile"] = profiledata
                        data["address"] = addresstext
                        data["genre"] = genretext
                        data["skill"] = skilltext
                        data["id"] = user!!.uid
                        data["image"] = bit
                        data["twitter"] = twitterid
                        data["instagram"] = instagram
                        data["soundcloud"] = soundcloud
                        localRef.setValue(data)
                        allRef.setValue(data)
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


            if (email.length != 0 && password.length >= 6 && name.length != 0) {
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // ユーザーが許可したとき
                    showChooser()
                }
                return
            }
        }
    }

//ログイン・アカウント作成の処理-ここまで-
private fun showChooser() {
    // ギャラリーから選択するIntent
    val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
    galleryIntent.type = "image/*"
    galleryIntent.addCategory(Intent.CATEGORY_OPENABLE)

    // カメラで撮影するIntent
    val filename = System.currentTimeMillis().toString() + ".jpg"
    val values = ContentValues()
    values.put(MediaStore.Images.Media.TITLE, filename)
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    mPictureUri = contentResolver
        .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPictureUri)

    // ギャラリー選択のIntentを与えてcreateChooserメソッドを呼ぶ
    val chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.get_image))

    // EXTRA_INITIAL_INTENTSにカメラ撮影のIntentを追加
    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))

    startActivityForResult(chooserIntent, CHOOSER_REQUEST_CODE)
}

}