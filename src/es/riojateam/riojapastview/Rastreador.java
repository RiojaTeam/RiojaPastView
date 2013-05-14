package es.riojateam.riojapastview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class Rastreador extends TimerTask {

	private MainActivity mainActivity;
	private Map<String, ArrayList<String>> fotografiasMap = new HashMap<String, ArrayList<String>>();
	LocationManager mLocationManager;
	Location mylocation;
	List<String> listaAux;
	public Cursor cursor;
	public SQLiteDatabase db;
	
	public static final String PREFS_NAME = "preferencias";

	private final LocationListener listener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {

			// updateUILocation(location);

			mylocation = location;
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
	};

	public Rastreador(MainActivity a) {

		mainActivity = a;
		//Cursor cursor = null;
		// contruir un lista ruta nombreLugar coordenadaNS coordenadaEO
		LugaresSQLiteHelper usdbh = new LugaresSQLiteHelper(mainActivity,
				"BDLugares", null, 1);
		db = usdbh.getReadableDatabase();
		if (db != null) {
			String consulta = "SELECT F.ruta, F.coordenadaNS, F.coordenadaEO FROM Fotografias F";
			cursor = db.rawQuery(consulta, null);
		}

		while (cursor.moveToNext()) {
			String ruta = cursor.getString(0);
			final String coordenadaNS = cursor.getString(1);
			final String coordenadaEO = cursor.getString(2);

			fotografiasMap.put(ruta, new ArrayList<String>() {
				{
					add(coordenadaNS);
					add(coordenadaEO);
				}
			});
		}

		mLocationManager = (LocationManager) mainActivity
				.getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				5000, 10, listener);

		listaAux = new ArrayList<String>();
		System.out.println("Rastreador construido");
	}

	@Override
	public void run() {
		System.out.println("Rastreador run");
		System.out.println("run 1");
		Location gpsLocation = null;
		
		if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			gpsLocation = mLocationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		gpsLocation = new Location(LocationManager.GPS_PROVIDER);
		gpsLocation.setLatitude(42.463988);
		gpsLocation.setLongitude(-2.427358);
		
		
		if (gpsLocation != null) {
			System.out.println("run 2");
			mylocation = gpsLocation;
			mainActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					/*Toast.makeText(
							mainActivity,
							"posicion gps: " + mylocation.getLatitude() + " "
									+ mylocation.getLongitude(),
							Toast.LENGTH_SHORT).show();*/

				}
			});

			boolean cambios = false;
			//System.out.println("run 2");
			for (Map.Entry<String, ArrayList<String>> entry : fotografiasMap
					.entrySet()) {
				final float[] results = new float[3];
				Location.distanceBetween(gpsLocation.getLatitude(),
						gpsLocation.getLongitude(),
						Double.valueOf(entry.getValue().get(0)),
						Double.valueOf(entry.getValue().get(1)), results);

				float distancia = results[0];
				System.out.println("distancia: "+distancia);
				System.out.println("distancia: " + entry.getKey() + " ("
						+ distancia + ")");
				for (String item : mainActivity.getListaDisponibles())
					listaAux.add(item);

				// debugeo
				// float radio = 60000;

				SharedPreferences settings = mainActivity.getSharedPreferences(	PREFS_NAME, 0);
				int distanciaNotificacion = settings.getInt("distanciaNotificacion", 50);

				
				if (distancia < 800000) {
					if (!listaAux.contains(entry.getKey())) {
						listaAux.add(entry.getKey());
						cambios = true;
						System.out.println("Añadido dentro de rango: "
								+ entry.getKey());
					}

				} else {
					if (listaAux.contains(entry.getKey())) {
						listaAux.remove(entry.getKey());
						cambios = true;
						System.out.println("Quitado dentro de rango: "
								+ entry.getKey());

					}
				}

			
			if (cambios) {
				mainActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mainActivity.setListaDisponibles(listaAux);
					}
				});

			}
			//System.out.println("run 3");
		}
		}

	}

}