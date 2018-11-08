/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guglecar;

/**
 *
 * @author Ruben
 */
import java.util.Comparator;

public class ComparatorNode implements Comparator<AStarNode>
{
    public ComparatorNode(){}
    
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