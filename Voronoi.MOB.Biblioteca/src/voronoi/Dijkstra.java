package voronoi;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * @author MariaOB
 */

public class Dijkstra {
    private final int MAX = 10000;  
    private final int INF = 1000000000;     
    private List<List<Node>> adyacentes = new ArrayList<List<Node>>();
    private int distancia[] = new int[MAX]; 
    private boolean visitado[] = new boolean[MAX];
    private PriorityQueue<Node> Q = new PriorityQueue<Node>(); 
    private int numVertices;                                    
    private int previo[] = new int[MAX];  
    private boolean dijkstra;
    
    public Dijkstra(int vertices){
        this.numVertices = vertices;
        for(int i = 0; i <= vertices; i++) 
            adyacentes.add(new ArrayList<Node>());
        dijkstra = false;
    }
    
    class Node implements Comparable<Node>{
        int primero, segundo;
        Node(int p, int s){                         
            this.primero = p;
            this.segundo = s;
        }
        @Override
        public int compareTo(Node otro){           
            if(segundo > otro.segundo) 
                return 1;
            if(segundo == otro.segundo) 
                return 0;
            return -1;
        }
    };
    
    private void iniciar(){
        for(int i = 0; i <= numVertices; i++){
            distancia[i] = INF; 
            visitado[i] = false; 
            previo[i] = -1; 
        }
    }

    private void algoritmo(int actual, int adyacente, int peso){
        if(distancia[actual]+peso < distancia[adyacente]){
            distancia[adyacente] = distancia[actual]+peso; 
            previo[adyacente] = actual;
            Q.add(new Node(adyacente,distancia[adyacente])); 
        }
    }

    public void dijkstra(int inicial){
        iniciar(); 
        Q.add(new Node(inicial,0)); 
        distancia[inicial] = 0;
        int actual,adyacente,peso;
        while(!Q.isEmpty()){       
            actual = Q.element().primero;
            Q.remove();
            if(visitado[actual]) 
                continue; 
            visitado[actual] = true; 

            for(int i = 0; i < adyacentes.get(actual).size(); i++){
                adyacente = adyacentes.get(actual).get(i).primero;  
                peso = adyacentes.get(actual).get(i).segundo; 
                if(!visitado[adyacente]){   
                    algoritmo(actual,adyacente,peso);
                }
            }
            dijkstra = true;
        }
    }
    
    public void aniadeArista(int origen,int destino,int peso){
        adyacentes.get(origen).add(new Node(destino, peso));  
        adyacentes.get(destino).add(new Node(origen, peso)); 
    }
     
    public ArrayList<Integer> caminoMasCorto( int destino){
        ArrayList<Integer> camino = new ArrayList<Integer>();
        int actual = destino;
        while(previo[actual] != -1){
            camino.add(actual);
            actual = previo[actual];
        }
        camino.add(actual);
        return camino;
    }

    public int getNumberOfVertices() {
        return numVertices;
    }

    public void setNumberOfVertices(int numeroDeVertices) {
        numVertices = numeroDeVertices;
    }
}