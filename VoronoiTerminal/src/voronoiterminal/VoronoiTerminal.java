package voronoiterminal;

import static com.sun.org.apache.bcel.internal.util.SecuritySupport.getResourceAsStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import voronoi.Punto;
import voronoi.Triangulacion2D;
import voronoi.Triangulo;

/**
 *
 * @author MariaOB
 */
public class VoronoiTerminal {
    
    static public String ExportResource(String nombre) throws Exception {
        InputStream camino = null;
        OutputStream salida = null;
        String archivoJar;
        try {
            camino = VoronoiTerminal.class.getResourceAsStream(nombre);
            if(camino == null) {
                throw new Exception("No se puede obtener \"" + nombre + "\" del archivo jar.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            archivoJar = new File(VoronoiTerminal.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/');
            salida = new FileOutputStream(nombre);
            while ((readBytes = camino.read(buffer)) > 0) {
                salida.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            camino.close();
            salida.close();
        }

        return archivoJar +"/"+ nombre;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        String filename = null;
        if (args.length == 1) {
            filename = args[0];
        }else{
            System.out.println("Mete un archivo: ");
            System.exit(0);
        }

        //Creamos una triangulación
        Triangulacion2D dt;
        Triangulo trianguloInicial = new Triangulo(
            new Punto(-10000, -10000),
            new Punto(10000, -10000),
            new Punto(0, 10000));
        dt = new Triangulacion2D(trianguloInicial);
        int dimension;
        int numeroVertices;
        Punto puntoAux;            

        BufferedReader br = null;
        FileReader fr = null;
        String sCurrentLine;
        String[] splitLine;

        fr = new FileReader(filename);
        br = new BufferedReader(fr);
        br = new BufferedReader(new FileReader(filename));
        List<String> salida = new ArrayList<String>();

        sCurrentLine = br.readLine();

        splitLine = sCurrentLine.split(" ");
        dimension = Integer.parseInt(splitLine[0]);
        numeroVertices = Integer.parseInt(splitLine[1]);            

        if (dimension == 2){
            ArrayList<Punto> listaPuntos=new ArrayList<>();

            while ((sCurrentLine = br.readLine()) != null) {
                splitLine = sCurrentLine.split(" ");
                puntoAux = new Punto(Integer.parseInt(splitLine[0]),Integer.parseInt(splitLine[1]));
                dt.aniadePuntoDelaunay(puntoAux);
                listaPuntos.add(puntoAux);
            }
            Punto bisectriz;
            for (Punto puntoCentral:listaPuntos){
                for (Punto punto : listaPuntos){
                    if (!punto.equals(puntoCentral)){
                        bisectriz = punto.bisectriz(puntoCentral);

                    }
                }
            }

            salida.add("Para la dimensión "+dimension+" y "+numeroVertices+" vértices, las coordenadas de los vértices de Voronoi son: ");
            for (Triangulo triangulo : dt){
                    puntoAux = triangulo.getCircuncentro();
                   // salida.add(puntoAux.toString());
                    salida.add("("+(int)puntoAux.coordenada(0)+","+(int)puntoAux.coordenada(1)+")");
            }   

            salida.add("El número de polígonos es "+ numeroVertices);

            FileWriter fichero = null;
            PrintWriter pw = null;
            try
            {
                fichero = new FileWriter("salida.txt");
                pw = new PrintWriter(fichero);

                for (int i = 0; i < salida.size(); i++)
                    pw.println(salida.get(i));

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
               try {
               // Nuevamente aprovechamos el finally para asegurarnos que se cierra el fichero.
               if (null != fichero)
                  fichero.close();
               } catch (Exception e2) {
                  e2.printStackTrace();
               }
            }
        }
        else{
            String rutaCompleta = ExportResource("voronoi.py");
            Process p = Runtime.getRuntime().exec("python "+ rutaCompleta + " " + filename);
            p.waitFor();
            File archivoPython = new File(rutaCompleta);
            archivoPython.delete();
        }
    }
}
