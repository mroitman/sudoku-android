package ar.edu.unlam.sudoku

import android.content.Intent
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

        binding.nivelDeDificultad = getString(R.string.medium)
        setSeekBarListener()
    }

    private fun setSeekBarListener() {
        binding.buttonNewGame.setOnClickListener { newGame() }

        binding.level.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.apply {
                    binding.difficultyLevel.text = getDifficultyLevel()
                }
            }
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun getDifficultyLevel() = when (binding.level.progress) {
        0 -> getString(R.string.super_easy)
        1 -> getString(R.string.easy)
        2 -> getString(R.string.medium)
        3 -> getString(R.string.hard)
        else -> throw Exception("Se seleccionó un nivel inexistente")
    }

    private fun newGame() {
        val intentNuevoJuego = Intent(this, SudokuActivity::class.java)
        startActivity(intentNuevoJuego)
    }
}




