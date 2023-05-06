package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityAddModuleStudentsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class AddModuleStudents : AppCompatActivity() {
    private lateinit var binding:ActivityAddModuleStudentsBinding
    private lateinit var user:FirebaseAuth
    private lateinit var studentArrayList : ArrayList<Student>
    private lateinit var studentRecyclerView : RecyclerView

    private var moduleIndex = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddModuleStudentsBinding.inflate(layoutInflater)
        user = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val moduleId = intent.getStringExtra("moduleId")
        moduleIndex = moduleId.toString()

        studentRecyclerView = binding.studentList
        studentRecyclerView.layoutManager = LinearLayoutManager(this)
        studentRecyclerView.setHasFixedSize(true)
        studentArrayList = arrayListOf<Student>()
        readData(user.uid.toString())

        binding.addStudent.setOnClickListener{
            addStudent(moduleId.toString())
        }
    }

    private fun readData(userid:String){
        binding.dataLayout.visibility = View.GONE
        binding.loaderLayout.visibility = View.VISIBLE
        studentArrayList.clear()
        FirebaseDatabase.getInstance().getReference("Student").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(fineSnapshot in snapshot.children){
                        if(fineSnapshot.child("teacherId").value.toString() == userid){
                            val studentItem =  fineSnapshot.getValue(Student::class.java)
                            studentArrayList.add(studentItem!!)
                        }
                    }
                    studentRecyclerView.adapter = AddStudentAdapter(studentArrayList,this@AddModuleStudents)
                    binding.dataLayout.visibility = View.VISIBLE
                    binding.loaderLayout.visibility = View.GONE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddModuleStudents, "error", Toast.LENGTH_SHORT).show()
            }
        })
    }
    fun onItemClick(position: Int) {
        var intent = Intent(this@AddModuleStudents,ConfirmAddModuleStudent::class.java).also {
            it.putExtra("studentId",studentArrayList[position].studentId)
            it.putExtra("moduleId",moduleIndex)
        }
        startActivity(intent)
    }


    private fun addStudent(moduleId:String){
        var nanasaId = binding.nanasaId.text.toString()
        if(nanasaId.isNotEmpty()){

            binding.nanasaSearching.visibility = View.GONE
            binding.nanasaLoading.visibility = View.VISIBLE

            FirebaseDatabase.getInstance().getReference("Student")
                .orderByChild("nanasaId")
                .equalTo(nanasaId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            for(childSnapshot in snapshot.children){
                                var intent = Intent(this@AddModuleStudents,ConfirmAddModuleStudent::class.java).also {
                                    it.putExtra("studentId",childSnapshot.key.toString())
                                    it.putExtra("moduleId",moduleId)
                                }
                                startActivity(intent)
                            }
                        }else{
                            binding.nanasaSearching.visibility = View.VISIBLE
                            binding.nanasaLoading.visibility = View.GONE
                            Toast.makeText(this@AddModuleStudents, "Entered Nanasa ID is wrong.", Toast.LENGTH_SHORT).show()
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        // handle error
                        binding.nanasaSearching.visibility = View.VISIBLE
                        binding.nanasaLoading.visibility = View.GONE
                    }
                })


        }else{
            Toast.makeText(this, "Enter a nanasa ID to search", Toast.LENGTH_SHORT).show()
        }
    }

}