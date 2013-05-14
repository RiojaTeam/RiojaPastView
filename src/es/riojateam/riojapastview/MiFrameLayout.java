package es.riojateam.riojapastview;

import es.riojateam.riojapastview.camara.TouchImageView;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class MiFrameLayout extends FrameLayout {

	private static final int NONE = 0;
	private static final int REGULANDO = 1;

	private PointF last = new PointF();
	private PointF start = new PointF();
	private int mode = 0;

	private TouchImageView touchImageView;
	private float inicioTocable;
	private float finTocable;

	float margenLateral = 30f;

	public MiFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MiFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MiFrameLayout(Context context) {
		super(context);
	}

	public MiFrameLayout(Context context, TouchImageView tiv) {
		super(context);
		touchImageView = tiv;

		Display display = ((SuperponerActivity) getContext())
				.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		// float anchuraTotal = size.x;

		this.inicioTocable = margenLateral;
		this.finTocable = size.x - margenLateral;
		/*
		 * anchuraDesplazamientoTocableF =
		 * (float)anchuraDisponibleTocar/2f*0.9f;
		 * this.anchuraDesplazamientoTocable =
		 * (int)anchuraDesplazamientoTocableF; int margen=30;
		 * inicioSeleccionable = margen +
		 * (int)(anchuraDesplazamientoTocableF/2f); finSeleccionable =
		 * anchuraDisponibleTocar - margen -
		 * (int)(anchuraDesplazamientoTocableF/2f);
		 * 
		 * System.out.println("anchuraDisponibleTocar"+anchuraDisponibleTocar);
		 * System.out.println("anchuraDesplazamientoTocable"+this.
		 * anchuraDesplazamientoTocable);
		 * System.out.println("inicioSeleccionable"+this.inicioSeleccionable);
		 * System.out.println("finSeleccionable"+this.finSeleccionable);
		 */
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {

		PointF curr = new PointF(event.getX(), event.getY());

		if (event.getPointerCount() == 1) {
			/*
			 * System.out.println("------------------------");
			 * System.out.println
			 * ("event.getPointerCount :"+event.getPointerCount()); switch
			 * (event.getAction()) { case 0:
			 * System.out.println("event.getAction :"+"ACTION_DOWN"); break;
			 * case 1: System.out.println("event.getAction :"+"ACTION_UP");
			 * break; case 2:
			 * System.out.println("event.getAction :"+"ACTION_MOVE"); break;
			 * case 5:
			 * System.out.println("event.getAction :"+"ACTION_POINTER_DOWN");
			 * break; case 6:
			 * System.out.println("event.getAction :"+"ACTION_POINTER_UP");
			 * break;
			 * 
			 * default: break; }
			 * System.out.println("event.getActionIndex :"+event
			 * .getActionIndex());
			 * System.out.println("------------------------");
			 */
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// last.set(curr);
				// start.set(last);
				if (curr.x > this.inicioTocable && curr.x < this.finTocable) {
					System.out.println("tocado dentro de seleccionable");
					last.set(curr);
					start.set(curr);
					mode = REGULANDO;
					// float mitad = this.anchuraDesplazamientoTocableF/2f;
					// this.inicioDisponibleTocable = (int)(curr.x - mitad);
					// this.finDisponibleTocable = (int)(curr.x + mitad);
					// touchImageView.setDibuja(inicioSeleccionable,
					// finSeleccionable, inicioDisponibleTocable,
					// finDisponibleTocable);

				}

				break;

			case MotionEvent.ACTION_MOVE:
				if (mode == REGULANDO) {
					float deltaX = curr.x - last.x;
					// float deltaY = curr.y - last.y;

					if (last.x + deltaX < finTocable
							&& last.x + deltaX > inicioTocable)
						touchImageView.setDatos(deltaX);

					last.set(curr);
				}
				break;

			case MotionEvent.ACTION_UP:
				mode = NONE;
				/*
				 * int xDiff = (int) Math.abs(curr.x - start.x); int yDiff =
				 * (int) Math.abs(curr.y - start.y); if (xDiff < CLICK && yDiff
				 * < CLICK) performClick();
				 */
				break;

			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				break;
			}
		}
		return super.onInterceptTouchEvent(event);

	}

}
