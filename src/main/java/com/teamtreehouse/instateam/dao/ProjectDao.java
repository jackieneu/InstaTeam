package com.teamtreehouse.instateam.dao;

import com.teamtreehouse.instateam.model.Project;

import java.util.List;

public interface ProjectDao {
    List<Project> findAll();
    Project findById(Long id);
    void save(Project category);
    void delete(Project category);
}
