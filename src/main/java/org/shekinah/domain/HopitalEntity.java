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

@Entity(name="Hopital")
@Table(name="\"HOPITAL\"")
@XmlRootElement
public class HopitalEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private HopitalImage image;
    
    @Size(max = 50)
    @Column(length = 50, name="\"designation\"")
    private String designation;

    @Size(max = 50)
    @Column(length = 50, name="\"adresse\"")
    private String adresse;

    @Size(max = 50)
    @Column(length = 50, name="\"telephone\"")
    private String telephone;

    @Size(max = 50)
    @Column(length = 50, name="\"email\"")
    private String email;

    @XmlTransient
    public HopitalImage getImage() {
        return image;
    }

    public void setImage(HopitalImage image) {
        this.image = image;
    }
    
    public String getDesignation() {
        return this.designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
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

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
