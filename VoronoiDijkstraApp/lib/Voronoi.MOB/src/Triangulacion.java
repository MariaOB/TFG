import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;


public class Triangulacion extends AbstractSet<Triangulo> {

    private Triangulo ultimoTriangulo = null;
    private Grafo<Triangulo> GrafoTriangulos;

    
    public Triangulacion (Triangulo triangulo) {
        GrafoTriangulos = new Grafo<Triangulo>();
        GrafoTriangulos.add(triangulo);
        ultimoTriangulo = triangulo;
    }


    @Override
    public String toString () {
        return "Triangulacion con " + size() + " triangulos";
    }

    @Override
    public boolean contains (Object triangulo) {
        return GrafoTriangulos.getConjuntoNodos().contains(triangulo);
    }

    
    public Triangulo vecinoOpuesto (Punto vertice, Triangulo triangulo) {
        if (!triangulo.contains(vertice))
            throw new IllegalArgumentException("Bad vertex; not in triangulo");
        for (Triangulo vecino: GrafoTriangulos.getVecinos(triangulo)) {
            if (!vecino.contains(vertice)) return vecino;
        }
        return null;
    }


    public Set<Triangulo> getVecinos(Triangulo triangulo) {
        return GrafoTriangulos.getVecinos(triangulo);
    }

    // lugar es vertice de triangulo
    public List<Triangulo> TriangulosDeAlrededor (Punto vertice, Triangulo triangulo) {
        if (!triangulo.contains(vertice))
            throw new IllegalArgumentException("No es un vertice del triangulo");
        
        List<Triangulo> lista = new ArrayList<>();
        
        Triangulo inicial = triangulo;
        Punto guia = triangulo.getVerticePeroNoUnoDeEstos(vertice);
        while (true) {
            lista.add(triangulo);
            Triangulo anterior = triangulo;
            triangulo = this.vecinoOpuesto(guia, triangulo);
            guia = anterior.getVerticePeroNoUnoDeEstos(vertice, guia);
            if (triangulo == inicial) break;
        }
        return lista;
    }

    public Triangulo enQueTrianguloEsta (Punto punto) {
        Triangulo triangulo = ultimoTriangulo;
        if (!this.contains(triangulo)) 
            triangulo = null;
        
        for (Triangulo tri: this) {
            if (punto.estaFuera(tri.toArray(new Punto[0])) == null) return tri;
        }
        
        System.out.println("Ningun triangulo contiene al punto " + punto);
        return null;
    }

    
    public void aniadePuntoDelaunay (Punto lugar) {
        
        Triangulo triangulo = enQueTrianguloEsta(lugar);
        
        if (triangulo == null)
            throw new IllegalArgumentException("No esta en ningun triangulo");
        
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
                continue; // El triangulo no esta en el hueco
            triangulosInvadidos.add(triangulo);
            
            for (Triangulo vecino: GrafoTriangulos.getVecinos(triangulo)){
                if (seleccionados.contains(vecino)) continue;
                seleccionados.add(vecino);
                aComprobar.add(vecino);
            }
        }
        return triangulosInvadidos;
    }

    
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
            GrafoTriangulos.remove(triangulo);

        
        Set<Triangulo> nuevosTriangulos = new HashSet<Triangulo>();
        for (Set<Punto> vertices: bordes) {
            vertices.add(lugar);
            Triangulo tri = new Triangulo(vertices);
            GrafoTriangulos.add(tri);
            nuevosTriangulos.add(tri);
        }

        
        triangulos.addAll(nuevosTriangulos);
        for (Triangulo triangulo: nuevosTriangulos)
            for (Triangulo other: triangulos)
                if (triangulo.esVecino(other))
                    GrafoTriangulos.add(triangulo, other);

        
        return nuevosTriangulos.iterator().next();
    }
    
    /* The following two methods are required by AbstractSet */

    @Override
    public Iterator<Triangulo> iterator () {
        return GrafoTriangulos.getConjuntoNodos().iterator();
    }

    @Override
    public int size () {
        return GrafoTriangulos.getConjuntoNodos().size();
    }

    
    

    /**
     * Main program; used for testing.
     */
    public static void main (String[] args) {
        Triangulo tri =
            new Triangulo(new Punto(-10,10), new Punto(10,10), new Punto(0,-10));
        System.out.println("Triangulo created: " + tri);
        Triangulacion dt = new Triangulacion(tri);
        System.out.println("DelaunayTriangulation created: " + dt);
        dt.aniadePuntoDelaunay(new Punto(0,0));
        dt.aniadePuntoDelaunay(new Punto(1,0));
        dt.aniadePuntoDelaunay(new Punto(0,1));
        System.out.println("After adding 3 points, we have a " + dt);
        System.out.println("Triangulos: " + dt.GrafoTriangulos.getConjuntoNodos());
    }
}