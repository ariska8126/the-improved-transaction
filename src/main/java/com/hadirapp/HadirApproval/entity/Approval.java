/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadirapp.HadirApproval.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author herli
 */
@Entity
@Table(name = "approval")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Approval.findAll", query = "SELECT a FROM Approval a")
    , @NamedQuery(name = "Approval.findByApprovalId", query = "SELECT a FROM Approval a WHERE a.approvalId = :approvalId")
    , @NamedQuery(name = "Approval.findByApprovalDate", query = "SELECT a FROM Approval a WHERE a.approvalDate = :approvalDate")
    , @NamedQuery(name = "Approval.findByApprovalDateUpdate", query = "SELECT a FROM Approval a WHERE a.approvalDateUpdate = :approvalDateUpdate")})
public class Approval implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "approval_id")
    private String approvalId;
    @Basic(optional = false)
    @Column(name = "approval_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date approvalDate;
    @Basic(optional = false)
    @Column(name = "approval_date_update")
    @Temporal(TemporalType.TIMESTAMP)
    private Date approvalDateUpdate;
    @Basic(optional = false)
    @Lob
    @Column(name = "approval_remark")
    private String approvalRemark;
    @JoinColumn(name = "request_id", referencedColumnName = "request_id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Request requestId;
    @JoinColumn(name = "approval_status_id", referencedColumnName = "approval_status_id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ApprovalStatus approvalStatusId;
    @JoinColumn(name = "approval_requester_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Users approvalRequesterId;
    @JoinColumn(name = "approval_approver_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Users approvalApproverId;
    @OneToMany(mappedBy = "approvalId", fetch = FetchType.LAZY)
    private List<Attendance> attendanceList;

    public Approval() {
    }

    public Approval(String approvalId) {
        this.approvalId = approvalId;
    }

    public Approval(String approvalId, Date approvalDate, Date approvalDateUpdate, String approvalRemark) {
        this.approvalId = approvalId;
        this.approvalDate = approvalDate;
        this.approvalDateUpdate = approvalDateUpdate;
        this.approvalRemark = approvalRemark;
    }

    public String getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(String approvalId) {
        this.approvalId = approvalId;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public Date getApprovalDateUpdate() {
        return approvalDateUpdate;
    }

    public void setApprovalDateUpdate(Date approvalDateUpdate) {
        this.approvalDateUpdate = approvalDateUpdate;
    }

    public String getApprovalRemark() {
        return approvalRemark;
    }

    public void setApprovalRemark(String approvalRemark) {
        this.approvalRemark = approvalRemark;
    }

    public Request getRequestId() {
        return requestId;
    }

    public void setRequestId(Request requestId) {
        this.requestId = requestId;
    }

    public ApprovalStatus getApprovalStatusId() {
        return approvalStatusId;
    }

    public void setApprovalStatusId(ApprovalStatus approvalStatusId) {
        this.approvalStatusId = approvalStatusId;
    }

    public Users getApprovalRequesterId() {
        return approvalRequesterId;
    }

    public void setApprovalRequesterId(Users approvalRequesterId) {
        this.approvalRequesterId = approvalRequesterId;
    }

    public Users getApprovalApproverId() {
        return approvalApproverId;
    }

    public void setApprovalApproverId(Users approvalApproverId) {
        this.approvalApproverId = approvalApproverId;
    }

    @XmlTransient
    public List<Attendance> getAttendanceList() {
        return attendanceList;
    }

    public void setAttendanceList(List<Attendance> attendanceList) {
        this.attendanceList = attendanceList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (approvalId != null ? approvalId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Approval)) {
            return false;
        }
        Approval other = (Approval) object;
        if ((this.approvalId == null && other.approvalId != null) || (this.approvalId != null && !this.approvalId.equals(other.approvalId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hadirapp.HadirApproval.entity.Approval[ approvalId=" + approvalId + " ]";
    }
    
}
