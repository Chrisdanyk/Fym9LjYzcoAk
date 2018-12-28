package org.shekinah.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity(name="Salle")
@Table(name="\"SALLE\"")
@XmlRootElement
public class SalleEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private SalleImage image;
    
    @ManyToOne(optional=true)
    @JoinColumn(name = "PAVILLON_ID", referencedColumnName = "ID")
    private PavillonEntity pavillon;

    @ElementCollection(targetClass = SalleCategorie.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "SALLE_CATEGORIE", joinColumns = { @JoinColumn(name = "SALLE_ID") })
    @Column(name = "\"CATEGORIE\"")
    private Set<SalleCategorie> categorie;

    @Digits(integer = 5,  fraction = 2)
    @Column(precision = 7, scale = 2, name="\"prix\"")
    private BigDecimal prix;

    @Size(max = 50)
    @Column(length = 50, name="\"designation\"")
    private String designation;

    @XmlTransient
    public SalleImage getImage() {
        return image;
    }

    public void setImage(SalleImage image) {
        this.image = image;
    }
    
    public PavillonEntity getPavillon() {
        return this.pavillon;
    }

    public void setPavillon(PavillonEntity pavillon) {
        this.pavillon = pavillon;
    }

    public Set<SalleCategorie> getCategorie() {
        return categorie;
    }

    public void setCategorie(Set<SalleCategorie> categorie) {
        this.categorie = categorie;
    }

    public BigDecimal getPrix() {
        return this.prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public String getDesignation() {
        return this.designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

}
