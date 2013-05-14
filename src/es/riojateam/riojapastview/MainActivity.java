package es.riojateam.riojapastview;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import android.net.Uri;
import android.os.Bundle;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * 
 * 
 * @author RiojaTeam
 * 
 */
public class MainActivity extends Activity implements OnSeekBarChangeListener,
		OnClickListener {

	ImageView imageViewRadar;
	ImageView imageViewLogoTipo;

	Timer timerRastreador;
	Rastreador rastreador;

	ComprobardorServicios comprobadorServicios;
	Timer timerComprobardorServicios;

	boolean rastreadorActivo;

	HorizontalListView listViewDisponibles;

	List<String> listaDisponibles;
	private int frameNumber;

	public static final String PREFS_NAME = "preferencias";
	
	int locale=-1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.principal);

		float density = getResources().getDisplayMetrics().density;
		float densityDpi = getResources().getDisplayMetrics().densityDpi;
		System.out.println("getResources().getDisplayMetrics().widthPixels: "
				+ getResources().getDisplayMetrics().widthPixels);
		System.out.println("getResources().getDisplayMetrics().heightPixels: "
				+ getResources().getDisplayMetrics().heightPixels);

		System.out.println("getResources().getDisplayMetrics().density: "
				+ getResources().getDisplayMetrics().density);
		System.out.println("getResources().getDisplayMetrics().densityDpi: "
				+ getResources().getDisplayMetrics().densityDpi);
		// Inserccion de datos en la BD
		// Abrimos la base de datos 'DBLugares' en modo escritura
		LugaresSQLiteHelper lsh = new LugaresSQLiteHelper(this, "BDLugares",
				null, 1);

		SQLiteDatabase db = lsh.getWritableDatabase();

		// Si hemos abierto correctamente la base de datos
		if (db != null) {
			// Insertamos los datos en la tabla Usuarios

			try {
				//db.execSQL("drop table Lugares");
				//db.execSQL("drop table Fotografias");
				db.execSQL("INSERT INTO Lugares (nombre, nombreMostrar, calle, descripcion) VALUES ('espartero', 'Estatua de Espartero', 'Plaza del Espol�n'," +
						"'La escultura ecuestre del General Espartero de Logro�o est� situado en el Paseo del Espol�n. Representa al pol�tico y militar Baldomero Espartero (1793�1879) montando a caballo. Fue inaugurado el 23 de septiembre de 1895. Es precisamente �sta la ciudad en la que falleci� el general, donde hab�a residido parte de su vida.')");
				db.execSQL("INSERT INTO Lugares (nombre, nombreMostrar, calle, descripcion) VALUES ('estacion_autobuses', 'Estaci�n de autobuses', 'Vara de rey', " +
						"'La Estaci�n de autobuses de Logro�o se encuentra situada en la c�ntrica calle General Vara de Rey. Se trata de un inmueble independiente cuyos flancos laterales se extienden hacia las calles P�o XII a la izquierda, y Avenida de Espa�a a la derecha, hasta llegar a encontrarse con la calle Belchite y al frente una explanada de forma triangular con el v�rtice ovalado. Fue inaugurada el d�a 9 de noviembre de 1958 y se construy� con arreglo al proyecto presentado por los arquitectos Gonz�lez y Carceller. Su presupuesto se elev� a la cantidad de 4.452.458 pesetas m�s la cifra de 6.734.733 pesetas correspondiente al valor de las viviendas que conforman todo el bloque. El singular edificio fue construido por la empresa �Esteban Ortega� y la denominaci�n del mismo es �Viviendas Teniente General Joaqu�n Gonz�lez Gallarza�, ya que este militar fue el promotor de su construcci�n."
						+"Las obras realizadas en 1991 modernizaron extraordinariamente todas las instalaciones. Una atractiva plaza con el Monumento a la Madre, determina una imagen muy sugestiva para el visitante que se acerque por primera vez a la capital de La Rioja. En el 2007 la construcci�n de un gran aparcamiento subterr�neo en la calle Avenida de Espa�a ha permitido ampliar la superficie de las aceras en ambos lados de la calle, aportando ese mismo ensanche una mayor extensi�n para la plazoleta de la Estaci�n de Autobuses, que ha vuelto a ser objeto de remodelaci�n, quedando un esparcimiento m�s amplio, con nuevas farolas, bancos y jardineras. Detr�s del Monumento a la Madre la colocaci�n de un centro floral, alegre y vistoso y otro posterior con dos cipreses y un granado determinan el �valo final de esta agradable plaza."
						+"La estaci�n capitaliza todas las comunicaciones por carretera entre las poblaciones m�s importantes de la Comunidad Aut�noma de La Rioja y localidades intermedias, las correspondientes entre Logro�o y las capitales de otras Comunidades m�s pr�ximas a su entorno o m�s importantes para las necesidades de los viajeros.')");
				db.execSQL("INSERT INTO Lugares (nombre, nombreMostrar, calle, descripcion) VALUES ('carretera_navarra', 'Carretera de Navarra', 'Carretera de Navarra'," +
						"'Calle que sirve de entrada por la zona norte de Logro�o desde Navarra a trav�s de la Nacional 111 que pasa cerca de Viana y Oy�n.')");
				db.execSQL("INSERT INTO Lugares (nombre, nombreMostrar, calle, descripcion) VALUES ('ciriaco_garrido', 'Calle Ciriaco Garrido', 'Calle Ciriaco Garrido'," +
						"'La calle toma este nombre por Don Ciriaco Garrido L�zaro, un cura penitenciario de la Colegiata de Santa Mar�a de la Redonda desde 1899. Fue un sacerdote de peque�a estatura, apodado Don Ciriaquito. En 1916 obtuvo una canonj�a (dignidad por la que se pertenec�a a los sacerdotes de una iglesia) como penitenciario. Muri� en 1949.')");
				db.execSQL("INSERT INTO Lugares (nombre, nombreMostrar, calle, descripcion) VALUES ('marques_de_vallejo', 'Calle Marqu�s de Vallejo', 'Calle Marqu�s de Vallejo', " +
						"'Recibe este nombre por Diego Fern�ndez Vallejo. Banquero y mecenas. Senador. Naci� en Soto de Cameros el 14 de marzo de 1824.Hijo de D. Manuel Fern�ndez Segura y de Da. Nicolasa Vallejo y Ba�os. Se traslad� joven a Madrid y vivi� en la calle Fuencarral, precisamente donde est� situado el edificio de Telef�nica."
						+"Dedic� toda su vida a la Banca, donde fue agente de bolsa, y a la pol�tica, cosechando grandes �xitos en ambos sectores. Fue elegido diputado a Cortes por los distritos de N�jera y Torrecilla en 1839; por la provincia de Logro�o en 1865 y desde 1877 senador vitalicio, consejero de Agricultura, Industria y Comercio y, desde el 4 de octubre de ese a�o, oficial mayor de la Secretar�a del Senado."
						+"Gran amigo de C�novas del Castillo tuvo gran influencia en la Restauraci�n Mon�rquica a cuyo proceso ayud� con su gran fortuna. La reina Isabel II le concedi� en atenci�n a sus buenos servicios a la Corona el t�tulo de Marqu�s de Vallejo en 1864.')");
				db.execSQL("INSERT INTO Lugares (nombre, nombreMostrar, calle, descripcion) VALUES ('portales', 'Calle Portales', 'Calle Portales', 'La Calle Portales es una de las calles del casco antiguo de Logro�o (La Rioja, Espa�a), una de las principales arterias de la ciudad antes de su expansi�n a mediados del siglo XX.')");
				db.execSQL("INSERT INTO Lugares (nombre, nombreMostrar, calle, descripcion) VALUES ('marques_de_murrieta', 'Calle Marqu�s de Murrieta', 'Calle Marqu�s de Murrieta'," +
						"'Nombrada as� por Don Luciano Francisco Ram�n Murrieta, nacido en Per� en Septiembre de 1822 se incorpor� pronto al ej�rcito al venir a Espa�a. Conoci� al General Espartero con qui�n realiz� la campa�a militar de la guerra Carlista y de qui�n ser�a ayudante personal desde 1840 hasta la ca�da de este de la Regencia con Isabel II en 1843 . Acompa�� al general en su exilio londinense y all� plane� su futuro, que no pasaba por el oficio de las armas."
						+"A la vuelta a Espa�a abandon� la carrera militar y se instal� en Logro�o. La amistad con Espartero y el hecho de que �ste contrajera matrimonio con la hija de una distinguida y adinerada familia riojana, le permiti� iniciarse en el negocio de los vinos, para lo cual el general le cedi� unos terrenos y le permiti� usar la propias bodegas que pose�a."
						+"Murrieta destac� por su capacidad para mejorar la calidad de los vinos del Duque de la Victoria, siendo recompensado con premios internacionales. Despu�s viaj� a Burdeos en donde aprendi� las �ltimas t�cnicas y conocimientos sobre viticultura."
						+"A la muerte de Espartero, se lanz� Luciano a crear sus propias bodegas adquiriendo unos terrenos en la finca conocida por Ygay. All� cultiv� vi�as, cereal y aceite, siempre con la pretensi�n de obtener una gran calidad en sus productos."
						+"El Rey de Espa�a, Amadeo de Saboya, le otorg� el t�tulo de Marqu�s de Murrieta en reconocimiento a su labor en la obtenci�n de vinos riojanos de calidad. Muri� en Noviembre de 1911.')");
				db.execSQL("INSERT INTO Lugares (nombre, nombreMostrar, calle, descripcion) VALUES ('plaza_del_mercado', 'Plaza del Mercado', 'Plaza del Mercado', " +
						"'Esta plaza, que ha tenido sucesivas denominaciones con el correr de los tiempos, fue originariamente en el siglo XVI la Plaza Mayor que todas las ciudades ten�an como centro y referencia, aunque el Ayuntamiento no empez� a controlar la disposici�n y ordenaci�n de los edificios que se levantan a su alrededor hasta mediados del siglo XIX.En este espacio se celebraron tradicionalmente las ferias que tanta prosperidad trajeron la ciudad."
						+"Desde el siglo XI, con el otorgamiento del Fuero de Logro�o y, sobre todo, la concesi�n real alcanzada en el 1195 de celebraci�n de dos mercados francos (libres de impuestos) al a�o, marcaron la tradici�n comercial de la ciudad."
						+"La plaza fue sufriendo diversas modificaciones y ampliaciones con el paso de los a�os.Los portalillos elemento que la caracteriza y establece un estilo acorde con los Portales fueron construidos en el siglo XIX."
						+"La �ltima y profunda remodelaci�n sufrida por la plaza fue en 1986, quedando un amplio espacio que han ocupado masivamente las terrazas de los bares que se han instalado en el per�metro de la plaza.Es, en estos momentos, un lugar de referencia para el ocio.')");
				db.execSQL("INSERT INTO Lugares (nombre, nombreMostrar, calle, descripcion) VALUES ('puente_de_hierro', 'Puente de Hierro', 'Puente de Hierro', 'El puente de Hierro o puente de Sagasta es el m�s antiguo de los cuatro puentes que atraviesan el r�o Ebro a su paso por la capital riojana. Fue inaugurado en 1882 y tiene una longitud de 330 metros. Construido a unos 8 metros sobre el r�o para salvar las grandes crecidas.  El proyecto se aprob� el 24 de octubre de 1881, y se inaugur� el 18 de diciembre de 1882. Se le denomin� como Puente de Sagasta, en honor al pol�tico riojano Pr�xedes Mateo Sagasta, que promovi� la construcci�n del mismo tras el hundimiento de un puente volante militar, que ocurri� el 1 de septiembre de 1880, en el que fallecieron noventa soldados.')");
				db.execSQL("INSERT INTO Lugares (nombre, nombreMostrar, calle, descripcion) VALUES ('espolon', 'Espol�n', 'Plaza del Espol�n', " +
						"'En el centro de la ciudad de Logro�o, situado entre el casco antiguo y el moderno ensanche, se encuentra el Paseo del Pr�ncipe de Vergara tambi�n llamado Paseo del Espol�n por su semejanza con el famoso paseo burgal�s. Sus or�genes se remontan al siglo XIX, destacando de todo el conjunto el impresionante paseo de pl�tanos podados que rodea todo su per�metro y la gran estatua dedicada al general Espartero, que vivi� en esta ciudad desde el regreso de su exilio londinense hasta su muerte en 1879.')");
				db.execSQL("INSERT INTO Lugares (nombre, nombreMostrar, calle, descripcion) VALUES ('estacion', 'Estaci�n', 'Vara de Rey', 'El 9 de noviembre de 1958, Jorge Vig�n ministro de Obras P�blicas, inaugur� una nueva estaci�n construida por RENFE.5 Conclu�an as� unas obras iniciadas diez a�os antes y que dotaban a la ciudad de un recinto de casi 11 000 metros cuadrados formado por un pabell�n central, dos alas laterales y dos torres en cada extremo que contaba entre otros con taquillas para la venta de billetes, estanco, bar, oficina bancaria, consigna, estafeta de correos y dependencias policiales. Incorporaba tambi�n una zona de viviendas destinada a los trabajadores ferrovarios. En total la obra cost� algo m�s de 10 millones de pesetas.5 El Correos Bilbao-Zaragoza fue el primer tren que estren� las instalaciones. El 9 de agosto de 2010 fue derribada.')");
				db.execSQL("INSERT INTO Lugares (nombre, nombreMostrar, calle, descripcion) VALUES ('gran_via', 'Gran Via', 'Calle Gran Via', " +
						"'Construida sobre las antiguas v�as de ferrocarril, en ella se dieron varios atrevidos proyectos urban�sticos como la Torre de Logro�o y sus 19 plantas. Se discuti� llamarla Gran V�a de Don Julio Pernas Heredia, pero �l mismo lo rechaz�. Desde Junio de 1967 hasta 1975 se llam� Gran V�a de Gonzalo de Berceo, a partir de esa fecha pas� a llamarse Gran V�a de Juan Carlos I. La gran avenida se fue poblando de edificios con gran celeridad y de notables establecimientos en sus bajos.')");
				db.execSQL("INSERT INTO Lugares (nombre, nombreMostrar, calle, descripcion) VALUES ('vara_de_rey', 'Vara de Rey', 'Calle Vara de Rey', ' Se denomina desde 1941 as� en honor al General Espa�ol Joaqu�n Vara de Rey que tuvo un papel destacado en la defensa del Caney durante la guerra hispano-estadounidense en Cuba')");
				db.execSQL("INSERT INTO Lugares (nombre, nombreMostrar, calle, descripcion) VALUES ('palacio_justicia', 'Palacio de justicia', 'Calle Muro de la Mata', 'Viejo Palacio de Justicia, el nuevo se construir� reformando el Hospital Militar de la zona de Murrieta. El proyecto para la construcci�n del nuevo Palacio de Justicia de La Rioja, que ir� ubicado en las dependencias del antiguo cuartel de la Guardia Civil de la logro�esa calle Murrieta, es promovido por el Gobierno de La Rioja, a trav�s de la Consejer�a de Presidencia y Justicia.')");

				// Puntos de pruebas

				db.execSQL("INSERT INTO Lugares (nombre, nombreMostrar, calle, descripcion) VALUES ('universidad', 'Edificio Vives', '--', '--------')");
				//db.execSQL("INSERT INTO Lugares (nombre, nombreMostrar, calle, descripcion) VALUES ('causade', 'Plaza de Causade', '--', '--------')");

				db.execSQL("INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
						+ "VALUES ('espartero_1'"
						+ ", '42.464833', '-2.446005', '343.5', 'espartero', 'A�os 50')");

				db.execSQL("INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
						+ "VALUES ('espartero_2'"
						+ ", '42.464835', '-2.445849', '320.5', 'espartero', 'A�os 60')");

				db.execSQL("INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
						+ "VALUES ('espartero_3'"
						+ ", '42.464771', '-2.446056', '320.5', 'espartero', 'Alrededor 1920')");

				db.execSQL("INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
						+ "VALUES ('espartero_4'"
						+ ", '42.464682', '-2.446061', '320.5', 'espartero', 'A�o 1959')");

				db.execSQL("INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar) "
						+ "VALUES ('estacion_autobuses_1'"
						+ ", '42.464472', '-2.445723', '343.5', 'estacion_autobuses')");

				db.execSQL("INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
						+ "VALUES ('estacion_autobuses_2'"
						+ ", '42.460603', '-2.445502', '343.5', 'estacion_autobuses', 'A�os 60')");

				db.execSQL("INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
						+ "VALUES ('carretera_navarra_1'"
						+ ", '42.472497', '-2.444659', '343.5', 'carretera_navarra', '-')");

				db.execSQL("INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
						+ "VALUES ('ciriaco_garrido_1'"
						+ ", '42.462914', '-2.439955', '343.5', 'ciriaco_garrido', '-')");

				db.execSQL("INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
						+ "VALUES ('portales_1'"
						+ ", '42.466361', '-2.448707', '343.5', 'portales', 'A�o 1961')");

				db.execSQL("INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
						+ "VALUES ('portales_2'"
						+ ", '42.466403', '-2.444109', '343.5', 'portales',  'A�os 60')");

				db.execSQL("INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
						+ "VALUES ('marques_de_vallejo_1'"
						+ ", '42.465925', '-2.445717', '343.5', 'marques_de_vallejo', '-')");

				db.execSQL("INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
						+ "VALUES ('plaza_del_mercado_1'"
						+ ", '42.465925', '-2.446256', '343.5', 'plaza_del_mercado', '-')");

				db.execSQL("INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
						+ "VALUES ('palacio_justicia_1'"
						+ ", '42.466495', '-2.446640', '343.5', 'palacio_justicia', '-')");
				
				db.execSQL(
				"INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
				+ "VALUES ('marques_de_murrieta_1'" +
				", '42.465925', '-2.445717', '343.5', 'marques_de_murrieta', '-')"
				);
				/*
				 * db.execSQL(
				 * "INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
				 * + "VALUES ('marques_de_murrieta_2'" +
				 * ", '42.465925', '-2.445717', '343.5', 'marques_de_murrieta', '-')"
				 * );
				 */

				db.execSQL("INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
						+ "VALUES ('puente_de_hierro_1'"
						+ ", '42.471232', '-2.447038', '176', 'puente_de_hierro', '-')");
				
				db.execSQL(
				"INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
				+ "VALUES ('vara_de_rey_1'" +
				", '42.304474', '-1.973428', '225.5', 'vara_de_rey', '-')");
				 
				
				db.execSQL("INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
				 + "VALUES ('vara_de_rey_2'" +
				 ", '42.304474', '-1.973428', '225.5', 'vara_de_rey', '-')");
				 

				db.execSQL("INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
						+ "VALUES ('gran_via_1'"
						+ ", '42.463540', '-2.447166', '225.5', 'gran_via', '-')");
			
				db.execSQL(
				"INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
				+ "VALUES ('gran_via_2'" +
				", '42.304474', '-1.973428', '225.5', 'gran_via', '-')");
				
				/*
				db.execSQL("INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
						+ "VALUES ('causade_1'"
						+ ", '42.303651', '-1.973942', '0', 'causade', '-')");

				db.execSQL("INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
						+ "VALUES ('causade_2'"
						+ ", '42.303607', '-1.973461', '90', 'causade', '-')");

				db.execSQL("INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
						+ "VALUES ('causade_3'"
						+ ", '42.303215', '-1.973998', '180', 'causade', '-')");

				db.execSQL("INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
						+ "VALUES ('causade_4'"
						+ ", '42.303077', '-1.973671', '270', 'causade', '-')");

				*/
				 db.execSQL(
				 "INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
				  + "VALUES ('universidad_1'" +
				  ", '42.463871', '-2.427422', '23', 'universidad', 'En la actualidad')"
				  );
				 
				 db.execSQL(
				 "INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
				 + "VALUES ('universidad_2'" +
				 ", '42.464066', '-2.426192', '296', 'universidad', 'En la actualidad')"
				 );
				  
				 db.execSQL(
				 "INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
				 + "VALUES ('universidad_3'" +
				 ", '42.463681', '-2.425970', '309', 'universidad', 'En la actualidad')"
				 );
				  
				 db.execSQL(
				 "INSERT INTO Fotografias (ruta, coordenadaNS, coordenadaEO, direccion, nombreLugar, epoca) "
				 + "VALUES ('universidad_4'" +
				 ", '42.463399', '-2.425907', '318', 'universidad', 'En la actualidad')"
				 );
				 

			} catch (Exception e) {

				e.printStackTrace();
			}

			// Cerramos la base de datos
			db.close();
		}
		// Fin de la insercion de datos

		imageViewRadar = (ImageView) findViewById(R.id.imageViewRadar);
		imageViewRadar.setAdjustViewBounds(true);
		imageViewRadar.setBackgroundColor(Color.BLACK);
		// imageViewRadar.setVisibility(View.VISIBLE);

		/*
		 * RelativeLayout.LayoutParams paramsRadar = new
		 * RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
		 * RelativeLayout.LayoutParams.WRAP_CONTENT);
		 * paramsRadar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
		 * RelativeLayout.TRUE);
		 * paramsRadar.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
		 * RelativeLayout.TRUE);
		 * 
		 * 
		 * float leftMargin = 97f*density; paramsRadar.leftMargin =
		 * (int)leftMargin; System.out.println("leftMargin: "+leftMargin); float
		 * bottomMargin = 110f*density; paramsRadar.bottomMargin =
		 * (int)bottomMargin; System.out.println("bottomMargin: "+bottomMargin);
		 * float width = 179f*density; paramsRadar.width = (int)width; float
		 * height = 179f*density; paramsRadar.height = (int)height;
		 * imageViewRadar.setLayoutParams(paramsRadar);
		 */

		imageViewRadar.setBackgroundResource(R.drawable.frame_animation);
		imageViewRadar.setOnClickListener(this);

		ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(imageViewRadar,
				"scaleX", 1.42f);
		ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(imageViewRadar,
				"scaleY", 1.35f);
		scaleDownX.setDuration(0);
		scaleDownY.setDuration(0);
		AnimatorSet scaleDown = new AnimatorSet();
		scaleDown.play(scaleDownX).with(scaleDownY);
		scaleDown.start();

		SeekBar mSeekBar = (SeekBar) findViewById(R.id.seekBar);
		mSeekBar.setOnSeekBarChangeListener(this);

		Bitmap bitmapImage = BitmapFactory.decodeResource(getResources(),
				R.drawable.manivela2);

		Bitmap correctedBitmap = Bitmap.createScaledBitmap(bitmapImage, 30, 80,
				true);

		BitmapDrawable bd = new BitmapDrawable(getResources(), correctedBitmap);
		mSeekBar.setThumb(bd);

		mSeekBar.setProgress(50);
		mSeekBar.refreshDrawableState();

		this.listaDisponibles = new ArrayList<String>();

		listViewDisponibles = (HorizontalListView) findViewById(R.id.listview);
		listViewDisponibles.setAdapter(new HorizontalAdapter(this,
				this.listaDisponibles));

		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) listViewDisponibles
				.getLayoutParams();
		params.setMargins(30, 20, 30, 0);

		listViewDisponibles.setLayoutParams(params);

		// System.out.println(listViewDisponibles.getChildCount());

		imageViewLogoTipo = (ImageView) findViewById(R.id.imageViewLogotipo);

		comprobadorServicios = new ComprobardorServicios(this);
		timerComprobardorServicios = new Timer();
		timerComprobardorServicios.schedule(comprobadorServicios, 0, 2000);

		rastreadorActivo = false;

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		int distancia = settings.getInt("distanciaNotificacion", 50);
		actualizaDistancia(distancia);

		// getActionBar().setDisplayHomeAsUpEnabled(true);

		ImageButton imageButtonYoutube = (ImageButton) findViewById(R.id.buttonYoutube);
		imageButtonYoutube.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://www.youtube.com"));
				startActivity(intent);

			}

		});
		
		ImageButton imageButtonFacebook = (ImageButton) findViewById(R.id.buttonFacebook);
		imageButtonFacebook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("https://www.youtube.com/user/RiojaPastView"));
				startActivity(intent);

			}

		});
		
		ImageButton imageButtonTwitter = (ImageButton) findViewById(R.id.buttonTwitter);
		imageButtonTwitter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("https://twitter.com/RiojaPastView"));
				startActivity(intent);

			}

		});

	}

	private void animate() {
		ImageView imgView = (ImageView) findViewById(R.id.imageViewRadar);
		imgView.setVisibility(View.VISIBLE);
		imgView.setBackgroundResource(R.drawable.frame_animation);

		AnimationDrawable frame = (AnimationDrawable) imgView.getBackground();
		if (frame.isRunning()) {

			frame.stop();

			/*
			 * // Get the frame of the animation Drawable currentFrame,
			 * checkFrame; currentFrame = frame.getCurrent();
			 * 
			 * // Checks the position of the frame for (int i = 0; i <
			 * frame.getNumberOfFrames(); i++) { checkFrame = frame.getFrame(i);
			 * if (checkFrame == currentFrame) { frameNumber = i; break; } }
			 */

		} else {

			frame.stop();
			frame.start();
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
		
		case R.id.action_settings:
    	    //System.out.println(getResources().getConfiguration().locale);

			final CharSequence[] items = {"Espa�ol", "English"};
			final int selectedItem=-1;
		    //AlertDialog.Builder builder = new AlertDialog.Builder(this);
			AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_DeviceDefault_Light_Dialog));
			String titulo = getResources().getString(R.string.configuracion);
			builder.setTitle(titulo);
			builder.setIcon(R.drawable.ic_launcher);
			builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			    	//Toast.makeText(getApplicationContext(), items[item]+" "+getApplicationContext().toString(), Toast.LENGTH_SHORT).show();
			    	locale = item;

			    }
			});
			String aceptar = getResources().getString(R.string.aceptar);
			String cancelar = getResources().getString(R.string.cancelar);

			builder.setPositiveButton(aceptar,
			 new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int id) {
			   //Toast.makeText(getApplicationContext(),"Success", Toast.LENGTH_SHORT).show();
				  Locale localeObj = null;
				  if (locale == 0){
					  localeObj = new Locale("es");
			    	}else{
			    		localeObj = new Locale("en");
			    	}
		    		//getApplicationContext().getResources().getConfiguration().locale= localeObj;
		    		DisplayMetrics dm = getResources().getDisplayMetrics();
		    	    Configuration conf = getResources().getConfiguration();
		    	    conf.locale = /*new Locale(language_code.toLowerCase());*/localeObj;
		    	    getResources().updateConfiguration(conf, dm);
		    	    
		    	    //System.out.println(conf.locale);
		    		//setContentView(R.layout.principal);
		    	    //finish();

			  }
			 });
			builder.setNegativeButton(cancelar,
			 new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int id) {
			   //Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
			  }
			 });
			AlertDialog alert = builder.create();
			alert.show();
			
			return true;

		case R.id.action_acerca:
			AlertDialog.Builder builder2 = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_DeviceDefault_Light_Dialog));
		    LayoutInflater inflater = getLayoutInflater();
		    builder2.setView(inflater.inflate(R.layout.acerca, null));
			AlertDialog alert2 = builder2.create();
			alert2.show();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		//Toast.makeText(this, "onPause MainActivity", Toast.LENGTH_SHORT).show();

		// imageViewRadar.getDrawable().setCallback(null);
		if (timerComprobardorServicios != null)
			timerComprobardorServicios.cancel();

		if (timerRastreador != null)
			timerRastreador.cancel();

	}

	@Override
	public void onProgressChanged(SeekBar arg0, int valor, boolean arg2) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("distanciaNotificacion", valor);
		editor.commit();

		actualizaDistancia(valor);

	}

	private void actualizaDistancia(int valor) {
		TextView textViewDistancia = (TextView) findViewById(R.id.textViewDisplayMetros);
		textViewDistancia.setText(valor + "m");
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	public void setInternetOn(boolean b) {
		ImageView iv = (ImageView) this.findViewById(R.id.imageViewInternet);
		if (b) {
			iv.setImageResource(R.drawable.led_circle_green);
		} else {
			iv.setImageResource(R.drawable.led_circle_red);
		}

	}

	public void setGpsOn(boolean b) {
		ImageView iv = (ImageView) this.findViewById(R.id.imageViewGps);
		if (b) {
			iv.setImageResource(R.drawable.led_circle_green);
		} else {
			iv.setImageResource(R.drawable.led_circle_red);
		}

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//Toast.makeText(this, "onStart MainActivity", Toast.LENGTH_SHORT).show();

		/*
		 * LocationManager locationManager = (LocationManager)
		 * getSystemService(Context.LOCATION_SERVICE); final boolean gpsEnabled
		 * = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		 * 
		 * if (!gpsEnabled) { // Build an alert dialog here that requests that
		 * the user enable // the location services, then when the user clicks
		 * the "OK" button, // call enableLocationSettings() //new
		 * EnableGpsDialogFragment().show(getSupportFragmentManager(),
		 * "enableGpsDialog");
		 * 
		 * Toast.makeText(this, "No GPS activado", Toast.LENGTH_SHORT).show();
		 * // activar gps }
		 */

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//Toast.makeText(this, "onDestroy MainActivity", Toast.LENGTH_SHORT).show();


		if (timerComprobardorServicios != null)
			timerComprobardorServicios.cancel();

		if (timerRastreador != null)
			timerRastreador.cancel();
		
	}

	@Override
	public void onClick(View v) {
		if (v == imageViewRadar) {

			animate();

			if (rastreadorActivo) {

				timerRastreador.cancel();
				rastreador.cursor.close();
				rastreador.db.close();
				rastreadorActivo = false;

			} else {

				rastreador = new Rastreador(this);
				timerRastreador = new Timer();
				timerRastreador.schedule(rastreador, 0, 4000);
				
				rastreadorActivo = true;
			}
		}

	}

	public void setListaDisponibles(List<String> listaAux) {

		HorizontalAdapter ha = new HorizontalAdapter(this, listaAux);
		this.listViewDisponibles.setAdapter(ha);
	}

	public List<String> getListaDisponibles() {

		return this.listaDisponibles;
	}

}
