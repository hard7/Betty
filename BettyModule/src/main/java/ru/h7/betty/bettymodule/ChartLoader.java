package ru.h7.betty.bettymodule;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Handler;
import android.provider.Settings;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.util.Vector;


interface ProgressPrimeAccessor {
    int getCount();
    int getDay(int index);
    int getMonth(int index);
    int getYear(int index);
    int getFoodEstimate(int index);
}

//class SimpleProgressPrimeAccessor42 implements ProgressPrimeAccessor {
//    @Override
//    public int getCount() {
//        return 42;
//    }
//}


class ChartLoader {
    static private String URL = "file:///android_asset/calendar.html";

    public interface BitmapHandler {
        void handleBitmap(Bitmap bitmap);
    }

    private final WebView webView;
    private Button button;     //FIXME

//    public ChartLoader(WebView webView) {
//        this(webView, null, null);
//    }

    public ChartLoader(WebView webView, ProgressPrimeAccessor accessor) {
        this(webView, accessor, null);
    }

    public ChartLoader(WebView webView_, ProgressPrimeAccessor accessor,  Button button_) { //FIXME button_
        webView = webView_;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        WebAppInterface webApp = new WebAppInterface(accessor);
        webApp.count = 14;
        webView.addJavascriptInterface(webApp, WebAppInterface.NAME);
//        initWebViewClient();

        button = button_;       //FIXME
        appendOut(":");
    }

    public void handleBitmapResponse(String url, BitmapHandler handler) {       //FIXME depricated
        webView.loadUrl(url);
        getScreenshot(handler, 0);
    }

    public void handleBitmapResponse(BitmapHandler handler) {
        webView.loadUrl(URL);
        getScreenshot(handler, 0);
    }

    private Bitmap makeMocBitmap() {
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    }

    private void initWebViewClient() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
//                getScreenshot();
            }
        });
    }

    private void getScreenshot(final BitmapHandler bitmapHandler, final int iterateNumber) {
        if(iterateNumber > 40) return;
        Picture picture = webView.capturePicture();
        int width = picture.getWidth();

        System.out.println("--------------------> " + width);

        appendOut("[" + Integer.toString(width) + "]");
        if(picture.getWidth() <= 320) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getScreenshot(bitmapHandler, iterateNumber + 1);
                }
            }, 200);
            return;
        }

        Bitmap bitmap = Bitmap.createBitmap( picture.getWidth(),
                picture.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        picture.draw(canvas);
        bitmapHandler.handleBitmap(bitmap);
    }

    private void appendOut(String msg) {
        if(button != null) button.setText(button.getText() + " " + msg);
    }

    static private class WebAppInterface {
        public int count = 5;
        private Vector<Integer> days = new Vector<Integer>();
        public static String NAME = "Android";

        ProgressPrimeAccessor progressPrimeAccessor;

        WebAppInterface(ProgressPrimeAccessor progressPrimeAccessor_) {
            progressPrimeAccessor = progressPrimeAccessor_;
            days.add(5);
            days.add(6);
            days.add(7);
        }

        @JavascriptInterface
        public int getX() {
            return 4000;
        }

        @JavascriptInterface
        public int getCount() {

//            return _demoGetInt.get();
            return progressPrimeAccessor.getCount();        // FAIL
//            return 5;                                     // OK
//            return days.size();                           // OK
        }

        @JavascriptInterface
        public int getDay(int index) {
            return progressPrimeAccessor.getDay(index);
        }

        @JavascriptInterface
        public int getMonth(int index) {
            return progressPrimeAccessor.getMonth(index);
        }

        @JavascriptInterface
        public int getYear(int index) {
            return progressPrimeAccessor.getYear(index);
        }

        @JavascriptInterface
        public int getFoodEstimate(int index) {
            return progressPrimeAccessor.getFoodEstimate(index);
        }
    }
}