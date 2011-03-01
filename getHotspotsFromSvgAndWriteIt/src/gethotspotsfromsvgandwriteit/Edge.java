package gethotspotsfromsvgandwriteit;

class Edge {
    private String _id; //The Edge ID
    private int _weight; //The weight of this edge
    private String _idV1; //The id of the first vertex of the edge
    private String _idV2; //The id of the second vertex of the edge
    private int _x1; //The first vertex X position
    private int _y1; //...
    private int _x2;
    private int _y2;

    Edge()
    {
    }
    
    Edge(String id, String idV1, String idV2)
    {
        this._id = id;
        this._idV1 = idV1;
        this._idV2 = idV2;
        this._weight = 0;
        //FIXME: Los vertices antes eran todos strings, no s porque los he cambiado a int, pero algun motivo tiene
        if(idV1.charAt(0) == 'E' && idV2.charAt(0) == 'E')
            this._weight = 50;
        else
            this._weight = 5;
        this._x1 = this._x2 = this._y1 = this._y2 = 0; //No coordinates, this is a floor's change
    }
    

    
    Edge(String id, String idV1, int x1, int y1, String idV2, int x2, int y2)
    {
        this._id = id;
        this._idV1 = idV1;
        this._idV2 = idV2;
        this._x1 = x1;
        this._x2 = x2;
        this._y1 = y1;
        this._y2 = y2;
        long a1 = x1 - x2;
        long a2 = a1 * a1;
        long b1 = y1 - y2;
        long b2 = b1 * b1;
        long sqrtParam = a2 + b2;
        this._weight = (int) Math.sqrt(sqrtParam);
    }

    public String get_id() {
        return _id;
    }

    public String get_idV1() {
        return _idV1;
    }

    public String get_idV2() {
        return _idV2;
    }

    public int get_weight() {
        return _weight;
    }

    public int get_x1() {
        return _x1;
    }

    public int get_x2() {
        return _x2;
    }

    public int get_y1() {
        return _y1;
    }

    public int get_y2() {
        return _y2;
    }
    
    //I've removed all the setter because at the moment I don't need it.
    
    @Override
    public String toString() {
        return "Edge ID: "+ _id +
               " |  Coordinates: " + _idV1 +":"+ _x1  +","+ _y1+ "/" + _idV2 + ":" + _x2 + "," + _y2 +
               " | Weight: "+ _weight;
    }
  

}
