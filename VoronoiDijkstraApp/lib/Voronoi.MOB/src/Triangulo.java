import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

class Triangulo extends Conjunto<Punto> {

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
            throw new IllegalArgumentException("Un triangulo tiene 3 vertices");
    }

    @Override
    public String toString () {
        return "Triangle" + identificador;
    }

    public Punto getVerticePeroNoUnoDeEstos (Punto... noDeseados) {
        Collection<Punto> noValidos = Arrays.asList(noDeseados);
        for (Punto v: this) if (!noValidos.contains(v)) return v;
        throw new NoSuchElementException("Ningun vertice encontrado");
    }

    public boolean esVecino (Triangulo triangulo) {
        boolean contiene = false;
        for (Punto vertice: this)
            if (!triangulo.contains(vertice)) 
                contiene=true;
        return contiene;
    }

    public Conjunto<Punto> caraOpuesta (Punto vertice) {
        Conjunto<Punto> cara = new Conjunto<Punto>(this);
        if (!cara.remove(vertice))
            throw new IllegalArgumentException("El vertice no esta en este triangulo");
        return cara;
    }

    public Punto getCircuncentro () {
        if (circuncentro == null)
            circuncentro = Punto.circuncentro(this.toArray(new Punto[0]));
        return circuncentro;
    }


    @Override
    public Iterator<Punto> iterator () {
        return new Iterator<Punto>() {
            private Iterator<Punto> it = Triangulo.super.iterator();
            public boolean hasNext() {return it.hasNext();}
            public Punto next() {return it.next();}
            public void remove() {throw new UnsupportedOperationException();}
        };
    }

}