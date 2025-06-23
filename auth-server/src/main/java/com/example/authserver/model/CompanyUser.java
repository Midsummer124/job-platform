package com.example.authserver.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity // Declares this class as a JPA entity
@Table(name = "company_user") // Maps this entity to the "user" table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUser {
    @Id // Specifies the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures the primary key generation strategy (AUTO_INCREMENT for MySQL)
    private Long id; // Use Long for BigInt AUTO_INCREMENT

    @Column(nullable = false, unique = true, length = 50)
    private Long user_id;

    @Column(nullable = false, length = 50)
    private Long company_id;

    @Column(length = 50)
    private String position;

    @Column(length = 50)
    private String department;

    public CompanyUser(Long user_id, Long company_id, String position, String department){
        this.user_id = user_id;
        this.company_id = company_id;
        this.position = position;
        this.department = department;
    }
}
