package org.shekinah.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import org.shekinah.domain.security.UserEntity;

@Entity(name="Creneau")
@Table(name="\"CRENEAU\"")
@XmlRootElement
public class CreneauEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name="\"debut\"")
    @Temporal(TemporalType.DATE)
    private Date debut;

    @Column(name="\"fin\"")
    @Temporal(TemporalType.DATE)
    private Date fin;

    @ManyToOne(optional=true)
    @JoinColumn(name = "MEDECIN_ID", referencedColumnName = "ID")
    private UserEntity medecin;

    public Date getDebut() {
        return this.debut;
    }

    public void setDebut(Date debut) {
        this.debut = debut;
    }

    public Date getFin() {
        return this.fin;
    }

    public void setFin(Date fin) {
        this.fin = fin;
    }

    public UserEntity getMedecin() {
        return this.medecin;
    }

    public void setMedecin(UserEntity user) {
        this.medecin = user;
    }

}
