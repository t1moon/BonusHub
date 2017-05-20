package com.example.bonuslib.qr;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.bonuslib.identificator.Identificator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.lang.Runnable;

/**
 * Created by ivan on 4/13/17.
 */

// renders in a new thread
public class QRRenderer implements Runnable {
    private QRDrawer qrReceiver; // activity to receive the QR code
    private QRCodeWriter renderer = new QRCodeWriter(); // QRCodeWriter instance
    private String identificator; // id
    private int width;
    private int height;

    // activity should be able to draw QR code
    public interface QRDrawer {
        public void drawQr(Bitmap img);
    }

    public QRRenderer(String id, QRDrawer receiver, int w, int h) {
        qrReceiver = receiver;
        identificator = id;
        width = w;
        height = h;
    }

    private Bitmap getBitmapFromBitMatrix(BitMatrix matrix) {
        Bitmap image = Bitmap.createBitmap(matrix.getWidth(), matrix.getHeight(), Bitmap.Config.RGB_565);
        for (int x = 0; x < matrix.getWidth(); ++x) {
            for (int y = 0; y < matrix.getHeight(); ++y) {
                image.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return image;
    }

    public Bitmap render() {
        try {
            BitMatrix matrix = renderer.encode(identificator, BarcodeFormat.QR_CODE, width, height);
            Bitmap image = getBitmapFromBitMatrix(matrix);
            qrReceiver.drawQr(image);
            return image;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void run() {
        render();
    }
}
