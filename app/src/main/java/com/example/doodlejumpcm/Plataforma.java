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
    public boolean tieneMuelle = false;
    public boolean esRompible = false;
    public boolean rota = false;
    private int jetpackW = 60, jetpackH = 80;
    private int muelleW = 40, muelleH = 30;

    public Plataforma(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void dibujar(Canvas canvas, Paint paint) {
        if (rota) return;

        if (esRompible) {
            paint.setColor(Color.rgb(139, 69, 19)); // Marrón para plataformas de madera/rompibles
        } else {
            paint.setColor(Color.BLUE);
        }
        
        canvas.drawRect(x, y, x + ancho, y + alto, paint);

        if (esRompible) {
            // Dibujar grietas
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(3);
            canvas.drawLine(x + 20, y + 5, x + 50, y + 35, paint);
            canvas.drawLine(x + 80, y + 10, x + 60, y + 30, paint);
            canvas.drawLine(x + 130, y + 5, x + 160, y + 35, paint);
        }

        if (tieneJetpack) {
            paint.setColor(Color.GRAY);
            canvas.drawRect(x + ancho / 2f - jetpackW / 2f, y - jetpackH, x + ancho / 2f + jetpackW / 2f, y, paint);
            paint.setColor(Color.RED);
            canvas.drawRect(x + ancho / 2f - jetpackW / 2f + 10, y - jetpackH + 10, x + ancho / 2f + jetpackW / 2f - 10, y - 10, paint);
        } else if (tieneMuelle) {
            paint.setColor(Color.LTGRAY);
            canvas.drawRect(x + ancho / 2f - muelleW / 2f, y - muelleH, x + ancho / 2f + muelleW / 2f, y, paint);
        }
    }

    public Rect getHitbox() {
        if (rota) return new Rect(0,0,0,0);
        return new Rect((int)x, (int)y, (int)(x + ancho), (int)(y + alto));
    }

    public Rect getJetpackHitbox() {
        if (!tieneJetpack || rota) return new Rect(0,0,0,0);
        return new Rect((int)(x + ancho / 2f - jetpackW / 2f), (int)(y - jetpackH), (int)(x + ancho / 2f + jetpackW / 2f), (int)y);
    }

    public Rect getMuelleHitbox() {
        if (!tieneMuelle || rota) return new Rect(0,0,0,0);
        return new Rect((int)(x + ancho / 2f - muelleW / 2f), (int)(y - muelleH), (int)(x + ancho / 2f + muelleW / 2f), (int)y);
    }
}
