package pathsgenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alex
 */
public class Writer {
    BufferedWriter _bw;

    public Writer(String filename) {
        try {
            File file = new File(filename);
            //El fichero lo queremos vacío.
            if (file.exists()) file.delete();
            _bw = new BufferedWriter(new FileWriter(file));
        } catch (IOException ex) {
            Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void write(String data) {
        try {
            _bw.write(data);
        } catch (IOException ex) {
            Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() {
        try {
            _bw.close();
        } catch (IOException ex) {
            Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
