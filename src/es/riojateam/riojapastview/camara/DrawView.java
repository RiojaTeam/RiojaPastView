package es.riojateam.riojapastview.camara;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class DrawView extends SurfaceView implements OnClickListener {

	private Paint textPaint = new Paint();
	private Paint paint = new Paint();
	private Bitmap bitmap;

	// -------------------------
	private Matrix matrix;
	private float[] m;

	private ScaleGestureDetector mScaleDetector;

	private Context context;

	public DrawView(Context context) {
		super(context);
		/*
		 * textPaint.setARGB(255, 200, 0, 0); textPaint.setTextSize(60); bitmap
		 * =
		 * BitmapFactory.decodeResource(getContext().getResources(),R.drawable.
		 * antartica1); paint.setAlpha(100); setWillNotDraw(false);
		 */

	}

	public DrawView(Context context, String ruta) {
		super(context);
		// Create out paint to use for drawing
		textPaint.setARGB(255, 200, 0, 0);
		textPaint.setTextSize(60);

		String uri = "drawable/" + ruta/* +"_tn" */;
		int imageResource = getContext()
				.getApplicationContext()
				.getResources()
				.getIdentifier(uri, null,
						getContext().getApplicationContext().getPackageName());

		bitmap = BitmapFactory.decodeResource(getContext().getResources(),
				imageResource);

		paint.setAlpha(100);

		setWillNotDraw(false);

		// -----------------------------------------------

		super.setClickable(true);
		this.context = context;
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		this.matrix = new Matrix();
		this.m = new float[9];
		// setImageMatrix(this.matrix);
		// setScaleType(ScaleType.MATRIX);

		// setOnClickListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// A Simple Text Render to test the display
		// canvas.drawText("Hello World!", 50, 50, textPaint);
		canvas.drawBitmap(bitmap, 0, 0, paint);
	}

	@Override
	public void onClick(View v) {
		Toast.makeText(getContext(), "onItemClick " + v.getId(),
				Toast.LENGTH_SHORT).show();

	}
}
