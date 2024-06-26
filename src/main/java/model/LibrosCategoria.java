package model;

public class LibrosCategoria {
    private int idLibro;
    private int idCategoria;
    
     public LibrosCategoria(int idLibros, int idCategoria) {
        this.idLibro = idLibros;
        this.idCategoria = idCategoria;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

   
}
