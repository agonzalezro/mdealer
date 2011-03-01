/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pathsgenerator;

import java.util.ArrayList;
import java.util.Iterator;



/**
 *
 * @author Administrator
 */
public class Planner {

    Euler littleEuler = new Euler();

    public Planner() {
    }

    public Path findPath(Vertex or, Vertex dest){
    littleEuler.matrixCreate(or.get_floor());
    Path myPath = littleEuler.buscarRuta(or, dest);
    return myPath;
    }

    public void planner(int idHotSpotOR,float beginningX, float beginningY, int beginningFloor,int idHotSpotDes ,float destinyX, float destinyY, int destinyFloor)
{

        ArrayList <Vertex> vertex = new ArrayList();

        //We are going to eliminate the coordenates decimals
        beginningX = Integer.parseInt(Tools.decimalsRemove(beginningX));
        beginningY = Integer.parseInt(Tools.decimalsRemove(beginningY));
        destinyX = Integer.parseInt(Tools.decimalsRemove(destinyX));
        destinyY = Integer.parseInt(Tools.decimalsRemove(destinyY));

        int floorTransporters = -1; //ladders and lifts, every matrix made from a floor has id, -1 is the id of transporters

        littleEuler.loadMatrixThings(beginningFloor);
        vertex=littleEuler.getVertex();

        Vertex auxiliarVertex = new Vertex();
        Vertex beginningVertex = new Vertex();

        Iterator <Vertex> vertexIterator = vertex.iterator();
        while (vertexIterator.hasNext()) {
            auxiliarVertex = vertexIterator.next(); 
            if ((beginningX <= auxiliarVertex.get_x()+1) && (beginningX >= auxiliarVertex.get_x()-1) &&
                     (beginningY <= auxiliarVertex.get_y()+1) && (beginningY >= auxiliarVertex.get_y()-1) )
                beginningVertex = auxiliarVertex;
        }

        littleEuler.matrixCreate(destinyFloor);
        vertex=littleEuler.getVertex();
        Vertex destinyVertex = null;

        vertexIterator = vertex.iterator();
        while (vertexIterator.hasNext()) {
            auxiliarVertex = vertexIterator.next();
            if (auxiliarVertex.get_x()-1 <=(int) destinyX && auxiliarVertex.get_x()+1 >=(int) destinyX &&
                    auxiliarVertex.get_y()-1 <= (int)destinyY && auxiliarVertex.get_y()+1 >= (int)destinyY )
                destinyVertex = auxiliarVertex;
        }

        Path pathToShow = new Path();
        if (beginningFloor == destinyFloor) {
            pathToShow = littleEuler.buscarRuta(beginningVertex, destinyVertex);

        } else {

            littleEuler.loadLinkersMatrixThings();
            vertex=littleEuler.getVertex();
            auxiliarVertex = null;
            vertexIterator = vertex.iterator();

            ArrayList transportersPaths = new ArrayList(); //contains every posible combination of lifters and stairs
            ArrayList <Path> longPaths = new ArrayList(); //contains a path for every transportersPath including the paths to reach the tranporters
            ArrayList beginningFloorTransporters = new ArrayList();
            ArrayList destinyFloorTransporters = new ArrayList();

            while(vertexIterator.hasNext()) { //loading the transporters points
                auxiliarVertex = vertexIterator.next();
                if (auxiliarVertex.get_floor() == beginningFloor)
                    beginningFloorTransporters.add(auxiliarVertex);
                else if (auxiliarVertex.get_floor() == destinyFloor)
                    destinyFloorTransporters.add(auxiliarVertex);
            }

            //calculating all posibles paths made of transporters between the beegining floor to the
            //destiny floor.
            littleEuler.matrixCreate(floorTransporters);
            Iterator <Vertex> beginningFloorTransportersIterator = beginningFloorTransporters.iterator();
            Iterator <Vertex> destinyFloorTransportersIterator  = destinyFloorTransporters.iterator();
            Vertex bftVertex = null; //A shorter name variable to define: beginningFloorTransportersVertex
            Vertex dftVertex = null;
            while(beginningFloorTransportersIterator.hasNext()) {
                bftVertex = beginningFloorTransportersIterator.next();
                while(destinyFloorTransportersIterator.hasNext()) {
                    dftVertex = destinyFloorTransportersIterator.next();
                    Path findPath = littleEuler.buscarRuta(bftVertex, dftVertex);
                    if (!findPath.isEmpty())
                        transportersPaths.add(findPath);
                }
                //Restore the iterator
                destinyFloorTransportersIterator = destinyFloorTransporters.iterator();
            }

            //After this, we will calculate the path to reach the first tranporter
            //the path to reach others tranporters if necessary and the path to arrive to the destiny
            //from the last transporter.
            //So on we will have as many paths as combinations of transporters. We will keep the shortest one.

           
            Iterator <Path> transportersPathIterator = transportersPaths.iterator();
            while(transportersPathIterator.hasNext()) {
                Path auxiliarPath = transportersPathIterator.next();
                Vertex middleDestiny = (Vertex) auxiliarPath.get_path().get(0);

                littleEuler.matrixCreate(beginningFloor);
                vertex=littleEuler.getVertex();
                vertexIterator = vertex.iterator();
                //we need to find out which coordenates has the first transporter at the matrix of the begining floor
                //we pass through an error maded by the svg of +1/-1 pixels
                while(vertexIterator.hasNext()) {
                    auxiliarVertex = vertexIterator.next();
                    if (middleDestiny.get_x() <= auxiliarVertex.get_x()+1 && middleDestiny.get_x() >= auxiliarVertex.get_x()-1 &&
                        middleDestiny.get_y() <= auxiliarVertex.get_y()+1 && middleDestiny.get_y() >= auxiliarVertex.get_y()-1) {
                        while(vertexIterator.hasNext()) vertexIterator.next();
                    }
                }
//---------------------------------------------------------------------------------------------------------------------------------
                //Calculate the path to the first transporter.
                //The id of every transporter we work with has to be storaged in the path. This is because we distinguis
                //if the transporte is stairs or lift from the first leter of it's id
                Path ap = littleEuler.buscarRuta(beginningVertex, auxiliarVertex);
                Vertex nv = new Vertex (middleDestiny.get_id(),0,0,middleDestiny.get_floor());
                ap.get_path().add(nv);
                longPaths.add(ap);
//---------------------------------------------------------------------------------------------------------------------------------
                littleEuler.matrixCreate(floorTransporters);
                vertex=littleEuler.getVertex();
                //we calculate the path that comunicates transporters at the same floor because this means that the user
                //has built a path which needs a change of transporters to reach the destiny. Becasue is possible to
                //deploy this system in a building so complex that several changes of transporters are needed to reach
                //a destiny we have implemented this loop that calculte the path between every pair of transporters we
                //find at the current transportePath.
                for (int i = 0; i <= auxiliarPath.get_path().size()-2; i++) {
                    Vertex auxiliarBeginningTransporter = (Vertex) auxiliarPath.get_path().get(i);
                    Vertex auxiliarDestinyTransporter = (Vertex) auxiliarPath.get_path().get(i+1);
                    Vertex auxTrans = auxiliarDestinyTransporter;
                    int fa = auxiliarBeginningTransporter.get_floor();
                    int fb = auxiliarDestinyTransporter.get_floor();

                    if ( fa==fb ) {
                        littleEuler.matrixCreate(auxiliarBeginningTransporter.get_floor());
                        vertex=littleEuler.getVertex();
                        vertexIterator = vertex.iterator();
                        while(vertexIterator.hasNext()) {
                            auxiliarVertex = vertexIterator.next();
                            if ((auxiliarBeginningTransporter.get_x() <= auxiliarVertex.get_x()+1) && (auxiliarBeginningTransporter.get_x() >= auxiliarVertex.get_x()-1) &&
                                (auxiliarBeginningTransporter.get_y() <= auxiliarVertex.get_y()+1) && (auxiliarBeginningTransporter.get_y() >= auxiliarVertex.get_y()-1))
                                    auxiliarBeginningTransporter = auxiliarVertex;
                             if ((auxiliarDestinyTransporter.get_x() <= auxiliarVertex.get_x()+1) && (auxiliarDestinyTransporter.get_x() >= auxiliarVertex.get_x()-1) &&
                                (auxiliarDestinyTransporter.get_y() <= auxiliarVertex.get_y()+1) && (auxiliarDestinyTransporter.get_y() >= auxiliarVertex.get_y()-1))
                                auxiliarDestinyTransporter = auxiliarVertex;
                        }
//---------------------------------------------------------------------------------------------------------------------------------
                        Path findPath = littleEuler.buscarRuta(auxiliarBeginningTransporter, auxiliarDestinyTransporter);
                        nv= new Vertex (auxTrans.get_id(),0,0,auxTrans.get_floor());
                        findPath.get_path().add(nv);
//---------------------------------------------------------------------------------------------------------------------------------
                        //we insert the new part of the path that has just been calculated in the longPath
                        Path auxiliarPath2 = (Path) longPaths.get(longPaths.size()-1);
                        Vertex r=null;
                        for (int j = 0; j < findPath.get_path().size(); j++){
                            r =  (Vertex) findPath.get_path().get(j);
                            auxiliarPath2.get_path().add(r);
                        }
                        longPaths.remove(longPaths.size() - 1);
                        longPaths.add(auxiliarPath2);
                    }
                }
                //take as origing the last checkpoint to the destiny vertex
                Vertex beginningTransporterVertex = (Vertex) auxiliarPath.get_path().get(auxiliarPath.get_path().size()-1);
                littleEuler.matrixCreate(destinyFloor);
                vertex=littleEuler.getVertex();
                vertexIterator = vertex.iterator();
                while (vertexIterator.hasNext()) {
                    auxiliarVertex = vertexIterator.next();
                    if ((beginningTransporterVertex.get_x() <= auxiliarVertex.get_x()+1) && (beginningTransporterVertex.get_x() >= auxiliarVertex.get_x()-1) &&
                       beginningTransporterVertex.get_y() <= auxiliarVertex.get_y()+1 && (beginningTransporterVertex.get_y() >= auxiliarVertex.get_y()-1) )
                    {
                        beginningTransporterVertex=auxiliarVertex;
                        while(vertexIterator.hasNext()) vertexIterator.next(); //Some as break;
                    }

                }
//---------------------------------------------------------------------------------------------------------------------------------
                //the last segment of the longPath
                Path findPath = littleEuler.buscarRuta(auxiliarVertex, destinyVertex);
//---------------------------------------------------------------------------------------------------------------------------------
                Path auxiliarPath2 = (Path) longPaths.get(longPaths.size() - 1);
                Vertex v=null;
                for (int k = 0; k < findPath.get_path().size(); k++){
                    v=(Vertex) findPath.get_path().get(k);
                    auxiliarPath2.get_path().add(v);
                }

                longPaths.remove(longPaths.size() - 1);
                longPaths.add(auxiliarPath2);
            }



            Path auxiliarPathToShow;

            for (int s = 0; s <= longPaths.size()-1; s++) {
                auxiliarPathToShow = (Path) longPaths.get(s);
                auxiliarPathToShow.set_weight();
                //auxiliarPathToShow.appliTransform();
                //System.out.println(auxiliarPathToShow.toString());
            }

            //Select the best way (the shortest)

            pathToShow = (Path) longPaths.get(0);


            for (int s = 1; s <= longPaths.size()-1; s++) {
                auxiliarPathToShow = (Path) longPaths.get(s);
                if (auxiliarPathToShow.get_weight() < pathToShow.get_weight())
                    pathToShow = auxiliarPathToShow;
            }
        }
        System.out.print(idHotSpotOR + " ");
        System.out.print(idHotSpotDes + " ");
        pathToShow.appliTransform();
        System.out.println(pathToShow.toString());
    }

}
