package org.shekinah.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity(name="Ordonnance")
@Table(name="\"ORDONNANCE\"")
@XmlRootElement
public class OrdonnanceEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name="\"date\"")
    @Temporal(TemporalType.DATE)
    private Date date;

    @Size(max = 2000)
    @Column(length = 2000, name="\"details\"")
    private String details;

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDetails() {
        return this.details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

}
