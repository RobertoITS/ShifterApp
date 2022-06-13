package com.hvdevs.shifterapp.signinactivity.fragments.registerfragment

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.hvdevs.shifterapp.dashboard.DashboardUserActivity
import com.hvdevs.shifterapp.databinding.FragmentTabProfileBinding
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

//Creamos las variables para navegar entre los resultados de las actividades
const val GALLERY = 3000
const val CAMERA = 3001
//Variable de permisos
private const val PERMISSION_REQUEST = 10

class TabProfileFragment : Fragment() {
    private var _binding: FragmentTabProfileBinding? = null
    private val binding get() = _binding!!

    private var calendar = Calendar.getInstance() //El calendario para obtener la instancia

    //La lista de los permisos que maneja la APP
    private var permission = arrayOf(android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private var imageUri: Uri? = null

    private lateinit var auth: FirebaseAuth

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabProfileBinding.inflate(inflater, container, false)

//        binding.bornDateInput.addTextChangedListener(textWatcher)

        //Creamos la instancia
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val date = DatePickerDialog.OnDateSetListener { view, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            updateLabel()
        }

        binding.bornDateInput.setOnClickListener {
            DatePickerDialog(requireContext(), date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        //Cargamos una foto de la galeria
        binding.add.setOnClickListener {
            if (checkPermission(requireContext(), permission)){
//                Log.i("PERMISSIONS", getText(R.string.permissions_accepted).toString())
                showPictureDialog()
            } else {
                //Caso contrario, pide los permisos nuevamente
                requestPermissions(permission, PERMISSION_REQUEST
                )
//                Log.e("PERMISSIONS", getText(R.string.permissions_request).toString())
            }
        }

        binding.finish.setOnClickListener {
            updateProfile()
            startActivity(Intent(context, DashboardUserActivity::class.java))
            activity?.finish()
        }

        return binding.root
    }

    private fun updateLabel() {
        val format = "dd/MM/yy"
        val dateFormat = SimpleDateFormat(format, Locale.US)
        binding.bornDateInput.setText(dateFormat.format(calendar.time))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST){
            var allSuccess = true
            for (i in permissions.indices){
                if (grantResults[i] == PackageManager.PERMISSION_DENIED){
                    allSuccess = false
                    val requestAgain = shouldShowRequestPermissionRationale(permissions[i])
                    if (requestAgain){
                        Toast.makeText(context, "Permisos denegados", Toast.LENGTH_LONG).show()
                    } else {
//                        Log.e("PERMISSIONS",
//                            getText(R.string.denied_permission).toString()
//                        )
                        Toast.makeText(context, "Los permisos fueron denegados, vaya a configuracion", Toast.LENGTH_LONG).show()
                    }
                }
            }
            if (allSuccess){
                showPictureDialog()
            }
        }
    }

    private fun checkPermission(context: Context, permissionArray: Array<String>): Boolean{
        var allSuccess = true
        for (i in permissionArray.indices){
            if (activity?.checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED){
                allSuccess = false
            }
        }
        return allSuccess
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == com.hvdevs.shifterapp.dashboard.myaccountfragment.CAMERA) {
                //Corresponde al CropImage Activity ---------------------------------
                CropImage.activity(imageUri)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setGuidelines(CropImageView.Guidelines.OFF)
                    .setAspectRatio(16, 16)
                    .setAutoZoomEnabled(false)
                    .start(requireContext(), this@TabProfileFragment)
                //------------------------------------------------------------------
            }
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == com.hvdevs.shifterapp.dashboard.myaccountfragment.GALLERY) {
                //Corresponde al CropImage Activity ---------------------------------
                CropImage.activity(data?.data)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setGuidelines(CropImageView.Guidelines.OFF)
                    .setAspectRatio(16, 16)
                    .setAutoZoomEnabled(false)
                    .start(requireContext(), this@TabProfileFragment)
                //------------------------------------------------------------------
            }

        }

        //Corresponde al CropImage Activity ------------------------------------------------------------------
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            try {
                val resultUri = result.uri //Tomamos el resultado de la imagen, lo pasamos a uri
                uploadImage(resultUri)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
        else {
            return
        }
        //---------------------------------------------------------------------------------------------------
    }

    //El dialogo para seleccionar la imagen
    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(context)
        pictureDialog.setTitle("Selecionar:")
        val pictureDialogItems = arrayOf("Desde galeria", "Desde la camara")
        pictureDialog.setItems(pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> choosePhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) //Instanciamos el intent para tomar del almacenamiento
        intent.type = "image/*" //Los tipos de valores que pude tomar
        requireActivity().startActivityFromFragment(this, intent,
            com.hvdevs.shifterapp.dashboard.myaccountfragment.GALLERY
        ) //Lanzamos la actividad
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //Instanciamos el intent de la camara
        val title = "image.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, title)
            put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera")
        }
        imageUri = activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) //Nos pie unos valores que se cargan en Values
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri) //Ponemos la imagen en un extra
        requireActivity().startActivityFromFragment(this, intent,
            com.hvdevs.shifterapp.dashboard.myaccountfragment.CAMERA
        ) //Lanzamos la actividad
    }

    //Sube la imagen original al servidor
    private fun uploadImage(path: Uri?) {

        //Un dialogo de proceso
        //Lamentablemente "Deprecated", como casi todo
        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Cargando imagen")
        progressDialog.setCancelable(false) //Que no sea cancelable hasta que la tarea termine
        progressDialog.show()

        //UID del usuario, para el nombre del archivo
        val user: String = auth.currentUser?.uid.toString()
        //La referencia a la BD
        val storage = FirebaseStorage.getInstance().getReference("users/$user")
        //Pasamos de URI a Bitmap
        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, path)
        //La subimos
        storage.putFile(path!!)
            .addOnSuccessListener{
                //Ponemos la imagen en la vista
                binding.imageProfile.setImageBitmap(bitmap)
                //La guardamos en la memoria del telefono
                saveToInternalStorage(bitmap, requireContext())
                //Sacamos el dialogo de proceso
                if (progressDialog.isShowing){
                    progressDialog.dismiss()
                }
                Toast.makeText(context, "Carga completada", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(context,"Fallo la carga", Toast.LENGTH_LONG).show()
                if (progressDialog.isShowing){
                    progressDialog.dismiss()
                }
            }
    }

    //Guarda la imagen en el almacenamiento interno (thumbnail)
    private fun saveToInternalStorage(bitmap: Bitmap, context: Context): String {
        val name = auth.currentUser?.uid
        //Obtenemos la direccion para guardian la imagen
        val cw = ContextWrapper(context.applicationContext)
        // path: /data/data/yourapp/app_data/.thumbnails
        val directory = cw.getDir(".thumbnails", Context.MODE_PRIVATE)
        // Creamos la direccion
        Log.d("PATH", directory.path)
        val myPath = File(directory, "$name.jpg")

        //Creamos una imagen thumbnail para facilitar la carga
        val thumbImage: Bitmap = ThumbnailUtils.extractThumbnail(
            bitmap,
            500,
            500
        )

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(myPath)
            thumbImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return directory.absolutePath
    }

    //Obtenemos la imagen creada tipo thumbnail, para mejorar el tiempo de carga
    private fun pickImgFromLocal(context: Context){
        val name = auth.currentUser?.uid //Le colocamos el nombre del usuario a la imagen en miniatura
        val cw = ContextWrapper(context.applicationContext)
        val directory = cw.getDir(".thumbnails", Context.MODE_PRIVATE) //Asignamos un directorio en los archivos de la app
        Log.d("PATH", directory.path) //Mostramos el directorio en el Logcat
        val myPath = File(directory, "$name.jpg") //Creamos el archivo temporal
        val bitmap = BitmapFactory.decodeFile(myPath.absolutePath) //Colocamos la imagen en el archivo
        if (bitmap != null){
            binding.imageProfile.setImageBitmap(bitmap) //Si la imagen existe, se coloca
        } else {
            pickImgFromServer() //Caso contrario, se saca de la bd
        }
    }

    //Carga la imagen desde el servidor (Su utilidad es para cuando abramos la imagen)
    private fun pickImgFromServer() {
        val user: String = auth.currentUser?.uid.toString()
        val storageRef = FirebaseStorage.getInstance().reference.child("users/$user") //Le asignamos como nombre la uid del usuario
        val localFile = File.createTempFile("tempImage", "jpg") //Creamos un archivo temporal
        //Cargamos la imagen del storage en ese archivo temporal
        storageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath) //Lo convertimos a bitmap
            saveToInternalStorage(bitmap, requireContext()) //Lo guardamos en el almacenamiento interno como thumbnail
        } .addOnFailureListener {
            Toast.makeText(context, "Fallo la carga", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateProfile(){
        val uid = auth.currentUser?.uid
        database.child("users/$uid/data/lastName").setValue(binding.lastNameInput.text.toString())
        database.child("users/$uid/data/name").setValue(binding.nameInput.text.toString())
        database.child("users/$uid/data/phone").setValue(binding.phoneInput.text.toString())
        database.child("users/$uid/data/birth").setValue(binding.bornDateInput.text.toString())
        database.child("users/$uid/data/email").setValue(binding.emailInput.text.toString())
        database.child("users/$uid/data/gender").setValue(binding.genderInput.text.toString())
    }









    private val textWatcher = object : TextWatcher{
        var current = ""
        val ddmmYYYY = "DDMMYYYY"
        val cal = Calendar.getInstance()
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s.toString() != current) {
                var clean: String = s.toString().replace("[^\\d.]|\\.", "")
                val cleanC = current.replace("[^\\d.]|\\.", "")
                val cl = clean.length
                var sel = cl
                var i = 2
                while (i <= cl && i < 6) {
                    sel++
                    i += 2
                }
                //Fix for pressing delete next to a forward slash
                if (clean == cleanC) sel--
                if (clean.length < 8) {
                    clean += ddmmYYYY.substring(clean.length)
                } else {
                    //This part makes sure that when we finish entering numbers
                    //the date is correct, fixing it otherwise
                    var day = clean.substring(0, 2).toInt()
                    var mon = clean.substring(2, 4).toInt()
                    var year = clean.substring(4, 8).toInt()
                    mon = if (mon < 1) 1 else if (mon > 12) 12 else mon
                    cal[Calendar.MONTH] = mon - 1
                    year = if (year < 1900) 1900 else if (year > 2100) 2100 else year
                    cal[Calendar.YEAR] = year
                    // ^ first set year for the line below to work correctly
                    //with leap years - otherwise, date e.g. 29/02/2012
                    //would be automatically corrected to 28/02/2012
                    day =
                        if (day > cal.getActualMaximum(Calendar.DATE)) cal.getActualMaximum(Calendar.DATE) else day
                    clean = String.format("%02d%02d%02d", day, mon, year)
                }
                clean = String.format(
                    "%s/%s/%s", clean.substring(0, 2),
                    clean.substring(2, 4),
                    clean.substring(4, 8)
                )
                sel = if (sel < 0) 0 else sel
                current = clean
                binding.bornDateInput.setText(current)
                binding.bornDateInput.setSelection(if (sel < current.length) sel else current.length)
            }
        }

        override fun afterTextChanged(p0: Editable?) {
        }

    }
}