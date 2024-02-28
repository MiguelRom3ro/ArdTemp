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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var radioButton1 : RadioButton? = null
    private var radioButton2 : RadioButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        radioButton1 = binding.radioButton1
        radioButton2 = binding.radioButton2


        binding.switchFbd.setOnCheckedChangeListener { _, b ->
            if(b){
                binding.tilDate.visibility = View.VISIBLE
            }else{
                binding.tilDate.visibility = View.GONE
            }
        }

        binding.btnSearch.setOnClickListener {
            searchTemperature()
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
                apiRequest(true)
            }
        }else{
            apiRequest(false)
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


    @OptIn(DelicateCoroutinesApi::class)
    private fun apiRequest(withDate : Boolean) {
        val url: String
        var optionRG = 0
        if (radioButton1?.isChecked == true) {
            optionRG = 1
        } else if (radioButton2?.isChecked == true) {
            optionRG = 2
        }

        url = if (withDate) {
            val fecha = binding.tilDate.hint.toString()
            if (optionRG == 0) {
                "https://arduinotomobile.000webhostapp.com/php/getData.php?date=$fecha"
            } else {
                "https://arduinotomobile.000webhostapp.com/php/getData.php?date=$fecha&filter=$optionRG"
            }
        } else {
            if (optionRG == 0) {
                "https://arduinotomobile.000webhostapp.com/php/getData.php"
            } else {
                "https://arduinotomobile.000webhostapp.com/php/getData.php?filter=$optionRG"
            }
        }

        GlobalScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()

            val dataList = mutableListOf<Temperature>()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    if (!responseData.isNullOrEmpty()) {
                        if (responseData.startsWith("{")) {
                            // El servidor devolvió un objeto JSON en lugar de un arreglo
                            val jsonObject = JSONObject(responseData)
                            if (jsonObject.has("mensaje")) {
                                val message = jsonObject.getString("mensaje")
                                println("Mensaje del servidor: $message")
                                // Aquí puedes mostrar el mensaje en la interfaz de usuario si lo deseas
                            } else {
                                println("Error: El servidor devolvió un objeto JSON inesperado")
                            }
                        } else if (responseData.startsWith("[")) {
                            // El servidor devolvió un arreglo JSON
                            val jsonArray = JSONArray(responseData)
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val temperature = jsonObject.getString("temperature").toInt()
                                val date = jsonObject.getString("date")
                                val time = jsonObject.getString("time")
                                val temperatureData = Temperature(temperature, date, time)
                                dataList.add(temperatureData)
                            }
                        } else {
                            println("Error: Respuesta del servidor no es JSON válido")
                        }
                    }
                } else {
                    println("Error en la solicitud: ${response.code}")
                }
            } catch (e: IOException) {
                println("Error al hacer la solicitud: ${e.message}")
            }

            GlobalScope.launch(Dispatchers.Main) {
                showInfo(dataList)
            }
        }
    }

    private fun showInfo(dataList: List<Temperature>){
        if (dataList.isEmpty()) {

            if(binding.rvTemperatures.visibility == View.VISIBLE){
                binding.rvTemperatures.visibility = View.GONE
                binding.tvPrueba.visibility = View.VISIBLE
            }
            // Mostrar mensaje de que no hay datos
            "No hay datos disponibles".also { binding.tvPrueba.text = it }

        } else {

            if(binding.rvTemperatures.visibility == View.GONE){
                binding.rvTemperatures.visibility = View.VISIBLE
                binding.tvPrueba.visibility = View.GONE
            }
            binding.rvTemperatures.layoutManager = LinearLayoutManager(binding.rvTemperatures.context, LinearLayoutManager.VERTICAL, false)
            binding.rvTemperatures.adapter = TemperatureViewAdapter(dataList)

        }
    }
}