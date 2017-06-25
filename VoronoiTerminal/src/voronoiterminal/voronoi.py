from scipy.spatial import Voronoi
import numpy as np
import sys

if(len(sys.argv) > 1):
    f = open(sys.argv[1], "r")
    linea = f.readline()
    linea_dividida = linea.split( )
    dimension = linea_dividida[0]
    numero_puntos = linea_dividida[1]
    linea = f.readline()
    linea_dividida = linea.split( )
    puntos = np.array([linea_dividida])
    while True:
        linea = f.readline()
        if not linea: break
        linea_dividida = linea.split( )
        punto = np.array([linea_dividida])
        puntos = np.vstack((puntos,punto))
    vor = Voronoi(puntos)
    "print(vor.vertices)"

    f = open("salida.txt", "w")
    vertices = vor.vertices
    ver1 = str(vertices).replace("[", "")
    ver2 = ver1.replace("]", "")
    f.write("Los vertices de Voronoi para dimension " +dimension+ " dados "  +numero_puntos+ " puntos son: \n\n")
    f.write(ver2+ "\n\n")
    f.write("Y las regiones formadas son: \n\n")
    f.write(str(vor.regions))
    f.write("\n\nComo podemos ver, una para cada punto.")

else:
    print ("Debes indicar el nombre del archivo")
