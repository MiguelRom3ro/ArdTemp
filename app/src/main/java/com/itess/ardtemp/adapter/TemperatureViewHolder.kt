package com.itess.ardtemp.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.itess.ardtemp.Temperature
import com.itess.ardtemp.databinding.TemperaturesViewBinding

class TemperatureViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    // Referencias a las vistas dentro del elemento de producto
    private val binding = TemperaturesViewBinding.bind(view)

    // Función para actualizar las vistas con datos específicos de un Producto
    fun render(temperatureModel: Temperature) {

        if(temperatureModel.temperature > 27){
            Glide.with(binding.ivStatusTemperature.context)
                .load("https://cdn-icons-png.flaticon.com/512/3631/3631790.png")
                .centerCrop() //Adapta al contenido
                .diskCacheStrategy(DiskCacheStrategy.ALL) //Le asignamos que guarde tanto la imagen original como la modificada para despues no vuelva a cargarla
                .into(binding.ivStatusTemperature)
        }else if( temperatureModel.temperature > 18){
            Glide.with(binding.ivStatusTemperature.context)
                .load("https://cdn-icons-png.flaticon.com/512/190/190411.png")
                .centerCrop() //Adapta al contenido
                .diskCacheStrategy(DiskCacheStrategy.ALL) //Le asignamos que guarde tanto la imagen original como la modificada para despues no vuelva a cargarla
                .into(binding.ivStatusTemperature)
        }else{
            Glide.with(binding.ivStatusTemperature.context)
                .load("https://cdn-icons-png.flaticon.com/512/13882/13882677.png")
                .centerCrop() //Adapta al contenido
                .diskCacheStrategy(DiskCacheStrategy.ALL) //Le asignamos que guarde tanto la imagen original como la modificada para despues no vuelva a cargarla
                .into(binding.ivStatusTemperature)
        }

        "${temperatureModel.temperature}°".also { binding.tvTemperature.text = it }
        (temperatureModel.date + " " + temperatureModel.time).also { binding.tvDatetime.text = it }
    }
}