package voronoi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author MariaOB
 */

public class Grafo<G> {
    private Map<G, Set<G>> vecinos = new HashMap<G, Set<G>>();
    private Set<G> conjuntoNodos = Collections.unmodifiableSet(vecinos.keySet());

    public void add (G nodo) {
        if (vecinos.containsKey(nodo)) 
            return;
        vecinos.put(nodo, new Conjunto<G>());
     }

    //Añade aristas
    public void add (G nodoA, G nodoB) throws NullPointerException {
        vecinos.get(nodoA).add(nodoB);
        vecinos.get(nodoB).add(nodoA);
    }

    public void remove (G nodo) {
        if (!vecinos.containsKey(nodo)) return;
        for (G vecino: vecinos.get(nodo))
            vecinos.get(vecino).remove(nodo);
        vecinos.get(nodo).clear();
        vecinos.remove(nodo);
    }

    //Elimina aristas
    public void remove (G nodoA, G nodoB) throws NullPointerException {
        vecinos.get(nodoA).remove(nodoB);
        vecinos.get(nodoB).remove(nodoA);
    }

    public Set<G> getVecinos (G nodo) throws NullPointerException {
        return Collections.unmodifiableSet(vecinos.get(nodo));
    }

    public Set<G> getConjuntoNodos () {
        return conjuntoNodos;
    }
}