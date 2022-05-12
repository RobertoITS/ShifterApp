package com.hvdevs.shifterapp.myaccountfragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
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
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.hvdevs.shifterapp.databinding.FragmentMyAccountBinding
import com.hvdevs.shifterapp.myaccountfragment.dialog.DialogMessageSimple
import com.hvdevs.shifterapp.registerfragment.constructor.User
import com.hvdevs.shifterapp.splashscreen.SplashScreen
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

//Creamos las variables para navegar entre los resultados de las actividades
const val GALLERY = 3000
const val CAMERA = 3001
//Variable de permisos
private const val PERMISSION_REQUEST = 10

class MyAccountFragment : Fragment(), DialogMessageSimple.Data {
    private var _binding: FragmentMyAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth

    //La lista de los permisos que maneja la APP
    private var permission = arrayOf(android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyAccountBinding.inflate(inflater, container, false)

        //Creamos la instancia
        auth = FirebaseAuth.getInstance()

        //Obtenemos la id de usuario
        val user: String = auth.currentUser!!.uid

        //Sacamos los datos de la DB
        getInfo(user)
        pickImgFromLocal(requireContext())

        //Escondemos el contenedor
        binding.containerGone.alpha = 0f
        binding.containerGone.isVisible = false
        //Desplazamos el boton
        binding.ok.translationX = -500f

        //Abrimos el menu de cambio de datos
        binding.data.setOnClickListener {
            when (binding.data.text){
                "Modificar datos" ->{
                    modify(user)
                }
                "Volver" ->{
                    back(user)
                }
            }
        }

        //Instanciamos la clase para mostrarla
        val dialog = DialogMessageSimple()

        //El boton de logout y su doble funcion
        binding.logout.setOnClickListener {
            when (binding.logout.text){
                "Cerrar sesion" -> {
                    logOut()
                }
                "Cambiar contraseña" -> {
                    when (binding.pass.isEnabled){
                        true -> {
                            //Comparamos que la contraseña cumpla las condiciones
                            if (binding.pass.text!!.length >= 6) {
                                if (binding.pass.text.toString() == binding.repPass.text.toString()){
                                    newPass()
                                } else {
                                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(context, "Contraseña muy corta!", Toast.LENGTH_LONG).show()
                            }
                        }
                        false -> {
                            dialog.dialogMessageSimple(
                                context,
                                this)
                        }
                    }
                }
            }
        }

        //Modificamos los datos del usuario
        binding.ok.setOnClickListener {
            //Llamar a cuadro de dialogo
            updateData(
                binding.email.text.toString(),
                binding.lastName.text.toString(),
                binding.name.text.toString(),
                binding.pass.text.toString(),
                binding.phone.text.toString())
            back(user)
        }

        //Cargamos una foto de la galeria
        binding.add.setOnClickListener {
            if (checkPermission(requireContext(), permission)){
//                Log.i("PERMISSIONS", getText(R.string.permissions_accepted).toString())
                showPictureDialog()
            } else {
                //Caso contrario, pide los permisos nuevamente
                requestPermissions(permission, PERMISSION_REQUEST)
//                Log.e("PERMISSIONS", getText(R.string.permissions_request).toString())
            }
        }


        return binding.root
    }

    //Manejo de permisos de la app
//---------------------------------------------------------------------------------------------------------------------
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST){
            var allSuccess = true
            for (i in permissions.indices){
                if (grantResults[i] == PackageManager.PERMISSION_DENIED){
                    allSuccess = false
                    var requestAgain = shouldShowRequestPermissionRationale(permissions[i])
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

    @SuppressLint("WrongConstant")
    fun checkPermission(context: Context, permissionArray: Array<String>): Boolean{
        var allSuccess = true
        for (i in permissionArray.indices){
            if (activity?.checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED){
                allSuccess = false
            }
        }
        return allSuccess
    }
//---------------------------------------------------------------------------------------------------------------------

    //Funcion para abrir la galeria y obtener la foto
//-------------------------------------------------------------------------------------------------------
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA) {
                //Corresponde al CropImage Activity ---------------------------------
                CropImage.activity(imageUri)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setGuidelines(CropImageView.Guidelines.OFF)
                    .setAspectRatio(16, 16)
                    .setAutoZoomEnabled(false)
                    .start(requireContext(), this@MyAccountFragment)
                //------------------------------------------------------------------
            }
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                //Corresponde al CropImage Activity ---------------------------------
                CropImage.activity(data?.data)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setGuidelines(CropImageView.Guidelines.OFF)
                    .setAspectRatio(16, 16)
                    .setAutoZoomEnabled(false)
                    .start(requireContext(), this@MyAccountFragment)
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
        requireActivity().startActivityFromFragment(this, intent, GALLERY) //Lanzamos la actividad
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
        requireActivity().startActivityFromFragment(this, intent, CAMERA) //Lanzamos la actividad
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
//-------------------------------------------------------------------------------------------------------

    private fun logOut(){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context, SplashScreen::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun newPass(){
        val user = FirebaseAuth.getInstance().currentUser
        val newPassword = binding.pass.text.toString()
        user!!.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "User password updated.")
                    Toast.makeText(context, "Se cambio la contraseña", Toast.LENGTH_LONG)
                        .show()
                    binding.repPass.setText("")
                    db.child("pass").setValue(binding.pass.text.toString())
                    binding.pass.isEnabled = false
                    binding.repPass.isEnabled = false
                }
            }
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private fun modify(uid: String){
        binding.ok.animate().translationX(0f).alpha(1F).setDuration(500).setStartDelay(300).start()
        binding.containerGone.isVisible = true
        binding.containerGone.animate()
            .alpha(1f)
            .setDuration(750)
            .setListener(null)
        binding.data.text = "Volver"
        binding.data.backgroundTintList = resources.getColorStateList(com.hvdevs.shifterapp.R.color.colorAccent)
        binding.logout.backgroundTintList = resources.getColorStateList(com.hvdevs.shifterapp.R.color.Secondary)
        binding.logout.text = "Cambiar contraseña"
        enabled()
        //Cuando finaliza la tarea, trae de nuevo los datos
        getInfo(uid)
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private fun back(uid: String){
        binding.ok.animate().translationX(-500f).alpha(0F).setDuration(500).setStartDelay(300).start()
        binding.containerGone.isVisible = false
        binding.data.text = "Modificar datos"
        binding.data.backgroundTintList = resources.getColorStateList(com.hvdevs.shifterapp.R.color.Secondary)
        binding.logout.backgroundTintList = resources.getColorStateList(com.hvdevs.shifterapp.R.color.colorAccent)
        binding.logout.text = "Cerrar sesion"
        noEnabled()


        //Si se cancela la tarea, pero se modificaron sin aceptar los cambios, pone los datos
        //correspondientes
        getInfo(uid)
    }

    private fun enabled(){
        binding.name.isEnabled = true
        binding.lastName.isEnabled = true
        binding.phone.isEnabled = true
        binding.email.isEnabled = true
    }

    private fun noEnabled(){
        binding.name.isEnabled = false
        binding.lastName.isEnabled = false
        binding.phone.isEnabled = false
        binding.email.isEnabled = false
        binding.pass.isEnabled = false
        binding.repPass.isEnabled = false
    }

    private fun getInfo(uid: String){

        db = FirebaseDatabase.getInstance().getReference("users/users/$uid/data")
        db.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val name: String = snapshot.child("name").value.toString()
                    binding.name.setText(name)
                    val lastName: String = snapshot.child("lastName").value.toString()
                    binding.lastName.setText(lastName)
                    val phone: String = snapshot.child("phone").value.toString()
                    binding.phone.setText(phone)
                    val email: String = snapshot.child("email").value.toString()
                    binding.email.setText(email)
                    val pass: String = snapshot.child("pass").value.toString()
                    binding.pass.setText(pass)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        if (binding.name.text == null){
            binding.name.setText("")
        }
        if (binding.lastName.text == null){
            binding.lastName.setText("")
        }
        if (binding.phone.text == null){
            binding.phone.setText("")
        }
        if (binding.email.text == null){
            binding.email.setText("")
        }
        if (binding.pass.text == null){
            binding.pass.setText("")
        }
    }

    private fun updateData(email: String, lastName: String, name: String, pass: String, phone: String){
        val data = User(email, lastName, name, pass, phone, "User", auth.currentUser?.uid)
        db.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(context, "Todo ok", Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(context, "Error al cargar los datos", Toast.LENGTH_LONG).show()
                Log.e("FIREBASE", "ERROR UPDATING DATA", it)
            }
    }

    //Funcion para pasar los datos al cuadro de dialogo
    override fun password(pass: String) {
        if (binding.pass.text.toString() == pass){
            //Mostrar la contraseña
            binding.pass.isEnabled = true
            binding.repPass.isEnabled = true
        } else {
            Toast.makeText(context, "No coinciden las contraseñas", Toast.LENGTH_LONG).show()
        }
        super.password(pass)
    }
}