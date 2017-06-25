package voronoidijkstraapp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import voronoi.Conjunto;
import voronoi.Punto;
import voronoi.Dijkstra;
import voronoi.Triangulacion2D;
import voronoi.Triangulo;

/**
 * @author MariaOB
 */

public class Lienzo extends javax.swing.JPanel {
    public int modo = 0;
    public double zoom = 1;
    public boolean pintaDelaunay = false;
    public boolean pintaCircunferencias = false;    
    public static Color voronoiColor = Color.WHITE;
    public static Color delaunayColor = Color.WHITE;
    public static int radioPunto = 5;
    public static VentanaPrincipal parent;
    
    public Triangulacion2D dt;                   
    private Map<Object, Color> tablaColor;    
    private Triangulo trianguloInicial;          
    private static int tamInicial = 10000;   
    private Graphics2D g;                         
    private Random random = new Random();  
    
    Conjunto<Punto> verticesDijkstra = new Conjunto<>(); 
    //Lista de aristas que luego añadiremos cuando creemos Dikjstra
    ArrayList<int[]> aristasParaLuego = new ArrayList<>();
    Dijkstra dijkstra = null;
    ArrayList<Integer> caminoDijkstra;
    
    public Lienzo(VentanaPrincipal vp) {
        initComponents();
        parent = vp;
        trianguloInicial = new Triangulo(
                new Punto(-tamInicial, -tamInicial),
                new Punto(tamInicial, -tamInicial),
                new Punto(0, tamInicial));
        dt = new Triangulacion2D(trianguloInicial);
        tablaColor = new HashMap<Object, Color>();
    }
    
    public void clear() {
        dt = new Triangulacion2D(trianguloInicial);
        caminoDijkstra = new ArrayList<>();
        verticesDijkstra = new Conjunto<>();
        aristasParaLuego = new ArrayList<>();
        this.repaint();
    }
    
    public void cambiaZoom(double zoom){
        this.zoom = zoom/5.0;
        this.repaint();
    }

    ///////////////// Métodos Dijkstra /////////////////////////////////////////  
    
    //Obtenemos el vértice de Voronoi más cercano
    private Punto puntoMasCercano(Punto punto){
        //Vemos cuál es su vértice más cercano
        Punto puntoMasCercano = null;
                
        //Sacamos todos los vértices de la triangulación para luego comparar distancias con ellos
        Conjunto<Punto> conjuntoPuntos = new Conjunto<>();
        for (Triangulo triangulo : dt){
            for(Punto puntoaux : triangulo){
                conjuntoPuntos.add(puntoaux);
            }
        }
        for (Punto puntoaux :this.trianguloInicial){
            conjuntoPuntos.remove(puntoaux);
        }
        double distanciaMinima = 1000000000;
        for (Punto puntoaux : conjuntoPuntos){
            if (punto.resta(puntoaux).norma() < distanciaMinima){
                puntoMasCercano = puntoaux;
                distanciaMinima = punto.resta(puntoaux).norma();
            }
        }
        return puntoMasCercano;
    }

    //Calcula el punto más cercano de un segmento a un punto
    private Punto obtenerPuntoMasCercanoASegmento(double x1, double y1, double x2, double y2, double px, double py){
      double xDelta = x2 - x1;
      double yDelta = y2 - y1;
      double u = ((px - x1) * xDelta + (py - y1) * yDelta) / (xDelta * xDelta + yDelta * yDelta);
      final Punto puntoMasCercano;

      if ((xDelta == 0) && (yDelta == 0)){
        throw new IllegalArgumentException("El segmento inicial es igual al segmento final");
      } 
      
      if (u < 0){
        puntoMasCercano = new Punto(x1, y1);
      }
      else if (u > 1){
        puntoMasCercano = new Punto(x2, y2);
      }
      else{
        puntoMasCercano = new Punto((int) Math.round(x1 + u * xDelta), (int) Math.round(y1 + u * yDelta));
      }
      return puntoMasCercano;
    }
    
