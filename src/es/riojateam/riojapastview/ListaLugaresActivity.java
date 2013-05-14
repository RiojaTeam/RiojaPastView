package es.riojateam.riojapastview;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class ListaLugaresActivity extends ListActivity implements
		OnItemClickListener {

	Cursor cursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista_lugares);

		this.getListView().setItemsCanFocus(true);
		this.getListView().setFocusable(true);
		this.getListView().setFocusableInTouchMode(true);

		// Rellenado de la lista
		// Abrimos la base de datos 'DBLugares' en modo lectura
		LugaresSQLiteHelper usdbh = new LugaresSQLiteHelper(this, "BDLugares",
				null, 1);

		SQLiteDatabase db = usdbh.getReadableDatabase();

		// Creamos un array con los nombres de los lugares
		// ArrayList<String> lugares = new ArrayList<String>();

		if (db != null) {
			/*
			 * String consulta =
			 * "SELECT l._id, f._id, l.nombre, f.ruta, l.calle FROM Lugares as l JOIN "
			 * + "Fotografias as f WHERE l.nombre=f.nombreLugar";
			 */
			String consulta = "SELECT L._id, L.nombre, L.nombreMostrar, F.ruta, L.calle, F.epoca "
					+ "FROM Lugares L JOIN Fotografias F WHERE L.nombre=F.nombreLugar order by L.nombre";

			cursor = db.rawQuery(consulta, null);
		}
		// cursor.moveToFirst();
		/*
		 * ListAdapter adapter = new SimpleCursorAdapter(this,
		 * R.layout.lista_lugares_entrada_layout, cursor, new String[] {
		 * cursor.getColumnName(2), cursor.getColumnName(4) }, new int[] {
		 * R.id.text1, R.id.text2 }, 1);
		 */

		MiAdaptador miAdaptador = new MiAdaptador(this, cursor);

		this.setListAdapter(miAdaptador);

		this.getListView().setOnItemClickListener(this);

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	/*
	 * @Override public void onItemClick(AdapterView<?> arg0, View arg1, int
	 * posicion, long arg3) { Toast.makeText(ListaLugaresActivity.this,
	 * "onItemClick", Toast.LENGTH_SHORT).show(); Intent intent = new Intent();
	 * 
	 * Cursor cursorNombre = (Cursor) getListView().getItemAtPosition(posicion);
	 * 
	 * Log.d("cursorNombre.getInt(0)", cursorNombre.getString(0));
	 * Log.d("cursorNombre.getString(1)", cursorNombre.getString(1));
	 * 
	 * intent.putExtra("id", String.valueOf(cursorNombre.getInt(0)));
	 * intent.putExtra("nombre", String.valueOf(cursorNombre.getString(1)));
	 * 
	 * intent.setClass(getApplicationContext(), FichaActivity.class);
	 * startActivity(intent);
	 * 
	 * }
	 */
	/*
	 * @Override protected void onListItemClick(ListView l, View v, int
	 * position, long id) { System.out.println("onListItemClick"); //get
	 * selected items String selectedValue = (String)
	 * getListAdapter().getItem(position); Toast.makeText(this,
	 * "selectedValue "+selectedValue, Toast.LENGTH_SHORT).show();
	 * 
	 * }
	 */

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		System.out.println("onItemClick");

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
