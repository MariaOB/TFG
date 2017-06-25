package voronoi;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * @author MariaOB
 *
 * Empleamos el algoritmo incremental que, aunque no es el más rápido para construir 
 * la triangulación de Delaunay, es una manera razonable y muy utilizada.
 * 
 */

/* Creamos una triangulación (conjunto de triángulos) de Delaunay en 2D con inserción incremental. */
public class Triangulacion2D extends AbstractSet<Triangulo> {
    private Triangulo ultimoTriangulo = null;
    private Grafo<Triangulo> grafoTriangulos;

    public Triangulacion2D (Triangulo triangulo) {
        grafoTriangulos = new Grafo<Triangulo>();
        grafoTriangulos.add(triangulo);
        ultimoTriangulo = triangulo;
    }
    
    // Devuelve el triángulo opuesto al vértice dado 
    public Triangulo vecinoOpuesto (Punto vertice, Triangulo triangulo) {
        if (!triangulo.contains(vertice))
            throw new IllegalArgumentException("Vértice incorrecto. No pertenece al triángulo.");
        for (Triangulo vecino: grafoTriangulos.getVecinos(triangulo)) {
            if (!vecino.contains(vertice)) 
                return vecino;
        }
        return null;
    }

    public Set<Triangulo> getVecinos(Triangulo triangulo) {
        return grafoTriangulos.getVecinos(triangulo);
    }

    // Obteniendo el triángulo opuesto, vamos recorriendo los triángulos "vecinos"
    public List<Triangulo> TriangulosDeAlrededor (Punto vertice, Triangulo triangulo) {
        if (!triangulo.contains(vertice))
            throw new IllegalArgumentException("No es un vértice del triángulo.");
        
        List<Triangulo> lista = new ArrayList<>();

        Triangulo inicial = triangulo;
        Punto guia = triangulo.getVerticePeroNoUnoDeEstos(vertice);
        while (true) {
            lista.add(triangulo);
            Triangulo anterior = triangulo;
            triangulo = this.vecinoOpuesto(guia, triangulo);
            guia = anterior.getVerticePeroNoUnoDeEstos(vertice, guia);
            if (triangulo == inicial) 
                break;
        }
        return lista;
    }

    public Triangulo enQueTrianguloEsta (Punto punto) {
        Triangulo triangulo = ultimoTriangulo;
        if (!this.contains(triangulo)) 
            triangulo = null;
        
        for (Triangulo tri: this) {
            if (punto.estaFuera(tri.toArray(new Punto[0])) == null) 
                return tri;
        }
        
        System.out.println("Ningún triángulo contiene al punto " + punto);
        return null;
    }
    
    public void aniadePuntoDelaunay (Punto lugar) {
        Triangulo triangulo = enQueTrianguloEsta(lugar);
        
        if (triangulo == null)
            throw new IllegalArgumentException("No está en ningún triángulo.");
        
        if (triangulo.contains(lugar)) 
            return;

       Set<Triangulo> hueco = getHueco(lugar, triangulo);
       ultimoTriangulo = actualiza(lugar, hueco);
    }

    private Set<Triangulo> getHueco (Punto lugar, Triangulo triangulo) {
        Set<Triangulo> triangulosInvadidos = new HashSet<Triangulo>();
        Queue<Triangulo> aComprobar = new LinkedList<Triangulo>();
        Set<Triangulo> seleccionados = new HashSet<Triangulo>();
        aComprobar.add(triangulo);
        seleccionados.add(triangulo);
        
        while (!aComprobar.isEmpty()) {
            triangulo = aComprobar.remove();
            if (lugar.relacionConCircunferenciaCircunscrita(triangulo.toArray(new Punto[0])) == 1)
                continue; // El triángulo no está en el hueco
            triangulosInvadidos.add(triangulo);
            
            for (Triangulo vecino: grafoTriangulos.getVecinos(triangulo)){
                if (seleccionados.contains(vecino)) 
                    continue;
                seleccionados.add(vecino);
                aComprobar.add(vecino);
            }
        }
        return triangulosInvadidos;
    }
    
    // Actualizamos la triangulación quitando los triángulos del hueco
    private Triangulo actualiza (Punto lugar, Set<Triangulo> hueco) {
        Set<Set<Punto>> bordes = new HashSet<Set<Punto>>();
        Set<Triangulo> triangulos = new HashSet<Triangulo>();
       
        for (Triangulo triangulo: hueco) {            
            triangulos.addAll(getVecinos(triangulo));            
            for (Punto vertice: triangulo) {
                Set<Punto> cara = triangulo.caraOpuesta(vertice);
                if (bordes.contains(cara)) 
                    bordes.remove(cara);
                else 
                    bordes.add(cara);
            }
        }
        triangulos.removeAll(hueco);
        
        for (Triangulo triangulo: hueco) 
            grafoTriangulos.remove(triangulo);
        
        Set<Triangulo> nuevosTriangulos = new HashSet<Triangulo>();
        for (Set<Punto> vertices: bordes) {
            vertices.add(lugar);
            Triangulo tri = new Triangulo(vertices);
            grafoTriangulos.add(tri);
            nuevosTriangulos.add(tri);
        }
        
        // Llenamos el hueco con los nuevos triángulos.
        triangulos.addAll(nuevosTriangulos);
        for (Triangulo triangulo: nuevosTriangulos)
            for (Triangulo other: triangulos)
                if (triangulo.esVecino(other))
                    grafoTriangulos.add(triangulo, other);
        
        return nuevosTriangulos.iterator().next();
    }
 
    ////////////////// Métodos necesarios para AbstractSet /////////////////////
    @Override
    public Iterator<Triangulo> iterator () {
        return grafoTriangulos.getConjuntoNodos().iterator();
    }

    @Override
    public int size () {
        return grafoTriangulos.getConjuntoNodos().size();
    }
        
    @Override
    public String toString () {
        return "Triangulación con " + size() + " triángulos";
    }

    @Override
    public boolean contains (Object triangulo) {
        return grafoTriangulos.getConjuntoNodos().contains(triangulo);
    }
}