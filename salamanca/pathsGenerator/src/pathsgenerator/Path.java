/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pathsgenerator;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Administrador
 */
public class Path {

    //-------------------------chapuza transforms------------------------------------
    int tx1=-563,ty1=0; //442 9
    //-------------------------chapuza transforms------------------------------------

    int contEsc=0;
    int contAsc=0;
    private boolean empty = true;
    int _weight = 0;
    ArrayList _path = null; //FIXME: pasar a ArrayList
    ArrayList translist = new ArrayList(3);


    Path(){
       _path = new ArrayList();
       

       //_ruta.trimToSize();
    }
    Path(ArrayList _path){
        this._path=_path;
        this.empty=false;        
    }

    public int get_weight() {
        return _weight;
    }

    public void set_weight() {
        //this._weight = peso;
        int weightAux=0,auxContEsc=0,auxContAsc=0;
        translist.add("A");
        translist.add("E");
        translist.add("e");
        Iterator i = _path.iterator();
        Vertex v1 = (Vertex) i.next();
        while (i.hasNext()) {            
            if (i.hasNext()) {
                Vertex v2 = (Vertex) i.next();
                if (!translist.contains(String.valueOf(v2.get_id().charAt(0)))
                        && !translist.contains(String.valueOf(v1.get_id().charAt(0)))) {
                    int x1 = v1.get_x();
                    int y1 = v1.get_y();
                    int x2 = v2.get_x();
                    int y2 = v2.get_y();
                    long a1 = Math.abs(x1 - x2);
                    long a2 = a1 * a1;
                    long b1 = Math.abs(y1 - y2);
                    long b2 = b1 * b1;
                    long sqrtParam = a2 + b2;
                    weightAux = weightAux + (int) Math.sqrt(sqrtParam);
                }else if (v1.get_id().charAt(0)=='E' ){
                    int dif = Math.abs(v1.get_floor()-v2.get_floor());
                    auxContEsc=auxContEsc+dif;
                }else if ( v1.get_id().charAt(0)=='A') {
                    int dif = Math.abs(v1.get_floor()-v2.get_floor());
                    auxContAsc=auxContAsc+dif;
                }
                v1=v2;
            }
        }
        //FIXME: como los mapas no estan a escala entre las distintas plantas hay problemas
        //para encontrar el peso ideal de los transporters
        this._weight=weightAux+100*auxContAsc+300*auxContEsc;
        this.contEsc=auxContEsc;
        this.contAsc=auxContAsc;
    }

    public ArrayList get_path() {
        return _path;
    }

    public void set_path(ArrayList _path) {
        this._path = _path;
        this.empty=false;
    }

    public boolean isEmpty() {
        return empty;
    }


    public void appliTransform(){
        Iterator<Vertex> i = _path.iterator();
        ArrayList aux=new ArrayList();
        while (i.hasNext()){
            Vertex x = i.next();
            if (x.get_floor()==1){
                x.set_x(x.get_x()+tx1);
                x.set_y(x.get_y()+ty1);
            }
            aux.add(x);
        }
        _path=aux;
    }

    @Override
    public String toString(){
        String cadena ="";// String.valueOf(contEsc) + " ----- ";
        Vertex v = (Vertex) _path.get(0);
        //cadena =  cadena + v.get_floor() + v.get_id() + " ";
        //v = (Vertex) _path.get(_path.size()-1);
        //cadena = cadena + v.get_floor() + v.get_id() + " ";
        Iterator<Vertex> i = _path.iterator();
        //System.out.println("Cambiar el transporter de la planta 3serr a 3comp por una P ");
        while (i.hasNext()){
            v = (Vertex) i.next();
            String v1c= String.valueOf(v.get_id().charAt(0));
            if ("A".equals(v1c) || "E".equals(v1c) ||"e".equals(v1c)){
                cadena = cadena+ v1c + " ";
                v = (Vertex) i.next();
                cadena = cadena+v.get_floor()+ " " + 0 +" "+ 0 +" " ;
            }            
            cadena = cadena+v.get_floor()+ " " +v.get_x()+" "+v.get_y()+" " ;
        }
        cadena = cadena + "@";
        return cadena;
    }

    void set_weightTrans() {
        _weight=0;
        Iterator<Vertex> i = _path.iterator();
        while(i.hasNext()){
            i.next();
            _weight++;
        }
    }
    
}
