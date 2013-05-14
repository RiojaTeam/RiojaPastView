package es.riojateam.riojapastview;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MiAdaptador implements ListAdapter, OnClickListener {

	public static class ViewHolder {
		TextView nombre;
		TextView epoca;
		TextView distancia;
		ImageView foto;
	}

	private Cursor cursor;
	private ListaLugaresActivity activity;
	private static LayoutInflater inflater = null;

	// private ImageLoader imageLoader;

	public MiAdaptador(ListaLugaresActivity activity, Cursor cursor) {
		this.cursor = cursor;
		// cursor.moveToFirst();

		this.activity = activity;
		MiAdaptador.inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// this.ageLoader=new ImageLoader(activity.getApplicationContext());

	}

	@Override
	public int getCount() {
		return this.cursor.getCount();
		// return 0;
	}

	@Override
	public Object getItem(int position) {
		return "position";
		// return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
		// return 0;
	}

	@Override
	public int getItemViewType(int position) {
		// return R.layout.entrada_lista;
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		cursor.moveToPosition(position);

		// if (cursor.moveToNext()){
		View vi = convertView;
		ViewHolder holder = null;
		vi = inflater.inflate(R.layout.lista_lugares_entrada, null);

		int idLugar = Integer.parseInt(cursor.getString(0));
		vi.setId(idLugar);

		holder = new ViewHolder();

		holder.nombre = (TextView) vi.findViewById(R.id.itemListaNombre);
		holder.epoca = (TextView) vi.findViewById(R.id.itemListaEpoca);
		holder.distancia = (TextView) vi.findViewById(R.id.itemListaDistancia);
		holder.foto = (ImageView) vi.findViewById(R.id.itemListaFoto);

		// vi.setTag(holder);
		holder.nombre.setText(cursor.getString(2));
		holder.distancia.setText("5 km");
		holder.epoca.setText(cursor.getString(5));

		// Setting an image
		String uri = "drawable/" + cursor.getString(3) + "_tn";
		System.out.println(uri.toString());

		int imageResource = vi
				.getContext()
				.getApplicationContext()
				.getResources()
				.getIdentifier(
						uri,
						null,
						vi.getContext().getApplicationContext()
								.getPackageName());
		Drawable image = vi.getContext().getResources()
				.getDrawable(imageResource);

		System.out.println("height: " + image.getMinimumHeight());
		System.out.println("width: " + image.getMinimumWidth());
		System.out.println("height intrinsic: " + image.getIntrinsicHeight());
		System.out.println("width intrinsic: " + image.getIntrinsicWidth());
		float aspectRatio = (float) image.getMinimumWidth()
				/ (float) image.getMinimumHeight();
		System.out.println("aspect ratio: " + aspectRatio);
		int height = image.getMinimumHeight();
		int width = image.getMinimumWidth();

		if (aspectRatio < 1.629726205997392f) {
			Bitmap bitmapImage = BitmapFactory.decodeResource(vi.getContext()
					.getResources(), imageResource);
			float correctedHeight = (0.6136f) * width;
			float diferencia = height - correctedHeight;
			int x = 0;
			int y = (int) (diferencia / 2f);

			Bitmap correctedBitmap = Bitmap.createBitmap(bitmapImage, x, y,
					width, (int) correctedHeight);
			BitmapDrawable bd = new BitmapDrawable(vi.getContext()
					.getResources(), correctedBitmap);
			holder.foto.setImageDrawable(bd);

		} else {
			Bitmap bitmapImage = BitmapFactory.decodeResource(vi.getContext()
					.getResources(), imageResource);
			float correctedWidth = (1.629726205997392f) * height;
			float diferencia = width - correctedWidth;
			int x = (int) (diferencia / 2f);
			int y = 0;

			Bitmap correctedBitmap = Bitmap.createBitmap(bitmapImage, x, y,
					(int) correctedWidth, height);
			BitmapDrawable bd = new BitmapDrawable(vi.getContext()
					.getResources(), correctedBitmap);
			holder.foto.setImageDrawable(bd);

		}
		vi.setOnClickListener(this);
		vi.setTag(cursor.getString(3));
		return vi;

		/*
		 * View vi = convertView; ViewHolder holder = null; if (convertView ==
		 * null) {
		 * 
		 * vi = inflater.inflate(R.layout.entrada_lista_2, null);
		 * 
		 * int idLugar = Integer.parseInt(cursor.getString(0));
		 * vi.setId(idLugar);
		 * 
		 * holder = new ViewHolder();
		 * 
		 * holder.nombre = (TextView) vi.findViewById(R.id.itemListaNombre);
		 * holder.epoca = (TextView) vi.findViewById(R.id.itemListaEpoca);
		 * holder.distancia = (TextView) vi
		 * .findViewById(R.id.itemListaDistancia); holder.foto = (ImageView)
		 * vi.findViewById(R.id.itemListaFoto);
		 * 
		 * //vi.setTag(holder); holder.nombre.setText(cursor.getString(2));
		 * holder.distancia.setText("5 km");
		 * holder.epoca.setText(cursor.getString(5));
		 * 
		 * // Setting an image String uri = "drawable/" + cursor.getString(3) +
		 * "_tn"; System.out.println(uri.toString());
		 * 
		 * int imageResource = vi .getContext() .getApplicationContext()
		 * .getResources() .getIdentifier( uri, null,
		 * vi.getContext().getApplicationContext() .getPackageName()); Drawable
		 * image = vi.getContext().getResources() .getDrawable(imageResource);
		 * 
		 * System.out.println("height: " + image.getMinimumHeight());
		 * System.out.println("width: " + image.getMinimumWidth());
		 * System.out.println("height intrinsic: " +
		 * image.getIntrinsicHeight()); System.out.println("width intrinsic: " +
		 * image.getIntrinsicWidth()); float aspectRatio = (float)
		 * image.getMinimumWidth() / (float) image.getMinimumHeight();
		 * System.out.println("aspect ratio: " + aspectRatio); int height =
		 * image.getMinimumHeight(); int width = image.getMinimumWidth();
		 * 
		 * if (aspectRatio < 1.629726205997392f) { Bitmap bitmapImage =
		 * BitmapFactory.decodeResource(vi.getContext() .getResources(),
		 * imageResource); float correctedHeight = (0.6136f) * width; float
		 * diferencia = height - correctedHeight; int x = 0; int y = (int)
		 * (diferencia / 2f);
		 * 
		 * Bitmap correctedBitmap = Bitmap.createBitmap(bitmapImage, x, y,
		 * width, (int) correctedHeight); BitmapDrawable bd = new
		 * BitmapDrawable(vi.getContext() .getResources(), correctedBitmap);
		 * holder.foto.setImageDrawable(bd);
		 * 
		 * 
		 * } else { Bitmap bitmapImage =
		 * BitmapFactory.decodeResource(vi.getContext() .getResources(),
		 * imageResource); float correctedWidth = (1.629726205997392f) * height;
		 * float diferencia = width - correctedWidth; int x = (int) (diferencia
		 * / 2f); int y = 0;
		 * 
		 * Bitmap correctedBitmap = Bitmap.createBitmap(bitmapImage, x, y, (int)
		 * correctedWidth, height); BitmapDrawable bd = new
		 * BitmapDrawable(vi.getContext() .getResources(), correctedBitmap);
		 * holder.foto.setImageDrawable(bd);
		 * 
		 * } } else { //holder = (ViewHolder) vi.getTag(); }
		 */

		// }
		// else return convertView;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onClick(View v) {

		//Toast.makeText(this.activity, "onItemClick " + v.getId(),Toast.LENGTH_SHORT).show();
		Intent intent = new Intent();
		String ruta = String.valueOf(v.getTag());
		intent.putExtra("ruta", ruta);
		intent.setClass(this.activity, FichaActivity.class);
		this.activity.startActivity(intent);

	}

}
