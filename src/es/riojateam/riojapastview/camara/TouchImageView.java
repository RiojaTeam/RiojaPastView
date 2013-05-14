/*
 * TouchImageView.java
 * By: Michael Ortiz
 * Updated By: Patrick Lackemacher
 * Updated By: Babay88
 * -------------------
 * Extends Android ImageView to include pinch zooming and panning.
 */

package es.riojateam.riojapastview.camara;

import es.riojateam.riojapastview.SuperponerActivity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.view.View.OnTouchListener;

public class TouchImageView extends ImageView implements OnTouchListener {

	Matrix matrix;

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// Remember some things for zooming
	PointF last = new PointF();
	PointF start = new PointF();
	float minScale = 1f;
	float maxScale = 3f;
	float[] m;

	int viewWidth, viewHeight;
	static final int CLICK = 3;
	float saveScale = 1f;
	protected float origWidth, origHeight;
	int oldMeasuredWidth, oldMeasuredHeight;

	ScaleGestureDetector mScaleDetector;
	Context context;
	String ruta;

	Paint paintCuadro = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint paintRelleno = new Paint(Paint.ANTI_ALIAS_FLAG);

	private float inicioTocable;
	private float finTocable;
	private float valor;

	private float alturaTotal;
	float margenInferior = 40f;
	float alturaBarra = 20f;
	float margenLateral = 30f;

