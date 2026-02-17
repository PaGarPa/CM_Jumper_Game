package com.example.doodlejumpcm;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Plataforma {
    public float x, y;
    public int ancho = 200, alto = 40;
    public boolean pisada = false;
    public boolean tieneJetpack = false;
    private int jetpackW = 60, jetpackH = 80;

    public Plataforma(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void dibujar(Canvas canvas, Paint paint) {
        paint.setColor(Color.BLUE);
        canvas.drawRect(x, y, x + ancho, y + alto, paint);

        if (tieneJetpack) {
            // Draw Jetpack (simple representation with rectangles)
            paint.setColor(Color.GRAY);
            canvas.drawRect(x + ancho / 2f - jetpackW / 2f, y - jetpackH, x + ancho / 2f + jetpackW / 2f, y, paint);
            paint.setColor(Color.RED);
            canvas.drawRect(x + ancho / 2f - jetpackW / 2f + 10, y - jetpackH + 10, x + ancho / 2f + jetpackW / 2f - 10, y - 10, paint);
        }
    }

    public Rect getHitbox() {
        return new Rect((int)x, (int)y, (int)(x + ancho), (int)(y + alto));
    }

    public Rect getJetpackHitbox() {
        if (!tieneJetpack) return new Rect(0,0,0,0);
        return new Rect((int)(x + ancho / 2f - jetpackW / 2f), (int)(y - jetpackH), (int)(x + ancho / 2f + jetpackW / 2f), (int)y);
    }
}