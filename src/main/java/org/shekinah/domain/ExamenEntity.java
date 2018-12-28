package org.shekinah.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity(name="Examen")
@Table(name="\"EXAMEN\"")
@XmlRootElement
public class ExamenEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 50)
    @Column(length = 50, name="\"designation\"")
    private String designation;

    @Digits(integer = 5,  fraction = 2)
    @Column(precision = 7, scale = 2, name="\"prix\"")
    private BigDecimal prix;

    @Size(max = 50)
    @Column(length = 50, name="\"type\"")
    private String type;

    public String getDesignation() {
        return this.designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public BigDecimal getPrix() {
        return this.prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
