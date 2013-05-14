package es.riojateam.riojapastview;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class RutaView extends View {

	// private float direction = 0;
	private float distancia = 0;

	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintFondo = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintUsuario = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintLugar = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintLetras = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintVerde = new Paint(Paint.ANTI_ALIAS_FLAG);

	private boolean firstDraw;

	private static final int LANDSCAPE_IZQUIERDA = 0;
	private static final int LANDSCAPE_DERECHA = 1;

	private int landScapeMode;

	private float direccionUsuario = 0;
	private LatLng posicionUsuario;
	private LatLng posicionLugar;

	public RutaView(Context context) {
		super(context);
		init();
	}

	public RutaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RutaView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {

		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		paint.setColor(Color.WHITE);
		paint.setTextSize(30);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);

		paintFondo.setStyle(Paint.Style.FILL_AND_STROKE);
		paintFondo.setColor(Color.BLACK);

		paintUsuario.setStyle(Paint.Style.FILL_AND_STROKE);
		paintUsuario.setColor(Color.BLUE);

		paintLugar.setStyle(Paint.Style.FILL_AND_STROKE);
		paintLugar.setColor(Color.RED);

		paintLetras.setStyle(Paint.Style.FILL_AND_STROKE);
		paintLetras.setColor(Color.WHITE);
		paintLetras.setTextSize(32);
		
		paintVerde.setStyle(Paint.Style.FILL_AND_STROKE);
		paintVerde.setColor(Color.GREEN);
		

		firstDraw = true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
				MeasureSpec.getSize(heightMeasureSpec));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		System.out.println("onDraw");
		Configuration config = getResources().getConfiguration();

		int cxCompass = getMeasuredWidth() / 2;
		int cyCompass = getMeasuredHeight() / 2;
		float centro_x = cxCompass;
		float centro_y = cyCompass;
		float radiusCompass = 0;
		float radiusCompassExt = 0;

		float correcionUsuario = 0;
		float correcionLugar = 0;
		if (cxCompass > cyCompass) {
			radiusCompass = (float) (cyCompass * 0.9);
		} else {
			radiusCompass = (float) (cxCompass * 0.9);
		}
		
		if (cxCompass > cyCompass) {
			radiusCompassExt = (cyCompass);
		} else {
			radiusCompassExt = (cxCompass);
		}

		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if (this.landScapeMode == LANDSCAPE_DERECHA) {

				correcionUsuario = -90f;
				correcionLugar = -0f;

			} else if (this.landScapeMode == LANDSCAPE_IZQUIERDA) {

				correcionUsuario = -270f;
				correcionLugar = 0f;
			}

		} else if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
			correcionUsuario = 0f;
		}

		canvas.drawCircle(cxCompass, cyCompass, radiusCompassExt, paintFondo);

		if (this.posicionUsuario != null) {
			float bearing = (float) getBearing(this.posicionUsuario.latitude,
					this.posicionUsuario.longitude,
					this.posicionLugar.latitude, this.posicionLugar.longitude);

			float ajusteDir = bearing - this.direccionUsuario;
			System.out.println("this.direccionUsuario: "
					+ this.direccionUsuario);
			System.out.println("bearing: " + bearing);
			System.out.println("ajusteDir: " + ajusteDir);

			float margen = 10;
			float coeficienteAcortamiento = 0.8f;

			float verticeA_x = (float) (cxCompass + radiusCompass
					* Math.sin((ajusteDir + correcionLugar) * 3.14 / 180));
			float verticeA_y = (float) (cyCompass - radiusCompass
					* Math.cos((ajusteDir + correcionLugar) * 3.14 / 180));

			float verticeB_x = (float) (cxCompass + radiusCompass
					* coeficienteAcortamiento
					* Math.sin((ajusteDir + correcionUsuario - margen) * 3.14 / 180));
			float verticeB_y = (float) (cyCompass - radiusCompass
					* coeficienteAcortamiento
					* Math.cos((ajusteDir + correcionUsuario - margen) * 3.14 / 180));
			float verticeC_x = (float) (cxCompass + radiusCompass
					* coeficienteAcortamiento
					* Math.sin((ajusteDir + correcionUsuario + margen) * 3.14 / 180));
			float verticeC_y = (float) (cyCompass - radiusCompass
					* coeficienteAcortamiento
					* Math.cos((ajusteDir + correcionUsuario + margen) * 3.14 / 180));

			Path pathUsuario = new Path();
			pathUsuario.setFillType(Path.FillType.EVEN_ODD);
			pathUsuario.moveTo(verticeA_x, verticeA_y);
			pathUsuario.lineTo(verticeB_x, verticeB_y);
			pathUsuario.lineTo(verticeC_x, verticeC_y);
			pathUsuario.lineTo(verticeA_x, verticeA_y);
			pathUsuario.close();
			canvas.drawPath(pathUsuario, paintVerde);
			canvas.drawCircle(cxCompass, cyCompass, radiusCompass * 0.1f, paintVerde);
			canvas.drawLine(cxCompass, cyCompass, verticeA_x, verticeA_y, paintVerde);
			
			canvas.drawText(String.valueOf((int) this.distancia) + " metros",
					((cxCompass + verticeA_x) / 2f)-cxCompass/2f, (cyCompass + verticeA_y) / 1.5f,
					paintLetras);
		}

	}

	/*
	 * public void updateDirection(float dir) { firstDraw = false;
	 * this.direction = dir; invalidate(); }
	 */

	public void updateDistancia(float distancia) {
		firstDraw = false;
		this.distancia = distancia;
		invalidate();
	}

	public void notificaCambioLandScape(int landScapeMode) {
		this.landScapeMode = landScapeMode;
	}

	public void updatePosicionUsuario(LatLng posicionUsuario) {
		this.posicionUsuario = posicionUsuario;
		invalidate();
	}

	public void updatePosicionLugar(LatLng posicionLugar) {
		this.posicionLugar = posicionLugar;
	}

	public void updateDireccionUsuario(float direccionUsuario) {
		this.direccionUsuario = direccionUsuario;
		invalidate();
	}

	public double RadToDeg(double radians) {
		return radians * (180 / Math.PI);
	}

	public double DegToRad(double degrees) {
		return degrees * (Math.PI / 180);
	}

	public double getBearing(double lat1, double long1, double lat2,
			double long2) {
		// Convert input values to radians
		lat1 = DegToRad(lat1);
		long1 = DegToRad(long1);
		lat2 = DegToRad(lat2);
		long2 = DegToRad(long2);

		double deltaLong = long2 - long1;

		double y = Math.sin(deltaLong) * Math.cos(lat2);
		double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
				* Math.cos(lat2) * Math.cos(deltaLong);
		double bearing = Math.atan2(y, x);
		return convertToBearing(RadToDeg(bearing));
	}

	public double convertToBearing(double deg) {
		return (deg + 360) % 360;
	}

}
