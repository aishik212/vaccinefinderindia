package com.simpleapps.vaccinefinder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simpleapps.vaccinefinder.databinding.PdfViewerLayoutBinding

class PDFViewer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflate = PdfViewerLayoutBinding.inflate(layoutInflater)
        setContentView(inflate.root)
        inflate.pdfView.fromAsset("swl").load()
    }
}