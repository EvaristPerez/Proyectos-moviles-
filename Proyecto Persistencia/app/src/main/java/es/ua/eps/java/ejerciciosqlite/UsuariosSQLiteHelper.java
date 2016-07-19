package es.ua.eps.java.ejerciciosqlite;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by mastermoviles on 10/12/15.
 */
public class UsuariosSQLiteHelper extends SQLiteOpenHelper {


    String sqlCreate = "CREATE TABLE Usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre_usuario TEXT, password TEXT, nombre_completo TEXT)";

    public UsuariosSQLiteHelper(Context contexto, String nombre, SQLiteDatabase.CursorFactory factory, int version) {
            super(contexto, nombre, factory, version);

    }

    public static UsuariosSQLiteHelper CreateUsuarioSQLiteHelper(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String nombreBD = sharedPreferences.getString("NombreFichero", "DBUsuarios");
        int versionBD = Integer.parseInt(sharedPreferences.getString("VersionBD", "1"));

        return new UsuariosSQLiteHelper(context, nombreBD, null, versionBD);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        Cursor cursor = db.rawQuery("SELECT * FROM Usuarios", null);
        int deleteStateColumnIndex = cursor.getColumnIndex("email");
        if (deleteStateColumnIndex < 0) {
            db.execSQL("ALTER TABLE Usuarios ADD COLUMN email TEXT DEFAULT 'usuario@db.com'");
        }

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("CREATE TABLE UsuariosAuxiliar as select id, nombre_usuario, password, nombre_completo from Usuarios");
        db.execSQL("DROP TABLE IF EXISTS Usuarios");
        db.execSQL("ALTER TABLE UsuariosAuxiliar RENAME TO Usuarios");

    }
}

