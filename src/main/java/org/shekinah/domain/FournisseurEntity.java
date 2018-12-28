package org.shekinah.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity(name="Fournisseur")
@Table(name="\"FOURNISSEUR\"")
@XmlRootElement
public class FournisseurEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private FournisseurImage image;
    
    @Size(max = 50)
    @Column(length = 50, name="\"nom\"")
    private String nom;

    @Size(max = 50)
    @Column(length = 50, name="\"adresse\"")
    private String adresse;

    @Size(max = 50)
    @Column(length = 50, name="\"telephone\"")
    private String telephone;

    @Size(max = 50)
    @Column(length = 50, name="\"mail\"")
    private String mail;

    @XmlTransient
    public FournisseurImage getImage() {
        return image;
    }

    public void setImage(FournisseurImage image) {
        this.image = image;
    }
    
    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return this.adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMail() {
        return this.mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

}
