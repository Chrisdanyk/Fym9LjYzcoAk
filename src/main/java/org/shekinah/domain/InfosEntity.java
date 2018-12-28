package org.shekinah.domain;

import java.io.Serializable;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity(name="Infos")
@Table(name="\"INFOS\"")
@XmlRootElement
public class InfosEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private InfosImage image;
    
    @Size(max = 50)
    @Column(length = 50, name="\"nom\"")
    private String nom;

    @Size(max = 50)
    @Column(length = 50, name="\"postnom\"")
    private String postnom;

    @Size(max = 50)
    @Column(length = 50, name="\"prenom\"")
    private String prenom;

    @Column(name="\"dateNaissance\"")
    @Temporal(TemporalType.DATE)
    private Date dateNaissance;

    @ElementCollection(targetClass = InfosGenre.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "INFOS_GENRE", joinColumns = { @JoinColumn(name = "INFOS_ID") })
    @Column(name = "\"GENRE\"")
    private Set<InfosGenre> genre;

    @ElementCollection(targetClass = InfosStatutMarital.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "INFOS_STATUTMARITAL", joinColumns = { @JoinColumn(name = "INFOS_ID") })
    @Column(name = "\"STATUTMARITAL\"")
    private Set<InfosStatutMarital> statutMarital;

    @Size(max = 50)
    @Column(length = 50, name="\"telephone\"")
    private String telephone;

    @Size(max = 50)
    @Column(length = 50, name="\"adresse\"")
    private String adresse;

    @XmlTransient
    public InfosImage getImage() {
        return image;
    }

    public void setImage(InfosImage image) {
        this.image = image;
    }
    
    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPostnom() {
        return this.postnom;
    }

    public void setPostnom(String postnom) {
        this.postnom = postnom;
    }

    public String getPrenom() {
        return this.prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Date getDateNaissance() {
        return this.dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Set<InfosGenre> getGenre() {
        return genre;
    }

    public void setGenre(Set<InfosGenre> genre) {
        this.genre = genre;
    }

    public Set<InfosStatutMarital> getStatutMarital() {
        return statutMarital;
    }

    public void setStatutMarital(Set<InfosStatutMarital> statutMarital) {
        this.statutMarital = statutMarital;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return this.adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

}
