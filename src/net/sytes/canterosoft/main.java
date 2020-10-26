package net.sytes.canterosoft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class main extends Component {

    public String url;
    private final String USER_AGENT = "Mozilla/5.0";
    private JsonObject ObjetoPrincipal;

    public static void main(String[] args){
        String url = "http://10.54.118.53:3000/logger/add/";
        //String url = "\\\\10.54.118.53\\C$\\Vigia\\Logs\\logs.txt";

        main d = new main();
        try {
            d.ejecutarLogs(url,"Param1","Prueba desde jason Papa","otro texto",0,0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ejecutarLogs(String url, String param1, String param2, String param3,Integer servidor_txt, Integer conVistaLogs) throws IOException {

        if (servidor_txt == 1){
            if (conVistaLogs == 1) {
                JOptionPane.showMessageDialog(null, "url: " + url);
                JOptionPane.showMessageDialog(null, "Param1: " + param1);
                JOptionPane.showMessageDialog(null, "param2: " + param2);
                JOptionPane.showMessageDialog(null, "param3: " + param3);
            }

            JFileChooser seleccion = new JFileChooser(); // Se crea la instancia para colocar parametros de interes
            seleccion.setDialogTitle("GUARDAR");    // Titulo de la ventana emergente
            seleccion.setDialogType(JFileChooser.SAVE_DIALOG); // El tipo de JFileChooser que vamos a usar
            seleccion.setFont(new java.awt.Font("Lucida Handwriting", 0, 18)); // (opcional) Cambiando el tipo de letra
            //seleccion.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); //(opcional) Estableciendo que solo me muestre los directorios.
            FileNameExtensionFilter imgFilter = new FileNameExtensionFilter("Archivos de Texto", "txt");
            seleccion.setFileFilter(imgFilter);

            int respuesta = seleccion.showSaveDialog(this); // Se apertura la ventana.
            switch(respuesta){ // segun la opcion del usuario se ejecutan los algoritmos de interes
                case JFileChooser.APPROVE_OPTION:
                    String archivo = seleccion.getSelectedFile().getName();
                    String ruta = String.valueOf(seleccion.getCurrentDirectory());
                    guardarFichero(archivo,ruta, param2);
                    break;
                case JFileChooser.CANCEL_OPTION:
                    System.out.println("Cancelado");
                    break;
                default :
                    System.out.println("Error");
                    break;
            }

        }else {
            ManejadorJson(url,param1,param2,param3);
        }
    }


    public void guardarFichero(String archivo, String ruta, String cadena) {

        System.out.println(archivo);
        System.out.println(ruta);
        System.out.println(cadena);

        try {
            String rutaTxt = ruta + "\\" + archivo + ".txt";
            System.out.println(rutaTxt);
            String contenido = cadena;

            File file = new File(rutaTxt);

            // Si el archivo no existe es creado
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(contenido);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void ManejadorJson(String url, String param1, String param2, String param3) {
        String informacion = "{pre-envio}";

        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost request = new HttpPost(url);
            StringEntity params = new StringEntity(devuelveJson(param1,param2,param3), ContentType.APPLICATION_JSON);
            request.setEntity(params);

            HttpResponse response = httpClient.execute(request);

            HttpEntity responseEntity = response.getEntity();
            informacion = "{post-envio}";
            if(responseEntity!=null) {
                informacion = EntityUtils.toString(responseEntity);
            }
            EntityUtils.consume(responseEntity);
            ((Closeable) response).close();
        } catch (Exception ex) {
            informacion = "{error-envio post sin proxy:" + ex.getMessage() + "}";
            ex.printStackTrace();
        }
    }

    public String devuelveJson(String param1, String param2, String param3) {
        ObjetoPrincipal = new JsonObject();
        ObjetoPrincipal.addProperty("clave",param1);
        ObjetoPrincipal.addProperty("valor",param2);
        ObjetoPrincipal.addProperty("opcional",param3);

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        return gson.toJson(ObjetoPrincipal);
    }

}