    //Una vez añadido un punto de corte se añaden las aristas que unen a dicho punto con sus aristas y su centro
    private void aniadeAristasPuntoCorte(Punto InicioArista, Punto FinArista, int InicialOFinal){
        int[] aristaTem;

        aristaTem = new int[3];
        aristaTem[0] = InicialOFinal;
        aristaTem[1] = this.verticesDijkstra.size()-1;
        aristaTem[2] = (int) this.verticesDijkstra.get(InicialOFinal).resta(this.verticesDijkstra.get(this.verticesDijkstra.size() -1)).norma();
        aristasParaLuego.add(aristaTem);

        for (int k = 0; k < verticesDijkstra.size(); k++){
            if (InicioArista.equals(verticesDijkstra.get(k))){
                aristaTem = new int[3];
                aristaTem[0] = k;
                aristaTem[1] = this.verticesDijkstra.size()-1;
                aristaTem[2] = (int) this.verticesDijkstra.get(k).resta(this.verticesDijkstra.get(this.verticesDijkstra.size() -1)).norma();
                aristasParaLuego.add(aristaTem);
            }
            if (FinArista.equals(verticesDijkstra.get(k))){
                aristaTem = new int[3];
                aristaTem[0] = k;
                aristaTem[1] = this.verticesDijkstra.size()-1;
                aristaTem[2] = (int) this.verticesDijkstra.get(k).resta(this.verticesDijkstra.get(this.verticesDijkstra.size() -1)).norma();
                aristasParaLuego.add(aristaTem);
            }
        }
    }
    
    //Para un punto, calcula dentro de que región está y calcula los puntos de corte con cada una de las aristas y los añadimos
    private void aniadeAristasDijkstra(int InicialOFinal){        
        Punto puntoMasCercano = puntoMasCercano(this.verticesDijkstra.get(InicialOFinal));
         //Calculamos el polígono que tiene alrededor nuestro punto
        List<Triangulo> list = dt.TriangulosDeAlrededor(puntoMasCercano, dt.enQueTrianguloEsta(puntoMasCercano));
        Punto[] vertices = new Punto[list.size()];
        int i = 0;
        for (Triangulo tri: list){
            vertices[i++] = tri.getCircuncentro();
        } 

        //Lo hacemos para todos los pares de circuncentros excepto para el que cierra
        for(int j = 0; j < vertices.length-1 ;j++){
            Punto puntoDeCorte = obtenerPuntoMasCercanoASegmento(vertices[j].coordenada(0), vertices[j].coordenada(1),vertices[j+1].coordenada(0), vertices[j+1].coordenada(1),
                                                                        this.verticesDijkstra.get(InicialOFinal).coordenada(0),this.verticesDijkstra.get(InicialOFinal).coordenada(1));
        
            if (!verticesDijkstra.contains(puntoDeCorte) &&  
                        puntoMasCercano.resta(this.obtenerPuntoMasCercanoASegmento(this.verticesDijkstra.get(InicialOFinal).coordenada(0), this.verticesDijkstra.get(InicialOFinal).coordenada(1),
                                puntoDeCorte.coordenada(0),puntoDeCorte.coordenada(1),
                                puntoMasCercano.coordenada(0),puntoMasCercano.coordenada(1)
                        )).norma() > 15 ){
                this.verticesDijkstra.add(puntoDeCorte);
                aniadeAristasPuntoCorte(vertices[j],vertices[j+1],InicialOFinal);
            }
        }
        
        //Ahora el que cierra
        Punto puntoDeCorte = obtenerPuntoMasCercanoASegmento(vertices[0].coordenada(0), vertices[0].coordenada(1),
                                                        vertices[vertices.length-1].coordenada(0), vertices[vertices.length-1].coordenada(1),
                                                        this.verticesDijkstra.get(InicialOFinal).coordenada(0),this.verticesDijkstra.get(InicialOFinal).coordenada(1));
        if (!verticesDijkstra.contains(puntoDeCorte)&&  
                        puntoMasCercano.resta(this.obtenerPuntoMasCercanoASegmento(this.verticesDijkstra.get(InicialOFinal).coordenada(0), this.verticesDijkstra.get(InicialOFinal).coordenada(1),
                                puntoDeCorte.coordenada(0),puntoDeCorte.coordenada(1),
                                puntoMasCercano.coordenada(0),puntoMasCercano.coordenada(1))).norma() > 15 ){
            this.verticesDijkstra.add(puntoDeCorte);
            aniadeAristasPuntoCorte(vertices[0],vertices[vertices.length-1],InicialOFinal);
        }        
    }
        
