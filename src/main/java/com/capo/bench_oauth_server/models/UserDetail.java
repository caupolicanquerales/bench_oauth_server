package com.capo.bench_oauth_server.models;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique= true)
	private String username;
	
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false, unique= true)
	private String email;

	@Column(name = "full_name")
	private String fullName;
	
	@Builder.Default
	@Column(nullable= false, columnDefinition= "BOOLEAN DEFAULT TRUE")
	private Boolean enable= Boolean.TRUE;
	
	@CreationTimestamp
    @Column(
        name = "created_at", 
        nullable = false, 
        updatable = false, 
        columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP"
    )
	private Instant createdAt;
	

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Roles> roles;
	
	@PrePersist
    protected void onCreate() {
        if (this.enable == null) {
            this.enable = true;
        }
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
