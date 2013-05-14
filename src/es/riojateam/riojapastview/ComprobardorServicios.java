package es.riojateam.riojapastview;

import java.util.TimerTask;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ComprobardorServicios extends TimerTask {

	private MainActivity mainActivity;

	public ComprobardorServicios(MainActivity a) {

		mainActivity = a;
	}

	@Override
	public void run() {

		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mainActivity.setInternetOn(compruebaInternet(mainActivity));
			}
		});

		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mainActivity.setGpsOn(compruebaGps(mainActivity));
			}
		});

	}

	public boolean compruebaInternet(Context context) {
		ConnectivityManager conMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo i = conMgr.getActiveNetworkInfo();
		if (i == null)
			return false;
		if (!i.isConnected())
			return false;
		if (!i.isAvailable())
			return false;
		return true;

	}

	public boolean compruebaGps(Context context) {
		LocationManager manager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return false;
		}
		return true;

	}

}
