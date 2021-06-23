package ar.edu.unlam.sudoku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import ar.edu.unlam.sudoku.databinding.ActivityMainBinding
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.level.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.apply {
                    binding.nivelDeDificultad = when (this.progress) {
                        0 -> getString(R.string.super_easy)
                        1 -> getString(R.string.easy)
                        2 -> getString(R.string.medium)
                        3 -> getString(R.string.hard)
                        else -> throw Exception("Se seleccionó un nivel inexistente")
                    }
                }
            }
        })
        binding.nivelDeDificultad = getString(R.string.medium)
    }
}




