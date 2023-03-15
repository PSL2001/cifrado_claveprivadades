/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cifrado_claveprivadades;

import java.security.*; //JCA
import javax.crypto.*; //JCE
import java.io.*; //ficheros
import java.util.logging.Level;
import java.util.logging.Logger;

//Programa que encripta y desencripta un fichero
//mediante clave privada o simétrica utilizando el algoritmo DES
public class Main {

    public static void main(String[] Args) {
        CifradoInterfaz cifrado = new CifradoInterfaz();
        cifrado.setVisible(true);
    }
}