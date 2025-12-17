package com.example.healthhub.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "enrollment")
public class Enrollment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "status")
    private String status;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "amount_paid")
    private Double amountPaid;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public Enrollment() {
        this.enrollmentDate = LocalDate.now();
        this.status = "Active";
        this.paymentStatus = "Pending";
    }

    public Enrollment(Member member, Program program) {
        this();
        this.member = member;
        this.program = program;
        this.amountPaid = program.getPrice();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
    
    public Program getProgram() { return program; }
    public void setProgram(Program program) { this.program = program; }
    
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public Double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(Double amountPaid) { this.amountPaid = amountPaid; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}