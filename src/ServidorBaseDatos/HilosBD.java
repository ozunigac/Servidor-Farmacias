/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServidorBaseDatos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import libreriaPBD.ConsultaJoinVictor;
import query_lector.Query;

/**
 *
 * @author telecom
 */
public class HilosBD extends Thread {
    private Socket s;
    private int clieNum;
    
    //cuando un cliente se conecte al servidor, mostrará que cliente y de que direccion ip
    public HilosBD(Socket socket, int clientNumber) {
        this.s = socket;
        this.clieNum = clientNumber;
        //mostrará la ip y el puerto.
        log("Conexión # " + clieNum + " en " + s);
    }
    
    //estará a la espera de mensajes del sistema 
    @Override
    public void run() {
        //empieza las acciones que tomará el server, dependiendo de que le manden al server
        try {
            //usaremos la variable "in" para leer lo que nos manden al server
            BufferedReader in = new BufferedReader( new InputStreamReader(s.getInputStream()));
            //envia datos a web
            DataOutputStream dout= new DataOutputStream(s.getOutputStream());
            //usaremos la variable "out" para responder al sistema cliente
            ObjectOutputStream outObject = new ObjectOutputStream(s.getOutputStream());
            PrintWriter out = new PrintWriter (s.getOutputStream(),true);
            //creamos una instancia del servidor de la base de datos 
            Query bd = new Query();
            //este ciclo mantendrá al servidor de la BD escuchando las peticiones siempre                    
            while (true){
                //leemos lo que nos mandó el cliente
                String consulta = in.readLine();
                //*****************************************************************/
                //si la consulta contiene select entonces regresará una matriz
                if(consulta.contains("select web")){
                    consulta=consulta.replace(" web", "");
                    log(consulta);
                    try{
                        bd.setQuery(consulta);
                        String [][] tabla=bd.tabla;
                        dout.writeBytes(tabla[1][0]);
                        log(tabla[1][0]);
                        dout.close();
                    }catch(Exception ex){
                        dout.writeBytes("Sin existencia");
                        dout.close();
                    }
                }else if (consulta.contains("select")){
                    //mandamos la consulta la base de datos
                    log(consulta);
                    try{
                        bd.setQuery(consulta);
                    //en caso de arrojar un error en la base de datos
                    }catch(Exception ex){
                        outObject.writeObject(null);
                    }
                    //recibimos la tabla del select
                    String [][] tabla=bd.tabla;
                    //regresamos el objeto de la tabla
                    outObject.writeObject(tabla);
                //*****************************************************************/
                //en caso de que la consulta sea un insert retornara solo si el insert fue exito o no
                }else if(consulta.contains("inner join")){
                    //mandamos la consulta la base de datos
                    log(consulta);
                    try{
                        ConsultaJoinVictor asd = new ConsultaJoinVictor("A:\\7 cuatrimestre\\POO\\Practica7\\BaseDeDatosGrupal");
                        asd.consulta(consulta);
                    //en caso de arrojar un error en la base de datos
                    }catch(Exception ex){
                        outObject.writeObject(null);
                    }
                    //recibimos la tabla del select
                    String [][] tabla=bd.tabla;
                    //regresamos el objeto de la tabla
                    outObject.writeObject(tabla);
                //manejaremos la consulta insert de esta forma
                }else if(consulta.contains("insert ")||consulta.contains("update ")||consulta.contains("delete ")){
                    //imprimimos la consulta
                    log(consulta);
                    String mensaje=bd.setQuery(consulta);
                    out.println(mensaje);
                    log(mensaje);
                }
            }
            //si hubo algun inconveniente arrojará el mensaje de error
        } catch (IOException e) {
            log("Error con la conexión # " + clieNum + ": " + e);
        } finally {
            //en caso de que el server se cierre de forma correcta.
            try {
                s.close();
            } catch (IOException e) {
                log("¿Deseas cerrar el socket?");
            }
            log("Conexión # " + clieNum + " cerrada");
        }
    }
    
    private void log(String message) {
            System.out.println(message);
    }
}
