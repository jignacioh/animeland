package com.clearmind.animeland.home

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.clearmind.animeland.R
import com.clearmind.animeland.core.di.AppDatabase
import com.clearmind.animeland.model.Place
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_upload.*
import java.util.*

class UploadFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference
    private var db: AppDatabase?=  null


    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null

    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    companion object {
        fun newInstance(): UploadFragment = UploadFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_upload, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initValues()
        initListener()

    }
    private fun initValues(){
        auth = FirebaseAuth.getInstance()
        ref = FirebaseDatabase.getInstance().reference.child("places").child(auth.uid!!)

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = firebaseStore!!.reference
    }

    private fun initListener(){
        buttonSelect.setOnClickListener {
            selectImageFromGallery()
        }
        buttonUpload.setOnClickListener {
            uploadImageFireStorage()
        }
    }

    private fun uploadImageFireStorage() {

        val progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Uploading Image")
        progressDialog.show()

        if(filePath != null){
            val ref = storageReference?.child("uploads/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)

            uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.printStackTrace()
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                progressDialog.dismiss()
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    Toast.makeText(activity, "ok", Toast.LENGTH_LONG).show()
                    //addUploadRecordToDb(downloadUri.toString())
                    saveInDatabase(downloadUri!!.toString())
                } else {
                    Toast.makeText(activity, "error", Toast.LENGTH_LONG).show()
                }
            }?.addOnFailureListener{
                progressDialog.dismiss()
                //Failure
                Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(activity, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveInDatabase(path: String?) {
        var place = Place()
        place.Description=imageNameEditText.text.toString()
        place.photos.add(path!!)
        place.Id= 0

        var reff = FirebaseDatabase.getInstance()
            .reference.child("places")
            .child(auth.uid!!)
            .child(UUID.randomUUID().toString())
            .setValue(place)
    }

    private fun selectImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        activity!!.startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    fun showImageGallery(imagePath : Uri){
        filePath=imagePath
        Picasso.get().load(filePath).fit().centerCrop().into(imageView)
    }

    /*
    private fun addUploadRecordToDb(uri: String){
        val db = FirebaseFirestore.getInstance()

        val data = HashMap<String, Any>()
        data["imageUrl"] = uri

        db.collection("posts")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(activity, "Saved to DB", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(activity, "Error saving to DB", Toast.LENGTH_LONG).show()
            }
    }
    */


}
