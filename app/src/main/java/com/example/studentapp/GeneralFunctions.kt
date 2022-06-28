package com.example.studentappimport android.content.Contextimport android.net.ConnectivityManagerimport android.net.NetworkCapabilitiesimport android.util.Logimport com.example.studentapp.database.MaterialDatabaseimport com.example.studentapp.database.MaterialsDataimport com.google.firebase.auth.ktx.authimport com.google.firebase.firestore.ktx.firestoreimport com.google.firebase.ktx.Firebaseimport kotlinx.coroutines.CoroutineScopeimport kotlinx.coroutines.Dispatchersimport kotlinx.coroutines.NonDisposableHandle.parentimport kotlinx.coroutines.launchobject GeneralFunctions {    fun checkForInternet(context: Context): Boolean {        val connectivityManager =            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager        val network = connectivityManager.activeNetwork ?: return false        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false        return when {            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true            else -> false        }    }    var check = false    var check1 = false    fun getMaterialsGeneral(context: Context) {        val localDb = MaterialDatabase.getDatabase(context)        val db = Firebase.firestore        Firebase.auth.currentUser?.let {            db.collection("materials")                .get()                .addOnSuccessListener { result ->                    CoroutineScope(Dispatchers.IO).launch {                        for (document in result) {                            if ((localDb.materialDao()                                    .isNotExists(document.get("materialTitle").toString()))                            ) {                                localDb.materialDao().insertData(                                    MaterialsData(                                        document.get("materialImgURI").toString(),                                        document.get("materialURL").toString(),                                        document.get("materialTitle").toString(),                                        document.get("materialDesc").toString(),                                        document.get("id").toString().toInt(),                                        document.get("idUser").toString(),                                    )                                )                            }                        }                    }                }                .addOnFailureListener { exception ->                    Log.w("TAG", "Error getting documents.", exception)                }        }    }// getMaterials()*}