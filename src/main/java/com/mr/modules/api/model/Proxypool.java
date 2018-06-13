package com.mr.modules.api.model;

import com.mr.common.base.model.BaseEntity;
import javax.persistence.*;

/**
 * @uther zjxu 18-4-10.
 */
public class Proxypool extends BaseEntity {
    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;*/

    private String ipaddress;

    private String ipport;

    private String serveraddress;

    private String iptype;

    private String ipspeed;

    /**
     * @return id
     */
    /*public Long getId() {
        return id;
    }

    *//**
     * @param id
     *//*
    public void setId(Long id) {
        this.id = id;
    }*/

    /**
     * @return IPAddress
     */
    public String getIpaddress() {
        return ipaddress;
    }

    /**
     * @param ipaddress
     */
    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    /**
     * @return IPPort
     */
    public String getIpport() {
        return ipport;
    }

    /**
     * @param ipport
     */
    public void setIpport(String ipport) {
        this.ipport = ipport;
    }

    /**
     * @return serverAddress
     */
    public String getServeraddress() {
        return serveraddress;
    }

    /**
     * @param serveraddress
     */
    public void setServeraddress(String serveraddress) {
        this.serveraddress = serveraddress;
    }

    /**
     * @return IPType
     */
    public String getIptype() {
        return iptype;
    }

    /**
     * @param iptype
     */
    public void setIptype(String iptype) {
        this.iptype = iptype;
    }

    /**
     * @return IPSpeed
     */
    public String getIpspeed() {
        return ipspeed;
    }

    /**
     * @param ipspeed
     */
    public void setIpspeed(String ipspeed) {
        this.ipspeed = ipspeed;
    }
}