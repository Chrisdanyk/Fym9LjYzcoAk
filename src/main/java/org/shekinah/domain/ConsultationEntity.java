package org.shekinah.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.shekinah.domain.security.UserEntity;

@Entity(name="Consultation")
@Table(name="\"CONSULTATION\"")
@XmlRootElement
public class ConsultationEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne(optional=true)
    @JoinColumn(name = "PATIENT_ID", referencedColumnName = "ID")
    private PatientEntity patient;

    @Size(max = 500)
    @Column(length = 500, name="\"diagnostique\"")
    private String diagnostique;

    @Column(name="\"date\"")
    @Temporal(TemporalType.DATE)
    private Date date;

    @Size(max = 50)
    @Column(length = 50, name="\"tensionArterielle\"")
    private String tensionArterielle;

    @Digits(integer = 5,  fraction = 2)
    @Column(precision = 7, scale = 2, name="\"temperature\"")
    private BigDecimal temperature;

    @Digits(integer = 5,  fraction = 2)
    @Column(precision = 7, scale = 2, name="\"poids\"")
    private BigDecimal poids;

    @Size(max = 500)
    @Column(length = 500, name="\"plainte\"")
    private String plainte;

    @ManyToOne(optional=true)
    @JoinColumn(name = "EXAMENS_ID", referencedColumnName = "ID")
    private ExamenEntity examens;

    @Size(max = 500)
    @Column(length = 500, name="\"prescription\"")
    private String prescription;

    @Size(max = 500)
    @Column(length = 500, name="\"resultatsExamen\"")
    private String resultatsExamen;

    @Column(name="\"pouls\"")
    @Digits(integer = 4, fraction = 0)
    private Integer pouls;

    @Column(name="\"frequenceRespiratoire\"")
    @Digits(integer = 4, fraction = 0)
    private Integer frequenceRespiratoire;

    @OneToOne(optional=true, cascade=CascadeType.DETACH)
    @JoinColumn(name="ORDONNANCE_ID", nullable=true)
    private OrdonnanceEntity ordonnance;

    @ManyToOne(optional=true)
    @JoinColumn(name = "MEDECIN_ID", referencedColumnName = "ID")
    private UserEntity medecin;

    @ManyToOne(optional=true)
    @JoinColumn(name = "INFIRMIER_ID", referencedColumnName = "ID")
    private UserEntity infirmier;

    @ManyToOne(optional=true)
    @JoinColumn(name = "LABORANTIN_ID", referencedColumnName = "ID")
    private UserEntity laborantin;

    public PatientEntity getPatient() {
        return this.patient;
    }

    public void setPatient(PatientEntity patient) {
        this.patient = patient;
    }

    public String getDiagnostique() {
        return this.diagnostique;
    }

    public void setDiagnostique(String diagnostique) {
        this.diagnostique = diagnostique;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTensionArterielle() {
        return this.tensionArterielle;
    }

    public void setTensionArterielle(String tensionArterielle) {
        this.tensionArterielle = tensionArterielle;
    }

    public BigDecimal getTemperature() {
        return this.temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public BigDecimal getPoids() {
        return this.poids;
    }

    public void setPoids(BigDecimal poids) {
        this.poids = poids;
    }

    public String getPlainte() {
        return this.plainte;
    }

    public void setPlainte(String plainte) {
        this.plainte = plainte;
    }

    public ExamenEntity getExamens() {
        return this.examens;
    }

    public void setExamens(ExamenEntity examen) {
        this.examens = examen;
    }

    public String getPrescription() {
        return this.prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getResultatsExamen() {
        return this.resultatsExamen;
    }

    public void setResultatsExamen(String resultatsExamen) {
        this.resultatsExamen = resultatsExamen;
    }

    public Integer getPouls() {
        return this.pouls;
    }

    public void setPouls(Integer pouls) {
        this.pouls = pouls;
    }

    public Integer getFrequenceRespiratoire() {
        return this.frequenceRespiratoire;
    }

    public void setFrequenceRespiratoire(Integer frequenceRespiratoire) {
        this.frequenceRespiratoire = frequenceRespiratoire;
    }

    public OrdonnanceEntity getOrdonnance() {
        return this.ordonnance;
    }

    public void setOrdonnance(OrdonnanceEntity ordonnance) {
        this.ordonnance = ordonnance;
    }

    public UserEntity getMedecin() {
        return this.medecin;
    }

    public void setMedecin(UserEntity user) {
        this.medecin = user;
    }

    public UserEntity getInfirmier() {
        return this.infirmier;
    }

    public void setInfirmier(UserEntity user) {
        this.infirmier = user;
    }

    public UserEntity getLaborantin() {
        return this.laborantin;
    }

    public void setLaborantin(UserEntity user) {
        this.laborantin = user;
    }

}