    //Añade una arista dentro de una cara dados dos puntos
    private void aniadeAristaInternaCara(Punto[] arista){
        //Sacamos los índices de ambos extremos de la cara
        int[] indicesVerticesArista = new int[2];
        for(int h = 0; h < this.verticesDijkstra.size()-1; h++){
            if( this.verticesDijkstra.get(h).equals(arista[0])){
                indicesVerticesArista[0] = h;
            }
            if(this.verticesDijkstra.get(h).equals(arista[1])){
                indicesVerticesArista[1] = h;
            }
        }

        //Vemos los puntos que hay en ese segmento
        int[] indicesPuntosDentroSegmento = new int[2];
        int verticesEncontrados = 0;
        int m = 0;

        //Recorremos los vértices de Dijkstra buscando un punto que tenga conexiones a ambos extremos del segmento
        for(int indice = 0; indice < this.verticesDijkstra.size(); indice++){
            verticesEncontrados = 0;
            for(int h = 0; h < this.aristasParaLuego.size(); h++){
                if (this.aristasParaLuego.get(h)[0] == indice && this.aristasParaLuego.get(h)[1] == indicesVerticesArista[0] 
                  ||this.aristasParaLuego.get(h)[1] == indice && this.aristasParaLuego.get(h)[0] == indicesVerticesArista[0]){
                        verticesEncontrados++;
                }
                if (this.aristasParaLuego.get(h)[0] == indice && this.aristasParaLuego.get(h)[1] == indicesVerticesArista[1] 
                  ||this.aristasParaLuego.get(h)[1] == indice && this.aristasParaLuego.get(h)[0] == indicesVerticesArista[1]){
                        verticesEncontrados++;
                }
            }
            if(verticesEncontrados == 2){
                indicesPuntosDentroSegmento[m] = indice;
                m++;
            }
        }
        //Si encontramos 2 puntos dentro del segmento
        if (m == 2){
            //Añadimos dicha arista
            int[] aristaTem = new int[3];
            aristaTem[0] = indicesPuntosDentroSegmento[0];
            aristaTem[1] = indicesPuntosDentroSegmento[1];
            aristaTem[2] = (int) this.verticesDijkstra.get(indicesPuntosDentroSegmento[0]).resta(this.verticesDijkstra.get(indicesPuntosDentroSegmento[1])).norma();
            aristasParaLuego.add(aristaTem);        
        }
    }    
    
