package com.example.sustitutorio;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

public class CanvasView extends View {

    private Paint polygonPaint;
    private Paint circlePaint;
    private Paint textPaint;
    private Path path;
    private List<float[][]> rooms = new ArrayList<>();
    private List<float[]> pictures = new ArrayList<>();
    private List<String> roomNames = new ArrayList<>();

    public CanvasView(Context context) {
        super(context);
        init(context);
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        polygonPaint = new Paint();
        polygonPaint.setColor(Color.BLUE);
        polygonPaint.setStyle(Paint.Style.STROKE);
        polygonPaint.setStrokeWidth(5);

        circlePaint = new Paint();
        circlePaint.setColor(Color.CYAN);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);

        path = new Path();

        // Leer datos de los archivos CSV
        readDataFromCSV(context);
    }

    private void readDataFromCSV(Context context) {
        List<String[]> roomData = CSVReader.readCSV(context, "rooms.csv");
        for (String[] room : roomData) {
            if (room.length < 9) {
                // Verificar que haya al menos 9 elementos (nombre + 8 coordenadas)
                continue;
            }
            roomNames.add(room[0]);
            float[][] coordinates = new float[(room.length - 1) / 2][2];
            for (int i = 1, j = 0; i < room.length; i += 2, j++) {
                coordinates[j][0] = Float.parseFloat(room[i]);
                coordinates[j][1] = Float.parseFloat(room[i + 1]);
            }
            rooms.add(coordinates);
        }

        List<String[]> pictureData = CSVReader.readCSV(context, "pictures.csv");
        for (String[] picture : pictureData) {
            if (picture.length < 3) {
                // Verificar que haya al menos 3 elementos (nombre + 2 coordenadas)
                continue;
            }
            float[] coordinates = new float[2];
            coordinates[0] = Float.parseFloat(picture[1]);
            coordinates[1] = Float.parseFloat(picture[2]);
            pictures.add(coordinates);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Dibujar polígonos
        for (int i = 0; i < rooms.size(); i++) {
            path.reset();
            float[][] room = rooms.get(i);
            path.moveTo(room[0][0] * 100, room[0][1] * 100);
            for (int j = 1; j < room.length; j++) {
                path.lineTo(room[j][0] * 100, room[j][1] * 100);
            }
            path.close();
            canvas.drawPath(path, polygonPaint);
            // Dibujar nombre del salón
            canvas.drawText(roomNames.get(i), room[0][0] * 100 + 20, room[0][1] * 100 + 120, textPaint);
        }

        // Dibujar pinturas
        for (int i = 0; i < pictures.size(); i++) {
            float[] picture = pictures.get(i);
            canvas.drawCircle(picture[0] * 100, picture[1] * 100, 30, circlePaint);
            canvas.drawText(String.valueOf(i + 1), picture[0] * 100 - 15, picture[1] * 100 + 15, textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX() / 100;
            float y = event.getY() / 100;
            for (int i = 0; i < rooms.size(); i++) {
                if (isPointInPolygon(x, y, rooms.get(i))) {
                    openFragmentDescripcion(roomNames.get(i));
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private boolean isPointInPolygon(float x, float y, float[][] polygon) {
        int intersectCount = 0;
        for (int i = 0; i < polygon.length - 1; i++) {
            if (rayCastIntersect(x, y, polygon[i], polygon[i + 1])) {
                intersectCount++;
            }
        }
        if (rayCastIntersect(x, y, polygon[polygon.length - 1], polygon[0])) {
            intersectCount++;
        }
        return (intersectCount % 2) == 1; // odd = inside, even = outside;
    }

    private boolean rayCastIntersect(float x, float y, float[] vertA, float[] vertB) {
        float aY = vertA[1];
        float bY = vertB[1];
        float aX = vertA[0];
        float bX = vertB[0];
        boolean intersect = ((aY > y) != (bY > y)) && (x < (bX - aX) * (y - aY) / (bY - aY) + aX);
        return intersect;
    }

    private void openFragmentDescripcion(String roomName) {
        FragmentActivity activity = (FragmentActivity) getContext();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentDescripcion.newInstance(roomName))
                .addToBackStack(null)
                .commit();
    }
}
