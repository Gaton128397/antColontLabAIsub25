import java.util.List;
public class CreadorCiudades {
    private static CreadorCiudades instancia = null;
    private List<Ciudad> ciudades;
    private double[][] distanciasEntreCiudades;

    private CreadorCiudades(){
        construirCiudades();
    }
    public static CreadorCiudades getInstancia(){
        if(instancia == null){
            instancia = new CreadorCiudades();
        }
        return instancia;
    }

    public void construirCiudades(){
        this.ciudades = ManejoDatos.cargarDatos();
        if(ciudades != null && !ciudades.isEmpty()){
            int n = ciudades.size();
            distanciasEntreCiudades = new double[n][n];
            for(int i = 0; i < n; i++){
                for(int j = 0; j < n; j++){
                    if(i != j){
                        distanciasEntreCiudades[i][j] = ciudades.get(i).distancia(ciudades.get(j));
                    } else {
                        distanciasEntreCiudades[i][j] = 0;
                    }
                }
            }
        } else {
            System.err.println("Error: No se pudieron cargar las ciudades.");
        }
    }
    public List<Ciudad> getCiudades(){
        return ciudades;
    }
    public double[][] getDistanciasEntreCiudades(){
        return distanciasEntreCiudades;
    }
    public int getNumeroCiudades(){
        if (ciudades != null) {
            return ciudades.size();
        }else {
            return 0;
        }
    }
    

}
