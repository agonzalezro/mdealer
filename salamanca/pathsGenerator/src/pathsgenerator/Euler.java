/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pathsgenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Administrator
 */
public class Euler {

    private ArrayList <Vertex> _vertex;
    private ArrayList <Edge> _edges;
    private XMLReader _myXMLReader = new XMLReader(Tools._svgPath);
    private List _matrix = null; //The matrix with all available path to walk on it. This matrix is a list with the adjacent points
    HashMap<Integer, Path> destPaths; //contains the paths that lead to the destiny.

    public Euler() {
    }
    public void matrixCreate(int actualFloor)
    {
        if (actualFloor == -1)
            loadLinkersMatrixThings();
        else
            loadMatrixThings(actualFloor);
        
        _matrix=new ArrayList();
        //If a Vertex is duplicated on adjacents, we must tag it to don't save.
        boolean doubled = false;
        Vertex auxiliarVertex = null;

        Iterator <Vertex> vertexIterator = _vertex.iterator();
        while (vertexIterator.hasNext()) {
            //All vertex have adjacents points that could be used to go from one vertex to another vertex.
            List adjacents = new ArrayList<Vertex>();
            auxiliarVertex = vertexIterator.next();
            /*//We need to traverse the list to find duplicated vertex. If a vertex is duplicated we tag it to don't save after the while.
            Iterator <Vertex> adjacentsIterator = adjacents.iterator();
            Vertex vertexAdAux = null;
            while (adjacentsIterator.hasNext()) {
                vertexAdAux = adjacentsIterator.next();
                if (auxiliarVertex.get_id().equals(vertexAdAux.get_id())) doubled = true;
            }

            //If the vertex isn't duplicated we must save it as an adjacente vertex.
            if (!doubled) adjacents.add(vertexAdAux);
            */
            adjacents.add(auxiliarVertex);
            Iterator <Edge> edgesIterator = _edges.iterator();
            while(edgesIterator.hasNext()) {
                Edge auxiliarEdge = edgesIterator.next();
                if (actualFloor != -1) { //FIXME: actualFloor is entorno?

                    if (auxiliarEdge.get_x1() == auxiliarVertex.get_x() && auxiliarEdge.get_y1() == auxiliarVertex.get_y()) {

                        Iterator <Vertex> vertexSecondIterator = _vertex.iterator();
                        while (vertexSecondIterator.hasNext()) {
                            Vertex auxiliarSecondVertex = vertexSecondIterator.next();
                            if (auxiliarSecondVertex.get_x() == auxiliarEdge.get_x2() && auxiliarSecondVertex.get_y() == auxiliarEdge.get_y2()) adjacents.add(auxiliarSecondVertex);
                        }

                    } else if (auxiliarEdge.get_x2() == auxiliarVertex.get_x() && auxiliarEdge.get_y2() == auxiliarVertex.get_y()) {

                        Iterator <Vertex> vertexSecondIterator = _vertex.iterator();
                        while (vertexSecondIterator.hasNext()) {
                            Vertex auxiliarSecondVertex = vertexSecondIterator.next();

                            if (auxiliarSecondVertex.get_x() == auxiliarEdge.get_x1() && auxiliarSecondVertex.get_y() == auxiliarEdge.get_y1()) adjacents.add(auxiliarSecondVertex);
                        }
                    }
                } else {
                    if (auxiliarEdge.get_idV1().equals(auxiliarVertex.get_id())) {
                        Iterator <Vertex> vertexSecondIterator = _vertex.iterator();
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
                        Iterator <Vertex> vertexSecondIterator = _vertex.iterator();
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
    public void loadMatrixThings (int actualFloor)
    {
        _vertex = new ArrayList<Vertex>();
        _edges = new ArrayList<Edge>();
        _edges= _myXMLReader.createEdges(actualFloor);
        Iterator <Edge> edgesIterator = _edges.iterator();
        Vertex auxVertex;
        Edge auxEdge;
        while (edgesIterator.hasNext()) {
            auxEdge = (Edge) edgesIterator.next(); //It's the same: auxArista=(Arista) aristas.elementAt(i);
            //auxVertex = new Vertex(auxEdge.get_idV1(), auxEdge.get_x1(), auxEdge.get_y1(), actualFloor);
            //vertex.add(auxVertex);

            //This iterator was created to find if a vertex've inserted yet or no. If the vertex doesn't exists on the vertex list, we must create it
            Iterator <Vertex> vertexIterator = _vertex.iterator();
            boolean duplicateV1 = false, duplicateV2 = false;
            while (vertexIterator.hasNext()) {
                auxVertex=(Vertex)vertexIterator.next();
                //The X coordenate doesn't match and the Y coordenate doesn't match too
                if (auxEdge.get_x1() == auxVertex.get_x() && auxEdge.get_y1() == auxVertex.get_y()) duplicateV1=true;
                if (auxEdge.get_x2() == auxVertex.get_x() && auxEdge.get_y2() == auxVertex.get_y()) duplicateV2=true;
            }
            if(duplicateV1 == false) {
                _vertex.add(new Vertex(auxEdge.get_idV1(), auxEdge.get_x1(), auxEdge.get_y1(), actualFloor));
            }
            if(duplicateV2 == false) {
                _vertex.add(new Vertex(auxEdge.get_idV2(), auxEdge.get_x2(), auxEdge.get_y2(), actualFloor));
            }
        }
    }
    public void loadLinkersMatrixThings()
    {
        _edges = new ArrayList<Edge> ();
        _vertex = new ArrayList<Vertex>();
        ArrayList[] ret  = _myXMLReader.readLinksCoordenates();
        _edges=ret[0];
        _vertex=ret[1];
    }
    public Path buscarRuta(Vertex origen, Vertex destino)
    {       

        destPaths = new HashMap<Integer, Path>();
        int clave = 0;
        int claveFin = 0;
        ArrayList<Vertex> path;
        HashMap<Integer, ArrayList> paths = new HashMap<Integer, ArrayList>();
        ArrayList pathsNuevos = new ArrayList();
        ArrayList adyacentes = buscarAdyacentes(origen);
        Iterator i = adyacentes.iterator(); //if adyacentes != null???
        i.next();
        while (i.hasNext()) {
            path = new ArrayList<Vertex>();
            path.add(origen);
            path.add((Vertex) i.next());
            paths.put(clave++, path);
        }

        boolean encontrada = false;
        ArrayList<Vertex> pathAux = new ArrayList<Vertex>();
        while (encontrada == false) {

            Iterator iPathsNuevos = pathsNuevos.iterator();
            while(iPathsNuevos.hasNext()){
                ArrayList aux = (ArrayList) iPathsNuevos.next();
                paths.put(createKey(paths), aux);
            }
            pathsNuevos = new ArrayList();
            //sacamos lo paths con ciclos y los que llegan al destino;
            paths = sanearPaths(paths, destino);
            //bucle maestro
            Iterator pathsIterator = paths.entrySet().iterator();
            Map.Entry entry = null;
            boolean buscando = false;
            while (pathsIterator.hasNext()) {
                entry = (Entry) pathsIterator.next();
                int clavePath = (Integer) entry.getKey();
                pathAux = (ArrayList<Vertex>) entry.getValue();
                //ignorar los paths null;Existen por que tenian un ciclo, no los puedo borrar por que
                //alteraria el hashmap y daria excepciones
                //si llega al final , pasamos a otro.
                if (pathAux != null) {
                    //buscarAdyacentes
                    adyacentes = buscarAdyacentes(pathAux.get(pathAux.size() - 1));
                    if (adyacentes.size() == 2) {
                        //si long 2: El actual y el anterior
                        //si es destino fin, sino via muerta. Nunca va a ser destino por que si lo fuera no estariamos aqui.
                        //estamos en un callejon sin salida y no hemos llegado a la salida, por lo tanto al volver sobre nuestros
                        //estamos generando un ciclo. Pos teriormente se va a borrar el path entero. Es lo que tiene que ser.
                        pathAux.add((Vertex) adyacentes.get(1));
                        paths.put(clavePath, pathAux);
                        //pathsNuevos.add(pathAux);
                        buscando = true;
                    }
                    if (adyacentes.size() == 3) {
                        //si longAdyacentes= 3: El actual, el anterior y el nuevo //Añadir y punto.
                        //el ultimo de pathAux es el primero de adyacentes y el anterior es uno de los otros 2 de adyacentes
                        //tenemos que descartarlo ya que no queremos añadir el vertice anterior al ultimo de pathAux
                        Vertex vAnterior = null;
                        if (pathAux.size() > 1) {
                            vAnterior = pathAux.get(pathAux.size() - 2);
                        } else {
                            vAnterior = pathAux.get(0); //al principio solo hay un vertice en pathAux
                        }
                        Vertex adAux = (Vertex) adyacentes.get(1);
                        if (!vAnterior.get_id().equals(adAux.get_id())) {
                            pathAux.add((Vertex) adyacentes.get(1));
                        } else {
                            pathAux.add((Vertex) adyacentes.get(2));
                        }
                        paths.put(clavePath, pathAux);
                        buscando = true;
                    }
                    if (adyacentes.size() > 3) {
                        //si long = 3+n : El actual, el anterior y bifurcaciones
                        //crear n paths cada uno acaba en n(i) excepto para el vertex anterior
                        //reiniciar el bucle de paths
                        Vertex vAnterior = pathAux.get(pathAux.size() - 2);
                        for (int j = 1; j < adyacentes.size(); j++) {
                            Vertex vc = (Vertex) adyacentes.get(j);
                            if (!vAnterior.get_id().equals(vc.get_id())) {
                                ArrayList pathAuxNew = new ArrayList(pathAux);
                                //pathAuxNew = pathAux;
                                pathAuxNew.add(vc);
                                pathsNuevos.add(pathAuxNew);
                            }
                        }
                        paths.put(clavePath, null);
                        buscando = true;
                    }
                }
            }//while paths
            if (buscando == false) //es igual a false cuando todos los adyacentes de todas las rutas
            //han sido visitados, dejaremos de aÃ±adir rutas, saldremos del while
            //que recorre las rutas y despues del while principal
            {
                encontrada = true;
            }
        }//while de encontrada

        Path shortest =null;
        Iterator ipDest = destPaths.entrySet().iterator();
        Map.Entry entry = null;
        try {
            entry = (Entry) ipDest.next();
        } catch (Exception e) {
            System.out.println("EXCEPTION: planta : " + origen.get_floor()  + "origen: "+origen.get_id()+" destino: "+destino.get_id());
        }
        Path currentPath=(Path) entry.getValue();
        Vertex v = (Vertex) currentPath.get_path().get(0);
        if (v.get_id().startsWith("E")||v.get_id().startsWith("A")||v.get_id().startsWith("e")){//this is a transporters path
            currentPath.set_weightTrans();
        }else  currentPath.set_weight();
        shortest=currentPath;
        while(ipDest.hasNext()){
            entry = (Entry) ipDest.next();
            currentPath=(Path) entry.getValue();

            if (v.get_id().startsWith("E")||v.get_id().startsWith("A")||v.get_id().startsWith("e")){//this is a transporters path
            currentPath.set_weightTrans();
            }else  currentPath.set_weight();

            v = (Vertex) currentPath.get_path().get(0);
            if (v.get_id().startsWith("E")||v.get_id().startsWith("A")||v.get_id().startsWith("e")){//this is a transporters path
                Vertex v2 = (Vertex) currentPath.get_path().get(1);
                if(v.get_floor()!=v2.get_floor()){
                    if(shortest.get_path().size()>=currentPath.get_path().size())
                        shortest=currentPath;
                }
            }
            else if (shortest.get_weight() >currentPath.get_weight()) {
                shortest = currentPath;
            }
        }
        return shortest;
    }
    public ArrayList buscarAdyacentes(Vertex vertexP)
    {
        ArrayList adyacentes=null;
        Vertex vertexAux = vertexP;
        //buscamos el vector de adyacentes para el ultimo de cada ruta
        //for(int k=0;k<matriz.size();k++){
        Iterator<ArrayList> matrixIterator = _matrix.iterator();
        while (matrixIterator.hasNext()) {
            //seleccionamos un vector de adyacentes
            ArrayList<Vertex> adyacentesAux = new ArrayList<Vertex>();
            adyacentesAux = matrixIterator.next();
            Iterator<Vertex> adyacentsAuxIterator = adyacentesAux.iterator();
            //selelccionamos el primero que indica si es el vector que buscamos
            Vertex vertexAux2 = adyacentsAuxIterator.next();
            if (vertexAux.get_id().compareTo(vertexAux2.get_id()) == 0) {
                adyacentes=adyacentesAux;
            }

        }
        return adyacentes;
    }
    public int createKey(HashMap hm)
    {
        int key=0;
        while(hm.containsKey(key)==true){
            key++;
        }
        return key;
    }
    private HashMap<Integer, ArrayList> sanearPaths(HashMap<Integer, ArrayList> paths, Vertex destino)
    {

        //ponemos null en las entradas en las que hay un ciclo o terminan en destino

            ArrayList<Vertex> pathAux = new ArrayList<Vertex>();
            Iterator pathsIterator = paths.entrySet().iterator();
            while (pathsIterator.hasNext()) {
                Map.Entry entry = (Entry) pathsIterator.next();
                int clavePath = (Integer) entry.getKey();
                pathAux = (ArrayList) entry.getValue();

                if (pathAux != null) {
                    Iterator<Vertex> pathAuxIte1 = pathAux.iterator();
                    Vertex vertexActual;
                    while (pathAuxIte1.hasNext()) {
                        vertexActual = pathAuxIte1.next();
                        int cont = 0;
                        Iterator<Vertex> pathAuxIte2 = pathAux.iterator();
                        while (pathAuxIte2.hasNext()) {
                            Vertex vertexAux2 = pathAuxIte2.next();
                            if (vertexActual.get_id().equals(vertexAux2.get_id())) {
                                cont++;
                            }
                        }
                        if (cont >= 2) {
                            pathAux = null;
                            //paths.remove(clavePath); //hay un elemento repetido , hemos creeado un ciclo, hay que eliminarlo
                            paths.put(clavePath, null);
                            while (pathAuxIte1.hasNext()) {
                                pathAuxIte1.next(); //terminamos el bucle
                            }
                            //sino tiene ciclos comprobamos si llega al final, si es así lo almacenamos en finales y ponemos null
                        } else if (pathAux.get(pathAux.size() - 1).get_id().equals(destino.get_id())) {
                            Path destPath = new Path(pathAux);
                            destPaths.put(createKey(destPaths), destPath);
                            paths.put(clavePath, null);
                            /*entry = (Entry) pathsIterator.next();
                            clavePath = (Integer) entry.getKey();
                            pathAux = (ArrayList<Vertex>) entry.getValue();*/
                             while (pathAuxIte1.hasNext()) {
                                pathAuxIte1.next(); //terminamos el bucle
                            }
                        }
                    }
                }
            }
          /*  System.out.println("---------------------------------------");
            sop(destPaths);
            System.out.println();
            System.out.println("---------------------------------------"); */
            return paths;
    }

    public ArrayList<Vertex> getVertex() {
        return _vertex;
    }

}

