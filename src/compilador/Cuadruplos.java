package compilador;

import java.util.ArrayList;

public class Cuadruplos {

    public  ArrayList<Cuadruplo> cuadruplos;
    private Compilador           cmp;
    
    public Cuadruplos ( Compilador c ) {
        cmp = c;
        cuadruplos = new ArrayList<Cuadruplo> ();
    }
    
    public void inicializar () {
        cuadruplos.clear();
    }
    
    public void insertar ( Cuadruplo c ) {
        cuadruplos.add ( c );
    }
    
    public ArrayList<Cuadruplo> getCuadruplos () {
        return cuadruplos;
    }
}
