package gethotspotsfromsvgandwriteit;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParserException;




/**
 *
 * @author xota
 */
public class Main {
    private HashMap<Integer, Vertex> _hotspots = new HashMap<Integer, Vertex>();
    private int _xTransform = 0;
    private int _yTransform = 0;
    private ArrayList<Edge> _edges = new ArrayList<Edge>(); //All the edges in a floor are saved temporally in this var
    private List _matrix = null; //The matrix with all available path to walk on it. This matrix is a list with the adjacent points
    private List<Vertex> _vertex = null;
    
public Main (){
    _edges = new ArrayList<Edge>();
            //mini-planificador-------------

        matrixCreate("./map.svg",0);

        //mini-planificador-------------
}


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Main elMonstruo = new Main();

      /*  try {
            elMonstruo.getHotspotsFromSvg("./map.svg");
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }




    private void edgesCreate(String polyline) {
        Edge auxArista = null;
        String idAux;        
        String[] fuckFloats;        
        int coordenateX1 = 0, coordenateY1 = 0, coordenateX2 = 0, coordenateY2 = 0;
        int p = 1;
        int limite = 0;

        System.out.println(polyline);        
        StringTokenizer vertex=new StringTokenizer(polyline, " ");
        vertex.nextToken();//the first token is not a vertex.
        while(vertex.hasMoreTokens()){
            String vertexActual=vertex.nextToken();
            if(!vertexActual.equals("L")&&!vertexActual.equals("z")){
                StringTokenizer coordenates = new StringTokenizer(vertexActual , ",");
                StringTokenizer coordenatesINT = new StringTokenizer(coordenates.nextToken(),".");
                coordenateX1 = Integer.parseInt(coordenatesINT.nextToken()) + _xTransform;
                coordenatesINT = new StringTokenizer(coordenates.nextToken(),".");
                coordenateY1 = Integer.parseInt(coordenatesINT.nextToken()) + _yTransform;
            }
            if(vertex.hasMoreTokens())vertexActual=vertex.nextToken();
            if(vertex.hasMoreElements())vertexActual=vertex.nextToken();
            if(!vertexActual.equals("L")&&!vertexActual.equals("z")){
                StringTokenizer coordenates = new StringTokenizer(vertexActual , ",");
                StringTokenizer coordenatesINT = new StringTokenizer(coordenates.nextToken(),".");
                coordenateX2 = Integer.parseInt(coordenatesINT.nextToken()) + _xTransform;
                coordenatesINT = new StringTokenizer(coordenates.nextToken(),".");
                coordenateY2 = Integer.parseInt(coordenatesINT.nextToken()) + _yTransform;
            }
            _edges.add(new Edge("", "", coordenateX1, coordenateY1, "", coordenateX2, coordenateY2));
        }
    }




    /**
     * @param svgFile the path to the map SVG file
     * @throws java.io.IOException
     * 
     * Read the SVG getting the interesting points (hotspots) and saving it into a HashMap and in a file hotspots.txt (needed to j2me)
     */
    private void getHotspotsFromSvg(String svgFile) throws IOException {
        KXmlParser parser = new KXmlParser();
        InputStream is = this.getClass().getResourceAsStream(svgFile);
        BufferedWriter bw = new BufferedWriter(new FileWriter("hotspots.txt"));
        int ne;
        boolean processing = false;
        String theActualId = null;
        int theActualX = 0, theActualY = 0, theActualFloor = 0;
        //We use the var identifier instead the SVG identifier, because a int is smaller than String
        int identifier = 0;
        double auxDouble = 0.0; //I use this var to cast from String to Double and from Double to Int
        
        //try-catch to IO and KXML exceptions
        try {
            parser.setInput(is, "UTF-8");
            
            while ((ne = parser.next()) != KXmlParser.END_DOCUMENT) {
                switch (ne) {
                    case KXmlParser.START_TAG:
                        //Save the floor (the inkscape layer)
                        if (parser.getName().equals("g")) {
                            for (int i = 0; i < parser.getAttributeCount(); i++) {
                                if (parser.getAttributeName(i).equals("id")) {
                                    theActualFloor = Integer.parseInt(parser.getAttributeValue(i));
                                }
                            }
                        }
                        
                        //The name of the hotspots is saved as <text ...><tspan ...>The name here</tspan></text>
                        if (parser.getName().equals("tspan")) {
                            processing = true;
                        } else {
                            //I only need the id of the <text> element, but I need to compare one-to-one the attribute name
                            if (parser.getName().equals("text")) {
                                for (int i = 0; i < parser.getAttributeCount(); i++) {
                                    if (parser.getAttributeName(i).equals("id")) {
                                        //We aren't using the theActualId because is bigger than a int identifier. But perhaps we will need it on the future.
                                        theActualId = parser.getAttributeValue(i);
                                        identifier++;
                                    } else if (parser.getAttributeName(i).equals("x")) {
                                        //Only get the value before the dot without aproximation
                                        StringTokenizer tokens = new StringTokenizer(parser.getAttributeValue(i),".");
                                        theActualX = Integer.parseInt(tokens.nextToken());
                                    } else if (parser.getAttributeName(i).equals("y")) {
                                        //Only get the value before the dot without aproximation
                                        StringTokenizer tokens = new StringTokenizer(parser.getAttributeValue(i),".");
                                        theActualY = Integer.parseInt(tokens.nextToken());
                                    }
                                }
                            }
                        }
                        break;
                        
                    case KXmlParser.END_TAG:
                        if (parser.getName().equals("tspan")) {
                            processing = false;
                        }
                        break;
                        
                    case KXmlParser.TEXT:
                        if (processing) {
                            StringTokenizer tokens = new StringTokenizer(parser.getText(),":");
                            while(tokens.hasMoreElements()) {
                                //Write the token ID and Name in the file
                                bw.write(identifier + " " + tokens.nextToken() + "\n");
                                //Save the token ID, x, y and floor
                                Vertex hotspot = new Vertex(String.valueOf(identifier), theActualX, theActualY, theActualFloor);
                                _hotspots.put(identifier, hotspot);
                            }
                        }
                        break;
                }
            }
        } catch (XmlPullParserException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        //Close the files to save the information
        bw.close();
        is.close();
        
        //Debug: show the hotsposts points.
        Vertex.showAll(_hotspots);      
    }
    
    
    

    /**
     * Get the X coordenate transform from the svg file and save them in a class var _xTransform and _yTransform.
     * 
     * @param svgFile The file where we are reading the xml map
     * @param actualFloor The actual floor id
     */
    private void getTransforms(String svgFile, int actualFloor) {
        int ne;
        //With this, we are sure that you aren't apply a other floor transform. If actualFloor doesn't have transform, the vars are setting to 0 by default
        _xTransform = 0; _yTransform = 0;
        
        KXmlParser parser = new KXmlParser();
        InputStream is = this.getClass().getResourceAsStream(svgFile);
    
        try {
            parser.setInput(is, "UTF-8");
            while ((ne = parser.next()) != KXmlParser.END_DOCUMENT) {

                //We remove the switch because only need to know when the elemente start
                //The "g" xml element is the representative of a layer. We are using layers to differentiate between one and another floor
                if ((ne == KXmlParser.START_TAG) && parser.getName().equals("g")) {
                    for (int i = 0; i < parser.getAttributeCount(); i++) {

                        //If we are reading the "id" attribute and this attrib is equal to the floor that we like, we will save the coordenates
                        if (parser.getAttributeName(i).equals("id") && (Integer.parseInt(parser.getAttributeValue(i)) == actualFloor)) {

                            while ((ne = parser.next()) != KXmlParser.END_DOCUMENT) {

                                if ((ne != KXmlParser.END_TAG) && (parser.getName() != null && parser.getName().equals("image"))) {

                                    for (int j = parser.getAttributeCount() - 1; j >= 0; j--) {
                                        //Read a XXX.YYY value and take only the XXX before the dot
                                        if (parser.getAttributeName(j).equals("x")) {
                                            StringTokenizer tokens = new StringTokenizer(parser.getAttributeValue(i), ".");
                                            _xTransform = Integer.parseInt(tokens.nextToken());
                                        } else if (parser.getAttributeName(j).equals("y")) {
                                            StringTokenizer tokens = new StringTokenizer(parser.getAttributeValue(i), ".");
                                            _yTransform = Integer.parseInt(tokens.nextToken());
                                        }
                                    }
                                } else if (parser.getName() != null && parser.getName().equals("g")) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
}



    /**
     * FIXME: Creo que esta función puede juntarse con readLinksCoordenates(String svgFile)
     * Son prácticamente iguales! No veo diferencia alguna ;_(
     */
    private void linksCreate() {
        KXmlParser parser=new KXmlParser();
        InputStream is = this.getClass().getResourceAsStream("floorsUpsa.svg");
        
        int ne;
        String xmlId = null;
        String xmlX = null; String xmlY = null; String tokensAux = null; //For the X and Y "cast" with the StringTokenizer
        String xmlLabel = null; String xmlLabelPoints = null; //For the StringTokenizer to get all the hostpot's names
        
        try {
            parser.setInput(is, "UTF-8");
            while ((ne = parser.next()) != KXmlParser.END_DOCUMENT) {
                if (parser.getName().equals("rect")) {
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        if (parser.getAttributeName(i).equals("id")) {
                            xmlId = parser.getAttributeValue(i);

                        } else if (parser.getAttributeName(i).equals("x")) {
                            xmlX = parser.getAttributeValue(i);

                        } else if (parser.getAttributeName(i).equals("y")) {
                            xmlY = parser.getAttributeValue(i);

                        } else if (parser.getAttributeName(i).equals("inkscape:label")) {
                            //Take only the int without decimals: XXX.YYY -> XXX
                            StringTokenizer tokens = new StringTokenizer(xmlX, ".");
                            int integerXmlX = Integer.parseInt(tokens.nextToken());
                            tokens = new StringTokenizer(xmlY, ".");
                            int integerXmlY = Integer.parseInt(tokens.nextToken());

                            //Get all the hotspot's name. In the SVG they're saved as name1:name2:...:nameN
                            tokens = new StringTokenizer(parser.getAttributeValue(i), ":");
                            while (tokens.hasMoreElements()) {
                                Edge auxEdge = new Edge("NULL", xmlId, tokens.nextToken());
                                _edges.add(auxEdge);
                            }
                            xmlLabelPoints = null;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /**
     * Create the matrix to load all the possible walking points at actual floor.
     * If a change of floor is needed (actualFloor = -1) we must create the links matrix, that do something like this, but only with stairs and lift.
     * 
     * @param svgFile The ref to the file
     * @param actualFloor
     */
    private void matrixCreate(String svgFile, int actualFloor) {
        int ne;
        String polyline = null;
        
        KXmlParser parser = new KXmlParser();
        InputStream is = this.getClass().getResourceAsStream(svgFile);

        //Create the list to manage the vertex in this floor. I only use it on the else, but I need to create it out-of-all to use after the if-else if I don't do this, java fails.
        List <Vertex> vertex = new ArrayList<Vertex>();
        
        /*
         * This block allows us to send all the polylines to the function edgesCreate
         */
        try {
            parser.setInput(is, "UTF-8");
            while ((ne = parser.next()) != KXmlParser.END_DOCUMENT) {
                //We need to search all the polylines int the actual floor to create the matrix
                if ((ne == KXmlParser.START_TAG) && (parser.getName().equals("g"))) {

                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        if ((parser.getAttributeName(i).equals("id")) && (parser.getAttributeValue(i).equals(String.valueOf(actualFloor)))) {

                            while ((ne = parser.next()) != KXmlParser.END_DOCUMENT) {
                                //At this point we only need to read the paths to create the matrix
                                if ((ne != KXmlParser.END_TAG) && (parser.getName() != null) && parser.getName().equals("path")) {

                                    for (int j = parser.getAttributeCount() - 1; j >= 0; j--) {
                                        //The "d" is the attribute where the polyline with all the points is saved
                                        if (parser.getAttributeName(j).equals("d")) {
                                            polyline = parser.getAttributeValue(j);
                                            edgesCreate(polyline);
                                        }
                                    }
                                } else if (parser.getName() != null && parser.getName().equals(("g"))) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  
        
        if (actualFloor == -1) {
            linksCreate(); //crearEnlaces()
            readLinksCoordenates(svgFile); //leerCoordenadasEnlaces()
        } else {
            //I need two auxiliar vars: auxVertex and auxEdge to save information temporally
            Vertex auxVertex;
            Edge auxEdge;
            
            //This code replaces the for(;;) with "arista" tipe Vector() -> deprecated!
            Iterator <Edge> edgesIterator = _edges.iterator();
            while (edgesIterator.hasNext()) {

                auxEdge = (Edge) edgesIterator.next(); //It's the same: auxArista=(Arista) aristas.elementAt(i);                                                
                auxVertex = new Vertex(auxEdge.get_idV1(), auxEdge.get_x1(), auxEdge.get_y1(), actualFloor);
                vertex.add(auxVertex);
     
                //This iterator was created to find if a vertex've inserted yet or no. If the vertex doesn't exists on the vertex list, we must create it
                Iterator <Vertex> vertexIterator = vertex.iterator();
                boolean duplicateV1 = false, duplicateV2 = false;
                while (vertexIterator.hasNext()) {
                    auxVertex=(Vertex)vertexIterator.next();
                    //The X coordenate doesn't match and the Y coordenate doesn't match too
                    if (auxEdge.get_x1() == auxVertex.get_x() && auxEdge.get_y1() == auxVertex.get_y()) duplicateV1=true;
                    if (auxEdge.get_x2() == auxVertex.get_x() && auxEdge.get_y2() == auxVertex.get_y()) duplicateV2=true;
                }
                if(duplicateV1 == false) vertex.add(new Vertex("", auxEdge.get_x1(), auxEdge.get_y1(), actualFloor));
                if(duplicateV2 == false) vertex.add(new Vertex("", auxEdge.get_x2(), auxEdge.get_y2(), actualFloor));
            }
        }    
        
        //All vertex have adjacents points that could be used to go from one vertex to another vertex.
        List adjacents = null;
        //If a Vertex is duplicated on adjacents, we must tag it to don't save.
        boolean doubled = false;
        Vertex auxiliarVertex = null;
        
        Iterator <Vertex> vertexIterator = vertex.iterator();
        while (vertexIterator.hasNext()) {    
            auxiliarVertex = vertexIterator.next();
            
            //We need to traverse the list to find duplicated vertex. If a vertex is duplicated we tag it to don't save after the while.
            Iterator <Vertex> adjacentsIterator = adjacents.iterator();
            while (adjacentsIterator.hasNext()) {
                if (auxiliarVertex.get_id().equals(adjacentsIterator.next().get_id())) doubled = true;
            }
            
            //If the vertex isn't duplicated we must save it as an adjacente vertex.
            if (!doubled) adjacents.add(vertexIterator.next());
            
            Iterator <Edge> edgesIterator = _edges.iterator();
            while(edgesIterator.hasNext()) {
                Edge auxiliarEdge = edgesIterator.next();
                if (actualFloor == -1) { //FIXME: actualFloor is entorno?
                    
                    if (auxiliarEdge.get_x1() == auxiliarVertex.get_x() && auxiliarEdge.get_y1() == auxiliarVertex.get_y()) {
                        
                        Iterator <Vertex> vertexSecondIterator = vertex.iterator();
                        while (vertexSecondIterator.hasNext()) {
                            Vertex auxiliarSecondVertex = vertexSecondIterator.next();
                            
                            if (auxiliarSecondVertex.get_x() == auxiliarEdge.get_x2() && auxiliarSecondVertex.get_y() == auxiliarEdge.get_y2()) adjacents.add(auxiliarSecondVertex);
                        }

                    } else if (auxiliarEdge.get_x2() == auxiliarVertex.get_x() && auxiliarEdge.get_y2() == auxiliarVertex.get_y()) {
                    
                        Iterator <Vertex> vertexSecondIterator = vertex.iterator();
                        while (vertexSecondIterator.hasNext()) {
                            Vertex auxiliarSecondVertex = vertexSecondIterator.next();
                            
                            if (auxiliarSecondVertex.get_x() == auxiliarEdge.get_x1() && auxiliarSecondVertex.get_y() == auxiliarEdge.get_y1()) adjacents.add(auxiliarSecondVertex);
                        }
                    }
                } else {
                    if (auxiliarEdge.get_idV1().equals(auxiliarVertex.get_id())) {
                        Iterator <Vertex> vertexSecondIterator = vertex.iterator();
                        while (vertexSecondIterator.hasNext()) {
                            Vertex auxiliarSecondVertex = vertexSecondIterator.next();
                            
                            if (auxiliarSecondVertex.get_id().equals(auxiliarEdge.get_idV2())) {
                                doubled = false;
                                Iterator <Vertex> adjacentsSecondIterator = adjacents.iterator();
                                while (adjacentsSecondIterator.hasNext()) {
                                    if (adjacentsSecondIterator.next().get_id().equals(auxiliarSecondVertex.get_id())) doubled = true;
                                }
                                
                                if (!doubled) adjacents.add(auxiliarSecondVertex);
                            }
                        }
                    } else if (auxiliarEdge.get_idV2().equals(auxiliarVertex.get_id())) {
                        Iterator <Vertex> vertexSecondIterator = vertex.iterator();
                        while (vertexSecondIterator.hasNext()) {
                            Vertex auxiliarSecondVertex = vertexSecondIterator.next();
                            
                            if (auxiliarSecondVertex.get_id().equals(auxiliarEdge.get_idV1())) {
                                doubled = false;
                                Iterator <Vertex> adjacentsSecondIterator = adjacents.iterator();
                                while (adjacentsSecondIterator.hasNext()) {
                                    if (adjacentsSecondIterator.next().get_id().equals(auxiliarSecondVertex.get_id())) doubled = true;
                                }
                                
                                if (!doubled) adjacents.add(auxiliarSecondVertex);
                            }
                        }
                    }
                }
            }
            _matrix.add(adjacents);
        }
    }




    /**
     * Read the links (lifts, ladders...) and save as vertex but without names
     * 
     * @param svgFile
     */
    private void readLinksCoordenates(String svgFile) {
        KXmlParser parser = new KXmlParser();
        InputStream is = this.getClass().getResourceAsStream(svgFile);

        int ne;
        int floor = 0; //The floor that actually we are reading from SVG
        String xmlId = null;
        String xmlX = null, xmlY = null;

        try {
            parser.setInput(is, "UTF-8");

            while ((ne = parser.next()) != KXmlParser.END_DOCUMENT) {
                if (ne == KXmlParser.START_TAG) {
                    
                    //The g xml element represents a layer at inkscape, so, for us, it represents a floor
                    if (parser.getName().equals("g")) {
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("id")) {
                                floor = Integer.parseInt(parser.getAttributeValue(i)); //The floor's ID always is a int
                            }
                        }
                    
                    //The rects elements represents links between lift and lift, ladder and ladder or some link point to another link point
                    } else if (parser.getName().equals("rect")) {
                        for (int i = 0; i < parser.getAttributeCount(); i++) {

                            //We must read the attributes: X, Y, id and value (the hotspot's name) and save it as a vertex
                            if (parser.getAttributeName(i).equals("id")) {
                                xmlId = parser.getAttributeValue(i); 
                            } else if (parser.getAttributeName(i).equals("x")) {
                                xmlX = parser.getAttributeValue(i);
                            } else if (parser.getAttributeName(i).equals("y")) {
                                xmlY = parser.getAttributeValue(i);
                            }
                            //FIXME: Aquí había cosas, pero quité el último else-if
                        }
                        StringTokenizer tokens = new StringTokenizer(xmlX, ".");
                        int integerXmlX = Integer.parseInt(tokens.nextToken());
                        tokens = new StringTokenizer(xmlY, ".");
                        int integerXmlY = Integer.parseInt(tokens.nextToken());
                        Vertex auxVertex = new Vertex(xmlId, integerXmlX, integerXmlY, floor);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