	public TouchImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		sharedConstructing(context);
	}

	public TouchImageView(Context context, String ruta, Drawable image) {
		super(context);

		sharedConstructing(context);

		this.ruta = ruta;

		Display display = ((SuperponerActivity) getContext())
				.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		this.alturaTotal = size.y;

		this.inicioTocable = margenLateral;
		this.finTocable = size.x - margenLateral;

		paintCuadro.setColor(Color.WHITE);
		paintCuadro.setStyle(Paint.Style.STROKE);

		paintRelleno.setColor(Color.WHITE);
		paintRelleno.setStrokeWidth(1);

		this.valor = (this.finTocable - this.inicioTocable) / 2f;

		this.setImageDrawable(image);
		this.tranformaDrawable(this.valor);

	}

	private void sharedConstructing(Context context) {
		super.setClickable(true);
		this.context = context;
		this.mScaleDetector = new ScaleGestureDetector(context,
				new ScaleListener());
		this.matrix = new Matrix();
		m = new float[9];
		this.setImageMatrix(matrix);
		this.setScaleType(ScaleType.MATRIX);

		this.setOnTouchListener(this);
	}

	public void setMaxZoom(float x) {
		maxScale = x;
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			mode = ZOOM;
			return true;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float mScaleFactor = detector.getScaleFactor();
			float origScale = saveScale;
			saveScale *= mScaleFactor;
			if (saveScale > maxScale) {
				saveScale = maxScale;
				mScaleFactor = maxScale / origScale;
			} else if (saveScale < minScale) {
				saveScale = minScale;
				mScaleFactor = minScale / origScale;
			}

			if (origWidth * saveScale <= viewWidth
					|| origHeight * saveScale <= viewHeight)
				matrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2,
						viewHeight / 2);
			else
				matrix.postScale(mScaleFactor, mScaleFactor,
						detector.getFocusX(), detector.getFocusY());

			fixTrans();
			return true;
		}
	}

	void fixTrans() {
		matrix.getValues(m);
		float transX = m[Matrix.MTRANS_X];
		float transY = m[Matrix.MTRANS_Y];

		float fixTransX = getFixTrans(transX, viewWidth, origWidth * saveScale);
		float fixTransY = getFixTrans(transY, viewHeight, origHeight
				* saveScale);

		if (fixTransX != 0 || fixTransY != 0)
			matrix.postTranslate(fixTransX, fixTransY);
	}

	float getFixTrans(float trans, float viewSize, float contentSize) {
		float minTrans, maxTrans;

		if (contentSize <= viewSize) {
			minTrans = 0;
			maxTrans = viewSize - contentSize;
		} else {
			minTrans = viewSize - contentSize;
			maxTrans = 0;
		}

		if (trans < minTrans)
			return -trans + minTrans;
		if (trans > maxTrans)
			return -trans + maxTrans;
		return 0;
	}

	float getFixDragTrans(float delta, float viewSize, float contentSize) {
		if (contentSize <= viewSize) {
			return 0;
		}
		return delta;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		viewWidth = MeasureSpec.getSize(widthMeasureSpec);
		viewHeight = MeasureSpec.getSize(heightMeasureSpec);

		//
		// Rescales image on rotation
		//
		if (oldMeasuredHeight == viewWidth && oldMeasuredHeight == viewHeight
				|| viewWidth == 0 || viewHeight == 0)
			return;
		oldMeasuredHeight = viewHeight;
		oldMeasuredWidth = viewWidth;

		if (saveScale == 1) {
			// Fit to screen.
			float scale;

			Drawable drawable = getDrawable();
			if (drawable == null || drawable.getIntrinsicWidth() == 0
					|| drawable.getIntrinsicHeight() == 0)
				return;
			int bmWidth = drawable.getIntrinsicWidth();
			int bmHeight = drawable.getIntrinsicHeight();

			Log.d("bmSize", "bmWidth: " + bmWidth + " bmHeight : " + bmHeight);

			float scaleX = (float) viewWidth / (float) bmWidth;
			float scaleY = (float) viewHeight / (float) bmHeight;
			scale = Math.min(scaleX, scaleY);
			matrix.setScale(scale, scale);

			// Center the image
			float redundantYSpace = viewHeight - (scale * bmHeight);
			float redundantXSpace = viewWidth - (scale * bmWidth);
			redundantYSpace /= 2;
			redundantXSpace /= 2;

			matrix.postTranslate(redundantXSpace, redundantYSpace);

			origWidth = viewWidth - 2 * redundantXSpace;
			origHeight = viewHeight - 2 * redundantYSpace;
			setImageMatrix(matrix);
		}
		fixTrans();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mScaleDetector.onTouchEvent(event);
		PointF curr = new PointF(event.getX(), event.getY());

		if (event.getPointerCount() == 2) {

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				last.set(curr);
				start.set(last);
				mode = DRAG;
				break;

			case MotionEvent.ACTION_MOVE:
				if (mode == DRAG) {
					float deltaX = curr.x - last.x;
					float deltaY = curr.y - last.y;
					float fixTransX = getFixDragTrans(deltaX, viewWidth,
							origWidth * saveScale);
					float fixTransY = getFixDragTrans(deltaY, viewHeight,
							origHeight * saveScale);
					matrix.postTranslate(fixTransX, fixTransY);
					fixTrans();
					last.set(curr.x, curr.y);
				}
				break;

			case MotionEvent.ACTION_UP:
				mode = NONE;
				int xDiff = (int) Math.abs(curr.x - start.x);
				int yDiff = (int) Math.abs(curr.y - start.y);
				if (xDiff < CLICK && yDiff < CLICK)
					performClick();
				break;

			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				break;
			}

			setImageMatrix(matrix);
			invalidate();
		}
		return true; // indicate event was handled
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawRect(this.inicioTocable, this.alturaTotal
				- this.margenInferior - this.alturaBarra, this.finTocable,
				this.alturaTotal - this.margenInferior, paintCuadro);
		canvas.drawRect(this.inicioTocable + 3, this.alturaTotal
				- this.margenInferior - this.alturaBarra + 5, this.valor,
				this.alturaTotal - this.margenInferior - 5, paintRelleno);

		invalidate();
	}

	public void setDatos(float deltaX) {

		if (this.valor + deltaX > this.inicioTocable + 1
				&& this.valor + deltaX < this.finTocable - 1) {
			this.valor += deltaX;
			this.tranformaDrawable(valor);
		}

	}

	public void tranformaDrawable(float valor) {

		float porcentaje = this.inicioTocable + valor / this.finTocable
				- this.inicioTocable;
		System.out.println("porcentaje: " + porcentaje);
		float porcentajeAlpha = porcentaje * 255;
		System.out.println("porcentajeAlpha: " + porcentajeAlpha);
		getDrawable().setAlpha((int) porcentajeAlpha);
	}
}
