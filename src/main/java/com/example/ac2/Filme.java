package com.example.ac2;

public class Filme {

    private Integer id;
    private String titulo;
    private String diretor;
    private String anoLancamento;
    private float notaPessoal;

    private String genero;
    private boolean viuNoCinema;

    public Filme() {
    }

    public Filme(Integer id, String titulo, String diretor, String anoLancamento, float notaPessoal, String genero, boolean viuNoCinema) {
        this.id = id;
        this.titulo = titulo;
        this.diretor = diretor;
        this.anoLancamento = anoLancamento;
        this.notaPessoal = notaPessoal;
        this.genero = genero;
        this.viuNoCinema = viuNoCinema;
    }

    public Integer getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDiretor() {
        return diretor;
    }

    public String getGenero() {
        return genero;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDiretor(String diretor) {
        this.diretor = diretor;
    }


    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getAnoLancamento() {
        return anoLancamento;
    }

    public float getNotaPessoal() {
        return notaPessoal;
    }

    public boolean isViuNoCinema() {
        return viuNoCinema;
    }

    public void setNotaPessoal(float notaPessoal) {
        this.notaPessoal = notaPessoal;
    }

    public void setAnoLancamento(String anoLancamento) {
        this.anoLancamento = anoLancamento;
    }

    public void setViuNoCinema(boolean viuNoCinema) {
        this.viuNoCinema = viuNoCinema;
    }
}
