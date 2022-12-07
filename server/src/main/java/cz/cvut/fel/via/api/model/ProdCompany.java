package cz.cvut.fel.via.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProdCompany {
    private String name;
    private int id;
    private String logo_path;
    private String origin_country;
}
