package com.teamtreehouse.instateam.web.controller;

import com.teamtreehouse.instateam.model.Collaborator;
import com.teamtreehouse.instateam.model.Project;
import com.teamtreehouse.instateam.model.Role;
import com.teamtreehouse.instateam.service.CollaboratorService;
import com.teamtreehouse.instateam.service.ProjectService;
import com.teamtreehouse.instateam.service.RoleService;
import com.teamtreehouse.instateam.web.FlashMessage;
import com.teamtreehouse.instateam.web.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.teamtreehouse.instateam.model.Project.projectComparator;

@Controller
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CollaboratorService collaboratorService;

    //Index of all projects
    @RequestMapping({"/projects", "/"})
    public String listProjects(Model model){
        List<Project> projects = projectService.findAll();
        projects.sort(projectComparator);
        model.addAttribute("projects", projects);
        return "project/index";

    }

    // Form for adding a new Project
    @RequestMapping("/projects/add")
    public String formNewProject(Model model){
        //Get all Roles
        List<Role> roles = roleService.findAll();

        //Add model attributes needed for new form
        if(!model.containsAttribute("project")) {
            model.addAttribute("project", new Project());
        }

        model.addAttribute("roles", roles);
        model.addAttribute("action", "/projects");
        model.addAttribute("heading", "Create Project");
        model.addAttribute("status", Status.values());

        return "project/edit_project";
    }

    // Add a project
    @RequestMapping(value="/projects", method = RequestMethod.POST)
    public String addProject(@Valid Project project, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.project", result);
            redirectAttributes.addFlashAttribute("project", project);
            return "redirect:/projects/add";
        }
        //Add project if valid data was received
        projectService.save(project);

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Project successfully added!", FlashMessage.Status.SUCCESS) );

        //Redirect browser to /projects
        return "redirect:/projects";
    }

    // Update a project
    @RequestMapping(value="/project/{projectId}", method = RequestMethod.POST)
    public String updateProject(@Valid Project project, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.project", result);
            redirectAttributes.addFlashAttribute("project", project);
            return String.format("redirect:/project/%s/edit", project.getId());
        }
        //Add project if valid data was received
        projectService.save(project);

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Project successfully updated!", FlashMessage.Status.SUCCESS) );

        //Redirect browser to /projects
        return "redirect:/projects";
    }

    // Form for editing a Project
    @RequestMapping("/project/{id}/edit")
    public String editProject(@PathVariable Long id, Model model){
        List<Role> roles = roleService.findAll();
        model.addAttribute("roles", roles);

        if (!model.containsAttribute("project")) {
            model.addAttribute("project", projectService.findById(id));
        }

        model.addAttribute("action", String.format("/project/%s", id));
        model.addAttribute("heading", "Edit Project");
        model.addAttribute("status", Status.values());

        return "project/edit_project";
    }

    // Project Detail
    @RequestMapping("/project/{id}")
    public String projectDetail(@PathVariable Long id, Model model){
        Project project = projectService.findById(id);
        Map<Role, Collaborator> rolesMap = getMap(project);

        model.addAttribute("project", project);
        model.addAttribute("rolesMap", rolesMap);
        return "project/project_detail";
    }

    // Project Collaborators
    @RequestMapping("/project/{id}/collaborators")
    public String projectCollaborators(@PathVariable Long id, Model model) {

        Project project = projectService.findById(id);
        model.addAttribute("project", project);
        return "project/project_collaborators";
    }

    // Add collaborators to project
    @RequestMapping(value = "/project/{projectId}/addCollaborator", method = RequestMethod.POST)
    public String addCollaboratorsToProject(@PathVariable Long projectId, Project project, RedirectAttributes redirectAttributes) {

        Project originalProject = projectService.findById(projectId);
        originalProject.setCollaborators(project.getCollaborators());
        projectService.save(originalProject);
        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Collaborators successfully assigned!", FlashMessage.Status.SUCCESS));
        return String.format("redirect:/project/%s", projectId);
    }

    // delete a project
    @RequestMapping(value = "/project/{projectId}/delete", method = RequestMethod.POST)
    public String deleteProject(@PathVariable Long projectId, RedirectAttributes redirectAttributes) {
        Project project = projectService.findById(projectId);
        List<Collaborator> collaborators = new ArrayList<>(project.getCollaborators());
        project.setRolesNeeded(null);
        collaborators.forEach(project::removeCollaborator);
        projectService.save(project);
        projectService.delete(project);
        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Project successfully deleted!", FlashMessage.Status.SUCCESS));
        return "redirect:/projects";
    }

    private Map<Role, Collaborator> getMap(Project project) {
        List<Role> rolesNeeded = project.getRolesNeeded();
        List<Collaborator> collaborators = project.getCollaborators();
        Map<Role, Collaborator> roleCollaborator = new LinkedHashMap<>();

//        if(!rolesNeeded.isEmpty()) {
            for (Role roleNeeded : rolesNeeded) {
                roleCollaborator.put(roleNeeded,
                        collaborators.stream()
                                .filter((col) -> col.getRole().getId().equals(roleNeeded.getId()))
                                .findFirst()
                                .orElseGet(() -> {
                                    Collaborator unassigned = new Collaborator();
                                    unassigned.setName("Unassigned");
                                    return unassigned;
                                }));
            }
//        }
        return roleCollaborator;
    }
}
