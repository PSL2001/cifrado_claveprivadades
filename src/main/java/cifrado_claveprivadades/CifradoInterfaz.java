package cifrado_claveprivadades;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import javax.crypto.SecretKey;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CifradoInterfaz extends JFrame implements ActionListener {

    // Componentes de la interfaz de usuario
    private JTextField rutaArchivoTextField;
    private JTextField rutaCifradoTextField;
    private JButton seleccionarArchivoButton;
    private JButton seleccionarCifradoButton;
    private JButton cifrarButton;
    private JButton descifrarButton;
    private JLabel mensajeLabel;

    public CifradoInterfaz() {
        // Configurar la ventana principal
        super("Cifrado de archivos");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 200);

        // Crear y configurar los componentes
        rutaArchivoTextField = new JTextField(20);
        rutaCifradoTextField = new JTextField(20);
        seleccionarArchivoButton = new JButton("Seleccionar archivo");
        seleccionarCifradoButton = new JButton("Seleccionar archivo de cifrado");
        cifrarButton = new JButton("Cifrar archivo");
        descifrarButton = new JButton("Descifrar archivo");
        mensajeLabel = new JLabel("");

        // Agregar controladores de eventos a los botones
        seleccionarArchivoButton.addActionListener(this);
        seleccionarCifradoButton.addActionListener(this);
        cifrarButton.addActionListener(this);
        descifrarButton.addActionListener(this);

        // Crear paneles para organizar los componentes
        JPanel archivoPanel = new JPanel(new BorderLayout());
        JPanel cifradoPanel = new JPanel(new BorderLayout());
        JPanel botonesPanel = new JPanel(new FlowLayout());
        JPanel mensajePanel = new JPanel(new FlowLayout());

        // Agregar los componentes a los paneles
        archivoPanel.add(new JLabel("Archivo: "), BorderLayout.WEST);
        archivoPanel.add(rutaArchivoTextField, BorderLayout.CENTER);
        archivoPanel.add(seleccionarArchivoButton, BorderLayout.EAST);

        cifradoPanel.add(new JLabel("Archivo de cifrado: "), BorderLayout.WEST);
        cifradoPanel.add(rutaCifradoTextField, BorderLayout.CENTER);
        cifradoPanel.add(seleccionarCifradoButton, BorderLayout.EAST);

        botonesPanel.add(cifrarButton);
        botonesPanel.add(descifrarButton);

        mensajePanel.add(mensajeLabel);

        // Agregar los paneles a la ventana
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
    }

    // Controlador de eventos para botones
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == seleccionarArchivoButton) {
            // Crear un cuadro de dialogo para seleccionar el archivo
            JFileChooser fc = new JFileChooser();
            int result = fc.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                // Actualizar la ruta del archivo seleccionado
                File archivo = fc.getSelectedFile();
                rutaArchivoTextField.setText(archivo.getAbsolutePath());
            }
        } else if (e.getSource() == seleccionarCifradoButton) {
            // Crear un cuadro de dialogo para seleccionar el archivo
            JFileChooser fc = new JFileChooser();
            int result = fc.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                // Actualizar la ruta del archivo seleccionado
                File archivo = fc.getSelectedFile();
                rutaCifradoTextField.setText(archivo.getAbsolutePath());
            }
        } else if (e.getSource() == cifrarButton) {
            //Ciframos el archivo
            try {
                SecretKey clave = cifrarFichero(rutaArchivoTextField.getText());
                guardarClavePrivada(clave, rutaCifradoTextField.getText());
                mensajeLabel.setText("Archivo cifrado correctamente");
            } catch (Exception ex) {
                mensajeLabel.setText("Error al cifrar el archivo");
            }
        } else if (e.getSource() == descifrarButton) {
            //Desciframos el fichero
            try {
                SecretKey clave = cargarClavePrivada(rutaCifradoTextField.getText());
                descifrarFichero(rutaArchivoTextField.getText(), clave, ruta.getText());
                mensajeLabel.setText("Archivo descifrado correctamente");
            } catch (Exception ex) {
                lblMensaje.setText("Error al descifrar el archivo");
            }
        }
    }

    //método que encripta el fichero que se pasa como parámetro
    //devuelve el valor de la clave privada utilizada en encriptación
    //El fichero encriptado lo deja en el archivo de nombre fichero.cifrado
    //en el mismo directorio
    private static SecretKey cifrarFichero(String file) throws NoSuchAlgorithmException,
            NoSuchPaddingException, FileNotFoundException, IOException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException {

        FileInputStream fentrada = null; //fichero de entrada
        FileOutputStream fsalida = null; //fichero de salida
        int bytesLeidos;

        //1. Crear e inicializar clave
        System.out.println("1.-Genera clave DES");

        // crea un objeto para generar la clave usando algoritmo Triple DES (112 bits longitud clave)
        KeyGenerator keyGen = KeyGenerator.getInstance("DESede");

        // Se podría haber usado DES de esta forma (pero tiene menos seguridad => 56 bits longitud clave)
        // Cipher cifrador = Cipher.getInstance("DES");
        // se indica el tamaño de la clave
        keyGen.init(112);

        // genera la clave privada
        SecretKey clave = keyGen.generateKey();

        System.out.println("Clave");
        // muestra la clave
        mostrarBytes(clave.getEncoded());
        System.out.println();

        // Se Crea el objeto Cipher para cifrar, utilizando el algoritmo Triple DES
        Cipher cifrador = Cipher.getInstance("DESede");

        // Se inicializa el cifrador en modo CIFRADO o ENCRIPTACIÓN
        cifrador.init(Cipher.ENCRYPT_MODE, clave);
        System.out.println("2.- Cifrar con Triple DES el fichero: " + file
                + ", y dejar resultado en " + file + ".cifrado");

        //declaración  de objetos
        byte[] buffer = new byte[1000]; //array de bytes para leer del fichero
        byte[] bufferCifrado;  // vector con los bytes cifrados del fichero

        fentrada = new FileInputStream(file); //objeto fichero de entrada
        fsalida = new FileOutputStream(file + ".cifrado"); //fichero de salida

        //lee el fichero de 1k en 1k y pasa los fragmentos leidos al cifrador
        bytesLeidos = fentrada.read(buffer, 0, 1000);

        //mientras no se llegue al final del fichero de entrada
        while (bytesLeidos != -1) {
            // pasa texto claro al cifrador y lo cifra, asignándolo a bufferCifrado
            bufferCifrado = cifrador.update(buffer, 0, bytesLeidos);

            // Graba el texto cifrado en fichero de salida
            fsalida.write(bufferCifrado);

            bytesLeidos = fentrada.read(buffer, 0, 1000);
        }

        // Completa el cifrado
        bufferCifrado = cifrador.doFinal();

        // Graba el final del texto cifrado, si lo hay
        fsalida.write(bufferCifrado);

        //Cierra ficheros
        fentrada.close();
        fsalida.close();

        return clave;
    }

    //método que desencripta el fichero pasado como primer parámetro file1
    //pasándole también la clave privada que necesita para desencriptar, key
    //y deja el fichero desencriptado en el tercer parámetro file2
    private static void descifrarFichero(String file1, SecretKey key, String file2) throws NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        FileInputStream fe = null; //fichero de entrada
        FileOutputStream fs = null; //fichero de salida
        int bytesLeidos;

        // escoger como algoritmo para descifrar el Triple DES
        Cipher cifrador = Cipher.getInstance("DESede");

        //3.- Poner cifrador en modo DESCIFRADO o DESENCRIPTACIÓN
        cifrador.init(Cipher.DECRYPT_MODE, key);
        System.out.println("3.- Descifrar con Triple DES el fichero: " + file1 + ", y dejar en  " + file2);

        fe = new FileInputStream(file1);
        fs = new FileOutputStream(file2);
        byte[] bufferClaro;
        byte[] buffer = new byte[1000]; //array de bytes

        // lee el fichero de 1k en 1k y pasa los fragmentos leidos al cifrador
        bytesLeidos = fe.read(buffer, 0, 1000);

        // mientras no se llegue al final del fichero EOF
        while (bytesLeidos != -1) {
            //pasa texto cifrado al cifrador y lo descifra, asignándolo a bufferClaro
            bufferClaro = cifrador.update(buffer, 0, bytesLeidos);

            //Graba el texto claro en fichero
            fs.write(bufferClaro);

            bytesLeidos = fe.read(buffer, 0, 1000);
        }

        //Completa el descifrado
        bufferClaro = cifrador.doFinal();

        //Graba el final del texto claro, si lo hay
        fs.write(bufferClaro);

        //cierra archivos
        fe.close();
        fs.close();
    }

    //método que muestra bytes
    public static void mostrarBytes(byte[] buffer) {
        System.out.write(buffer, 0, buffer.length);
    }

    /*
     * Guarda la clave privada en un fichero
     * @param clave
     * @param text
     */
    private void guardarClavePrivada(SecretKey clave, String nombreArchivo) {
        try {
            // Creamos un objeto FileOutputStream para escribir en el archivo
            FileOutputStream archivo = new FileOutputStream(nombreArchivo);
            // Creamos un objeto ObjectOutputStream para escribir la clave en el archivo
            ObjectOutputStream objeto = new ObjectOutputStream(archivo);
            // Escribimos la clave en el archivo
            objeto.writeObject(clave);
            // Cerramos ambos objetos
            objeto.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static SecretKey cargarClavePrivada(String file) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        SecretKey clave = (SecretKey) ois.readObject();
        ois.close();
        fis.close();
        return clave;
    }

}
