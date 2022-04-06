package ru.meseen.dev.lan4tap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.meseen.dev.lan4tap.databinding.MainActivityBinding
import ru.meseen.dev.lan4tap.ui.main.MainFragment

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    private val vb by viewBinding(MainActivityBinding::bind)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

    companion object {
        init {
            System.loadLibrary("Lan4Gate");
        }
    }
}