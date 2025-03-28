package LogicaTienda.Model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NonNull
public class Domicilio {
    private String idDomicilio;
    private String nombreDomicilio;
    private String descripcionDomicilio;
    private String estadoDomicilio;
}
