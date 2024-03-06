package com.itess.ardtemp

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.itess.ardtemp.adapter.TemperatureViewAdapter
import com.itess.ardtemp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var radioButton1 : RadioButton? = null
    private var radioButton2 : RadioButton? = null
    private val dataTemperatures = mutableListOf<Temperature>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        radioButton1 = binding.radioButton1
        radioButton2 = binding.radioButton2

        binding.switchFbd.setOnCheckedChangeListener { _, b ->
            showOrGoneSwitch(b)
        }

        binding.btnSearch.setOnClickListener {
            searchTemperature()
        }

        binding.tietDate.setOnClickListener{
            selectDate()
        }
    }

    private fun showOrGoneSwitch(b : Boolean){
        if(b){
            binding.tilDate.visibility = View.VISIBLE
        }else{
            binding.tilDate.visibility = View.GONE
        }
    }

    private fun initRecyclerView() {
        binding.rvTemperatures.layoutManager = LinearLayoutManager(binding.rvTemperatures.context, LinearLayoutManager.VERTICAL, false)
        binding.rvTemperatures.adapter = TemperatureViewAdapter(dataTemperatures)
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
                    "$y-${m + 1}-$dayOfM" // Se suma 1 al mes ya que es 0-indexed
                binding.tilDate.hint = selectedDate
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }

    private fun searchTemperature(){
        if(binding.switchFbd.isChecked){
            if(validateSelectDate()){
                request(true)
            }
        }else{
            request(false)
        }
    }

    private fun validateSelectDate() : Boolean{
        val getDate = binding.tilDate.hint.toString()
        return if(getDate[0].isDigit()){
            true
        }else{
            Toast.makeText(this, getString(R.string.must_select_date), Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun getRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://arduinotomobile.000webhostapp.com/php/getData.php/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun request(withDate : Boolean){
        val query = getQuery(withDate)
        CoroutineScope(Dispatchers.IO).launch {
            val call : Response<List<Temperature>> = getRetrofit().create(APIService::class.java).getTemperature(query)
            val temperatures : List<Temperature>? = call.body()
            runOnUiThread{
                if(call.isSuccessful){
                    val listTemperatures : List<Temperature> = temperatures ?: emptyList()
                    dataTemperatures.clear()
                    dataTemperatures.addAll(listTemperatures)
                    if(dataTemperatures.isNotEmpty()){
                        initRecyclerView()
                    }else{
                        notDataFound()
                    }
                }else{
                    showError()
                }
            }
        }
    }

    private fun getQuery(withDate: Boolean) : String{
        var query = "?"
        if(radioButton1?.isChecked == true){
            query += "filter=1"
        }else if(radioButton2?.isChecked == true){
            query += "filter=2"
        }
        if(withDate){
            val date = binding.tilDate.hint.toString()
            query += "&date=$date"
        }
        return query
    }

    private fun notDataFound() {
        initRecyclerView()
        Toast.makeText(this, "Datos no encontrados", Toast.LENGTH_SHORT).show()
    }

    private fun showError() {
        Toast.makeText(this, "Ah ocurrido un error", Toast.LENGTH_SHORT).show()
    }

}