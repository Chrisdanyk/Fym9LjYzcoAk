package org.shekinah.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity(name="SocieteAbonnement")
@Table(name="\"SOCIETEABONNEMENT\"")
@XmlRootElement
public class SocieteAbonnementEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 50)
    @Column(length = 50, name="\"designation\"")
    private String designation;

    @Size(max = 50)
    @Column(length = 50, name="\"telephone\"")
    private String telephone;

    @Size(max = 50)
    @Column(length = 50, name="\"email\"")
    private String email;

    @Size(max = 50)
    @Column(length = 50, name="\"adresse\"")
    private String adresse;

    public String getDesignation() {
        return this.designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresse() {
        return this.adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

}
