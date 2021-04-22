package com.finacial.model;

import javax.persistence.*;

@Entity
@Table(name = "STATUS")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "STATUS_ID")
    private Long statusId;

    @Column(name = "PAR_NAME")
    private String parName;

    @Column(name = "PAR_TYPE")
    private String parType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getParName() {
        return parName;
    }

    public void setParName(String parName) {
        this.parName = parName;
    }

    public String getParType() {
        return parType;
    }

    public void setParType(String parType) {
        this.parType = parType;
    }

    public Status() {
    }

    public Status(Long id, Long statusId, String parName, String parType) {
        this.id = id;
        this.statusId = statusId;
        this.parName = parName;
        this.parType = parType;
    }
}
