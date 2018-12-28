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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity(name="Patient")
@Table(name="\"PATIENT\"")
@XmlRootElement
public class PatientEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private PatientImage image;
    
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

    @ElementCollection(targetClass = PatientGenre.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "PATIENT_GENRE", joinColumns = { @JoinColumn(name = "PATIENT_ID") })
    @Column(name = "\"GENRE\"")
    private Set<PatientGenre> genre;

    @ElementCollection(targetClass = PatientStatutMarital.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "PATIENT_STATUTMARITAL", joinColumns = { @JoinColumn(name = "PATIENT_ID") })
    @Column(name = "\"STATUTMARITAL\"")
    private Set<PatientStatutMarital> statutMarital;

    @Size(max = 50)
    @Column(length = 50, name="\"telephone\"")
    private String telephone;

    @Size(max = 50)
    @Column(length = 50, name="\"adresse\"")
    private String adresse;

    @Size(max = 50)
    @Column(length = 50, name="\"email\"")
    private String email;

    @ElementCollection(targetClass = PatientGroupeSanguin.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "PATIENT_GROUPESANGUIN", joinColumns = { @JoinColumn(name = "PATIENT_ID") })
    @Column(name = "\"GROUPESANGUIN\"")
    private Set<PatientGroupeSanguin> groupeSanguin;

    @ManyToOne(optional=true)
    @JoinColumn(name = "HOPITAL_ID", referencedColumnName = "ID")
    private HopitalEntity hopital;

    @XmlTransient
    public PatientImage getImage() {
        return image;
    }

    public void setImage(PatientImage image) {
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

    public Set<PatientGenre> getGenre() {
        return genre;
    }

    public void setGenre(Set<PatientGenre> genre) {
        this.genre = genre;
    }

    public Set<PatientStatutMarital> getStatutMarital() {
        return statutMarital;
    }

    public void setStatutMarital(Set<PatientStatutMarital> statutMarital) {
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

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<PatientGroupeSanguin> getGroupeSanguin() {
        return groupeSanguin;
    }

    public void setGroupeSanguin(Set<PatientGroupeSanguin> groupeSanguin) {
        this.groupeSanguin = groupeSanguin;
    }

    public HopitalEntity getHopital() {
        return this.hopital;
    }

    public void setHopital(HopitalEntity hopital) {
        this.hopital = hopital;
    }

}
