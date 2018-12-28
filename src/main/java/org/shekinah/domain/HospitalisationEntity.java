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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity(name="Hospitalisation")
@Table(name="\"HOSPITALISATION\"")
@XmlRootElement
public class HospitalisationEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne(optional=true)
    @JoinColumn(name = "SALLE_ID", referencedColumnName = "ID")
    private SalleEntity salle;

    @ManyToOne(optional=true)
    @JoinColumn(name = "PATIENT_ID", referencedColumnName = "ID")
    private PatientEntity patient;

    @Column(name="\"dateEntree\"")
    @Temporal(TemporalType.DATE)
    private Date dateEntree;

    @Column(name="\"dateSortie\"")
    @Temporal(TemporalType.DATE)
    private Date dateSortie;

    @Size(max = 500)
    @Column(length = 500, name="\"observation\"")
    private String observation;

    public SalleEntity getSalle() {
        return this.salle;
    }

    public void setSalle(SalleEntity salle) {
        this.salle = salle;
    }

    public PatientEntity getPatient() {
        return this.patient;
    }

    public void setPatient(PatientEntity patient) {
        this.patient = patient;
    }

    public Date getDateEntree() {
        return this.dateEntree;
    }

    public void setDateEntree(Date dateEntree) {
        this.dateEntree = dateEntree;
    }

    public Date getDateSortie() {
        return this.dateSortie;
    }

    public void setDateSortie(Date dateSortie) {
        this.dateSortie = dateSortie;
    }

    public String getObservation() {
        return this.observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

}
