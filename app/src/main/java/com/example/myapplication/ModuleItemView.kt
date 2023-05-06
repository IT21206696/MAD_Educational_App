package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityModuleItemViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ModuleItemView : AppCompatActivity() {
    private lateinit var binding:ActivityModuleItemViewBinding
    private lateinit var user:FirebaseAuth
    private lateinit var studentArrayList : ArrayList<Material>
    private lateinit var studentRecyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityModuleItemViewBinding.inflate(layoutInflater)
        user = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val moduleId = intent.getStringExtra("moduleId")

        readData(moduleId.toString())

        studentRecyclerView = binding.materialList
        studentRecyclerView.layoutManager = LinearLayoutManager(this)
        studentRecyclerView.setHasFixedSize(true)
        studentArrayList = arrayListOf<Material>()
        readMaterials(moduleId.toString())

        binding.students.setOnClickListener{
            var intent = Intent(this,ViewModuleStudents::class.java).also {
                it.putExtra("moduleId",moduleId.toString())
            }
            startActivity(intent)
        }
        binding.uploadMaterial.setOnClickListener{
            var intent = Intent(this,UploadVideo::class.java).also {
                it.putExtra("moduleId",moduleId.toString())
            }
            startActivity(intent)
        }
    }

    private fun readData(moduleId:String){
        FirebaseDatabase.getInstance().getReference("Module").child(moduleId).get().addOnSuccessListener {
            if(it.exists()){
                binding.name.text = it.child("name").value.toString()
                binding.code.text = it.child("code").value.toString()
                binding.description.text = it.child("description").value.toString()
            }
        }
    }

    private fun readMaterials(moduleId:String){
        binding.dataLayout.visibility = View.GONE
        binding.loaderLayout.visibility = View.VISIBLE
        studentArrayList.clear()
        FirebaseDatabase.getInstance().getReference("Material").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(childSnapshot in snapshot.children){
                        if(childSnapshot.child("moduleId").value.toString() == moduleId){
                            val material =  childSnapshot.getValue(Material::class.java)
                            studentArrayList.add(material!!)
                        }
                    }
                    studentRecyclerView.adapter = MaterialAdapter(studentArrayList,this@ModuleItemView)
                    binding.dataLayout.visibility = View.VISIBLE
                    binding.loaderLayout.visibility = View.GONE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ModuleItemView, "error", Toast.LENGTH_SHORT).show()
            }
        })
    }
    fun onItemClick(position: Int) {

    }

}