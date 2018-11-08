/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2;

/**
 *
 * @author Rubén Mogica Garrido
 * @author Antonio Camarero Ortega
 * 
 * Clase comparadora de nodos.
 * 
 */
import java.util.Comparator;

public class ComparatorNode implements Comparator<AStarNode>
{
    public ComparatorNode(){}
    
    /**
     * @author Rubén Mogica Garrido
     * @author Antonio Camarero Ortega
     * 
     * Función que compara dos AStarNode.
     * 
     * @param a Representa el primer nodo que se quiere comparar
     * @param b Representa el segundo nodo que se quiere comparar
     * 
     * @return Integer que vale -1 si el primer nodo es menor,
     * 0 si son iguales y 1 si es menor.
     */
    @Override
    public int compare(AStarNode a, AStarNode b)
    {

        if (a.getFValue() < b.getFValue())
        {
            return -1;
        }
        if (a.getFValue() > b.getFValue())
        {
            return 1;
        }
        return 0;
    }
}