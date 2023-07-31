package com.example.gymmanagement.fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide

import com.example.gymmanagement.R
import com.example.gymmanagement.databinding.FragmentAddMemberBinding
import com.example.gymmanagement.global.CaptureImage
import com.example.gymmanagement.global.MyFunction

import com.github.florent37.runtimepermission.RuntimePermission
import com.techplus.gymmanagement.global.DB
import java.util.Calendar
import java.util.Locale

@Suppress("UNREACHABLE_CODE", "DEPRECATION")
class FragmentAddMember : Fragment() {
    var db:DB?=null
    private lateinit var binding: FragmentAddMemberBinding
    private var captureImage:CaptureImage?=null
    private val REQUEST_CAMERA =1234
    private val REQUEST_PHOTOS = 5464
    private var actualImagePath = ""
    private var gender = "Male"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddMemberBinding.inflate(inflater,container,false)
        return binding.root

    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = activity?.let {DB(it)}
        captureImage = CaptureImage(activity)

        val cal = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener{view1, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR,year)
            cal.set(Calendar.MONTH,monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)

            val myFormat = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(myFormat,Locale.US)
            binding.edtJoining.setText(sdf.format(cal.time))
        }
        binding.radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (id){
                R.id.rdMale ->{
                    gender = "Male"
                }
                R.id.rdFeMale ->{
                    gender = "Female"
                }
            }

        }

        binding.btnAddMemberSave.setOnClickListener{
            if (validate()){
                saveData()
            }

        }



        binding.imgPicDate.setOnClickListener{
            activity?.let { it1 -> DatePickerDialog(it1,dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show() }
        }

        binding.imgTakeImage.setOnClickListener{
            getImage()

        }
    }




    private fun getImage(){
        val items:Array<CharSequence>
        try {
            items = arrayOf("Zrob zdjecie","Wybierz zdjecie", "Wroc")
            val builder = android.app.AlertDialog.Builder(activity)
            builder.setCancelable(false)
            builder.setTitle("Wybierz zdjecie")
            builder.setItems(items,DialogInterface.OnClickListener { dialog, i->
                if (items[i] == "Zrob zdjecie"){
                    RuntimePermission.askPermission(this)
                        .request(android.Manifest.permission.CAMERA)
                        .onAccepted {
                            takePicture()

                        }
                        .onDenied {
                            android.app.AlertDialog.Builder(activity)
                                .setMessage("Prosze zaakceptowac, aby wykonac zdjecie")
                                .setPositiveButton("Tak", DialogInterface.OnClickListener{ dialog, i ->
                                    it.askAgain()
                                })
                                .setNegativeButton("Nie", DialogInterface.OnClickListener{ dialog, i ->
                                    dialog.dismiss()
                                })
                                .show()
                        }
                        .ask()

                }else if (items[i] == "Wybierz zdjecie"){
                    RuntimePermission.askPermission(this)
                        .request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .onAccepted {
                            takeFromGallery()
                        }
                        .onDenied {
                            android.app.AlertDialog.Builder(activity)
                                .setMessage("Prosze zaakceptowac, aby wybrac zdjecie")
                                .setPositiveButton("Tak", DialogInterface.OnClickListener{ dialog, i ->
                                    it.askAgain()
                                })
                                .setNegativeButton("Nie", DialogInterface.OnClickListener{ dialog, i ->
                                    dialog.dismiss()
                                })
                                .show()
                        }
                        .ask()

                }else{
                    dialog.dismiss()
                }
            })
            builder.show()


        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun takePicture(){
        val takePicIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT,captureImage?.setImageUri())
        takePicIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(takePicIntent, REQUEST_CAMERA)
    }
    private fun takeFromGallery(){
        val intent = Intent()
        intent.type = "image/jpg"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(intent,REQUEST_PHOTOS)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK){
            captureImage(captureImage?.getRightAngleImage(captureImage?.imagePath).toString())
        }else if (requestCode == REQUEST_PHOTOS && resultCode == Activity.RESULT_OK){
            captureImage(captureImage?.getRightAngleImage(captureImage?.getPath(data?.data,context)).toString())
        }
    }
    private fun captureImage(path:String){
        Log.d("FragmentAdd", "imagePath: $path")
        getImagePath(captureImage?.decodeFile(path))
    }

    private fun getImagePath(bitmap:Bitmap?){
        val tempUri: Uri? = captureImage?.getImageUri(activity,bitmap)
        actualImagePath = captureImage?.getRealPathFromURI(tempUri,activity).toString()
        Log.d("FragmentAdd", "ActualImagePath: $actualImagePath")

        activity?.let {
            Glide.with(it)
                .load(actualImagePath)
                .into(binding.imgPic)

        }
    }
    private fun showToast(value:String){
        Toast.makeText(activity,value,Toast.LENGTH_LONG).show()
    }
    private fun validate():Boolean{
        if(binding.edtFirstName.text.toString().trim().isEmpty()){
            showToast("Wprowadz imie")
            return false
        }else if(binding.edtLastName.text.toString().trim().isEmpty()) {
            showToast("Wprowadz nazwisko")
            return false
        }else if(binding.edtAge.text.toString().trim().isEmpty()) {
            showToast("Wprowadz wiek")
            return false
        }else if(binding.edtMobile.text.toString().trim().isEmpty()) {
            showToast("Wprowadz numer telefonu")
            return false
        }


        return true
    }
    private fun saveData(){
        try {
            val sqlQuery = "INSERT INTO MEMBER(ID,FIRST_NAME,LAST_NAME,GENDER,AGE,WEIGHT,MOBIlE, STATUS) VALUES('2','Michal','Loque','Male','20','70','852136789','A')"
            db?.executeQuery(sqlQuery)
            showToast("Zapisano!")

        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun getIncrementedId():String{
        var incrementId = ""

        val sqlQuery = "SELECT IFNULL(MAX(ID)+1,'1') AS ID FROM MEMBER"
        db?.fireQuery(sqlQuery)?.use {
            if(it.count>0){
                incrementId = MyFunction.getValue(it, "ID")
            }
        }

        return incrementId
    }


}