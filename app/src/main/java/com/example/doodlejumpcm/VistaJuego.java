package com.example.doodlejumpcm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.Random;

public class VistaJuego extends SurfaceView implements Runnable {
    private Thread hilo;
    private boolean jugando;
    private Jugador player;
    private ArrayList<Plataforma> plataformas;
    private SurfaceHolder holder;
    private Paint paint;
    private int screenX, screenY;
    private Random random = new Random();
    private boolean jetpackActivo = false;
    private long jetpackTiempo;
    private int jetpackPuntosAcumulados = 0;
    
    private int score = 0;
    
    // Game States
    private enum State { MENU, CUSTOMIZE, PLAYING, GAME_OVER }
    private State currentState = State.MENU;

    // Colors
    private int currentBgColor = Color.WHITE;
    private int currentCharacterColor = Color.GREEN;
    private int[] availableColors = {Color.WHITE, Color.LTGRAY, Color.CYAN, Color.YELLOW, Color.MAGENTA};
    private int[] charColors = {Color.GREEN, Color.RED, Color.BLUE, Color.BLACK, Color.rgb(255, 165, 0)};
    private int bgIndex = 0;
    private int charIndex = 0;

    // Buttons
    private Rect playButton, customizeButton, bgColorButton, charColorButton, returnButton, restartButton, backToMenuButton;

    public VistaJuego(Context context, int x, int y) {
        super(context);
        this.screenX = x;
        this.screenY = y;
        holder = getHolder();
        paint = new Paint();
        
        initButtons();
        reiniciarJuego();
    }

    private void initButtons() {
        int btnW = 500, btnH = 150;
        int centerX = screenX / 2;
        int centerY = screenY / 2;

        playButton = new Rect(centerX - btnW/2, centerY - 200, centerX + btnW/2, centerY - 50);
        customizeButton = new Rect(centerX - btnW/2, centerY + 50, centerX + btnW/2, centerY + 200);
        
        bgColorButton = new Rect(centerX - btnW/2, centerY - 300, centerX + btnW/2, centerY - 150);
        charColorButton = new Rect(centerX - btnW/2, centerY - 100, centerX + btnW/2, centerY + 50);
        returnButton = new Rect(centerX - btnW/2, centerY + 100, centerX + btnW/2, centerY + 250);
        
        restartButton = new Rect(centerX - 200, centerY + 150, centerX + 200, centerY + 270);
        backToMenuButton = new Rect(centerX - 200, centerY + 300, centerX + 200, centerY + 420);
    }

