package ServidorBaseDatos;

import java.io.IOException;
import java.net.ServerSocket;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ServidorBD {
    
    //encender el servidor de la base de datos
    public static void main(String[] args) throws IOException {
        System.out.println("El servidor de base de datos se esta ejecutando.");
        int clientNumber = 1;
        ServerSocket listener = new ServerSocket(9899);
        try {
            while (true) {
                //se crean los hilos de clientes
                new HilosBD(listener.accept(), clientNumber++).start();
            }
        }finally {
            listener.close();
        }
    }
    
}
