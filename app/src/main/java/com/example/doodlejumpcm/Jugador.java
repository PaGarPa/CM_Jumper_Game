package com.example.doodlejumpcm;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Jugador {
    public float x, y;
    public float velocidadY = 0;
    private final float GRAVEDAD = 0.8f;
    private final float FUERZA_SALTO = -25f;
    private final float FUERZA_MUELLE = -60f; // Impulso de unas 10 plataformas
    public int ancho = 100, alto = 100;
    private int screenWidth;

    public Jugador(int screenX, int screenY) {
        this.screenWidth = screenX;
        this.x = screenX / 2f;
        this.y = screenY - 300;
    }

    public void actualizar() {
        velocidadY += GRAVEDAD;
        y += velocidadY;

        if (x + ancho < 0) x = screenWidth;
        else if (x > screenWidth) x = -ancho;
    }

    public void saltar() {
        velocidadY = FUERZA_SALTO;
    }

    public void saltoMuelle() {
        velocidadY = FUERZA_MUELLE;
    }

    public void dibujar(Canvas canvas, Paint paint) {
        paint.setColor(Color.GREEN);
        canvas.drawRect(x, y, x + ancho, y + alto, paint);
    }

    public Rect getHitbox() {
        return new Rect((int)x, (int)y, (int)(x + ancho), (int)(y + alto));
    }
}