    private void reiniciarJuego() {
        player = new Jugador(screenX, screenY);
        plataformas = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Plataforma p = new Plataforma(random.nextInt(screenX - 200), screenY - (i * 300));
            plataformas.add(p);
        }
        jetpackActivo = false;
        jetpackPuntosAcumulados = 0;
        score = 0;
    }

    @Override
    public void run() {
        while (jugando) {
            if (currentState == State.PLAYING) {
                actualizar();
            }
            dibujar();
            try { Thread.sleep(16); } catch (Exception e) {}
        }
    }

    private void actualizar() {
        if (jetpackActivo) {
            player.y -= 30; // Vuela hacia arriba
            long tiempoTranscurrido = System.currentTimeMillis() - jetpackTiempo;
            
            if (tiempoTranscurrido > 1500) { // 1,5 segundos de vuelo
                jetpackActivo = false;
            }
        } else {
            player.actualizar();
        }

        float scrollAmount = 0;
        if (player.y < screenY / 2f) {
            scrollAmount = screenY / 2f - player.y;
            if (!jetpackActivo) player.y = screenY / 2f;
        }

        for (Plataforma p : plataformas) {
            if (!jetpackActivo && player.velocidadY > 0) {
                if (Rect.intersects(player.getHitbox(), p.getHitbox())) {
                    player.saltar();
                    if (!p.pisada) {
                        p.pisada = true;
                        score++;
                    }
                    if (p.esRompible) {
                        p.rota = true;
                    }
                } else if (p.tieneMuelle && Rect.intersects(player.getHitbox(), p.getMuelleHitbox())) {
                    player.saltoMuelle();
                    p.tieneMuelle = false; // El muelle se usa una vez
                }
            }
            if (p.tieneJetpack && Rect.intersects(player.getHitbox(), p.getJetpackHitbox())) {
                p.tieneJetpack = false;
                jetpackActivo = true;
                jetpackTiempo = System.currentTimeMillis();
                jetpackPuntosAcumulados = 0;
                score += 50; // Sume 50 puntos en el momento que lo cojes
            }
            p.y += scrollAmount;
        }

        for (Plataforma p : plataformas) {
            if (p.y > screenY) {
                float minY = screenY;
                for (Plataforma plat : plataformas) {
                    if (plat.y < minY) minY = plat.y;
                }
                p.y = minY - 300;
                p.x = random.nextInt(screenX - 200);
                p.pisada = false;
                p.rota = false;
                
                // Ratio de aparición
                p.tieneJetpack = (random.nextInt(30) == 0); // 1 de cada 30
                p.tieneMuelle = !p.tieneJetpack && (random.nextInt(10) == 0); // 1 de cada 10
                p.esRompible = !p.tieneJetpack && !p.tieneMuelle && (random.nextInt(20) == 0); // 1 de cada 20
            }
        }

        if (player.y > screenY) {
            currentState = State.GAME_OVER;
        }
    }

    private void dibujar() {
        if (holder.getSurface().isValid()) {
            Canvas c = holder.lockCanvas();
            c.drawColor(currentBgColor);
            
            if (currentState == State.PLAYING || currentState == State.GAME_OVER) {
                for (Plataforma p : plataformas) p.dibujar(c, paint);
                
                // Draw player with current color
                paint.setColor(currentCharacterColor);
                c.drawRect(player.x, player.y, player.x + player.ancho, player.y + player.alto, paint);

                paint.setColor(Color.BLACK);
                paint.setTextSize(60);
                String scoreText = "Score: " + score;
                c.drawText(scoreText, screenX - paint.measureText(scoreText) - 50, 130, paint);
            }

            if (currentState == State.MENU) {
                drawMenu(c);
            } else if (currentState == State.CUSTOMIZE) {
                drawCustomize(c);
            } else if (currentState == State.GAME_OVER) {
                drawGameOver(c);
            }
            
            holder.unlockCanvasAndPost(c);
        }
    }

    private void drawMenu(Canvas c) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(120);
        String title = "DOODLE JUMP";
        c.drawText(title, screenX/2 - paint.measureText(title)/2, screenY/4, paint);

        drawButton(c, playButton, "PLAY", Color.BLUE);
        drawButton(c, customizeButton, "CUSTOMIZE", Color.DKGRAY);
    }

    private void drawCustomize(Canvas c) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(100);
        String title = "CUSTOMIZATION";
        c.drawText(title, screenX/2 - paint.measureText(title)/2, screenY/6, paint);

        drawButton(c, bgColorButton, "BG COLOR", currentBgColor == Color.WHITE ? Color.GRAY : currentBgColor);
        drawButton(c, charColorButton, "CHAR COLOR", currentCharacterColor);
        drawButton(c, returnButton, "RETURN", Color.RED);
    }

    private void drawGameOver(Canvas c) {
        paint.setColor(Color.argb(150, 0, 0, 0));
        c.drawRect(0, 0, screenX, screenY, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(120);
        c.drawText("YOU LOSE", screenX/2 - paint.measureText("YOU LOSE")/2, screenY/2 - 150, paint);
        
        paint.setTextSize(80);
        String finalScore = "Final Score: " + score;
        c.drawText(finalScore, screenX/2 - paint.measureText(finalScore)/2, screenY/2 + 80, paint);

        drawButton(c, restartButton, "RESTART", Color.RED);
        drawButton(c, backToMenuButton, "MENU", Color.DKGRAY);
    }

    private void drawButton(Canvas c, Rect rect, String text, int color) {
        paint.setColor(color);
        c.drawRect(rect, paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        float textW = paint.measureText(text);
        c.drawText(text, rect.centerX() - textW/2, rect.centerY() + 15, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int)event.getX();
            int y = (int)event.getY();

            if (currentState == State.MENU) {
                if (playButton.contains(x, y)) {
                    reiniciarJuego();
                    currentState = State.PLAYING;
                } else if (customizeButton.contains(x, y)) {
                    currentState = State.CUSTOMIZE;
                }
            } else if (currentState == State.CUSTOMIZE) {
                if (bgColorButton.contains(x, y)) {
                    bgIndex = (bgIndex + 1) % availableColors.length;
                    currentBgColor = availableColors[bgIndex];
                } else if (charColorButton.contains(x, y)) {
                    charIndex = (charIndex + 1) % charColors.length;
                    currentCharacterColor = charColors[charIndex];
                } else if (returnButton.contains(x, y)) {
                    currentState = State.MENU;
                }
            } else if (currentState == State.GAME_OVER) {
                if (restartButton.contains(x, y)) {
                    reiniciarJuego();
                    currentState = State.PLAYING;
                } else if (backToMenuButton.contains(x, y)) {
                    currentState = State.MENU;
                }
            }
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public void mover(float tx) { 
        if (currentState == State.PLAYING) player.x -= tx * 5; 
    }
    public void resume() { jugando = true; hilo = new Thread(this); hilo.start(); }
    public void pause() { jugando = false; try { hilo.join(); } catch (Exception e) {} }
}
