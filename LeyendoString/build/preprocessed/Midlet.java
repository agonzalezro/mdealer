
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.*;

public class Midlet extends MIDlet {

    public void startApp() {
        Form formulario = new Form("Leyendo strings");
        formulario.append(searchIdentifierPath("100"));
        Display.getDisplay(this).setCurrent(formulario);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    private String getTheFindPath(String cadenaConLasRutas, int i) {
        String findPathString = null;

        //if (i == 1) i = 0; //If I receive a 1, it's because on after function it send me 0 + 1, so the identifier is the first identifier on the String

        for (; i < cadenaConLasRutas.length(); i++) {
            if (cadenaConLasRutas.charAt(i) != '\n') {
                if (findPathString == null)
                    findPathString = "" + cadenaConLasRutas.charAt(i);
                else
                    findPathString = findPathString + cadenaConLasRutas.charAt(i);
            } else {
                return findPathString;
            }
        }

        if (i == cadenaConLasRutas.length())  return findPathString;
        else return null;
    }

    private String searchIdentifierPath(String identifier) {
        String searchingString = null;

        String cadenaConLasRutas = getFileString(identifier);

        for (int i = 0; i < cadenaConLasRutas.length(); i++) {
            if (cadenaConLasRutas.charAt(i) == '\n' || i == 0) { //I'm reading the \n, so the i+1 is the first identifier's char
                if (i != 0) i++; //If I'm reading the firs identifier, I don't need to change this. If I'm reading a '\n' I must jump it
                int j = 0;

                do { //We go to search the identifier, from the \n to the next space
                    if (searchingString == null) {
                        searchingString = "" + cadenaConLasRutas.charAt(i + j); //At first time, the searchingString will be empty, so I need to start it with the cadena's value
                    } else {
                        searchingString = searchingString + cadenaConLasRutas.charAt(i + j); //At N time, we must concatenate
                    }
                    j++;
                } while (cadenaConLasRutas.charAt(i + j) != ' ');

                if (searchingString != null && searchingString.equals(identifier)) {
                    /* We know where the path start, so I call to a function that get from the path start, to the next \n
                     * The +1 at the second argument is beacuse the i is the '\n' char, so the first path element is the +1 */
                    return getTheFindPath(cadenaConLasRutas, i);
                } else {
                    searchingString = null; //We don't find the searching identifier
                }
            }
        }
        return null;
    }

    private String getFileString(String hotSpot) {
        try {
            InputStream is = getClass().getResourceAsStream("/" + hotSpot.charAt(0) + ".txt.gz");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int i = -1;
            while ((i = is.read()) != -1) {
                baos.write(i);
            }
            is.close();
            // Decompress.
            byte[] data = GZIP.inflate(baos.toByteArray());

            //CHANGE
            //String tempStringToGetTheLine = new String(data);
            //System.out.println(tempStringToGetTheLine);

            /*String datos = new String(data);
            TextBox textBox = new TextBox("GZIP data", new String(data), data.length, 1);
            Display.getDisplay(this).setCurrent(textBox);*/

            return new String(data);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
