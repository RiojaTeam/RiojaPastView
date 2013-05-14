package es.riojateam.riojapastview;

import java.util.List;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class LocalizarActivity extends Activity implements LocationListener,
		SensorEventListener, OnClickListener {

	// Cursor cursor;

	public GoogleMap map;
	public LocationManager locationManager;
	public String provider;

	Circle circuloUsuario = null;
	Circle circuloLugar = null;

	String ruta;// rutaFoto
	// double latitudeLugar;
	// double longitudeLugar;

	private static SensorManager mySensorManager;
	private boolean sersorrunning;
	private CompassView compassView;
	private RutaView rutaView;

	private Button buttonSimulaAcercado;
	private Button buttonNormal;
	private Button buttonSatelite;

	boolean primerCambioOrientacion = true;
	private static final int LANDSCAPE_IZQUIERDA = 0;
	private static final int LANDSCAPE_DERECHA = 1;

	private static final int MODO_ACERCAR = 0;
	private static final int MODO_ORIENTAR = 1;
	private int modo = 0;

	private int landScapeMode;
	private float direccionUsuario = 0;
	private LatLng posicionUsuario = null;
	private Polyline lineaAngulo1 = null;
	private Polyline lineaAngulo2 = null;
	private LatLng ultimaPosicionUsiario = null;
	private LatLng posicionLugar = null;

	private float distancia;
	private final float distanciaMinima = 5f;

	public Cursor cursor = null;

	boolean alcanzadoPosicion = false;
	boolean alcanzadoAngulo = false;
	private float direccionLugar;
	private ImageView imageViewSuperponer;
	private ImageView imageViewFoto;

	int contador = 0;
	public SQLiteDatabase db;
	
	public LocalizarActivity() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		System.out.println("LocalizarActivity onCreate 1");

		/*Configuration config = getResources().getConfiguration();
		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setContentView(R.layout.localizar_landscape);

		} else if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
			setContentView(R.layout.localizar_portrait);
		}*/setContentView(R.layout.localizar_portrait);

		this.map = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();
		this.map.getUiSettings().setAllGesturesEnabled(true);

		this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);

		this.provider = locationManager.getBestProvider(criteria, true);
		this.locationManager.requestLocationUpdates(provider, 1000, 0, this);

		this.ruta = getIntent().getStringExtra("ruta");

		LugaresSQLiteHelper usdbh = new LugaresSQLiteHelper(this, "BDLugares",
				null, 1);
		db = usdbh.getReadableDatabase();
		if (db != null) {
			String consulta = "SELECT F.ruta, F.coordenadaNS, F.coordenadaEO, F.direccion FROM Fotografias F WHERE F.ruta ='"
					+ ruta + "'";
			cursor = db.rawQuery(consulta, null);
		}

		this.cursor.moveToFirst();

		double latitudeLugar = Double.valueOf(cursor.getString(1));
		double longitudeLugar = Double.valueOf(cursor.getString(2));
		this.direccionLugar = Float.valueOf(cursor.getString(3));

		this.cursor.close();
		db.close();
		
		this.posicionLugar = new LatLng(latitudeLugar, longitudeLugar);

		this.compassView = (CompassView) findViewById(R.id.mycompassview);
		this.compassView.setDireccionLugar(this.direccionLugar);
		this.compassView.setVisibility(View.INVISIBLE);

		this.rutaView = (RutaView) findViewById(R.id.rutaView);
		this.rutaView.updatePosicionLugar(this.posicionLugar);

		mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> mySensors = mySensorManager
				.getSensorList(Sensor.TYPE_ORIENTATION);

		if (mySensors.size() > 0) {
			mySensorManager.registerListener(this, mySensors.get(0),
					SensorManager.SENSOR_DELAY_NORMAL);
			this.sersorrunning = true;
			// Toast.makeText(this, "Start ORIENTATION Sensor",

		} else {
			// Toast.makeText(this, "No ORIENTATION Sensor",
			this.sersorrunning = false;
			finish();// ¿?¿?¿?
		}

		this.buttonSimulaAcercado = (Button) findViewById(R.id.buttonSimulaAcercado);
		this.buttonSimulaAcercado.setOnClickListener(this);

		this.buttonNormal = (Button) findViewById(R.id.buttonNormal);
		this.buttonNormal.setOnClickListener(this);
		
		this.buttonSatelite = (Button) findViewById(R.id.buttonSatelite);
		this.buttonSatelite.setOnClickListener(this);
		
		/*
		 * Location loc =
		 * locationManager.getLastKnownLocation(LOCATION_SERVICE);
		 * System.out.println("location inicial: "+loc); final float[] results =
		 * new float[3]; Location.distanceBetween(loc.getLatitude(),
		 * loc.getLongitude(), posicionLugar.latitude, posicionLugar.longitude,
		 * results);
		 */

		imageViewFoto = (ImageView) findViewById(R.id.imageViewFoto);
		imageViewFoto.setVisibility(View.INVISIBLE);

		Location location = null;
		if (locationManager.isProviderEnabled(provider)) {
			locationManager.requestLocationUpdates(provider, 10000, 10, this);
			location = locationManager.getLastKnownLocation(provider);
		}
		
		
		//this.posicionUsuario = new LatLng(Double.valueOf(location.getAltitude()),Double.valueOf(location.getLongitude()));
		location = new Location(provider);
		location.setLatitude(42.463988);
		location.setLongitude(-2.427358);
		
		this.posicionUsuario = new LatLng(Double.valueOf(location.getLatitude()),Double.valueOf(location.getLongitude()));
		
		/*
		final float[] results = new float[3];
		Location.distanceBetween(posicionUsuario.latitude,
				posicionUsuario.longitude, posicionLugar.latitude,
				posicionLugar.longitude, results);
		this.distancia = results[0];*/
		
		this.distancia = 1000;
		
		System.out.println("LocalizarActivity onCreate 2");
		//this.onResume();
	}

	@Override
	public void onLocationChanged(Location location) {

		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		this.posicionUsuario = new LatLng(Double.valueOf(latitude),
				Double.valueOf(longitude));

		final float[] results = new float[3];
		Location.distanceBetween(posicionUsuario.latitude,
				posicionUsuario.longitude, posicionLugar.latitude,
				posicionLugar.longitude, results);
		this.distancia = results[0];
		//System.out.println("distancia: " + distancia);

		dibujaCirculoUsuario();
		this.rutaView.updateDistancia(this.distancia);
		this.rutaView.updatePosicionUsuario(this.posicionUsuario);
		Toast.makeText(
				this,
				"onLocationChanged: " + this.contador + " "
						+ this.posicionUsuario.latitude + ","
						+ this.posicionUsuario.longitude, Toast.LENGTH_SHORT)
				.show();

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		//Toast.makeText(this, "onRestoreInstanceState LocalizarActivity", Toast.LENGTH_SHORT).show();
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		//Toast.makeText(this, "onStart LocalizarActivity", Toast.LENGTH_SHORT).show();
		setup();

	}
	

	@Override
	public void onPause() {
		super.onPause();
		//Toast.makeText(this, "onPause LocalizarActivity", Toast.LENGTH_SHORT).show();

		if (sersorrunning) {
			mySensorManager.unregisterListener(this);
		}

		// timer.cancel();

	}

	@Override
	public void onStop() {
		//Toast.makeText(this, "onStop LocalizarActivity", Toast.LENGTH_SHORT).show();

		super.onStop();
		
		//Toast.makeText(this, "onStop LocalizarActivity", Toast.LENGTH_SHORT).show();

		locationManager.removeUpdates(this);

	}

	@Override
	protected void onResume() {
		//Toast.makeText(this, "onResume LocalizarActivity", Toast.LENGTH_SHORT).show();

		super.onResume();
		//Toast.makeText(this, "onResume LocalizarActivity", Toast.LENGTH_SHORT).show();
		setup();
	}

	@Override
	protected void onDestroy() {
		//Toast.makeText(this, "onDestroy LocalizarActivity", Toast.LENGTH_SHORT).show();
		super.onDestroy();
		if (sersorrunning) {
			mySensorManager.unregisterListener(this);
		}

	}

	private void setup() {

		Location location = null;
		locationManager.removeUpdates(this);

		location = requestUpdatesFromProvider(LocationManager.GPS_PROVIDER,
				R.string.not_support_gps);

		if (location != null) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			posicionUsuario = new LatLng(Double.valueOf(latitude),
					Double.valueOf(longitude));
			// ultimaPosicionUsiario = posicionUsuario;
			
			this.posicionUsuario = new LatLng(Double.valueOf(latitude),
					Double.valueOf(longitude));

			final float[] results = new float[3];
			Location.distanceBetween(posicionUsuario.latitude,
					posicionUsuario.longitude, posicionLugar.latitude,
					posicionLugar.longitude, results);
			this.distancia = results[0];
			rutaView.updateDistancia(this.distancia);
			
			dibujaCirculoUsuario();
		}

		if (circuloLugar == null) {

			CircleOptions circleOptionsLugar = new CircleOptions()
					.center(this.posicionLugar).radius(10)
					.fillColor(0x40FF0000) // semi-transparent
					.strokeColor(Color.RED).strokeWidth(2);

			circuloLugar = map.addCircle(circleOptionsLugar);
		}

		LatLng centrar = new LatLng(
				((posicionUsuario.latitude + posicionLugar.latitude) / 2d) - 0.000277778,
				((posicionUsuario.longitude + posicionLugar.longitude) / 2d));

		CameraPosition camPos = new CameraPosition.Builder().target(centrar)
				.zoom(18).build();
		CameraUpdate camUpd = CameraUpdateFactory.newCameraPosition(camPos);
		map.animateCamera(camUpd);

		dibujaCirculoUsuario();
		this.rutaView.updateDistancia(this.distancia);
		this.rutaView.updatePosicionUsuario(this.posicionUsuario);
		/*
		Toast.makeText(
				this,
				"setup: " + this.contador + " " + this.posicionUsuario.latitude
						+ "," + this.posicionUsuario.longitude,
				Toast.LENGTH_SHORT).show();*/

	}

	private Location requestUpdatesFromProvider(final String provider,
			final int errorResId) {
		Location location = null;
		if (locationManager.isProviderEnabled(provider)) {
			locationManager.requestLocationUpdates(provider, 10000, 10, this);
			location = locationManager.getLastKnownLocation(provider);
		} else {
			Toast.makeText(this, errorResId, Toast.LENGTH_LONG).show();
		}
		return location;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		this.direccionUsuario = event.values[0];
		// System.out.println("this.direccionUsuario: "+this.direccionUsuario);
		this.compassView.updateDirection(this.direccionUsuario);

		this.rutaView.updateDireccionUsuario(this.direccionUsuario);

		float margen = 20;

		// System.out.println(event.values[1]+"  "+event.values[2]);
		// System.out.println(event.values[2]);

		// if ((event.values[2] > -180 && event.values[2]< -180+margen) ||
		// (event.values[2] > 180 && event.values[2]> 180-margen) ){
		if (event.values[2] > -90 && event.values[2] < -90 + margen) {
			if (!primerCambioOrientacion) {

				if (this.landScapeMode == LANDSCAPE_IZQUIERDA) {
					Toast.makeText(this,
							"notificaCambioLandScape LANDSCAPE_DERECHA",
							Toast.LENGTH_SHORT).show();
					this.landScapeMode = LANDSCAPE_DERECHA;
					compassView.notificaCambioLandScape(this.landScapeMode);

				}
			} else {
				this.landScapeMode = LANDSCAPE_DERECHA;
				Toast.makeText(this,
						"notificaCambioLandScape LANDSCAPE_DERECHA",
						Toast.LENGTH_SHORT).show();
				primerCambioOrientacion = false;
				compassView.notificaCambioLandScape(this.landScapeMode);
			}
		}

		// if ((event.values[2] > -180 && event.values[2]< -180+margen) ||
		// (event.values[2] > 180 && event.values[2]> 180-margen) ){
		if (event.values[2] > 90 - margen && event.values[2] < 90) {
			if (!primerCambioOrientacion) {

				if (this.landScapeMode == LANDSCAPE_DERECHA) {
					Toast.makeText(this,
							"notificaCambioLandScape LANDSCAPE_IZQUIERDA",
							Toast.LENGTH_SHORT).show();
					this.landScapeMode = LANDSCAPE_IZQUIERDA;
					compassView.notificaCambioLandScape(this.landScapeMode);

				}
			} else {
				this.landScapeMode = LANDSCAPE_IZQUIERDA;
				Toast.makeText(this,
						"notificaCambioLandScape LANDSCAPE_IZQUIERDA",
						Toast.LENGTH_SHORT).show();
				primerCambioOrientacion = false;
				compassView.notificaCambioLandScape(this.landScapeMode);
			}
		}

		dibujaCirculoUsuario();

		if (this.modo == MODO_ACERCAR)
			if (this.distancia <= this.distanciaMinima) {
				this.modo = MODO_ORIENTAR;
				activaModoOrientar();
			}

		if (this.modo == MODO_ORIENTAR)
			if (this.direccionUsuario < this.direccionLugar + 10
					&& this.direccionUsuario > this.direccionLugar - 10) {
				permiteSuperponer();
			}

	}

	private void activaModoOrientar() {

		this.rutaView.setVisibility(View.INVISIBLE);
		this.compassView.setVisibility(View.VISIBLE);
		this.compassView.setBackgroundResource(R.drawable.rosa);

	}

	private void permiteSuperponer() {
		System.out.println("permiteSuperponer");

		imageViewSuperponer = (ImageView) findViewById(R.id.imageViewSuperponer);
		imageViewSuperponer.setVisibility(View.VISIBLE);
		imageViewSuperponer
				.setBackgroundResource(R.drawable.frame_animation_superponer);
		imageViewSuperponer.setOnClickListener(this);

		ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(imageViewSuperponer,
				"scaleX", 1.10f);
		ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(imageViewSuperponer,
				"scaleY", 1.10f);
		scaleDownX.setDuration(0);
		scaleDownY.setDuration(0);
		AnimatorSet scaleDown = new AnimatorSet();
		scaleDown.play(scaleDownX).with(scaleDownY);
		scaleDown.start();

		//String uri = "drawable/" + cursor.getString(0);
		String uri = "drawable/" +this.ruta;
		// System.out.println(uri.toString());

		int imageResource = getResources().getIdentifier(uri, null,
				getPackageName());
		Drawable image = getResources().getDrawable(imageResource);

		imageViewFoto.setVisibility(View.VISIBLE);
		// System.out.println("imageViewFoto.getWidth(): "+imageViewFoto.getWidth());
		// System.out.println("imageViewFoto.getHeight(): "+imageViewFoto.getHeight());
		imageViewFoto.setImageDrawable(image);

		compassView.setVisibility(View.INVISIBLE);

		AnimationDrawable frame = (AnimationDrawable) imageViewSuperponer
				.getBackground();
		frame.start();
		imageViewSuperponer.setOnClickListener(this);

	}

	public void dibujaCirculoUsuario() {

		CircleOptions circleOptionsUsuario = new CircleOptions()
				.center(this.posicionUsuario).radius(5) // set radius in meters
				.fillColor(0x400000FF) // semi-transparent
				.strokeColor(Color.BLUE).strokeWidth(1);

		// ----------------------------angulo vision
		/*float correcionUsuario = 0;
		Configuration config = getResources().getConfiguration();

		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if (this.landScapeMode == LANDSCAPE_DERECHA) {
				correcionUsuario = 0f;

			} else if (this.landScapeMode == LANDSCAPE_IZQUIERDA) {
				correcionUsuario = 180f;
			}
		} else if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
			correcionUsuario = 90f;
		}*/

		//float margenDireccionUsuario = 18;
		//float radio = 0.00012f;

		/*float verticeLugarN_x = (float) (posicionUsuario.latitude + radio
				* Math.sin((direccionUsuario + correcionUsuario - margenDireccionUsuario) * 3.14 / 180));
		float verticeLugarN_y = (float) (posicionUsuario.longitude - radio
				* Math.cos((direccionUsuario + correcionUsuario - margenDireccionUsuario) * 3.14 / 180));
		float verticeLugarP_x = (float) (posicionUsuario.latitude + radio
				* Math.sin((direccionUsuario + correcionUsuario + margenDireccionUsuario) * 3.14 / 180));
		float verticeLugarP_y = (float) (posicionUsuario.longitude - radio
				* Math.cos((direccionUsuario + correcionUsuario + margenDireccionUsuario) * 3.14 / 180));
				*/
		/*
		 * PolylineOptions rectOptions = new PolylineOptions() .add(new
		 * LatLng(posicionUsuario.latitude,posicionUsuario.longitude)) .add(new
		 * LatLng(verticeLugarN_x,verticeLugarN_y)).color(Color.BLUE).width(1);
		 * 
		 * PolylineOptions rectOptions2 = new PolylineOptions() .add(new
		 * LatLng(posicionUsuario.latitude,posicionUsuario.longitude)) .add(new
		 * LatLng(verticeLugarP_x,verticeLugarP_y)).color(Color.BLUE).width(1);
		 */

		/*PolylineOptions rectOptions = new PolylineOptions()
				.add(new LatLng(verticeLugarN_x, verticeLugarN_y))
				.add(new LatLng(posicionUsuario.latitude,
						posicionUsuario.longitude)).color(Color.BLUE).width(1);

		PolylineOptions rectOptions2 = new PolylineOptions()
				.add(new LatLng(verticeLugarP_x, verticeLugarP_y))
				.add(new LatLng(posicionUsuario.latitude,
						posicionUsuario.longitude)).color(Color.BLUE).width(1);*/

		if (ultimaPosicionUsiario != posicionUsuario) {
			if (circuloUsuario != null)
				circuloUsuario.remove();
			circuloUsuario = map.addCircle(circleOptionsUsuario);
		}

		/*if (lineaAngulo1 != null)
			lineaAngulo1.remove();
		lineaAngulo1 = map.addPolyline(rectOptions);

		if (lineaAngulo2 != null)
			lineaAngulo2.remove();
		lineaAngulo2 = map.addPolyline(rectOptions2);*/

		ultimaPosicionUsiario = posicionUsuario;

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

	}

	@Override
	public void onClick(View v) {
		if (v == this.buttonSimulaAcercado) {
			this.modo = MODO_ORIENTAR;
			activaModoOrientar();
		}
		if (v == this.imageViewSuperponer) {
			Intent intentSuperponer = new Intent();
			intentSuperponer.putExtra("ruta", this.ruta);
			intentSuperponer.setClass(getApplicationContext(),
					SuperponerActivity.class);
			startActivity(intentSuperponer);
		}
		if (v == this.buttonNormal) {
			this.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		}
		if (v == this.buttonSatelite) {
			this.map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;

		case R.id.action_mapa:
			if (!this.getClass().getName()
					.equals(MapaLugaresActivity.class.getName())) {
				Intent intentMapa = new Intent();
				intentMapa.setClass(getApplicationContext(),
						MapaLugaresActivity.class);
				startActivity(intentMapa);
			}

			return true;
		case R.id.action_lista:

			if (!this.getClass().getName()
					.equals(ListaLugaresActivity.class.getName())) {
				Intent intentLista = new Intent();
				intentLista.setClass(getApplicationContext(),
						ListaLugaresActivity.class);
				startActivity(intentLista);
			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
