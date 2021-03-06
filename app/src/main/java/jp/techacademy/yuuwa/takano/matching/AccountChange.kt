package jp.techacademy.yuuwa.takano.matching

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView.BufferType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_account_change.*
import java.io.ByteArrayOutputStream


class AccountChange : AppCompatActivity() {
    //権限やデータベースリファレンスの初期化
    private lateinit var mDataBaseReference: DatabaseReference
    private var mPictureUri: Uri? = null

    companion object {
        private val PERMISSIONS_REQUEST_CODE = 100
        private val CHOOSER_REQUEST_CODE = 100
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AccountChange.CHOOSER_REQUEST_CODE) {

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

            val resizedImage = Bitmap.createBitmap(
                image,
                0,
                0,
                imageWidth,
                imageHeight,
                matrix,
                true
            )

            // BitmapをImageViewに設定する
            change_imageView.setImageBitmap(resizedImage)

            mPictureUri = null
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_change)
        window.statusBarColor = ContextCompat.getColor(this, R.color.transparent_status_bar)

        var bitMap = ""
        //ログインアカウントデータ受け
        val address = intent.getStringExtra("loginaddress")
        val name = intent.getStringExtra("loginname")
        val profile = intent.getStringExtra("loginprofile")
        val genre = intent.getStringExtra("logingenre")
        val skill = intent.getStringExtra("loginskill")
        val id = intent.getStringExtra("loginid")
        val twitterid = intent.getStringExtra("logintwitterid")
        val instagramid = intent.getStringExtra("logininstagramid")
        val soundcloudid = intent.getStringExtra("loginsoundcloudid")
        val icon = intent.getByteArrayExtra("loginimage")

        change_nameText.setText(name.toString(), BufferType.NORMAL)
        change_profileText.setText(profile.toString(), BufferType.NORMAL)
        change_twitter_Text.setText(twitterid.toString(), BufferType.NORMAL)
        change_instagram_Text.setText(instagramid.toString(), BufferType.NORMAL)
        change_soundcloudID_Text.setText(soundcloudid.toString(), BufferType.NORMAL)


        if (icon!!.isNotEmpty()) {
            val image = BitmapFactory.decodeByteArray(icon, 0, icon.size).copy(
                Bitmap.Config.ARGB_8888,
                true
            )
            val imageView = change_imageView as ImageView
            imageView.setImageBitmap(image)
        } else {
            val imageView = change_imageView as ImageView
            imageView.setImageBitmap(null)
        }

        saveButton.setOnClickListener {//保存ボタン
            val user = FirebaseAuth.getInstance().currentUser
            val local = "Local"

            mDataBaseReference = FirebaseDatabase.getInstance().reference
            val localRef = mDataBaseReference.child(AccountPATH).child(local).child(address).child(
                genre
            ).child(user!!.uid)
            val all = "all"
            val allRef = mDataBaseReference.child(AccountPATH).child(all).child(user!!.uid)

            val drawable = change_imageView.drawable as? BitmapDrawable

            // 添付画像が設定されていれば画像を取り出してBASE64エンコードする
            if (drawable != null) {
                val bitmap = drawable.bitmap
                if (bitmap != null) {
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
                    bitMap = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
                }
            }

            val bit = bitMap
            var namedata = change_nameText.text.toString().trim()
            var profiledata = change_profileText.text.toString().trim()
            var twitterdata = change_twitter_Text.text.toString().trim()
            var soundclouddata = change_soundcloudID_Text.text.toString().trim()
            var instagramdata = change_instagram_Text.text.toString().trim()

            if (namedata.isNotEmpty()) {
                val sender: MutableMap<String, Any> = HashMap()
                sender["name"] = namedata
                sender["image"] = bit
                sender["profile"] = profiledata
                sender["twitter"] = twitterdata
                sender["instagram"] = instagramdata
                sender["soundcloud"] = soundclouddata
                mDataBaseReference.child(AccountPATH).child(local).child(address).child(genre)
                    .child(
                        user!!.uid
                    ).updateChildren(sender)
                mDataBaseReference.child(AccountPATH).child(all).child(id).updateChildren(sender)
                Toast.makeText(this, "編集内容を保存", Toast.LENGTH_SHORT).show()

                finish()
            } else {
                Toast.makeText(this, "名前を入力してください", Toast.LENGTH_SHORT).show()

            }
        }

        change_imageView.setOnClickListener { v ->
            // パーミッションの許可状態を確認する
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // 許可されている
                    showChooser()
                } else {
                    // 許可されていないので許可ダイアログを表示する
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        AccountChange.PERMISSIONS_REQUEST_CODE
                    )
                }
            } else {
                showChooser()
            }
        }

        cancelButton.setOnClickListener {//編集内容破棄ボタン
            Toast.makeText(this, "編集内容を破棄", Toast.LENGTH_SHORT).show()
            finish()
        }

        back_buttom.setOnClickListener {//戻るボタン
            finish()
        }

    }

    override fun onResume() {
        super.onResume()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AccountChange.PERMISSIONS_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // ユーザーが許可したとき
                    showChooser()
                }
                return
            }
        }
    }

    private fun showChooser() {//アイコン写真を選択し設定する関数
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

        startActivityForResult(chooserIntent, AccountChange.CHOOSER_REQUEST_CODE)
    }
}