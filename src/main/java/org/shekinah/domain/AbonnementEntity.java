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

@Entity(name="Abonnement")
@Table(name="\"ABONNEMENT\"")
@XmlRootElement
public class AbonnementEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name="\"debut\"")
    @Temporal(TemporalType.DATE)
    private Date debut;

    @Column(name="\"fin\"")
    @Temporal(TemporalType.DATE)
    private Date fin;

    @ManyToOne(optional=true)
    @JoinColumn(name = "PATIENT_ID", referencedColumnName = "ID")
    private PatientEntity patient;

    @ManyToOne(optional=true)
    @JoinColumn(name = "SOCIETE_ID", referencedColumnName = "ID")
    private SocieteAbonnementEntity societe;

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

    public PatientEntity getPatient() {
        return this.patient;
    }

    public void setPatient(PatientEntity patient) {
        this.patient = patient;
    }

    public SocieteAbonnementEntity getSociete() {
        return this.societe;
    }

    public void setSociete(SocieteAbonnementEntity societeAbonnement) {
        this.societe = societeAbonnement;
    }

}
