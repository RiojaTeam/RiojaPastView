package es.riojateam.riojapastview.camara;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.ScaleGestureDetector;

public class ScaleListener extends
		ScaleGestureDetector.SimpleOnScaleGestureListener {

	private int mode = NONE;
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;

	private PointF last = new PointF();
	private PointF start = new PointF();
	private float minScale = 1f;
	private float maxScale = 3f;
	private float[] m;

	private int viewWidth, viewHeight;
	private static final int CLICK = 3;
	private float saveScale = 1f;
	protected float origWidth, origHeight;
	private int oldMeasuredWidth, oldMeasuredHeight;

	private Matrix matrix;

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		this.mode = ZOOM;
		return true;
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		float mScaleFactor = detector.getScaleFactor();
		float origScale = this.saveScale;
		this.saveScale *= mScaleFactor;
		if (this.saveScale > this.maxScale) {
			this.saveScale = this.maxScale;
			mScaleFactor = this.maxScale / origScale;
		} else if (this.saveScale < this.minScale) {
			this.saveScale = this.minScale;
			mScaleFactor = this.minScale / origScale;
		}

		if (this.origWidth * this.saveScale <= this.viewWidth
				|| this.origHeight * this.saveScale <= this.viewHeight)
			this.matrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2,
					viewHeight / 2);
		else
			this.matrix.postScale(mScaleFactor, mScaleFactor,
					detector.getFocusX(), detector.getFocusY());

		fixTrans();
		return true;
	}

	void fixTrans() {
		this.matrix.getValues(m);
		float transX = this.m[Matrix.MTRANS_X];
		float transY = this.m[Matrix.MTRANS_Y];

		float fixTransX = getFixTrans(transX, this.viewWidth, this.origWidth
				* this.saveScale);
		float fixTransY = getFixTrans(transY, this.viewHeight, this.origHeight
				* this.saveScale);

		if (fixTransX != 0 || fixTransY != 0)
			this.matrix.postTranslate(fixTransX, fixTransY);
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
}