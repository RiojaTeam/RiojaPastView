package es.riojateam.riojapastview;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class CompassView extends View {

	private float direction = 0;
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintFondo = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintUsuario = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintLugar = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintLetras = new Paint(Paint.ANTI_ALIAS_FLAG);

	private boolean firstDraw;
	private Float direccionLugar;

	private static final int LANDSCAPE_IZQUIERDA = 0;
	private static final int LANDSCAPE_DERECHA = 1;

	private int landScapeMode;

	public CompassView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public CompassView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public CompassView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {

		this.direccionLugar = null;

		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		paint.setColor(Color.WHITE);
		paint.setTextSize(30);

		paintFondo.setStyle(Paint.Style.FILL_AND_STROKE);
		paintFondo.setColor(Color.BLACK);

		paintUsuario.setStyle(Paint.Style.FILL_AND_STROKE);
		paintUsuario.setColor(Color.BLUE);

		paintLugar.setStyle(Paint.Style.FILL_AND_STROKE);
		paintLugar.setColor(Color.RED);

		paintLetras.setStyle(Paint.Style.FILL_AND_STROKE);
		paintLetras.setColor(Color.WHITE);
		paintLetras.setTextSize(32);
		firstDraw = true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
				MeasureSpec.getSize(heightMeasureSpec));
	}

	@Override
	protected void onDraw(Canvas canvas) {

		Configuration config = getResources().getConfiguration();

		int cxCompass = getMeasuredWidth() / 2;
		int cyCompass = getMeasuredHeight() / 2;
		float centro_x = cxCompass;
		float centro_y = cyCompass;
		float radiusCompass = 0;
		float correcionUsuario = 0;
		float correcionLugar = 0;
		if (cxCompass > cyCompass) {
			radiusCompass = (float) (cyCompass * 0.9);
		} else {
			radiusCompass = (float) (cxCompass * 0.9);
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

		// canvas.drawCircle(cxCompass, cyCompass, radiusCompass, paintFondo);

		/*canvas.drawText("N", cxCompass, cyCompass - radiusCompass * 0.9f,
				paintLetras);
		canvas.drawText("S", cxCompass, cyCompass + radiusCompass * 0.9f,
				paintLetras);
		canvas.drawText("O", cxCompass - radiusCompass * 0.9f, cyCompass,
				paintLetras);
		canvas.drawText("E", cxCompass + radiusCompass * 0.9f, cyCompass,
				paintLetras);*/
		if (!firstDraw) {

			if (this.direccionLugar != null) {

				float margenDireccionLugar = 10;
				float verticeLugarN_x = (float) (cxCompass + radiusCompass
						* Math.sin((direccionLugar + correcionLugar - margenDireccionLugar) * 3.14 / 180));
				float verticeLugarN_y = (float) (cyCompass - radiusCompass
						* Math.cos((direccionLugar + correcionLugar - margenDireccionLugar) * 3.14 / 180));
				float verticeLugarP_x = (float) (cxCompass + radiusCompass
						* Math.sin((direccionLugar + correcionLugar + margenDireccionLugar) * 3.14 / 180));
				float verticeLugarP_y = (float) (cyCompass - radiusCompass
						* Math.cos((direccionLugar + correcionLugar + margenDireccionLugar) * 3.14 / 180));

				Path pathLugar = new Path();
				pathLugar.setFillType(Path.FillType.EVEN_ODD);
				pathLugar.moveTo(centro_x, centro_y);
				pathLugar.lineTo(verticeLugarP_x, verticeLugarP_y);
				pathLugar.lineTo(verticeLugarN_x, verticeLugarN_y);
				pathLugar.lineTo(centro_x, centro_y);
				pathLugar.close();
				canvas.drawPath(pathLugar, paintLugar);
			}

			float margen = 10;
			float coeficienteAcortamiento = 0.75f;
			float verticeN_x = (float) (cxCompass + radiusCompass
					* coeficienteAcortamiento
					* Math.sin((direction + correcionUsuario - margen) * 3.14 / 180));
			float verticeN_y = (float) (cyCompass - radiusCompass
					* coeficienteAcortamiento
					* Math.cos((direction + correcionUsuario - margen) * 3.14 / 180));
			float verticeP_x = (float) (cxCompass + radiusCompass
					* coeficienteAcortamiento
					* Math.sin((direction + correcionUsuario + margen) * 3.14 / 180));
			float verticeP_y = (float) (cyCompass - radiusCompass
					* coeficienteAcortamiento
					* Math.cos((direction + correcionUsuario + margen) * 3.14 / 180));

			Path pathUsuario = new Path();
			pathUsuario.setFillType(Path.FillType.EVEN_ODD);
			pathUsuario.moveTo(centro_x, centro_y);
			pathUsuario.lineTo(verticeP_x, verticeP_y);
			pathUsuario.lineTo(verticeN_x, verticeN_y);
			pathUsuario.lineTo(centro_x, centro_y);
			pathUsuario.close();
			canvas.drawPath(pathUsuario, paintUsuario);

		}

	}

	public void updateDirection(float dir) {
		firstDraw = false;
		direction = dir;
		invalidate();
	}

	public void setDireccionLugar(float direccion) {
		this.direccionLugar = direccion;

	}

	public void notificaCambioLandScape(int landScapeMode) {
		this.landScapeMode = landScapeMode;

	}

}
