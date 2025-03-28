package LogicaTienda;


import LogicaTienda.Model.Factura;

import static LogicaTienda.TipoFactura.Electronica;
import static LogicaTienda.TipoFactura.Impresa;

public class LogicaFacturaElectronicaeImpr {
    public String TipoFactura(Factura factura){
        if (!factura.getTipoFactura().equals(""+Electronica) || (!factura.getTipoFactura().equals(""+Impresa))){
            throw new IllegalArgumentException("El tipo de factura no es correcto.");
        }
        return switch (factura.getTipoFactura()) {
            case "Electronica" -> "Factura Electronica";
            case "Impresa" -> "Factura Impresa";
            default -> "";
        };
    }
}
