package voronoi;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author MariaOB
 */

public class Triangulo extends Conjunto<Punto> {
    private int identificador;
    private Punto circuncentro = null;
    private static int generadorDeIdentificadores = 0;
    
    public Triangulo (Punto... vertices) {
        this(Arrays.asList(vertices));
    }

    public Triangulo (Collection<? extends Punto> coleccion) {
        super(coleccion);
        identificador = generadorDeIdentificadores++;
        if (this.size() != 3)
            throw new IllegalArgumentException("Un triángulo tiene 3 vértices.");
    }

    public Punto getVerticePeroNoUnoDeEstos (Punto... noDeseados) {
        Collection<Punto> noValidos = Arrays.asList(noDeseados);
        for (Punto v: this) 
            if (!noValidos.contains(v)) 
                return v;
        
        throw new NoSuchElementException("Ningun vértice encontrado.");
    }

    //Dos triángulos son vecinos si comparten alguna cara.
    public boolean esVecino (Triangulo triangulo) {
        int count = 0;
        for (Punto vertex: this)
            if (!triangulo.contains(vertex)) 
                count++;
        return count == 1;
    }

    public Conjunto<Punto> caraOpuesta (Punto vertice) {
        Conjunto<Punto> cara = new Conjunto<Punto>(this);
        if (!cara.remove(vertice))
            throw new IllegalArgumentException("El vértice no está en este triángulo");
        return cara;
    }

    public Punto getCircuncentro () {
        if (circuncentro == null)
            circuncentro = Punto.circuncentro(this.toArray(new Punto[0]));
        return circuncentro;
    }
    
    @Override
    public String toString () {
        return "Triángulo" + super.toString();
    }

    @Override
    public Iterator<Punto> iterator () {
        return new Iterator<Punto>() {
            private Iterator<Punto> it = Triangulo.super.iterator();
            @Override
            public boolean hasNext() {return it.hasNext();}
            @Override
            public Punto next() {return it.next();}
            @Override
            public void remove() {throw new UnsupportedOperationException();}
        };
    }
}