package com.example.studentapp.accountFragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.studentapp.R
import com.example.studentapp.databinding.FragmentAccountPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


@DelicateCoroutinesApi
class AccountPageFragment : Fragment() {

    private lateinit var binding: FragmentAccountPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var docId: String
    private var selectPhotoUri: Uri? = null
    private lateinit var changeDialog: View
    private lateinit var imgUri: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        readDataFirestore()
        binding = FragmentAccountPageBinding.inflate(inflater)
        return binding.root
    }//


    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileEditBtn.setOnClickListener {
            changeDialog =
                LayoutInflater
                    .from(context)
                    .inflate(R.layout.edit_profil_dialog, null)

            val mBuilder = AlertDialog.Builder(context)
                .setView(changeDialog)
                .setTitle("Թարմացնել")
            val mAlertDialog = mBuilder.show()

            changeDialog.findViewById<Button>(R.id.cancelChange).setOnClickListener {
                mAlertDialog.dismiss()
            }//cancle

            changeDialog.findViewById<ImageView>(R.id.changeImage).setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 0)
            }//change image

            changeDialog.findViewById<Button>(R.id.saveChange).setOnClickListener {
                val hashMap = hashMapOf<String, Any>()

                if (checkImage) {
                    binding.updateProgressBar.visibility = View.VISIBLE
                    uploadImageToFirebaseStorage()
                    readDataFirestore()
                    mAlertDialog.dismiss()
                    binding.updateProgressBar.visibility = View.GONE
                }
                val newName = changeDialog.findViewById<EditText>(R.id.changeName).text.toString()
                if (newName.isNotEmpty()) {
                    hashMap["name"] = newName
                    updateUserInfo(hashMap)
                    readDataFirestore()
                    binding.updateProgressBar.visibility = View.GONE
                    mAlertDialog.dismiss()
                }
            }//save


        }// profileEditBtn
    }// onViewCreated()



    private fun uploadImageToFirebaseStorage() {
        if (selectPhotoUri == null) return

        val ref = FirebaseStorage
            .getInstance()
            .getReference("/images/${UUID.randomUUID()}.png")

        ref.putFile(selectPhotoUri!!)
            .addOnSuccessListener {
                imgUri = it.uploadSessionUri.toString()
                val hashMap = hashMapOf<String, Any>()
                hashMap["userURLtoImage"] = imgUri
                updateUserInfo(hashMap)
                //Toast.makeText(context, imgUri, Toast.LENGTH_LONG).show()
            }
    }//-----

    private var checkImage = false
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectPhotoUri = data.data
            val bitmap =
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            changeDialog.findViewById<ImageView>(R.id.changeImage)
                .setBackgroundDrawable(bitmapDrawable)
            checkImage = true
        }
    }


    private fun updateUserInfo(newUserData: Map<String, Any>) =
        CoroutineScope(Dispatchers.IO).launch {
            val db = Firebase.firestore
            db.collection("users")
                .document(docId)
                .set(newUserData, SetOptions.merge())
        }


    private fun readDataFirestore() {
        val db = Firebase.firestore
        Firebase.auth.currentUser?.let {
            db.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if ((document.get("idUser")
                                .toString()) == (FirebaseAuth.getInstance().uid).toString()
                        ) {
                            binding.profileName.text = document.get("name").toString()
                            binding.scoreProfile.text = document.get("score").toString()
                            binding.emailProfil.text = document.get("email").toString()
                            docId = document.id

                            context?.let { it1 ->
                                Glide.with(it1)
                                    .load(document.get("userURLtoImage").toString())
                                    .placeholder(R.drawable.ic_person_gray)
                                    .into(binding.personImage)
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents.", exception)
                }
        }
    }// readDataFirestore()

}//class
