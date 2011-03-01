package gethotspotsfromsvgandwriteit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * @author xota
 */
public class Vertex {
    private String _id;
    private int _x;
    private int _y;
    private int _floor;
    //If an object of Vertex is immutable, we can't remove it because it's a hotspot
    //Perhaps this isn't necesary because we are using two Collections, one for the hotspost and another one for the normal vertex
    private boolean _immutable = false;
    //FIXME: String _type = null; //This could be lift, ladder or p2p (we use p2p when we broke a floor as 2 images)
    
    public Vertex (String id, int x, int y, int floor)
    {
        this._id = id;
        this._x = x;
        this._y = y;
        this._floor = floor;
    }

    public Vertex (String id, int x, int y, int floor, boolean immutable)
    {
        this._id = id;
        this._x = x;
        this._y = y;
        this._floor = floor;
        this._immutable = immutable;
    }
    
    public int get_floor() {
        return _floor;
    }

    public void set_floor(int floor) {
        this._floor = floor;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String id) {
        this._id = id;
    }

    public int get_x() {
        return _x;
    }

    public void set_x(int x) {
        this._x = x;
    }

    public int get_y() {
        return _y;
    }

    public void set_y(int y) {
        this._y = y;
    }
    
    public boolean get_immutable() {
        return _immutable;
    }
    
    public void set_immutable(boolean immutable) {
        this._immutable = immutable;
    }
    
    public static void showAll(HashMap<Integer, Vertex> theVertex) {
        Iterator it = theVertex.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry)it.next();
            Vertex auxVertice = (Vertex) e.getValue();
            System.out.println("ID: "+auxVertice.get_id()+", x: "+auxVertice.get_x()+", y: "+auxVertice.get_y()+", floor: "+auxVertice.get_floor());
        }
    }
}