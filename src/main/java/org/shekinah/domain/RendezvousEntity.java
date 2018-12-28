package org.shekinah.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity(name="Rendezvous")
@Table(name="\"RENDEZVOUS\"")
@XmlRootElement
public class RendezvousEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne(optional=true)
    @JoinColumn(name = "PATIENT_ID", referencedColumnName = "ID")
    private PatientEntity patient;

    @ManyToOne(optional=true)
    @JoinColumn(name = "CRENEAU_ID", referencedColumnName = "ID")
    private CreneauEntity creneau;

    public PatientEntity getPatient() {
        return this.patient;
    }

    public void setPatient(PatientEntity patient) {
        this.patient = patient;
    }

    public CreneauEntity getCreneau() {
        return this.creneau;
    }

    public void setCreneau(CreneauEntity creneau) {
        this.creneau = creneau;
    }

}
