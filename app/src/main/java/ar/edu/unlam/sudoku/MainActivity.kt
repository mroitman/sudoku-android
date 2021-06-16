package ar.edu.unlam.sudoku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import ar.edu.unlam.sudoku.databinding.ActivityMainBinding

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
                    binding.difficultyLevel.text= when (progress) {
                        0 -> "Muy Fácil"
                        1 -> "Fácil"
                        2 -> "Normal"
                        3 -> "Difícil"
                        4 -> "Muy Difícil"
                        else->"Seleccione un Nivel"
                    }
                }
            }
        })
        }
    }




