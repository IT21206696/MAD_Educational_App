package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.myapplication.databinding.ActivityAddStudentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.util.UUID

class AddStudent : AppCompatActivity() {
    private lateinit var binding:ActivityAddStudentBinding
    private lateinit var user:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddStudentBinding.inflate(layoutInflater)
        user = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.register.setOnClickListener{
            addStudent();
        }
        binding.dashboard.setOnClickListener{
            startActivity(Intent(this,Dashboard::class.java))
            finish()
        }
        binding.addAnother.setOnClickListener{
            binding.fname.text.clear()
            binding.lname.text.clear()
            binding.phone.text.clear()
            binding.email.text.clear()
            binding.nanasaId.text.clear()
            binding.password.text.clear()
            binding.cpassword.text.clear()
            binding.loginLayout.visibility = View.VISIBLE
            binding.loaderLayout.visibility = View.GONE
            binding.successLayout.visibility = View.GONE
        }
    }

    private fun addStudent(){
        var fName = binding.fname.text.toString()
        var lName = binding.lname.text.toString()
        var phone = binding.phone.text.toString()
        var email = binding.email.text.toString()
        var nanasaId = binding.nanasaId.text.toString()
        var password = binding.password.text.toString()
        var cpassword = binding.cpassword.text.toString()


        if(fName.isNotEmpty() && lName.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && cpassword.isNotEmpty() && nanasaId.isNotEmpty()) {
            if (password == cpassword) {
                binding.loginLayout.visibility = View.GONE
                binding.loaderLayout.visibility = View.VISIBLE

                FirebaseDatabase.getInstance().getReference("Student")
                    .orderByChild("email")
                    .equalTo(email)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                binding.loginLayout.visibility = View.VISIBLE
                                binding.loaderLayout.visibility = View.GONE
                                Toast.makeText(this@AddStudent, "Entered Email address is already registered with another account.", Toast.LENGTH_SHORT).show()
                            }else{

                                val uniqueId = UUID.randomUUID().toString()
                                val student = Student(fName,lName,email,password,phone,nanasaId,uniqueId,user.uid.toString())
                                FirebaseDatabase.getInstance().getReference("Student/$uniqueId").setValue(student).addOnSuccessListener {
                                    binding.loginLayout.visibility = View.GONE
                                    binding.loaderLayout.visibility = View.GONE
                                    binding.successLayout.visibility = View.VISIBLE
                                }.addOnFailureListener{
                                    binding.loginLayout.visibility = View.VISIBLE
                                    binding.loaderLayout.visibility = View.GONE
                                    Toast.makeText(this@AddStudent, "Failed!!. Server Error", Toast.LENGTH_SHORT).show()
                                }

                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            // handle error
                            binding.loginLayout.visibility = View.VISIBLE
                            binding.loaderLayout.visibility = View.GONE
                        }
                    })
            }else{
                Toast.makeText(this, "Passwords are not matched", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "Fill all the inputs", Toast.LENGTH_SHORT).show()
        }
    }

}