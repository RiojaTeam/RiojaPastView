package es.riojateam.riojapastview;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaLugaresActivity extends Activity implements LocationListener,
		OnMapClickListener, OnCameraChangeListener, OnClickListener {

	Cursor cursor;
	public GoogleMap map;
	public LocationManager locationManager;
	public String provider;
	// Timer timer;

	Circle circuloUsuario;

	private static final int LEJOS = 0;
	private static final int CERCA = 1;
	private int nivel;

	private TextView mLatLng;

	int cont = 0;
	private Button buttonNormal;
	private Button buttonSatelite;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapa_lugares);

		mLatLng = (TextView) findViewById(R.id.textView1);

		nivel = CERCA;

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map.setOnCameraChangeListener(this);

		// cursor
		LugaresSQLiteHelper usdbh = new LugaresSQLiteHelper(this, "BDLugares",
				null, 1);
		SQLiteDatabase db = usdbh.getReadableDatabase();
		if (db != null) {
			String consulta = "SELECT L.nombre, F.coordenadaNS, F.coordenadaEO FROM Lugares L JOIN Fotografias F on L.nombre = F.nombreLugar";
			cursor = db.rawQuery(consulta, null);
		}

		// centrar mapa en logroño
		LatLng logrono = new LatLng(42.461994, -2.445083);
		CameraPosition camPos = new CameraPosition.Builder().target(logrono)
				.zoom(14).build();
		CameraUpdate camUpd = CameraUpdateFactory.newCameraPosition(camPos);
		map.animateCamera(camUpd);

		dibujaSitios(nivel);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, true);
		locationManager.requestLocationUpdates(provider, 2000, 0, this);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		this.buttonNormal = (Button) findViewById(R.id.buttonNormalMap);
		this.buttonNormal.setOnClickListener(this);
		
		this.buttonSatelite = (Button) findViewById(R.id.buttonSateliteMap);
		this.buttonSatelite.setOnClickListener(this);

	}

	@Override
	public void onStart() {
		super.onStart();
		Toast.makeText(this, "onStart ", Toast.LENGTH_SHORT).show();
		// timer = new Timer();
		// timer.schedule(new MiTimer(this), 0, 3000);

	}

	@Override
	public void onPause() {
		super.onPause();
		Toast.makeText(this, "onPause ", Toast.LENGTH_SHORT).show();
		// timer.cancel();

	}

	@Override
	public void onStop() {
		super.onStop();
		Toast.makeText(this, "onStop ", Toast.LENGTH_SHORT).show();

		locationManager.removeUpdates(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		Toast.makeText(this, "onResume ", Toast.LENGTH_SHORT).show();
		setup();
	}

	private void setup() {

		Location location = null;
		locationManager.removeUpdates(this);

		location = requestUpdatesFromProvider(LocationManager.GPS_PROVIDER,
				R.string.not_support_gps);

		if (location != null) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();

			CircleOptions circleOptions = new CircleOptions()
					.center(new LatLng(Double.valueOf(latitude), Double
							.valueOf(longitude))).radius(50) // set radius in
																// meters
					.fillColor(0x400000FF) // semi-transparent
					.strokeColor(Color.BLUE).strokeWidth(2);

			circuloUsuario = map.addCircle(circleOptions);

		}

	}

	private void dibujaLocation(Location location) {
		if (location != null) {
			cont++;

			double latitude = location.getLatitude();
			double longitude = location.getLongitude();

			CircleOptions circleOptions = new CircleOptions()
					.center(new LatLng(Double.valueOf(latitude), Double
							.valueOf(longitude))).radius(50) // set radius in
																// meters
					.fillColor(0x400000FF) // semi-transparent
					.strokeColor(Color.BLUE).strokeWidth(2);

			circuloUsuario.remove();

			circuloUsuario = map.addCircle(circleOptions);

			mLatLng.setText(location.toString() + " cont(" + cont + ")");
		} else {

			Toast.makeText(this, "GPS NO", Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * Method to register location updates with a desired location provider. If
	 * the requested provider is not available on the device, the app displays a
	 * Toast with a message referenced by a resource id.
	 * 
	 * @param provider
	 *            Name of the requested provider.
	 * @param errorResId
	 *            Resource id for the string message to be displayed if the
	 *            provider does not exist on the device.
	 * @return A previously returned {@link android.location.Location} from the
	 *         requested provider, if exists.
	 */
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
	public void onLocationChanged(Location location) {
		dibujaLocation(location);
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
	public void onMapClick(LatLng point) {
		CircleOptions circleOptions = new CircleOptions().center(point) // set
																		// center
				.radius(10) // set radius in meters
				.fillColor(0x40ff0000) // semi-transparent
				.strokeColor(Color.BLUE).strokeWidth(2);

		// myCircle = myMap.addCircle(circleOptions);
		map.addCircle(circleOptions);

	}

	@Override
	public void onCameraChange(CameraPosition cameraPosition) {

		// TODO Auto-generated method stub
		if (cameraPosition.zoom < 14 && nivel == CERCA) {
			nivel = LEJOS;
			dibujaSitios(nivel);
		}

		if (cameraPosition.zoom >= 14 && nivel == LEJOS) {
			nivel = CERCA;
			dibujaSitios(nivel);
		}

	}

	private void dibujaSitios(int nivel) {
		switch (nivel) {
		case LEJOS:
			cursor.moveToFirst();

			map.clear();

			while (cursor.moveToNext()) {

				String nombre = cursor.getString(0);
				String coordNS = cursor.getString(1);
				String coordEO = cursor.getString(2);

				map.addMarker(new MarkerOptions().position(
						new LatLng(Double.valueOf(coordNS), Double
								.valueOf(coordEO))).title(nombre));

			}

			break;
		case CERCA:

			cursor.moveToFirst();

			map.clear();

			while (cursor.moveToNext()) {

				// String nombre = cursor.getString(0);
				String coordNS = cursor.getString(1);
				String coordEO = cursor.getString(2);

				CircleOptions circleOptions = new CircleOptions()
						.center(new LatLng(Double.valueOf(coordNS), Double
								.valueOf(coordEO))).radius(10) // set radius in
																// meters
						.fillColor(0x40ff0000) // semi-transparent
						.strokeColor(Color.RED).strokeWidth(2);
				map.addCircle(circleOptions);

			}

			break;
		default:
			break;
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

	@Override
	public void onClick(View v) {
		if (v == this.buttonNormal) {
			this.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		}
		if (v == this.buttonSatelite) {
			this.map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		}		
	}

}