/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pathsgenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author Administrator
 */
public class XMLReader {

 private int _xTransform = 0;
 private int _yTransform = 0;
 private String _svgFile;

    public XMLReader(String svgPath) {
        this._svgFile = svgPath;
    }
 

public HashMap<Integer, Vertex> getHotspotsFromSvg(String svgFile) throws IOException {
        HashMap<Integer, Vertex> hotspots = new HashMap<Integer, Vertex>();

        KXmlParser parser = new KXmlParser();
        InputStream is = this.getClass().getResourceAsStream(svgFile);
        BufferedWriter bwNames = new BufferedWriter(new FileWriter("hotnames.txt"));
        BufferedWriter bwIds = new BufferedWriter(new FileWriter("hotids.txt"));
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
                                    getTransforms(theActualFloor);
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
                                        theActualX = Integer.parseInt(tokens.nextToken()) + _xTransform;
                                    } else if (parser.getAttributeName(i).equals("y")) {
                                        //Only get the value before the dot without aproximation
                                        StringTokenizer tokens = new StringTokenizer(parser.getAttributeValue(i),".");
                                        theActualY = Integer.parseInt(tokens.nextToken()) + _yTransform;
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
                                String nombre = tokens.nextToken();
                                bwNames.write( nombre + "\n");
                                bwIds.write((identifier - 1) + "\n");
                                //Save the token ID, x, y and floor
                                Vertex hotspot = new Vertex(String.valueOf(identifier), theActualX, theActualY, theActualFloor, nombre);
                                hotspots.put(identifier, hotspot);
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
        bwNames.close();
        bwIds.close();
        is.close();

        //Debug: show the hotsposts points.
        Vertex.showAll(hotspots);

        return hotspots;
    }
private void getTransforms(int actualFloor) {
        int ne;
        //With this, we are sure that you aren't apply a other floor transform. If actualFloor doesn't have transform, the vars are setting to 0 by default
        _xTransform = 0; _yTransform = 0;

        KXmlParser parser = new KXmlParser();
        InputStream is = this.getClass().getResourceAsStream(_svgFile);

        try {
            parser.setInput(is, "UTF-8");

            //I use this variable to know if all the <g><path...</g> have a translate attribute, if they have it I can set
            //the x/y transform, if they haven't it x/y transform will be 0
            boolean testingTranslateFound = false;
            boolean translateAttributeFound = false;

            while ((ne = parser.next()) != KXmlParser.END_DOCUMENT) {

                //We remove the switch because only need to know when the elemente start
                //The "g" xml element is the representative of a layer. We are using layers to differentiate between one and another floor
                boolean found = false;
                if ((ne == KXmlParser.START_TAG) && parser.getName().equals("g")) {
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        if (parser.getAttributeName(i).equals("id") && Integer.parseInt(parser.getAttributeValue(i)) == actualFloor) {
                            found = true;
                            testingTranslateFound = true;
                        } else if (parser.getAttributeName(i).equals("id")) { //If this var is false, I'm in another layer (floor)
                            testingTranslateFound = false;
                        } else if (parser.getAttributeName(i).equals("transform") && found) {
                            //We've received translate(XXX.XXX,YYY.YYY)
                            StringTokenizer st = new StringTokenizer(parser.getAttributeValue(i), ",");
                            String transform = st.nextToken();
                            _xTransform = Integer.parseInt(Tools.decimalsRemove(transform.substring(10)));
                            transform = st.nextToken();
                            _yTransform = Integer.parseInt(Tools.decimalsRemove(transform.substring(0, transform.length() - 1)));
                            found = false;
                            break;
                        }
                    }
                } else if ((ne == KXmlParser.START_TAG) && parser.getName().equals("path") && testingTranslateFound) {
                    //If all the paths in the layer don't have a translate attribute, then, we set the x/yTransform = 0
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        if (parser.getAttributeName(i).equals("transform")) {
                            i = parser.getAttributeCount();
                            translateAttributeFound = true;
                        }
                    }
                }
            }

