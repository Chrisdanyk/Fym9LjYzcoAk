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

import org.shekinah.domain.security.UserEntity;

@Entity(name="Labo")
@Table(name="\"LABO\"")
@XmlRootElement
public class LaboEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne(optional=true)
    @JoinColumn(name = "EXAMEN_ID", referencedColumnName = "ID")
    private ExamenEntity examen;

    @ManyToOne(optional=true)
    @JoinColumn(name = "PATIENT_ID", referencedColumnName = "ID")
    private PatientEntity patient;

    @Size(max = 50)
    @Column(length = 50, name="\"resultat\"")
    private String resultat;

    @ManyToOne(optional=true)
    @JoinColumn(name = "LABORANTIN_ID", referencedColumnName = "ID")
    private UserEntity laborantin;

    @Column(name="\"date\"")
    @Temporal(TemporalType.DATE)
    private Date date;

    public ExamenEntity getExamen() {
        return this.examen;
    }

    public void setExamen(ExamenEntity examen) {
        this.examen = examen;
    }

    public PatientEntity getPatient() {
        return this.patient;
    }

    public void setPatient(PatientEntity patient) {
        this.patient = patient;
    }

    public String getResultat() {
        return this.resultat;
    }

    public void setResultat(String resultat) {
        this.resultat = resultat;
    }

    public UserEntity getLaborantin() {
        return this.laborantin;
    }

    public void setLaborantin(UserEntity user) {
        this.laborantin = user;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
