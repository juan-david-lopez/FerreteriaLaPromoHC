package LogicaTienda.Model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Domicilio {
    private int idDomicilio;
    private String nombreDomicilio;
    private String descripcionDomicilio;
    private String estadoDomicilio;
}
