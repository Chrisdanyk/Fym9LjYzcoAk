package org.shekinah.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity(name="Medicament")
@Table(name="\"MEDICAMENT\"")
@XmlRootElement
public class MedicamentEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private MedicamentImage image;
    
    @Size(max = 50)
    @Column(length = 50, name="\"designation\"")
    private String designation;

    @ElementCollection(targetClass = MedicamentType.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "MEDICAMENT_TYPE", joinColumns = { @JoinColumn(name = "MEDICAMENT_ID") })
    @Column(name = "\"TYPE\"")
    private Set<MedicamentType> type;

    @Column(name="\"dateFabrication\"")
    @Temporal(TemporalType.DATE)
    private Date dateFabrication;

    @Column(name="\"dateExpiration\"")
    @Temporal(TemporalType.DATE)
    private Date dateExpiration;

    @Digits(integer = 5,  fraction = 2)
    @Column(precision = 7, scale = 2, name="\"prix\"")
    private BigDecimal prix;

    @Column(name="\"stock\"")
    @Digits(integer = 4, fraction = 0)
    private Integer stock;

    @ManyToOne(optional=true)
    @JoinColumn(name = "FOURNISSEURS_ID", referencedColumnName = "ID")
    private FournisseurEntity fournisseurs;

    @ManyToOne(optional=true)
    @JoinColumn(name = "FAMILLE_ID", referencedColumnName = "ID")
    private FamilleEntity famille;

    @XmlTransient
    public MedicamentImage getImage() {
        return image;
    }

    public void setImage(MedicamentImage image) {
        this.image = image;
    }
    
    public String getDesignation() {
        return this.designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Set<MedicamentType> getType() {
        return type;
    }

    public void setType(Set<MedicamentType> type) {
        this.type = type;
    }

    public Date getDateFabrication() {
        return this.dateFabrication;
    }

    public void setDateFabrication(Date dateFabrication) {
        this.dateFabrication = dateFabrication;
    }

    public Date getDateExpiration() {
        return this.dateExpiration;
    }

    public void setDateExpiration(Date dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public BigDecimal getPrix() {
        return this.prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public Integer getStock() {
        return this.stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public FournisseurEntity getFournisseurs() {
        return this.fournisseurs;
    }

    public void setFournisseurs(FournisseurEntity fournisseur) {
        this.fournisseurs = fournisseur;
    }

    public FamilleEntity getFamille() {
        return this.famille;
    }

    public void setFamille(FamilleEntity famille) {
        this.famille = famille;
    }

}
