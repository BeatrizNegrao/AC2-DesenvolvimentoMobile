package com.example.ac2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FilmeDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "filmes.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_FILMES = "filmes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITULO = "titulo";
    public static final String COLUMN_DIRETOR = "diretor";
    public static final String COLUMN_ANO = "ano";
    public static final String COLUMN_NOTA = "nota";
    public static final String COLUMN_GENERO = "genero";
    public static final String COLUMN_VIU_CINEMA = "viu_cinema";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_FILMES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITULO + " TEXT, " +
                    COLUMN_DIRETOR + " TEXT, " +
                    COLUMN_ANO + " TEXT, " +
                    COLUMN_NOTA + " REAL, " +
                    COLUMN_GENERO + " TEXT, " +
                    COLUMN_VIU_CINEMA + " INTEGER" + // SQLite não tem boolean, usamos 0 ou 1
                    ");";

    public FilmeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILMES);
        onCreate(db);
    }


    public long inserirFilme(String titulo, String diretor, String anoLancamento, Float notaPessoal, String genero, Integer viuNoCinema)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITULO, titulo);
        values.put(COLUMN_DIRETOR, diretor);
        values.put(COLUMN_ANO, anoLancamento);
        values.put(COLUMN_NOTA, notaPessoal);
        values.put(COLUMN_GENERO, genero);
        values.put(COLUMN_VIU_CINEMA, viuNoCinema);
        return db.insert(TABLE_FILMES, null, values);
    }

    public Cursor listarFilmes()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_FILMES, null);
    }

    public int atualizarFilme(Long id, String titulo, String diretor, String anoLancamento, Float notaPessoal, String genero, Integer viuNoCinema)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITULO, titulo);
        values.put(COLUMN_DIRETOR, diretor);
        values.put(COLUMN_ANO, anoLancamento);
        values.put(COLUMN_NOTA, notaPessoal);
        values.put(COLUMN_GENERO, genero);
        values.put(COLUMN_VIU_CINEMA, viuNoCinema);
        return db.update(TABLE_FILMES, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int excluirFilme(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_FILMES, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }
}
