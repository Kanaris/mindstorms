package charkes.siarhei.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ConnectionStatus extends View {
	/*****************************************
	 * Constants
	 */
	private static final int VIEW_HEIGHT = 30;
	private static final Paint disconnected = new Paint();
	private static final Paint connected = new Paint();
	static {
		disconnected.setColor(Color.RED);
		connected.setColor(Color.GREEN);
	}
	/*****************************************
	 * Variables
	 */
	private boolean isConnected = false;
	
	public ConnectionStatus(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public ConnectionStatus(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public ConnectionStatus(Context context) {
		super(context);
	}	
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isConnected) {
			canvas.drawCircle(VIEW_HEIGHT/2, VIEW_HEIGHT/2, VIEW_HEIGHT/2, connected);
		} else {
			canvas.drawCircle(VIEW_HEIGHT/2, VIEW_HEIGHT/2, VIEW_HEIGHT/2, disconnected);			
		}
	}
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(VIEW_HEIGHT, VIEW_HEIGHT);
	}
	public boolean isConnected() {
		return isConnected;
	}
	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
		invalidate();
	}	
}
