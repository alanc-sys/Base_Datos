package models;



import java.time.LocalDateTime;
import java.time.Year;

public class Libro {
    private int id;
    private String titulo;
    private String isbn;
    private String genero;
    private Year anoPublicacion;
    private int paginas;
    private Autor autor;
    private LocalDateTime createdAt;

    public Libro() {
    }
    public Libro(String titulo, String isbn, String genero, Year anoPublicacion, int paginas, LocalDateTime createdAt, Autor autor) {
        this.titulo = titulo;
        this.isbn = isbn;
        this.genero = genero;
        this.anoPublicacion = anoPublicacion;
        this.paginas = paginas;
        this.createdAt = createdAt;
        this.autor = autor;
    }
    public Libro(int id, String titulo, String isbn, String genero, Year anoPublicacion, int paginas, LocalDateTime createdAt, Autor autor) {
        this.id = id;
        this.titulo = titulo;
        this.isbn = isbn;
        this.genero = genero;
        this.anoPublicacion = anoPublicacion;
        this.paginas = paginas;
        this.createdAt = createdAt;
        this.autor = autor;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public Autor getAutor() {
        return autor;
    }
    public void setAutor(Autor autor) {
        this.autor = autor;
    }
    public int getPaginas() {
        return paginas;
    }
    public void setPaginas(int paginas) {
        this.paginas = paginas;
    }
    public Year getAnoPublicacion() {
        return anoPublicacion;
    }
    public void setAnoPublicacion(Year ano_publicacion) {
        this.anoPublicacion = ano_publicacion;
    }
    public String getGenero() {
        return genero;
    }
    public void setGenero(String genero) {
        this.genero = genero;
    }
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}