            //If we have a translate attributte in the <g> element, but we don't have it at <path> elements,
            //the x/y transform will be 0
            if (!translateAttributeFound) {
                _xTransform = 0;
                _yTransform = 0;
            }
         } catch (Exception e) {
            e.printStackTrace();
        }
}

    
    /*Las polylineas son cadenas en le svg que contienen vertices adyacentes, de cada polyline se geran las aristas (edges)*/
    public ArrayList createEdges(int actualFloor) {
        int ne;
        String polyline = null;

        int coordenateX1 = 0, coordenateY1 = 0, coordenateX2 = 0, coordenateY2 = 0;
        ArrayList<Edge> edges = new ArrayList<Edge>(); //All the edges in a floor are saved temporally in this var

        KXmlParser parser = new KXmlParser();
        InputStream is = this.getClass().getResourceAsStream(_svgFile);

        /*This block allows us to send all the polylines to the function edgesCreate*/
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
                                            getTransforms(actualFloor);
                                            StringTokenizer vertexST = new StringTokenizer(polyline, " ");
                                            vertexST.nextToken();//the first token is not a vertex.
                                            String vertexActual = vertexST.nextToken();
                                            if (!vertexActual.equals("L") && !vertexActual.equals("z")) {
                                                StringTokenizer coordenates = new StringTokenizer(vertexActual, ",");
                                                StringTokenizer coordenatesINT = new StringTokenizer(coordenates.nextToken(), ".");
                                                coordenateX1 = Integer.parseInt(coordenatesINT.nextToken());// + _xTransform;
                                                coordenatesINT = new StringTokenizer(coordenates.nextToken(), ".");
                                                coordenateY1 = Integer.parseInt(coordenatesINT.nextToken());// + _yTransform;
                                            }
                                            while (vertexST.hasMoreTokens()) {
                                                if (vertexST.hasMoreTokens()) {
                                                    vertexActual = vertexST.nextToken();
                                                }
                                                if (vertexST.hasMoreElements()) {
                                                    vertexActual = vertexST.nextToken();
                                                }
                                                if (!vertexActual.equals("L") && !vertexActual.equals("z")) {
                                                    /*System.out.println("DEBUG: " + vertexActual);
                                                    StringTokenizer coordenates = new StringTokenizer(vertexActual, ",");

                                                    //I can get here a xxx.xxx or only xxx, so I need to test if it has decimals or no
                                                    String decimals = coordenates.nextToken();
                                                    StringTokenizer coordenatesINT;
                                                    if (decimals.contains(".")) {
                                                        coordenatesINT = new StringTokenizer(decimals, ".");
                                                        coordenateX2 = Integer.parseInt(coordenatesINT.nextToken());// + _xTransform;
                                                    } else {
                                                        coordenateX2 = Integer.parseInt(decimals);
                                                    }

                                                    decimals = coordenates.nextToken();
                                                    if (decimals.contains(".")) {
                                                        coordenatesINT = new StringTokenizer(decimals, ".");
                                                        coordenateY2 = Integer.parseInt(coordenatesINT.nextToken());// + _yTransform;
                                                    } else {
                                                        coordenateY2 = Integer.parseInt(decimals);
                                                    }

                                                    edges.add(new Edge("", String.valueOf(coordenateX1) + String.valueOf(coordenateY1), coordenateX1, coordenateY1,
                                                            String.valueOf(coordenateX2) + String.valueOf(coordenateY2), coordenateX2, coordenateY2));
                                                    coordenateX1 = coordenateX2;
                                                    coordenateY1 = coordenateY2;*/

                                                    StringTokenizer coordenates = new StringTokenizer(vertexActual, ",");
                                                    StringTokenizer coordenatesINT = new StringTokenizer(coordenates.nextToken(), ".");
                                                    coordenateX2 = Integer.parseInt(coordenatesINT.nextToken());// + _xTransform;
                                                    coordenatesINT = new StringTokenizer(coordenates.nextToken(), ".");
                                                    coordenateY2 = Integer.parseInt(coordenatesINT.nextToken());// + _yTransform;
                                                    edges.add(new Edge("", String.valueOf(coordenateX1) + String.valueOf(coordenateY1), coordenateX1, coordenateY1,
                                                            String.valueOf(coordenateX2) + String.valueOf(coordenateY2), coordenateX2, coordenateY2));
                                                    coordenateX1 = coordenateX2;
                                                    coordenateY1 = coordenateY2;
                                                }
                                            }
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
        return edges;
    }
     public ArrayList[] readLinksCoordenates() {
        KXmlParser parser = new KXmlParser();
        InputStream is = this.getClass().getResourceAsStream(Tools._svgPath);
        ArrayList<Edge> edges = new ArrayList<Edge>(); //All the edges in a floor are saved temporally in this var
        ArrayList<Vertex> vertex = new ArrayList<Vertex>();

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
                                getTransforms(floor);
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
                            } else if (parser.getAttributeName(i).equals("inkscape:label")) {
                                StringTokenizer tokens = new StringTokenizer(parser.getAttributeValue(i), ":");
                                while (tokens.hasMoreElements()) {
                                    Edge auxEdge = new Edge("NULL", xmlId, tokens.nextToken());
                                    edges.add(auxEdge);
                                }
                            }                        
                        }
                        int integerXmlX = Integer.parseInt(Tools.decimalsRemove(xmlX)) + _xTransform;
                        int integerXmlY = Integer.parseInt(Tools.decimalsRemove(xmlY)) + _yTransform;
                        Vertex auxVertex = new Vertex(xmlId, integerXmlX, integerXmlY, floor);
                        vertex.add(auxVertex);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList[] linkersThings = new ArrayList[2];
        linkersThings[0]=edges;
        linkersThings[1]=vertex;
        return linkersThings;
    }
}
