import java.io.*;
import java.util.*;
public class ManejoDatos {
    private static final String archivoSerializado = "ciudades.ser";
    private static final String datoOriginal = "a280.txt";
    //serializar la lista de ciudades
    public static List<Ciudad> cargarDatos() {
        List<Ciudad> ciudades = deserializarDatos(); // Intentar cargar datos serializados
        if (ciudades == null) { //si no estan serializados leer el archivo original
            ciudades = leerArchivoOriginal();
            if (ciudades != null && !ciudades.isEmpty()) {
                serializarDatos(ciudades); // Serializar para futuros usos
            }
        }
        return ciudades;
    }
    private static List<Ciudad> leerArchivoOriginal() {
        List<Ciudad> ciudades = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(datoOriginal))) {
            String line;
            boolean leyendoDatos = false;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("NODE_COORD_SECTION")) {
                    leyendoDatos = true;
                    continue;
                }
                if (leyendoDatos) {
                    if (line.startsWith("EOF"))
                        break; // Fin de los datos
                    String[] partes = line.trim().split("\\s+");
                    if (partes.length >= 3) {
                        int id = Integer.parseInt(partes[0]);
                        double x = Double.parseDouble(partes[1]);
                        double y = Double.parseDouble(partes[2]);
                        ciudades.add(new Ciudad(id, x, y));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ciudades;
    }
    private static void serializarDatos(List<Ciudad> ciudades) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivoSerializado))) {
            oos.writeObject(ciudades);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static List<Ciudad> deserializarDatos() {
        List<Ciudad> ciudades = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivoSerializado))) {
            ciudades = (List<Ciudad>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ciudades;
    }
}
