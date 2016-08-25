package akai.pet.one.piece;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import akai.floatView.op.luffy.R;

public class AppWebView extends Activity {
	
	private WebView mWebView;
	private ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_web_view);
        
        mWebView = (WebView)findViewById(R.id.webView);
        mProgressBar = (ProgressBar)findViewById(R.id.webView_progressBar);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.setWebViewClient(new WebViewClient(){
        	public boolean shouldOverrideUrlLoading(WebView view, String url) {       
        		super.shouldOverrideUrlLoading(view, url);      
        		return false;       
        	}
        });
        mWebView.setWebChromeClient(new WebChromeClient(){       

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if(newProgress >= 0 && newProgress < 100){
					mProgressBar.setVisibility(View.VISIBLE);
				}
				else{
					mProgressBar.setVisibility(View.GONE);
				}
				
			}
			
			
        });
        mWebView.setDownloadListener(new DownloadListener() {
			
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				Uri uri = Uri.parse(url);  
	            Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
	            startActivity(intent);  
			}
		});
        mWebView.loadUrl("http://0kai.net/app/");
    }
//    
//	@Override
//	public void onBackPressed() {
//		mWebView.goBack();       
//	}

}

