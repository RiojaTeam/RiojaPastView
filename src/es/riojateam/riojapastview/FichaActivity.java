package es.riojateam.riojapastview;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FichaActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the three primary sections of the app. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	AppSectionsPagerAdapter mAppSectionsPagerAdapter;
	// Para hacer la prueba
	/**
	 * The {@link ViewPager} that will display the three primary sections of the
	 * app, one at a time.
	 */
	ViewPager mViewPager;
	static Cursor cursor;
	// static String idLugar;
	static String ruta;
	static String nombreLugar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ficha_layout);

		PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_header);
		pagerTabStrip.setDrawFullUnderline(true);
		pagerTabStrip.setTabIndicatorColor(getResources().getColor(
				R.color.granate_fondo));

		ruta = getIntent().getStringExtra("ruta");

		LugaresSQLiteHelper usdbh = new LugaresSQLiteHelper(this, "BDLugares",
				null, 1);
		SQLiteDatabase db = usdbh.getReadableDatabase();
		cursor = null;
		if (db != null) {
			String consulta = "SELECT L.nombreMostrar, L.calle, L.descripcion, F.ruta, L.nombre FROM Lugares L JOIN Fotografias F WHERE L.nombre=F.nombreLugar and F.ruta='"
					+ ruta + "'";

			cursor = db.rawQuery(consulta, null);
		}
		cursor.moveToFirst();
		nombreLugar = cursor.getString(4);

		TextView textViewNombre = (TextView) findViewById(R.id.fichaNombre);
		textViewNombre.setText(cursor.getString(0));

		mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();

		// Specify that the Home/Up button should not be enabled, since there is
		// no hierarchical
		// parent.
		// actionBar.setHomeButtonEnabled(false);

		// Specify that we will be displaying tabs in the action bar.
		// actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Set up the ViewPager, attaching the adapter and setting up a listener
		// for when the
		// user swipes between sections.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mAppSectionsPagerAdapter);
		/*
		 * mViewPager.setOnPageChangeListener(new
		 * ViewPager.SimpleOnPageChangeListener() {
		 * 
		 * @Override public void onPageSelected(int position) { // When swiping
		 * between different app sections, select // the corresponding tab. //
		 * We can also use ActionBar.Tab#select() to do this if // we have a
		 * reference to the // Tab.
		 * actionBar.setSelectedNavigationItem(position); } });
		 */

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter.
			// Also specify this Activity object, which implements the
			// TabListener interface, as the
			// listener for when this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mAppSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

		public AppSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			switch (i) {
			case 0:
				return new FichaFragment(cursor);
			case 1:
				Fragment fragmentMapa = new MapaFragment(ruta);
				return fragmentMapa;
			default:
				Fragment fragmentGaleria = new GaleriaFragment(nombreLugar);
				return fragmentGaleria;
			}
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {

			switch (position) {
			case 0:

				return "Ficha";

			case 1:

				return "Mapa";
			default:

				return "Galeria";

			}
		}
	}

	/**
	 * A fragment that launches other parts of the demo application.
	 */
	public static class FichaFragment extends Fragment {

		private static final int TEXTVIEW_DESCRIPCION_ID = 1;
		private static final int IMAGEVIEW_FOTO_ID = 2;

		Cursor cursor;

		// String ruta;

		public FichaFragment(Cursor cursor/* String ruta */) {
			this.cursor = cursor;
			// this.ruta = ruta;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.ficha_fragment_descripcion, container, false);

			// Demonstration of a collection-browsing activity.
			/*
			 * TextView textViewNombre =
			 * (TextView)rootView.findViewById(R.id.textView1); String nombre =
			 * getActivity().getIntent().getStringExtra("nombre");
			 * textViewNombre.setText(nombre);
			 */

			String uri = "drawable/" + cursor.getString(3);
			int imageResource = getResources().getIdentifier(uri, null,
					getActivity().getPackageName());

			Drawable image = getResources().getDrawable(imageResource);

			System.out.println("height: " + image.getMinimumHeight());
			System.out.println("width: " + image.getMinimumWidth());
			int height = image.getMinimumHeight();
			int width = image.getMinimumWidth();
			// float correctedHeight = (0.6136f)*width;
			// float diferencia = height - correctedHeight;

			// Bitmap bitmapImage =
			// BitmapFactory.decodeResource(getResources(),imageResource);

			// Bitmap correctedBitmap = Bitmap.createBitmap(bitmapImage, 0,
			// (int)(diferencia/2), width,(int)(bitmapImage.getHeight() -
			// (diferencia/2)));
			// BitmapDrawable bd = new
			// BitmapDrawable(vi.getContext().getResources(),correctedBitmap);

			// holder.foto.setImageDrawable(image);
			ImageView imageViewFoto = new ImageView(getActivity());
			imageViewFoto.setId(TEXTVIEW_DESCRIPCION_ID);

			imageViewFoto.setImageResource(imageResource);

			// RelativeLayout rl = (RelativeLayout)
			// container.findViewById(R.id.RelativeLayout1);
			RelativeLayout rl = (RelativeLayout) rootView
					.findViewById(R.id.RelativeLayout1);

			RelativeLayout.LayoutParams lpImageViewFoto = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lpImageViewFoto.addRule(RelativeLayout.CENTER_HORIZONTAL);
			lpImageViewFoto.setMargins(0, 0, 5, 0);
			// lp.addRule(RelativeLayout.BELOW, textViewDescripcion.getId());
			// lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

			// img_global_content_background.9
			TextView textViewDescripcion = new TextView(getActivity());
			textViewDescripcion.setId(IMAGEVIEW_FOTO_ID);
			// int imageResourceBackground =
			// getResources().getIdentifier("img_global_content_background",
			// null, getActivity().getPackageName());

			Drawable imageBackground = getResources().getDrawable(
					R.drawable.img_global_content_background);
			// textViewDescripcion.setBackgroundDrawable(imageBackground);
			// Drawable imageBackground;
			// textViewDescripcion.setBackground(imageBackground);
			textViewDescripcion.setBackgroundDrawable(imageBackground);
			String descripcion = cursor.getString(2);
			textViewDescripcion.setTextAppearance(getActivity(),
					R.style.FuentePequenaNegro);

			textViewDescripcion.setText(descripcion);

			RelativeLayout.LayoutParams lpDescripcion = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			lpDescripcion.addRule(RelativeLayout.BELOW, imageViewFoto.getId());// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			// lp.addRule(RelativeLayout.ABOVE, textViewDescripcion.getId());
			// lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			// lp.addRule(RelativeLayout.ABOVE, textViewDescripcion.getId());

			rl.addView(imageViewFoto, lpImageViewFoto);
			rl.addView(textViewDescripcion, lpDescripcion);
			return rootView;
		}
	}

	public static class MapaFragment extends Fragment {
		String ruta;

		public MapaFragment(String ruta) {
			this.ruta = ruta;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.ficha_fragment_mapa,
					container, false);

			LugaresSQLiteHelper usdbh = new LugaresSQLiteHelper(getActivity(),
					"BDLugares", null, 1);
			SQLiteDatabase db = usdbh.getReadableDatabase();
			if (db != null) {
				String consulta = "SELECT F.coordenadaNS, F.coordenadaEO FROM Lugares L JOIN Fotografias F WHERE L.nombre=F.nombreLugar AND F.ruta = '"
						+ ruta + "'";
				cursor = db.rawQuery(consulta, null);
			}
			if (cursor.moveToNext()) {

				String coordNS = cursor.getString(0);
				String coordEO = cursor.getString(1);
				GoogleMap mMap;

				SupportMapFragment smf = (SupportMapFragment) getFragmentManager()
						.findFragmentById(R.id.map);
				mMap = smf.getMap();

				LatLng logrono = new LatLng(42.461994, -2.445083);
				CameraPosition camPos = new CameraPosition.Builder()
						.target(logrono).zoom(14).build();

				CameraUpdate camUpd = CameraUpdateFactory
						.newCameraPosition(camPos);

				mMap.animateCamera(camUpd);

				// mMap.moveCamera(camPos);
				// mMap = ((MapFragment)
				// getFragmentManager().findFragmentById(R.id.map)).getMap();
				mMap.addMarker(new MarkerOptions().position(
						new LatLng(Double.valueOf(coordNS), Double
								.valueOf(coordEO))).title("Hello world"));
			}

			GoogleMap mMap;
			SupportMapFragment smf = (SupportMapFragment) getFragmentManager()
					.findFragmentById(R.id.map);
			mMap = smf.getMap();
			// mMap = ((MapFragment)
			// getFragmentManager().findFragmentById(R.id.map)).getMap();
			mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0))
					.title("Hello world"));

			return rootView;
		}
	}

	public static class GaleriaFragment extends Fragment {
		/*
		 * Integer[] pics= { R.drawable.antartica1, R.drawable.antartica2,
		 * R.drawable.antartica3};
		 */
		List<Integer> pics;
		ImageView imageView;

		String nombre;

		public GaleriaFragment(String nombre) {
			this.nombre = nombre;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.ficha_fragment_galeria,
					container, false);
			Gallery ga = (Gallery) rootView.findViewById(R.id.Gallery01);

			LugaresSQLiteHelper usdbh = new LugaresSQLiteHelper(getActivity(),
					"BDLugares", null, 1);
			SQLiteDatabase db = usdbh.getReadableDatabase();
			if (db != null) {
				String consulta = "SELECT F.ruta FROM Lugares L JOIN Fotografias F WHERE L.nombre=F.nombreLugar AND L.nombre = '"
						+ nombre + "'";
				cursor = db.rawQuery(consulta, null);
			}
			// cursor.moveToFirst();
			pics = new ArrayList<Integer>();
			while (cursor.moveToNext()) {

				String uri = "drawable/" + cursor.getString(0);
				System.out.println(uri);
				int imageResource = getResources().getIdentifier(uri, null,
						getActivity().getPackageName());

				pics.add(imageResource);
			}

			ga.setAdapter(new ImageAdapter(this.getActivity(), pics));

			imageView = (ImageView) rootView.findViewById(R.id.ImageView01);
			imageView.setImageResource(pics.get(0));

			ga.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// Toast.makeText(getBaseContext(),
					// "You have selected picture " + (arg2+1) +
					// " of Antartica",Toast.LENGTH_SHORT).show();
					// Toast.makeText(getActivity().getBaseContext(),
					// "You have selected picture " + (arg2+1) +
					// " of Antartica",Toast.LENGTH_SHORT).show();
					imageView.setImageResource(pics.get(arg2));

				}

			});

			return rootView;
		}
	}
}