    //En caso de compartir una cara de sus polígonos el punto de inicio y el final, añadimos una arista que conecte sus puntos de corte con dicha cara
    private void aniadeAristaVecinos(){
        //Sacamos los vértices de alrededor del punto inicial
        Punto puntoMasCercanoInicial = puntoMasCercano(this.verticesDijkstra.get(0));
        List<Triangulo> listInicial = dt.TriangulosDeAlrededor(puntoMasCercanoInicial, dt.enQueTrianguloEsta(puntoMasCercanoInicial));
        Punto[] verticesInicial = new Punto[listInicial.size()];
        int i = 0;
        for (Triangulo tri: listInicial){
            verticesInicial[i++] = tri.getCircuncentro();
        } 
        
        //Sacamos los vértices de alrededor del punto final
        Punto puntoMasCercanoFinal = puntoMasCercano(this.verticesDijkstra.get(1));
        List<Triangulo> listFinal = dt.TriangulosDeAlrededor(puntoMasCercanoFinal, dt.enQueTrianguloEsta(puntoMasCercanoFinal));
        Punto[] verticesFinal = new Punto[listFinal.size()];
        i = 0;
        for (Triangulo tri: listFinal){
            verticesFinal[i++] = tri.getCircuncentro();
        } 
        
        //Vemos, comparando esos puntos dos a dos, si coinciden, es decir, si comparten una cara
        Punto[] arista = new Punto[2];
        //Caso general
        for (int j = 0; j < verticesInicial.length-1; j++){
            for (int k = 0; k < verticesFinal.length-1; k++){
                if( (verticesInicial[j] == verticesFinal[k] && verticesInicial[j+1] == verticesFinal[k+1]) ||
                    (verticesInicial[j] == verticesFinal[k+1] && verticesInicial[j+1] == verticesFinal[k])){
                    arista[0] = verticesInicial[j];
                    arista[1] = verticesInicial[j+1];
                    
                }
            }
            //Caso k = 0 y k = verticesFinal.length-1
            if((verticesInicial[j] == verticesFinal[0] && verticesInicial[j+1] == verticesFinal[verticesFinal.length-1]) ||
                    (verticesInicial[j] == verticesFinal[verticesFinal.length-1] && verticesInicial[j+1] == verticesFinal[0])){
                    arista[0]= verticesInicial[j];
                    arista[1]= verticesInicial[j+1];
            }
        }
        //Caso j = 0 y j = verticesInicial.length-1
        for (int k = 0; k < verticesFinal.length-1; k++){
            if( (verticesInicial[0] == verticesFinal[k] && verticesInicial[verticesInicial.length-1] == verticesFinal[k+1]) ||
                (verticesInicial[0] == verticesFinal[k+1] && verticesInicial[verticesInicial.length-1] == verticesFinal[k])){
                arista[0] = verticesInicial[0];
                arista[1] = verticesInicial[verticesInicial.length-1];
            }
        }
        //Caso j = 0 , j = verticesInicial.length-1 , k = 0 y k = verticesFinal.length-1
        if((verticesInicial[0] == verticesFinal[0] && verticesInicial[verticesInicial.length-1] == verticesFinal[verticesFinal.length-1]) ||
                (verticesInicial[0] == verticesFinal[verticesFinal.length-1] && verticesInicial[verticesInicial.length-1] == verticesFinal[0])){
                arista[0] = verticesInicial[0];
                arista[1] = verticesInicial[verticesInicial.length-1];
        }
        
        //En caso de haber encontrado dicha cara
        if ((arista[0] != null) && (arista[1] != null)){
            aniadeAristaInternaCara(arista);
        }
    }

    private void aniadeAristasMismaZona(){
        //Sacamos los vértices de alrededor del punto inicial
        Punto puntoMasCercano = puntoMasCercano(this.verticesDijkstra.get(0));
        List<Triangulo> list = dt.TriangulosDeAlrededor(puntoMasCercano, dt.enQueTrianguloEsta(puntoMasCercano));
        Punto[] vertices = new Punto[list.size()];
        int i = 0;
        for (Triangulo tri: list){
            vertices[i++] = tri.getCircuncentro();
        }
        //Añadimos las aristas internas a cada cara
        Punto[] arista = new Punto[2];
        for (int j = 0; j < vertices.length-1; j++){
            arista[0] = vertices[j];
            arista[1] = vertices[j+1];
            aniadeAristaInternaCara(arista);
        }
        arista[0] = vertices[0];
        arista[1] = vertices[vertices.length-1];
        aniadeAristaInternaCara(arista);
    }
    
    ///////////////// Métodos de trabajo con ficheros //////////////////////////     
        
