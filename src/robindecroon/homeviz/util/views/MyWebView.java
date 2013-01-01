package robindecroon.homeviz.util.views;

import robindecroon.homeviz.listeners.TouchListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

public class MyWebView extends WebView implements MyView {

	private TouchListener listener;

	public MyWebView(Context context) {
		super(context);
	}

	public MyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	};

	
	public MyWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs,defStyle);
	}
	
	public void setListener(TouchListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return listener.onTouch(v, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean consumed = super.onTouchEvent(event);
		onTouch(this, event);
		return consumed;
	}

}