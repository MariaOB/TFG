import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


public class Conjunto<T> extends AbstractSet<T> {

    private ArrayList<T> elementos;

    // Creamos un conjunto vacio de tamaño 3
    public Conjunto () {
        this(3);
    }

    public Conjunto (int capacidadInicial) {
        elementos  = new ArrayList<>(capacidadInicial);
    }
    
    public Conjunto (Collection<? extends T> coleccion) {
        elementos = new ArrayList<>(coleccion.size());
        for (T item: coleccion)
            if (!elementos.contains(item)) 
                elementos.add(item);
    }

    
    public T get (int i) throws IndexOutOfBoundsException {
        return elementos.get(i);
    }

    public boolean contieneAlguno (Collection<?> coleccion) {
        for (Object elem: coleccion)
            if (this.contains(elem)) 
                return true;
        return false;
    }

    @Override
    public boolean add(T elem) {
        if (elementos.contains(elem)) 
            return false;
        return elementos.add(elem);
    }

    @Override
    public Iterator<T> iterator() {
        return elementos.iterator();
    }

    @Override
    public int size() {
        return elementos.size();
    }

}
