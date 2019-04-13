package com.bsep.PKI.model;

import javax.persistence.*;

@Entity
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    private Long id;

    @Column(name="name", unique=true, nullable=false)
    private String name;

    @Column(name="url", unique=false, nullable=false)
    private String url;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private IntermediateCA intermediateCA;

    public Application() {

    }

    public Application(String name, String url, IntermediateCA intermediateCA) {
        this.name = name;
        this.url = url;
        this.intermediateCA = intermediateCA;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public IntermediateCA getIntermediateCA() {
        return intermediateCA;
    }

    public void setIntermediateCA(IntermediateCA intermediateCA) {
        this.intermediateCA = intermediateCA;
    }
}
