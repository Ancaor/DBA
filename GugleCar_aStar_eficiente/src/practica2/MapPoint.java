/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2;

/**
 *
 * @author Rubén Mogica Garrido
 * 
 * Clase que representa un punto en el mapa.
 */
public class MapPoint {
    public int x;
    public int y;

    
    
    /**
     * @author Rubén Mogica Garrido
     * 
     * Constructor con parámetros
     * 
     * @param x representa el valor de la coordenada X en un punto del mapa.
     * @param y representa el valor de la coordenada Y en un punto del mapa.
     */
    MapPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    
    
    /**
     * @author Rubén Mogica Garrido
     * 
     * Función que transforma la información de un objeto de la clase en un String.
     * 
     * @return String que muestra la información del objeto.
     */
    @Override
    public String toString() {
        return "MapPoint{" + "x=" + x + ", y=" + y + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.x;
        hash = 17 * hash + this.y;
        return hash;
    }

    
    /**
     * @author Rubén Mogica Garrido
     * 
     * Función que compara si dos objetos de la clase son iguales.
     * 
     * @param obj representa al objeto con el que se quiere comparar.
     * @return Booleano a true si los objetos son iguales. False en caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MapPoint other = (MapPoint) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }
    
    
}
