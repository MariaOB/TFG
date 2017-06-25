/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class Punto {
    
    private double[] coordenadas;          // Las coordenadas del punto

    
    public Punto (double... coords) {
        // Almacenamos las coordenadas para que no se puedan modificar
        coordenadas = new double[coords.length];
        System.arraycopy(coords, 0, coordenadas, 0, coords.length);
    }
    
    @Override
    public String toString () {
        if (coordenadas.length == 0) 
            return "()";
        
        String salida = "(" + coordenadas[0];
        for (int i = 1; i < coordenadas.length; i++)
            salida = salida + "," + coordenadas[i];
        salida = salida + ")";
        
        return salida;
    }
    
    @Override
    public boolean equals (Object ElOtro) {
        if (!(ElOtro instanceof Punto)) 
            return false;
        
        Punto p = (Punto) ElOtro;
        if (this.coordenadas.length != p.coordenadas.length) 
            return false;
        
        for (int i = 0; i < this.coordenadas.length; i++)
            if (this.coordenadas[i] != p.coordenadas[i]) 
                return false;
        return true;
    }
    
    
    
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////// PUNTOS COMO VECTORES ////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public double coordenada (int i) {
        return this.coordenadas[i];
    }

    public int dimension () {
        return coordenadas.length;
    }

    public int mismaDimension (Punto p) {
        int tam = this.coordenadas.length;
        if (tam != p.coordenadas.length)
            throw new IllegalArgumentException("Las dimensiones no son iguales");
        return tam;
    }
    
    public Punto extiende (double... coords) {
        double[] coordenadasSalida = new double[coordenadas.length + coords.length];
        System.arraycopy(coordenadas, 0, coordenadasSalida, 0, coordenadas.length);
        System.arraycopy(coords, 0, coordenadasSalida, coordenadas.length, coords.length);
        return new Punto(coordenadasSalida);
    }
    
    public double productoEscalar (Punto p) {
        int tam = mismaDimension(p);
        double sum = 0;
        for (int i = 0; i < tam; i++)
            sum += this.coordenadas[i] * p.coordenadas[i];
        return sum;
    }
    
    public double norma () {
        return Math.sqrt(this.productoEscalar(this));
    }

    public Punto resta (Punto p) {
        int tam = mismaDimension(p);
        double[] coords = new double[tam];
        for (int i = 0; i < tam; i++)
            coords[i] = this.coordenadas[i] - p.coordenadas[i];
        return new Punto(coords);
    }
    
    public Punto suma (Punto p) {
        int tam = mismaDimension(p);
        double[] coords = new double[tam];
        for (int i = 0; i < tam; i++)
            coords[i] = this.coordenadas[i] + p.coordenadas[i];
        return new Punto(coords);
    }
    
    // En radianes
    public double angulo (Punto p) {
        return Math.acos(this.productoEscalar(p) / (this.norma()* p.norma()));
    }
    
    public Punto bisectriz (Punto p) {
        mismaDimension(p);
        Punto diff = this.resta(p);
        Punto sum = this.suma(p);
        double producto = diff.productoEscalar(sum);
        return diff.extiende(-producto / 2);
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    ///////////////// VECTORES DE PUNTOS COMO MATRICES /////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    public static String toString (Punto[] matriz) {
        StringBuilder buffer = new StringBuilder("{");
        for (Punto fila: matriz) buffer.append(" " + fila);
        buffer.append(" }");
        return buffer.toString();
    }
    
    public static double determinante (Punto[] matriz) {
        if (matriz.length != matriz[0].dimension())
            throw new IllegalArgumentException("La matriz no es cuadrada");
        
        boolean[] columnas = new boolean[matriz.length];
        for (int i = 0; i < matriz.length; i++) 
            columnas[i] = true;
        try {
            return determinante(matriz, 0, columnas);
        }catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("No todas las filas miden lo mismo");
        }
    }
    
    private static double determinante(Punto[] matriz, int fila, boolean[] columnas){
        if (fila == matriz.length) 
            return 1;
        
        double suma = 0;
        int signo = 1;
        for (int columna = 0; columna < columnas.length; columna++) {
            if (!columnas[columna]) continue;
            columnas[columna] = false;
            suma += signo * matriz[fila].coordenadas[columna] *
                   determinante(matriz, fila+1, columnas);
            columnas[columna] = true;
            signo = -signo;
        }
        return suma;
    }
    
    public static Punto productoMatricial (Punto[] matriz) {
        int tam = matriz.length + 1;
        if (tam != matriz[0].dimension())
            throw new IllegalArgumentException("No tienen la misma dimension");
        
        boolean[] columnas = new boolean[tam];
        for (int i = 0; i < tam; i++) 
            columnas[i] = true;
        
        double[] salida = new double[tam];
        
        int signo = 1;
        try {
            for (int i = 0; i < tam; i++) {
                columnas[i] = false;
                salida[i] = signo * determinante(matriz, 0, columnas);
                columnas[i] = true;
                signo = -signo;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Las filas no miden lo mismo que las columnas");
        }
        return new Punto(salida);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    //////// VECTORES DE PUNTOS COMO SIMPLICES (ENVOLVENTE CONVEXA) ////////////
    ////////////////////////////////////////////////////////////////////////////
   
    public static double volumen (Punto[] simplice) {
        Punto[] matriz = new Punto[simplice.length];
        for (int i = 0; i < matriz.length; i++)
            matriz[i] = simplice[i].extiende(1);
        
        int factor = 1;
        for (int i = 1; i < matriz.length; i++)
            factor = factor*i;
        return determinante(matriz) / factor;
    }
    
    /**
     * Relacion entre este Punto y un simplice.
     * El resultada es un array de signos, uno para cada vertice, indicando: 
     *
     * -1 si el punto esta en el mismo lado que el vertice respecto a dicha cara,
     * 0 significa que el punto esta en la cara,
     * +1 si el punto esta en el lado contrario. 
     */
    public int[] relacion (Punto[] simplice) {
        
        int dim = simplice.length - 1;
        if (this.dimension() != dim)
            throw new IllegalArgumentException("Dimensiones distintas");

        /* Creamos la matriz */
        Punto[] matriz = new Punto[dim+1];
        
        /* Primera fila */
        double[] coordenadas = new double[dim+2];
        for (int j = 0; j < coordenadas.length; j++) 
            coordenadas[j] = 1;
        
        matriz[0] = new Punto(coordenadas);
        
        /* Las demas filas */
        for (int i = 0; i < dim; i++) {
            coordenadas[0] = this.coordenadas[i];
            for (int j = 0; j < simplice.length; j++)
                coordenadas[j+1] = simplice[j].coordenadas[i];
            
            matriz[i+1] = new Punto(coordenadas);
        }

        /* Calculamos el volumen y vemos que pasa*/
        Punto vector = productoMatricial(matriz);
        double volumen = vector.coordenadas[0];
        int[] salida = new int[dim+1];
        for (int i = 0; i < salida.length; i++) {
            double valor = vector.coordenadas[i+1];
            if (Math.abs(valor) <= 1.0e-6 * Math.abs(volumen)) salida[i] = 0;
            else if (valor < 0) salida[i] = -1;
            else salida[i] = 1;
        }
        if (volumen < 0) {
            for (int i = 0; i < salida.length; i++)
                salida[i] = -salida[i];
        }
        if (volumen == 0) {
            for (int i = 0; i < salida.length; i++)
                salida[i] = Math.abs(salida[i]);
        }
        return salida;
    }
    
    public Punto estaFuera (Punto[] simplice) {
        int[] salida = this.relacion(simplice);
        for (int i = 0; i < salida.length; i++) {
            if (salida[i] > 0) 
                return simplice[i];
        }
        return null;
    }
    
    public Punto estaEn (Punto[] simplice) {
        int[] salida = this.relacion(simplice);
        Punto proximidad = null;
        for (int i = 0; i < salida.length; i++) {
            if (salida[i] == 0) 
                proximidad = simplice[i];
            else if (salida[i] > 0) 
                return null;
        }
        return proximidad;
    }
    
    public boolean estaDentro (Punto[] simplice) {
        int[] salida = this.relacion(simplice);
        for (int r: salida) 
            if (r >= 0) 
                return false;
        return true;
    }

    public int relacionConCircunferenciaCircunscrita (Punto[] simplice) {
        Punto[] matriz = new Punto[simplice.length + 1];
        for (int i = 0; i < simplice.length; i++)
            matriz[i] = simplice[i].extiende(1, simplice[i].productoEscalar(simplice[i]));
        matriz[simplice.length] = this.extiende(1, this.productoEscalar(this));
        double d = determinante(matriz);
        int salida = (d < 0)? -1 : ((d > 0)? +1 : 0);
        if (volumen(simplice) < 0) 
            salida = - salida;
        return salida;
    }
    
    public static Punto circuncentro (Punto[] simplice) {
        int dim = simplice[0].dimension();
        if (simplice.length - 1 != dim)
            throw new IllegalArgumentException("Dimensiones distintas");
        
        Punto[] matriz = new Punto[dim];
        for (int i = 0; i < dim; i++)
            matriz[i] = simplice[i].bisectriz(simplice[i+1]);
        
        // El centro serían las coordenadas homogeneas
        Punto centro = productoMatricial(matriz);     
        double ultimo = centro.coordenadas[dim];
        double[] salida = new double[dim];
        for (int i = 0; i < dim; i++) 
            salida[i] = centro.coordenadas[i] / ultimo;
        return new Punto(salida);
    }
    
     public static void main (String[] args) {
        Punto p = new Punto(1, 2, 3);
        System.out.println("Punto created: " + p);
        Punto[] matrix1 = {new Punto(1,2), new Punto(3,4)};
        Punto[] matrix2 = {new Punto(7,0,5), new Punto(2,4,6), new Punto(3,8,1)};
        System.out.print("Results should be -2 and -288: ");
        System.out.println(determinante(matrix1) + " " + determinante(matrix2));
        Punto p1 = new Punto(1,1); Punto p2 = new Punto(-1,1);
        System.out.println("Angle between " + p1 + " and " +
                p2 + ": " + p1.angulo(p2));
        System.out.println(p1 + " subtract " + p2 + ": " + p1.resta(p2));
        Punto v0 = new Punto(0,0), v1 = new Punto(1,1), v2 = new Punto(2,2);
        Punto[] vs = {v0, new Punto(0,1), new Punto(1,0)};
        Punto vp = new Punto(.1, .1);
        System.out.println(vp + " isInside " + toString(vs) +
                ": " + vp.estaDentro(vs));
        System.out.println(v1 + " isInside " + toString(vs) +
                ": " + v1.estaDentro(vs));
        System.out.println(vp + " vsCircumcircle " + toString(vs) + ": " +
                           vp.relacionConCircunferenciaCircunscrita(vs));
        System.out.println(v1 + " vsCircumcircle " + toString(vs) + ": " +
                           v1.relacionConCircunferenciaCircunscrita(vs));
        System.out.println(v2 + " vsCircumcircle " + toString(vs) + ": " +
                           v2.relacionConCircunferenciaCircunscrita(vs));
        System.out.println("Circumcenter of " + toString(vs) + " is " +
                circuncentro(vs));
    }
    
}
