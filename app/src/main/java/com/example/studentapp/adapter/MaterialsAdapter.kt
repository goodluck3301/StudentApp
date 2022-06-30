package com.example.studentapp.adapterimport android.annotation.SuppressLintimport android.content.Contextimport android.content.Intentimport android.content.Intent.ACTION_SENDimport android.net.Uriimport android.os.Bundleimport android.view.LayoutInflaterimport android.view.MenuItemimport android.view.Viewimport android.view.ViewGroupimport android.widget.*import androidx.core.content.ContextCompat.startActivityimport androidx.recyclerview.widget.RecyclerViewimport androidx.swiperefreshlayout.widget.SwipeRefreshLayoutimport com.bumptech.glide.Glideimport com.bumptech.glide.load.resource.drawable.DrawableDecoderCompat.getDrawableimport com.example.studentapp.GeneralFunctionsimport com.example.studentapp.Rimport com.example.studentapp.database.MaterialDatabaseimport com.example.studentapp.database.MaterialsDataimport com.example.studentapp.database.topUsersDatabase.TopUserDataimport com.example.studentapp.database.topUsersDatabase.TopUserDatabaseimport com.example.studentapp.models.UserModelimport com.example.studentapp.questions.Data.TopDataListimport com.google.firebase.auth.FirebaseAuthimport com.google.firebase.auth.ktx.authimport com.google.firebase.firestore.ktx.firestoreimport com.google.firebase.ktx.Firebaseimport kotlinx.coroutines.*import java.util.*class MaterialsAdapter(    val context: Context,    private var materialsList: MutableList<MaterialsData>,    private val onItemClick: () -> Unit,) : RecyclerView.Adapter<MaterialsAdapter.MaterialsViewHolder>() {    private lateinit var firebaseAuth: FirebaseAuth    private lateinit var localDb: TopUserDatabase    private lateinit var materialDb: MaterialDatabase    val initialArticleDataList = ArrayList<MaterialsData>().apply {        addAll(materialsList)    }    override fun onCreateViewHolder(        parent: ViewGroup,        viewType: Int    ): MaterialsAdapter.MaterialsViewHolder {        val view = LayoutInflater            .from(context)            .inflate(R.layout.material_item, parent, false)        return MaterialsViewHolder(view)    }    override fun onBindViewHolder(holder: MaterialsAdapter.MaterialsViewHolder, position: Int) {        val allMaterialsModel = materialsList[position]        holder.bind(allMaterialsModel)    }    override fun getItemCount(): Int = materialsList.size    inner class MaterialsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {        @SuppressLint("NotifyDataSetChanged", "SetTextI18n")        fun bind(materialModel: MaterialsData) {            firebaseAuth = FirebaseAuth.getInstance()            val db = Firebase.firestore            localDb = TopUserDatabase.getDatabase(context)            materialDb = MaterialDatabase.getDatabase(context)            val img = itemView.findViewById<ImageView>(R.id.materialImage)            val title = itemView.findViewById<TextView>(R.id.materialTitile)            val desc = itemView.findViewById<TextView>(R.id.materialDesc)            val editMenu = itemView.findViewById<ImageView>(R.id.editmenu)            val userImage = itemView.findViewById<ImageView>(R.id.userImage)            val materialUserName = itemView.findViewById<TextView>(R.id.materialuserName)            editMenu.setOnClickListener {                val popupMenu = PopupMenu(context, it)                var docID: String                popupMenu.setOnMenuItemClickListener { item ->                    when (item.itemId) {                        R.id.deleteMaterial -> {                            db.collection("materials")                                .get()                                .addOnSuccessListener { result ->                                    for (document in result) {                                        if (firebaseAuth.uid == document.get("idUser")                                            && materialModel.materialTitle == document.get("materialTitle")                                        ) {                                            docID = document.id                                            db.collection("materials")                                                .document(docID)                                                .delete()                                            materialsList.remove(materialModel)                                            CoroutineScope(Dispatchers.IO).launch {                                                materialDb.materialDao().delete(materialModel)                                            }                                        }                                    }                                }                            onItemClick.invoke()                            notifyDataSetChanged()                            true                        }                        R.id.shareLink -> {                            try {                                val intent = Intent()                                intent.action = ACTION_SEND                                intent.putExtra(Intent.EXTRA_SUBJECT, "Android Studio Pro")                                intent.putExtra(                                    Intent.EXTRA_TEXT,                                    "Նորոթյուն ;)\n ${materialModel.materialURL}"                                )                                intent.type = "text/plain"                                startActivity(context, intent, Bundle())                            } catch (e: Exception) {                            }                            true                        }                        R.id.openUrl -> {                            try {                                val url = materialModel.materialURL                                val intent =                                    Intent(Intent.ACTION_VIEW, Uri.parse(url)).also { (it) }                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)                                context.startActivity(intent)                            } catch (e: Exception) {                            }                            true                        }                        else -> false                    }                }                popupMenu.inflate(R.menu.change_menu)                if (firebaseAuth.uid == materialModel.userID) {                    popupMenu.menu.findItem(R.id.deleteMaterial).isVisible = true                }                popupMenu.show()            }//            title.text = materialModel.materialTitle            desc.text = materialModel.materialDesc            Glide.with(context)                .load(materialModel.materialImgURI)                .placeholder(R.drawable.defoultimage)                .into(img)            db.collection("users")                .get()                .addOnSuccessListener { result ->                    for (document in result) {                        if (document.get("idUser") == materialModel.userID) {                            materialUserName.text = document.get("name").toString()                            Glide.with(context)                                .load(document.get("userURLtoImage"))                                .placeholder(R.drawable.costum_person_icon)                                .into(userImage)                        }                    }                }            checkMaterials()        }//bind        private fun checkMaterials() {            var listDb = mutableListOf<MaterialsData>()            var listFirestore = mutableListOf<MaterialsData>()            val db = Firebase.firestore            Firebase.auth.currentUser?.let {                db.collection("materials")                    .get()                    .addOnSuccessListener { result ->                        CoroutineScope(Dispatchers.IO).launch {                            for (document in result) {                                listFirestore.add(                                    MaterialsData(                                        document.get("materialImgURI").toString(),                                        document.get("materialURL").toString(),                                        document.get("materialTitle").toString(),                                        document.get("materialDesc").toString(),                                        document.get("id").toString().toInt(),                                        document.get("idUser").toString(),                                    )                                )                            }                            listDb = materialDb.materialDao().getAll().toMutableList()                            val difference = listDb.minus(listFirestore)                            difference.forEach {                                materialDb.materialDao().delete(it)                            }                        }                    }            }        }    }    private val titleFilter = object : Filter() {        override fun performFiltering(constraint: CharSequence?): FilterResults {            val filteredList: ArrayList<MaterialsData> = ArrayList()            if (constraint == null || constraint.isEmpty()) {                initialArticleDataList.let { filteredList.addAll(it) }            } else {                val query = constraint.toString().trim().toLowerCase()                initialArticleDataList.forEach {                    if (it.materialTitle?.toLowerCase(Locale.ROOT)!!.contains(query)) {                        filteredList.add(it)                    }                }            }            val results = FilterResults()            results.values = filteredList            return results        }        @SuppressLint("NotifyDataSetChanged")        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {            if (results?.values is ArrayList<*>) {                materialsList.clear()                materialsList.addAll(results.values as ArrayList<MaterialsData>)                notifyDataSetChanged()            }        }    }    fun getFilter(): Filter {        return titleFilter    }}