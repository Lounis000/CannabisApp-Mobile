package com.example.cannabisappmobile

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CircularProgressBar(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var progress: Int = 50
    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 40f // Augmenter l'épaisseur du cercle
    }

    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 70f // Augmenter la taille du texte
        textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val radius = Math.min(width, height) / 2 - paint.strokeWidth / 2

        // Draw the background circle
        paint.color = Color.LTGRAY
        canvas.drawCircle(width / 2, height / 2, radius, paint)

        // Change color based on progress
        paint.color = when {
            progress <= 100 -> Color.GREEN
            progress <= 200 -> Color.YELLOW
            else -> Color.RED
        }

        // Draw the progress circle
        val sweepAngle = 360 * (progress / 300f)
        canvas.drawArc(paint.strokeWidth / 2, paint.strokeWidth / 2, width - paint.strokeWidth / 2, height - paint.strokeWidth / 2, -90f, sweepAngle, false, paint)

        // Draw the progress text
        val progressText = "$progress / 300"
        canvas.drawText(progressText, width / 2, height / 2 + textPaint.textSize / 3, textPaint)
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate() // Redraw the view
    }
}
