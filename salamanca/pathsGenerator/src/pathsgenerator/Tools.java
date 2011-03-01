/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pathsgenerator;

import java.util.StringTokenizer;

/**
 *
 * @author Administrator
 */
public class Tools {



    public static String _svgPath= "./map.svg";




    public static String decimalsRemove(Object object) {
        StringTokenizer st = new StringTokenizer(String.valueOf(object), ".");
        return st.nextToken();
    }




}
