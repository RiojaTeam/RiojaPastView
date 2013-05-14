package es.riojateam.riojapastview;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class LugaresSQLiteHelper extends SQLiteOpenHelper {

	// Sentencia SQL para crear la tabla de Usuarios
	// String sqlDelete = "DROP TABLE Lugares";
	// String sqlDelete2 = "DROP TABLE Forografia";
	String sqlCreate = "CREATE TABLE Lugares (_id integer primary key autoincrement, nombre TEXT unique, nombreMostrar TEXT,  calle TEXT, descripcion TEXT)";
	String sqlCreate2 = "CREATE TABLE Fotografias (_id integer primary key autoincrement, ruta TEXT unique, coordenadaNS TEXT, coordenadaEO TEXT, direccion TEXT, nombreLugar TEXT, epoca TEXT)";

	public LugaresSQLiteHelper(Context contexto, String nombre,
			CursorFactory factory, int version) {
		super(contexto, nombre, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Se ejecuta la sentencia SQL de creaci�n de la tabla
		// db.execSQL(sqlDelete);
		// db.execSQL(sqlDelete2);
		db.execSQL(sqlCreate);
		db.execSQL(sqlCreate2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int versionAnterior,
			int versionNueva) {
		// Se elimina la versi�n anterior de la tabla
		db.execSQL("DROP TABLE IF EXISTS Lugares");
		db.execSQL("DROP TABLE IF EXISTS Fotografias");

		// Se crea la nueva versi�n de la tabla
		db.execSQL(sqlCreate);
		db.execSQL(sqlCreate2);
	}
}