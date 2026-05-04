
package com.benjamovil.circuitowatch;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;

public class MainActivity extends Activity {

    private WebView webView;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Pantalla siempre encendida en el reloj
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            // Fondo negro
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);

            // Crear WebView
            webView = new WebView(getApplicationContext());
            webView.setBackgroundColor(Color.BLACK);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

            // Hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

            setContentView(webView);

            // Configurar WebSettings
            WebSettings ws = webView.getSettings();
            ws.setJavaScriptEnabled(true);
            ws.setDomStorageEnabled(true);
            ws.setAllowFileAccess(true);
            ws.setAllowContentAccess(true);
            ws.setAllowFileAccessFromFileURLs(true);
            ws.setAllowUniversalAccessFromFileURLs(true);
            ws.setCacheMode(WebSettings.LOAD_DEFAULT);
            ws.setLoadWithOverviewMode(true);
            ws.setUseWideViewPort(true);
            ws.setSupportZoom(false);
            ws.setBuiltInZoomControls(false);
            ws.setDisplayZoomControls(false);
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            ws.setMediaPlaybackRequiresUserGesture(false);

            // WebViewClient simple — sin bloquear nada
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(android.webkit.WebView view, String url) {
                    super.onPageFinished(view, url);
                }
                @Override
                public void onReceivedError(android.webkit.WebView view,
                        WebResourceRequest request, WebResourceError error) {
                    // No hacer nada — el HTML maneja sus propios errores
                }
            });

            webView.setWebChromeClient(new WebChromeClient());

            // Cargar con pequeño delay para que el WebView esté listo
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (webView != null) {
                        webView.loadUrl("file:///android_asset/watch.html");
                    }
                }
            }, 100);

        } catch (Exception e) {
            // Si algo falla, intentar carga directa
            if (webView != null) {
                webView.loadUrl("file:///android_asset/watch.html");
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
            webView.resumeTimers();
        }
    }

    @Override
    protected void onPause() {
        if (webView != null) {
            webView.onPause();
            webView.pauseTimers();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
