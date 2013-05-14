package es.riojateam.riojapastview;

import java.util.TimerTask;

import com.google.android.gms.maps.model.LatLng;

import android.location.Location;
import android.widget.Toast;

public class MiTimer extends TimerTask {

	MapaLugaresActivity mapaLugaresActivity;

	public MiTimer(MapaLugaresActivity a) {

		mapaLugaresActivity = a;
	}

	@Override
	public void run() {

		LatLng logrono = new LatLng(42.461994, -2.445083);
		final Location location = mapaLugaresActivity.locationManager
				.getLastKnownLocation(mapaLugaresActivity.provider);

		if (location != null) {
			// Toast.makeText(mapaLugaresActivity,
			// "GPS SI",Toast.LENGTH_SHORT).show();

			mapaLugaresActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(mapaLugaresActivity, "GPS SI",
							Toast.LENGTH_SHORT).show();
					mapaLugaresActivity.onLocationChanged(location);

				}
			});
			// System.out.println("SI hay GPS");
			// System.out.println("location: "+location);
			// mapaLugaresActivity.onLocationChanged(location);
		} else {
			mapaLugaresActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(mapaLugaresActivity, "GPS NO",
							Toast.LENGTH_SHORT).show();
				}
			});
			// System.out.println("No hay GPS");
			/*
			 * map.addMarker(new MarkerOptions() .position(logrono) .title("yo")
			 * .
			 * icon(BitmapDescriptorFactory.fromResource(R.drawable.cur_position
			 * )));
			 */
		}

	}

}
