/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pathsgenerator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class Main {

    
    
    XMLReader _myXMLReader = new XMLReader(Tools._svgPath);
    HashMap<Integer, Vertex> _hotspots = new HashMap<Integer, Vertex>();
    private List _matrix = null; //The matrix with all available path to walk on it. This matrix is a list with the adjacent points


    void loadHotSpots(){
        try {
            _hotspots = _myXMLReader.getHotspotsFromSvg(Tools._svgPath);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    Main() {
        int idHotSpotDES = 0;
        int idHotSpotOR = 0;
        loadHotSpots();
        Planner myPlanner = new Planner();
        Iterator iHS1 = _hotspots.entrySet().iterator();
        while (iHS1.hasNext()) {
            Map.Entry entry = (Entry) iHS1.next();
            Vertex vHS1 = (Vertex) entry.getValue();
            //idHotSpotOR=Integer.valueOf(vHS1.get_id());
            int x1 = vHS1.get_x();
            int y1 = vHS1.get_y();
            int vHS1f = vHS1.get_floor();
            Iterator iHS2 = _hotspots.entrySet().iterator();
            while (iHS2.hasNext()) {
                Map.Entry entry2 = (Entry) iHS2.next();
                Vertex vHS2 = (Vertex) entry2.getValue();
                //idHotSpotDES=Integer.valueOf(vHS2.get_id());
                int x2 = vHS2.get_x();
                int y2 = vHS2.get_y();
                int vHS2f = vHS2.get_floor();
                if (x1 != x2 && y1 != y2 )
                    myPlanner.planner(idHotSpotOR,x1,y1,vHS1f,idHotSpotDES,x2,y2,vHS2f);
                 idHotSpotDES++;
            }
            idHotSpotDES=0;
            idHotSpotOR++;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Main main = new Main();
    }

}