    public void aniadeDesdeArchivo(String filename) throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader(filename));
        FileReader fr = new FileReader(filename);     
        Punto puntoAux;
        String sCurrentLine = br.readLine();
        String[] splitLine;
        
        while ((sCurrentLine = br.readLine()) != null) {
            splitLine = sCurrentLine.split(" ");
            puntoAux = new Punto(Double.parseDouble(splitLine[0]),Double.parseDouble(splitLine[1]));
            dt.aniadePuntoDelaunay(puntoAux);
            repaint();
        }
    }

    public void guardarEnArchivo(String filename) throws IOException {
        Conjunto<Punto> conjuntoPuntos = new Conjunto<>();
        for (Triangulo triangulo : dt){
            for(Punto punto : triangulo){
                conjuntoPuntos.add(punto);
            }
        }
        for (Punto punto :this.trianguloInicial){
            conjuntoPuntos.remove(punto);
        }
        List<String> salida = new ArrayList<>();
        salida.add("2 "+conjuntoPuntos.size());
        for (Punto punto : conjuntoPuntos){
            salida.add(String.valueOf(punto.coordenada(0))+" "+String.valueOf(punto.coordenada(1)));
        }

        FileWriter fichero  = new FileWriter(filename);
        PrintWriter pw = new PrintWriter(fichero);
        for (int i = 0; i < salida.size(); i++){
            pw.println(salida.get(i));
        }
        fichero.close();
    }
    
    public void exportarEnArchivo(String filename) throws IOException {
        List<String> salida = new ArrayList<>();
        
        Conjunto<Punto> verticesVoronoi = new Conjunto<>();
        for (Triangulo triangulo : dt){
            verticesVoronoi.add(triangulo.getCircuncentro());
        }
                
        int[][] matrizAristas = new int [verticesVoronoi.size()][verticesVoronoi.size()];
        
        salida.add("Teniendo en cuenta que el triángulo inicial en el que dibujamos es:");
        salida.add(this.trianguloInicial.toString());
        
        salida.add("");
        
        salida.add("La lista de vértices de Voronoi con su índice:");
        int i = 0;
        for (Punto punto: verticesVoronoi){
            salida.add(String.valueOf(i)+" - ("+(int)punto.coordenada(0)+","+(int)punto.coordenada(1)+")");
            i++;
        }
        
        salida.add("");
        
        salida.add("Y la matriz de aristas es:");
        String linea="";
        
        HashSet<Punto> yaRecorridos = new HashSet<>(trianguloInicial);
        int primerIndice = 0;
        int segundoIndice = 0;
        for (Triangulo triangulo : dt){
            for (Punto vertice: triangulo) {
                if (yaRecorridos.contains(vertice)) 
                    continue;
                
                yaRecorridos.add(vertice);
                
                List<Triangulo> list = dt.TriangulosDeAlrededor(vertice, triangulo);
                Punto[] vertices = new Punto[list.size()];
                i = 0;
                for (Triangulo tri: list){
                    vertices[i++] = tri.getCircuncentro();
                }
                for (int k = 0; k < vertices.length-1; k++){
                    for (int j = 0; j < verticesVoronoi.size(); j++){
                        if (vertices[k].equals(verticesVoronoi.get(j)))
                            primerIndice = j;
                        if (vertices[k+1].equals(verticesVoronoi.get(j)))
                            segundoIndice = j;
                    }
                    matrizAristas[primerIndice][segundoIndice] = 1;
                    matrizAristas[segundoIndice][primerIndice] = 1;
                }
                for (int j = 0; j < verticesVoronoi.size(); j++){
                        if (vertices[0].equals(verticesVoronoi.get(j)))
                            primerIndice = j;
                        if (vertices[vertices.length-1].equals(verticesVoronoi.get(j)))
                            segundoIndice = j;
                }
                matrizAristas[primerIndice][segundoIndice] = 1;
                matrizAristas[segundoIndice][primerIndice] = 1;
            }
        }        
        linea = " * ";
        for(int c = 0; c < matrizAristas.length; c++){
            if (c < 10)
                linea = linea+" ";
            linea = linea+" "+c;
        }
        salida.add(linea);
        
        for(int f = 0; f < matrizAristas.length; f++){
            linea = " "+f;
            if (f < 10)
                linea = linea+" ";
                
            for(int c = 0; c < matrizAristas.length; c++){
                if (f != c)
                    linea = linea+"  "+matrizAristas[f][c];
                else
                    linea = linea+"  1";
            }
            salida.add(linea);
        }

        FileWriter fichero  = new FileWriter(filename);
        PrintWriter pw = new PrintWriter(fichero);
        for (int  j = 0; j < salida.size(); j++){
            pw.println(salida.get(j));
        }
        fichero.close();
    }
    
    ////////////////// Métodos básicos de dibujado /////////////////////////////    
    
    public void dibuja (Punto punto) {
        int r = radioPunto;
        int x = (int) punto.coordenada(0);
        int y = (int) punto.coordenada(1);
        this.g.fillOval(x-r, y-r, r+r, r+r);
    }

    public void dibuja (Punto centro, double radio, Color colorRelleno) {
        int x = (int) centro.coordenada(0);
        int y = (int) centro.coordenada(1);
        int r = (int) radio;
        if (colorRelleno != null) {
            Color aux = this.g.getColor();
            this.g.setColor(colorRelleno);
            this.g.fillOval(x-r, y-r, r+r, r+r);
            this.g.setColor(aux);
        }
        this.g.drawOval(x-r, y-r, r+r, r+r);
    }

    public void dibuja (Punto[] poligono, Color colorRelleno) {
        int[] x = new int[poligono.length];
        int[] y = new int[poligono.length];
        for (int i = 0; i < poligono.length; i++) {
            x[i] = (int) poligono[i].coordenada(0);
            y[i] = (int) poligono[i].coordenada(1);
        }
        if (colorRelleno != null) {
            Color temp = this.g.getColor();
            this.g.setColor(colorRelleno);
            this.g.fillPolygon(x, y, poligono.length);
            this.g.setColor(temp);
        }
        this.g.drawPolygon(x, y, poligono.length);
    }
        
    /////////////////// Métodos Complejos de dibujado //////////////////////////
    
    private Color getColor (Object elem) {
        if (tablaColor.containsKey(elem)) return tablaColor.get(elem);
        Color color = new Color(Color.HSBtoRGB(random.nextFloat(), 1.0f, 1.0f));
        tablaColor.put(elem, color);
        return color;
    }

    public void dibujaDelaunay (boolean conRelleno) {
        for (Triangulo triangulo : dt) {
            Punto[] vertices = triangulo.toArray(new Punto[0]);
            dibuja(vertices, conRelleno? getColor(triangulo) : null);
        }
    }
    
    public void dibujaCircunferencias () {
        for (Triangulo triangulo: dt) {
            if (triangulo.contieneAlguno(trianguloInicial))
                continue;
            
            Punto c = triangulo.getCircuncentro();
            double radius = c.resta(triangulo.get(0)).norma();
            dibuja(c, radius, null);
        }
    }    

    public void dibujaVoronoi (boolean conRelleno, boolean conPuntos) {
        HashSet<Punto> yaDibujados = new HashSet<>(trianguloInicial);
        int primerIndice = 0;
        int segundoIndice = 0;
        for (Triangulo triangulo : dt){
            for (Punto vertice: triangulo) {
                if (yaDibujados.contains(vertice)) 
                    continue;
                
                yaDibujados.add(vertice);
                
                List<Triangulo> list = dt.TriangulosDeAlrededor(vertice, triangulo);
                Punto[] vertices = new Punto[list.size()];
                int i = 0;
                for (Triangulo tri: list){
                    vertices[i++] = tri.getCircuncentro();
                }
                dibuja(vertices, conRelleno? getColor(vertice) : null);
                if (modo == 3){
                    for (int k = 0; k < vertices.length-1; k++){
                        
                        for (int j = 0; j < verticesDijkstra.size(); j++){
                            if (vertices[k].equals(verticesDijkstra.get(j)))
                                primerIndice = j;
                            if (vertices[k+1].equals(verticesDijkstra.get(j)))
                                segundoIndice = j;
                        }
                        double distancia = vertices[k].resta(vertices[k+1]).norma();
                        dijkstra.aniadeArista(primerIndice,segundoIndice,(int)distancia);
                    }
                    for (int j = 0; j < verticesDijkstra.size(); j++){
                            if (vertices[0].equals(verticesDijkstra.get(j)))
                                primerIndice = j;
                            if (vertices[vertices.length-1].equals(verticesDijkstra.get(j)))
                                segundoIndice = j;
                    }
                    double distancia = vertices[0].resta(vertices[vertices.length-1]).norma();
                    dijkstra.aniadeArista(primerIndice,segundoIndice,(int)distancia);
                }

                if (conPuntos) dibuja(vertice);
            }
        }
        if(modo == 3){
            dijkstra.dijkstra(0);
            this.caminoDijkstra = dijkstra.caminoMasCorto(1);
        }
    } 
    
    @Override
    public void paintComponent (Graphics g) {
        super.paintComponent(g);
        this.g = (Graphics2D) g;
                
        this.g.scale(this.zoom, this.zoom);
        
        this.g.addRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ));
        Color temp = g.getColor();
        if (this.modo == 0) 
            this.g.setColor(delaunayColor);
        else if (dt.contains(trianguloInicial)) 
            this.g.setColor(this.getBackground());
        else 
            this.g.setColor(voronoiColor);
        
        this.g.fillRect(0, 0, this.getWidth(), this.getHeight());
        this.g.setColor(temp);

        dibujaVoronoi(true,true);
        
        this.g.setColor(Color.BLACK);
        if (pintaCircunferencias) 
            dibujaCircunferencias();
        this.g.setColor(Color.BLACK);
        if (pintaDelaunay){
            this.g.setStroke(new BasicStroke(2.0f));
            dibujaDelaunay(false);
        }
        if(modo == 2){
            this.g.setColor(Color.WHITE);
            dibuja(this.verticesDijkstra.get(0));
        }
        if (modo == 3){
            this.g.setColor(Color.WHITE);
            this.g.setStroke(new BasicStroke(4.0f));        
            for(int i = 0; i < caminoDijkstra.size(); i++){
                dibuja(this.verticesDijkstra.get(caminoDijkstra.get(i)));
            }
            for(int i = 0;i < caminoDijkstra.size()-1; i++){
                this.g.drawLine((int) this.verticesDijkstra.get(caminoDijkstra.get(i)).coordenada(0), (int) this.verticesDijkstra.get(caminoDijkstra.get(i)).coordenada(1), 
                        (int) this.verticesDijkstra.get(caminoDijkstra.get(i+1)).coordenada(0), (int) this.verticesDijkstra.get(caminoDijkstra.get(i+1)).coordenada(1));
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(255, 255, 255));
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        if (evt.getSource() != this) 
            return;
        
        Punto punto = new Punto(evt.getX()/this.zoom, evt.getY()/this.zoom);
        switch (modo) {
            case 0:
                dt.aniadePuntoDelaunay(punto);
                break;
            case 1:
                //El primero siempre es el inicial
                if(punto.resta(puntoMasCercano(punto)).norma() > 16){
                    int i = 0;
                    for (Triangulo triangulo : dt){
                        //En caso de estar suficientemente cerca se añade el vertice directamente
                        if(punto.resta(triangulo.getCircuncentro()).norma() < 7){
                            this.verticesDijkstra.add(triangulo.getCircuncentro());
                            i++;
                        } 
                    }
                    if(i == 0){
                        this.verticesDijkstra.add(punto);
                    }
                    this.setModo(2);
                }
                break;
            case 2:
                if(punto.resta(puntoMasCercano(punto)).norma() > 16 && punto.resta(this.verticesDijkstra.get(0)).norma() > 16){
                    //El segundo siempre es el final
                    int i = 0;
                    for (Triangulo triangulo : dt){
                        //En caso de estar suficientemente cerca se añade el vértice directamente
                        if(punto.resta(triangulo.getCircuncentro()).norma() < 7){
                            this.verticesDijkstra.add(triangulo.getCircuncentro());
                            i++;
                        } 
                    }
                    if(i == 0){
                        this.verticesDijkstra.add(punto);
                    }
                    
                    //Luego añadimos todos los circuncentros
                    for (Triangulo triangulo : dt){
                        this.verticesDijkstra.add(triangulo.getCircuncentro());
                    }

                    //Si están en la misma región se añade una arista que los conecte siempre que no pase por el centro
                    if(puntoMasCercano(this.verticesDijkstra.get(0)).equals(puntoMasCercano(this.verticesDijkstra.get(1)))
                                && puntoMasCercano(this.verticesDijkstra.get(0)).resta( this.obtenerPuntoMasCercanoASegmento(
                                        this.verticesDijkstra.get(0).coordenada(0), this.verticesDijkstra.get(0).coordenada(1),
                                        this.verticesDijkstra.get(1).coordenada(0), this.verticesDijkstra.get(1).coordenada(1),
                                        puntoMasCercano(this.verticesDijkstra.get(0)).coordenada(0),puntoMasCercano(this.verticesDijkstra.get(0)).coordenada(1)) 
                                ).norma() > 15   
                            ){
                        int[] aristaTem = new int[3];
                        aristaTem[0] = 0;
                        aristaTem[1] = 1;
                        aristaTem[2] = (int) this.verticesDijkstra.get(1).resta(this.verticesDijkstra.get(0)).norma();
                        aristasParaLuego.add(aristaTem);
                    }     
                    
                    //Añadimos las aristaas que van del inicio a sus lados
                    this.aniadeAristasDijkstra(0);
                    //Añadimos las aristaas que van del fin a sus lados
                    this.aniadeAristasDijkstra(1);

                    if(puntoMasCercano(this.verticesDijkstra.get(0)).equals(puntoMasCercano(this.verticesDijkstra.get(1)))){
                        this.aniadeAristasMismaZona();
                    }
                    else{
                        this.aniadeAristaVecinos();
                    }

                    int v = verticesDijkstra.size();
                    dijkstra = new Dijkstra(v);

                    for(int[] arista :aristasParaLuego){
                        dijkstra.aniadeArista(arista[0],arista[1],arista[2]);
                    }

                    this.setModo(3);
                }
                break;
            case 3:
                this.verticesDijkstra = new Conjunto<>();
                this.aristasParaLuego = new ArrayList<>();
                this.setModo(1);
                break;
            default:
                break;
        }
        this.repaint();
    }//GEN-LAST:event_formMousePressed

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        int x = (int) (evt.getX()/this.zoom);
        int y = (int) (evt.getY()/this.zoom);
        parent.cambiaCoordenadas("X:"+x+", Y:"+y);
    }//GEN-LAST:event_formMouseMoved
    
    ///////// GETTERS AND SETTERS ////////////////////////////////////////////
    
    public int getModo() {
        return modo;
    }

    public void setModo(int modo) {
        switch (modo) {
            case 0:
                caminoDijkstra = new ArrayList<>();
                verticesDijkstra = new Conjunto<>();
                break;
            default:
                break;
        }
        this.modo = modo;
    }

    public boolean isPintaDelaunay() {
        return pintaDelaunay;
    }

    public void setPintaDelaunay(boolean pintaDelaunay) {
        this.pintaDelaunay = pintaDelaunay;
    }

    public boolean isPintaCircunferencias() {
        return pintaCircunferencias;
    }

    public void setPintaCircunferencias(boolean pintaCircunferencias) {
        this.pintaCircunferencias = pintaCircunferencias;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}