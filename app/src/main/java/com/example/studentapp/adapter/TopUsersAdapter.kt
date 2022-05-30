package com.example.studentapp.adapterimport android.annotation.SuppressLintimport android.content.Contextimport android.graphics.Colorimport android.view.LayoutInflaterimport android.view.Viewimport android.view.ViewGroupimport android.widget.ImageViewimport android.widget.TextViewimport android.widget.Toastimport androidx.recyclerview.widget.RecyclerViewimport com.bumptech.glide.Glideimport com.example.studentapp.Rimport com.example.studentapp.models.UserModelclass TopUsersAdapter(    val context: Context?,    private val userList: MutableList<UserModel>,) : RecyclerView.Adapter<TopUsersAdapter.TopUserViewHolder>() {    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopUserViewHolder {        val view = LayoutInflater.from(context).inflate(R.layout.top_user_item, parent, false)        return TopUserViewHolder(view)    }    @SuppressLint("SetTextI18n")    override fun onBindViewHolder(holder: TopUsersAdapter.TopUserViewHolder, position: Int) {        val topUserModel = userList[position]        holder.bind(topUserModel)        holder.userPrice.text = "Top ${position.plus(1)}"        when (holder.userPrice.text) {            "Top 1" -> {                holder.userPrice.setTextColor(Color.parseColor("#FFD700"))                holder.userPrice.textSize = 27.0f            }            "Top 2" -> {                holder.userPrice.setTextColor(Color.parseColor("#9B9B9B"))                holder.userPrice.textSize = 25.0f            }            "Top 3" -> {                holder.userPrice.setTextColor(Color.parseColor("#CD7F32"))                holder.userPrice.textSize = 23.0f            }            else -> holder.userPrice.setTextColor(Color.parseColor("#118dda"))        }    }    override fun getItemCount(): Int = userList.size    inner class TopUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {        val userImage = itemView.findViewById<ImageView>(R.id.topUserImage)        val userName = itemView.findViewById<TextView>(R.id.TopUserName)        val userScore = itemView.findViewById<TextView>(R.id.topUserScore)        val userPrice = itemView.findViewById<TextView>(R.id.topUserPrice)        @SuppressLint("SetTextI18n")        fun bind(topUserModel: UserModel) {            // Toast.makeText(context,topUserModel.userName,Toast.LENGTH_SHORT).show()            userName.text = topUserModel.userName            userScore.text = "${topUserModel.userScore} միավոր"            context?.let { it1 ->                Glide.with(it1)                    .load(topUserModel.userImgUri)                    .placeholder(R.drawable.costum_person_icon)                    .into(userImage)            }        }//bind    }}