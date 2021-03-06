package com.spacecombat;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;

public class CanvasText extends GenericText
{
	private String text;
	private int color;
	private Typeface font;
	private static Canvas canvas;
	private static Paint paint;
	
	public static void setCanvas(final Canvas canvas) {
		CanvasText.canvas = canvas;
	}

	public static void setPaint(final Paint paint) {
		CanvasText.paint = paint;
	}
	
	public void create(final String text, String font, int color) {
		this.text = text;
		this.font = Typeface.create(font, 0);
		this.color = color;
	}
	
	public void setText  (final String text)
	{
		this.text = text;
	}
	
	public void setFont (final String font)
	{
		this.font = Typeface.create(font, 0);
	}
	
	public void setColor (final int color)
	{
		this.color = color;
	}	

	@Override
	public void draw(final float offsetx, final float offsety, final int rotx, final int roty, final float scalex, final float scaley) {
		//System.out.println("DRAWING:" + text + " AT (" + offsetx + "," + offsety + ")");
		//CanvasText.paint.setTypeface(this.font);
		//CanvasText.paint.setColor(this.color);
		//CanvasText.paint.setStrokeWidth(2);

		float temp = CanvasText.paint.getTextSize();
		//CanvasText.paint.setTextSize(temp*2*scalex);
		CanvasText.canvas.drawText(text, offsetx, offsety, paint);
		//CanvasText.paint.setTextSize(temp);

		//System.out.println(offsetx +","+ offsety+":"+text);
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int getWidth() {
		return 0;
	}

}
