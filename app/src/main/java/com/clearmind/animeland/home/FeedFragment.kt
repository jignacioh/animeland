package com.clearmind.animeland.home

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.clearmind.animeland.R
import com.clearmind.animeland.core.di.AppDatabase
import com.clearmind.animeland.model.Place
import com.clearmind.animeland.model.feed.Feed
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.android.synthetic.main.fragment_feed.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FeedFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference
    private var db: AppDatabase ?=  null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_feed, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        ref = FirebaseDatabase.getInstance().reference.child("places").child(auth.uid!!)
        view.recyclerview.layoutManager = LinearLayoutManager(activity)
        view.recyclerview.addItemDecoration(MarginItemDecoration(resources.getDimension(R.dimen.default_padding).toInt()))


        firebaseData()

        db = Room.databaseBuilder(
            activity!!.applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()

        GlobalScope.launch {
            var data=db!!.userDao().getAll()

            data?.forEach {
                println("data"+it.uid+" and "+it.username+" and "+it.usermail+" and "+it.lastLatitude)
            }
        }
    }

    fun firebaseData() {


        val option = FirebaseRecyclerOptions.Builder<Place>()
            .setQuery(ref, Place::class.java)
            .setLifecycleOwner(this)
            .build()


        val firebaseRecyclerAdapter = object: FirebaseRecyclerAdapter<Place, MyViewHolder>(option) {


            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                val itemView = LayoutInflater.from(activity).inflate(R.layout.item_feed,parent,false)
                return MyViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Place) {
                val placeid = getRef(position).key.toString()

                ref.child(placeid).addValueEventListener(object: ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        Toast.makeText(activity, "Error Occurred "+ p0.toException(), Toast.LENGTH_SHORT).show()

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        this@FeedFragment.progress_bar.visibility = if(itemCount == 0) View.VISIBLE else View.GONE

                        holder.txt_name.text=model.Description
                        Picasso.get().load(model.photos[1]).into(holder.img_vet)

                    }
                })
            }
        }
        recyclerview.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        internal var txt_name: TextView = itemView!!.findViewById(R.id.Display_title)
        internal var img_vet: ImageView = itemView!!.findViewById(R.id.Display_img)


    }
    class MarginItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View,
                                    parent: RecyclerView, state: RecyclerView.State) {
            with(outRect) {

                if (parent.getChildAdapterPosition(view) == 0) {
                    top = spaceHeight
                }
                left =  spaceHeight
                right = spaceHeight
                bottom = spaceHeight

            }

        }
    }
}
