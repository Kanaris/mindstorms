package charkes.siarhei.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Custom view to display 
 * battery status
 * @author Merl1n
 *
 */
public class BatteryStatus extends View {
	/**
	 * Size of the view
	 */
	private static final int VIEW_WIDTH = 100;
	private static final int VIEW_HEIGHT = 30;
	/**
	 * Colors
	 */
	private static final Paint paintBackground = new Paint();
	private static final Paint paintVoltage = new Paint();
	private static final Paint paintText = new Paint();
	static {
		paintBackground.setColor(Color.GRAY);
		paintVoltage.setColor(Color.GREEN);
		
		paintText.setColor(Color.BLUE);
		paintText.setTextSize(20);
	}
	private float voltage = (float) 0.0;
	
	public BatteryStatus(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public BatteryStatus(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BatteryStatus(Context context) {
		super(context);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawRoundRect(new RectF(0, 0, VIEW_WIDTH, VIEW_HEIGHT), 8, 8, paintBackground);
		canvas.drawRoundRect(new RectF(0, 0, (int)(voltage*VIEW_WIDTH/9), VIEW_HEIGHT), 8, 8, paintVoltage);
		canvas.drawText("" + voltage, 10, VIEW_HEIGHT - 5, paintText);
	}
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(VIEW_WIDTH, VIEW_HEIGHT);
	}	
	/**
	 * Setters and getters
	 */
	public float getVoltage() {
		return voltage;
	}
	public void setVoltage(float voltage) {
		this.voltage = voltage;
		invalidate();
	}
}
