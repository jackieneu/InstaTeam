package com.teamtreehouse.instateam.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 60)
    private String name;

    @NotNull
    @Size(min = 2, max = 240)
    private String description;

    private String status;

    @Column(updatable = false)
    private Date startDate = new Date();

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "PROJECT_ROLE",
            joinColumns = {@JoinColumn(name = "PROJECT_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLESNEEDED_ID")})
    private List<Role> rolesNeeded = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "PROJECT_COLLABORATOR",
            joinColumns = {@JoinColumn(name = "PROJECT_ID")},
            inverseJoinColumns = {@JoinColumn(name = "COLLABORATORS_ID")})
    private List<Collaborator> collaborators = new ArrayList<>();

    public Project(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public List<Role> getRolesNeeded() {
        return rolesNeeded;
    }

    public void setRolesNeeded(List<Role> rolesNeeded) {
        this.rolesNeeded = rolesNeeded;
    }

    public List<Collaborator> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(List<Collaborator> collaborators) {
        this.collaborators = collaborators;
    }

    public void removeCollaborator(Collaborator collaborator) { collaborators.remove(collaborator); }

    public void removeRole(Role role) {
        rolesNeeded.remove(role);
    }

    public static Comparator<Project>
            projectComparator =
            (p1, p2) -> p1.getStartDate().compareTo(p2.getStartDate());
}
