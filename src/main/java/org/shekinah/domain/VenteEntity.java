package org.shekinah.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.shekinah.domain.security.UserEntity;

@Entity(name="Vente")
@Table(name="\"VENTE\"")
@XmlRootElement
public class VenteEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne(optional=true)
    @JoinColumn(name = "PHARMACIEN_ID", referencedColumnName = "ID")
    private UserEntity pharmacien;

    @ManyToOne(optional=true)
    @JoinColumn(name = "MEDICAMENT_ID", referencedColumnName = "ID")
    private MedicamentEntity medicament;

    @Column(name="\"date\"")
    @Temporal(TemporalType.DATE)
    private Date date;

    @Digits(integer = 5,  fraction = 2)
    @Column(precision = 7, scale = 2, name="\"quantite\"")
    private BigDecimal quantite;

    @Size(max = 50)
    @Column(length = 50, name="\"client\"")
    private String client;

    @Column(name = "ENTRY_CREATED_BY")
    private String createdBy;

    @Column(name = "ENTRY_CREATED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "ENTRY_MODIFIED_BY")
    private String modifiedBy;

    @Column(name = "ENTRY_MODIFIED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt;
    
    public UserEntity getPharmacien() {
        return this.pharmacien;
    }

    public void setPharmacien(UserEntity user) {
        this.pharmacien = user;
    }

    public MedicamentEntity getMedicament() {
        return this.medicament;
    }

    public void setMedicament(MedicamentEntity medicament) {
        this.medicament = medicament;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getQuantite() {
        return this.quantite;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public String getClient() {
        return this.client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }
    
    public void updateAuditInformation(String username) {
        if (this.getId() != null) {
            modifiedAt = new Date();
            modifiedBy = username;
        } else {
            createdAt = new Date();
            modifiedAt = createdAt;
            createdBy = username;
            modifiedBy = createdBy;
        }
    }
    
}
