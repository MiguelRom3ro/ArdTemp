package com.itess.ardtemp

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.itess.ardtemp.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.switchFbd.setOnCheckedChangeListener { _, b ->
            if(b){
                binding.tilDate.visibility = View.VISIBLE
            }else{
                binding.tilDate.visibility = View.GONE
            }
        }

        binding.tietDate.setOnClickListener{
            selectDate()
        }
    }

    private fun selectDate(){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        // Creando una instancia de DatePickerDialog y mostrándola
        val datePickerDialog = DatePickerDialog(
            this,
            { _, y, m, dayOfM ->
                val selectedDate =
                    "$dayOfM/${m + 1}/$y" // Se suma 1 al mes ya que es 0-indexed
                binding.tilDate.hint = selectedDate
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }

}