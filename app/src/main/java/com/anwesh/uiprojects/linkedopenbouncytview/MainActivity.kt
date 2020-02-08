package com.anwesh.uiprojects.linkedopenbouncytview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.openbouncytview.OpenBouncyTView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OpenBouncyTView.create(this)
    }
}
