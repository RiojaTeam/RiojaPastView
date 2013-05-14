package es.riojateam.riojapastview;

import es.riojateam.riojapastview.camara.CameraPreview;
import es.riojateam.riojapastview.camara.DrawView;
import es.riojateam.riojapastview.camara.TouchImageView;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.Toast;

public class SuperponerActivity extends Activity {

	CameraPreview cv;
	DrawView dv;
	FrameLayout alParent;
	String ruta;
	TouchImageView tiv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * Set the screen orientation to landscape, because the camera preview
		 * will be in landscape, and if we don't do this, then we will get a
		 * streached image.
		 */
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		// requesting to turn the title OFF
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// making it full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		ruta = getIntent().getStringExtra("ruta");

	}

	public void load() {
		// Try to get the camera
		Camera c = getCameraInstance();

		// If the camera was received, create the app
		if (c != null) {
			/*
			 * Create our layout in order to layer the draw view on top of the
			 * camera preview.
			 */
			// alParent = new FrameLayout(this);

			// Create a new draw view and add it to the layout

			String uri = "drawable/" + ruta/* +"_tn" */;
			int imageResource = getApplicationContext().getResources()
					.getIdentifier(uri, null,
							getApplicationContext().getPackageName());

			Drawable image = getResources().getDrawable(imageResource);
			image.setAlpha(100);

			tiv = new TouchImageView(this, ruta, image);

			LayoutParams lp = new LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

			// tiv.setPadding(50, 0, 50, 0);
			tiv.setLayoutParams(lp);

			// tiv.setImageResource(imageResource);
			// tiv.setImageDrawable(image);
			tiv.setMaxZoom(4f);

			alParent = new MiFrameLayout(this, tiv);
			/*
			 * alParent.setLayoutParams(new LayoutParams(
			 * android.view.ViewGroup.LayoutParams.FILL_PARENT,
			 * android.view.ViewGroup.LayoutParams.FILL_PARENT));
			 */

			alParent.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.FILL_PARENT));

			// Create a new camera view and add it to the layout
			cv = new CameraPreview(this, c);
			alParent.addView(cv);

			// Create a new draw view and add it to the layout
			/*
			 * dv = new DrawView(this,ruta); dv.setLayoutParams(new
			 * LayoutParams(
			 * android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
			 * android.widget.FrameLayout.LayoutParams.WRAP_CONTENT));
			 * alParent.addView(dv);
			 */

			alParent.addView(tiv);

			// ViewGroup vg = alParent;

			// Set the layout as the apps content view
			setContentView(alParent);
		}
		// If the camera was not received, close the app
		else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Unable to find camera. Closing.", Toast.LENGTH_SHORT);
			toast.show();
			finish();
		}
	}

	/* This method is strait for the Android API */
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;

		try {
			c = Camera.open();// attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
			e.printStackTrace();
		}
		return c; // returns null if camera is unavailable
	}

	/*
	 * Override the onPause method so that we can release the camera when the
	 * app is closing.
	 */
	@Override
	protected void onPause() {
		super.onPause();

		if (cv != null) {
			cv.onPause();
			cv = null;
		}
	}

	/*
	 * We call Load in our Resume method, because the app will close if we call
	 * it in onCreate
	 */
	@Override
	protected void onResume() {
		super.onResume();

		load();
	}
}