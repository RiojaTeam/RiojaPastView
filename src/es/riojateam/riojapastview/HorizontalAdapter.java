package es.riojateam.riojapastview;

import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HorizontalAdapter extends BaseAdapter implements OnClickListener {

	private MainActivity mainActivity;
	private List<String> lista;
	private View clickedView;

	public HorizontalAdapter(MainActivity mainActivity, List<String> listaAux) {
		super();
		this.mainActivity = mainActivity;
		lista = listaAux;// no se modifica aqui
	}

	@Override
	public int getCount() {
		// return dataObjects.length;
		System.out.println("getCount: " + lista.size());
		return lista.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println("getView position: " + position);

		String ruta = lista.get(position);
		System.out.println("getView ruta: " + ruta);

		if (ruta != null) {
			Cursor cursor = null;
			LugaresSQLiteHelper usdbh = new LugaresSQLiteHelper(mainActivity,
					"BDLugares", null, 1);
			SQLiteDatabase db = usdbh.getReadableDatabase();
			if (db != null) {

				String consulta = "SELECT L.nombre, L.nombreMostrar, F.ruta FROM Lugares L JOIN Fotografias F WHERE L.nombre=F.nombreLugar AND F.ruta ='"
						+ ruta + "'";

				cursor = db.rawQuery(consulta, null);
			}
			System.out.println(ruta);
			System.out.println("cursor: " + cursor.moveToFirst());
			// cursor.moveToFirst();
			View retval = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.pricipal_lista_entrada, null);
			TextView textViewNombre = (TextView) retval
					.findViewById(R.id.itemListaAccesiblesNombre);
			ImageView imageViewFoto = (ImageView) retval
					.findViewById(R.id.itemListaAccesiblesFoto);

			// ImageView imageViewBoton = (ImageView)
			// retval.findViewById(R.id.itemListaAccesiblesBoton);
			// imageViewBoton.setTag(ruta);
			// imageViewBoton.setOnClickListener(this);

			textViewNombre.setText(cursor.getString(1));

			String uri = "drawable/" + cursor.getString(2) + "_tn";
			System.out.println(uri.toString());
			int imageResource = mainActivity.getResources()
					.getIdentifier(
							uri,
							null,
							mainActivity.getApplicationContext()
									.getPackageName());
			Drawable image = mainActivity.getResources().getDrawable(
					imageResource);
			/*
			 * System.out.println("height: "+image.getMinimumHeight());
			 * System.out.println("width: "+image.getMinimumWidth());
			 * System.out.
			 * println("height intrinsic: "+image.getIntrinsicHeight());
			 * System.out
			 * .println("width intrinsic: "+image.getIntrinsicWidth());
			 */
			float aspectRatio = (float) image.getMinimumWidth()
					/ (float) image.getMinimumHeight();
			// System.out.println("aspect ratio: "+aspectRatio);
			int height = image.getMinimumHeight();
			int width = image.getMinimumWidth();

			// retval.setOnClickListener(this);

			if (aspectRatio < 1.610619469026549f) {
				Bitmap bitmapImage = BitmapFactory.decodeResource(
						mainActivity.getResources(), imageResource);

				float correctedHeight = (0.6208791208791208f) * width;
				float diferencia = height - correctedHeight;
				int x = 0;
				int y = (int) (diferencia / 2f);

				Bitmap correctedBitmap = Bitmap.createBitmap(bitmapImage, x, y,
						width, (int) correctedHeight);
				BitmapDrawable bd = new BitmapDrawable(
						mainActivity.getResources(), correctedBitmap);
				imageViewFoto.setImageDrawable(bd);
			} else {
				Bitmap bitmapImage = BitmapFactory.decodeResource(
						mainActivity.getResources(), imageResource);
				float correctedWidth = (1.610619469026549f) * height;
				float diferencia = width - correctedWidth;
				int x = (int) (diferencia / 2f);
				int y = 0;

				Bitmap correctedBitmap = Bitmap.createBitmap(bitmapImage, x, y,
						(int) correctedWidth, height);
				BitmapDrawable bd = new BitmapDrawable(
						mainActivity.getResources(), correctedBitmap);
				imageViewFoto.setImageDrawable(bd);
			}
			// retval.setOnClickListener(this);

			retval.setTag(ruta);
			
			cursor.close();
			db.close();
			return retval;
		} else
			return null;
	}

	@Override
	public void onClick(View v) {
		System.out.println("onClick view");
		this.clickedView = v;
		Toast.makeText(this.mainActivity, "onItemClick " + v.getId(),
				Toast.LENGTH_SHORT).show();

		/*
		 * Intent intent = new Intent(); String ruta = (String)v.getTag();
		 * intent.putExtra("ruta", ruta); intent.setClass(this.mainActivity,
		 * LocalizarActivity.class); this.mainActivity.startActivity(intent);
		 */

	}

	public View getClickedView() {

		return this.clickedView;
	}

}