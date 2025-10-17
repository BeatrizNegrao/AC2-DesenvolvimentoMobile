package com.example.ac2;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText editTitulo, editDiretor, editAnoLancamento, editNota, editGenero;
    CheckBox checkBoxViuCinema;
    Button buttonSalvar;
    ListView listViewFilmes;
    FilmeDbHelper databaseHelper;
    ArrayAdapter<String> adapter;
    ArrayList<String> listaFilmes;
    ArrayList<Integer> listaIds;

    private Integer filmeSelecionadoId = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTitulo = findViewById(R.id.editTitulo);
        editDiretor = findViewById(R.id.editDiretor);
        editAnoLancamento = findViewById(R.id.editAnoLancamento);
        editNota = findViewById(R.id.editNota);
        editGenero = findViewById(R.id.editGenero);
        checkBoxViuCinema = findViewById(R.id.checkBoxViuCinema);
        buttonSalvar = findViewById(R.id.buttonSalvar);
        listViewFilmes = findViewById(R.id.listViewFilmes);

        databaseHelper = new FilmeDbHelper(this);

        // Carrega os filmes do banco na lista ao iniciar a activity
        carregarFilmes();

        buttonSalvar.setOnClickListener(v -> salvarOuAtualizarFilme());

        listViewFilmes.setOnItemClickListener((parent, view, position, id) -> {
            filmeSelecionadoId = listaIds.get(position);
            try (Cursor cursor = databaseHelper.getReadableDatabase().rawQuery("SELECT * FROM " + FilmeDbHelper.TABLE_FILMES + " WHERE " + FilmeDbHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(filmeSelecionadoId)})) {
                if (cursor.moveToFirst()) {
                    editTitulo.setText(cursor.getString(cursor.getColumnIndexOrThrow(FilmeDbHelper.COLUMN_TITULO)));
                    editDiretor.setText(cursor.getString(cursor.getColumnIndexOrThrow(FilmeDbHelper.COLUMN_DIRETOR)));
                    editAnoLancamento.setText(cursor.getString(cursor.getColumnIndexOrThrow(FilmeDbHelper.COLUMN_ANO)));
                    editNota.setText(String.valueOf(cursor.getFloat(cursor.getColumnIndexOrThrow(FilmeDbHelper.COLUMN_NOTA))));
                    editGenero.setText(cursor.getString(cursor.getColumnIndexOrThrow(FilmeDbHelper.COLUMN_GENERO)));

                    // Marca o CheckBox se o valor for 1 (true)
                    boolean viuCinema = cursor.getInt(cursor.getColumnIndexOrThrow(FilmeDbHelper.COLUMN_VIU_CINEMA)) == 1;
                    checkBoxViuCinema.setChecked(viuCinema);
                    buttonSalvar.setText("Atualizar");
                }
            }
        });


        listViewFilmes.setOnItemLongClickListener((parent, view, position, id) -> {
            int idFilme = listaIds.get(position);

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Excluir Filme")
                    .setMessage("Tem certeza que deseja excluir este filme?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        int deletado = databaseHelper.excluirFilme(idFilme);
                        if (deletado > 0) {
                            Toast.makeText(MainActivity.this, "Filme excluído!", Toast.LENGTH_SHORT).show();
                            limparCamposEVoltarParaModoSalvar();
                            carregarFilmes();
                        } else {
                            Toast.makeText(MainActivity.this, "Erro ao excluir!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Não", null)
                    .show();

            return true;
        });
    }

    private void salvarOuAtualizarFilme() {
        // Pega os dados dos campos de texto
        String titulo = editTitulo.getText().toString().trim();
        String diretor = editDiretor.getText().toString().trim();
        String ano = editAnoLancamento.getText().toString().trim();
        String notaStr = editNota.getText().toString().trim();
        String genero = editGenero.getText().toString().trim();
        boolean viuCinema = checkBoxViuCinema.isChecked();
        // Converte o boolean para inteiro (1 para true, 0 para false) para o SQLite
        int viuCinemaInt = viuCinema ? 1 : 0;

        if (titulo.isEmpty() || diretor.isEmpty() || ano.isEmpty() || notaStr.isEmpty() || genero.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        Float nota;
        try {
            nota = Float.parseFloat(notaStr);
            if (nota < 1 || nota > 5) {
                Toast.makeText(this, "A nota deve ser entre 1 e 5.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Por favor, insira um valor numérico para a nota.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (filmeSelecionadoId == null) {
            // MODO SALVAR (nenhum filme selecionado)
            long resultado = databaseHelper.inserirFilme(titulo, diretor, ano, nota, genero, viuCinemaInt);
            if (resultado != -1) {
                Toast.makeText(this, "Filme salvo com sucesso!", Toast.LENGTH_SHORT).show();
                limparCamposEVoltarParaModoSalvar();
            } else {
                Toast.makeText(this, "Erro ao salvar filme!", Toast.LENGTH_SHORT).show();
            }
        } else {
            int resultado = databaseHelper.atualizarFilme((long) filmeSelecionadoId, titulo, diretor, ano, nota, genero, viuCinemaInt);
            if (resultado > 0) {
                Toast.makeText(this, "Filme atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                limparCamposEVoltarParaModoSalvar();
            } else {
                Toast.makeText(this, "Erro ao atualizar filme!", Toast.LENGTH_SHORT).show();
            }
        }

        carregarFilmes();
    }


    private void carregarFilmes() {
        Cursor cursor = databaseHelper.listarFilmes();
        listaFilmes = new ArrayList<>();
        listaIds = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(FilmeDbHelper.COLUMN_ID));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow(FilmeDbHelper.COLUMN_TITULO));
                String ano = cursor.getString(cursor.getColumnIndexOrThrow(FilmeDbHelper.COLUMN_ANO));
                float nota = cursor.getFloat(cursor.getColumnIndexOrThrow(FilmeDbHelper.COLUMN_NOTA));
                String genero = cursor.getString(cursor.getColumnIndexOrThrow(FilmeDbHelper.COLUMN_GENERO));

                listaIds.add(id);
                listaFilmes.add(titulo + " (" + ano + ")\n" + "Gênero: " + genero + " - Nota: " + nota);

            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaFilmes);
        listViewFilmes.setAdapter(adapter);
    }

    private void limparCamposEVoltarParaModoSalvar() {
        // Limpa todos os campos de entrada
        editTitulo.setText("");
        editDiretor.setText("");
        editAnoLancamento.setText("");
        editNota.setText("");
        editGenero.setText("");
        checkBoxViuCinema.setChecked(false);
        filmeSelecionadoId = null;
        buttonSalvar.setText("Salvar");
        editTitulo.requestFocus();
    }
}