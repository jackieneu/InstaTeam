package com.teamtreehouse.instateam.web.controller;

import com.teamtreehouse.instateam.model.Collaborator;
import com.teamtreehouse.instateam.model.Project;
import com.teamtreehouse.instateam.model.Role;
import com.teamtreehouse.instateam.service.CollaboratorService;
import com.teamtreehouse.instateam.service.ProjectService;
import com.teamtreehouse.instateam.service.RoleService;
import com.teamtreehouse.instateam.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
public class CollaboratorController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private CollaboratorService collaboratorService;

    @Autowired
    private ProjectService projectService;

    //Index of all collaborators
    @RequestMapping("/collaborators")
    public String listCollaborators(Model model){
        //Get all Roles
        List<Role> roles = roleService.findAll();
        List<Collaborator> collaboarators = collaboratorService.findAll();

        //Add model attributes needed for new form
        if(!model.containsAttribute("collaborator")) {
            model.addAttribute("collaborator", new Collaborator());
        }

        model.addAttribute("roles", roles);
        model.addAttribute("collaborators", collaboarators);

        return "collaborator/collaborators";
    }

    //Add a collaborator
    @RequestMapping(value="/collaborators", method = RequestMethod.POST)
    public String addCollaborator(@Valid Collaborator collaborator, BindingResult result, RedirectAttributes redirectAttributes){
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.collaborator", result);
            redirectAttributes.addFlashAttribute("collaborator", collaborator);
            return "redirect:/collaborators";
        }
        collaboratorService.save(collaborator);
        redirectAttributes.addFlashAttribute("flash",
                new FlashMessage("Collaborator successfully added!", FlashMessage.Status.SUCCESS));
        return "redirect:/collaborators";
    }

    //Edit a collaborator
    @RequestMapping("collaborators/{collaboratorId}/edit")
    public String editCollaborator(@PathVariable Long collaboratorId, Model model){
        Collaborator collaborator = collaboratorService.findById(collaboratorId);
        List<Role> roles = roleService.findAll();

        if(!model.containsAttribute("collaborator")) {
            model.addAttribute("collaborator",collaborator);
        }
        model.addAttribute("roles", roles);

        return "collaborator/edit_collaborator";
    }

    //Save Edit
    @RequestMapping(value = "/collaborators/update", method = RequestMethod.POST)
    public String updateCollaborator(@Valid Collaborator collaborator, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.collaborator", result);
            redirectAttributes.addFlashAttribute("collaborator", collaborator);
            return String.format("redirect:/collaborators/%s/edit", collaborator.getId());
        }
        collaboratorService.save(collaborator);
        redirectAttributes.addFlashAttribute("flash",
                new FlashMessage("Collaborator successfully updated!", FlashMessage.Status.SUCCESS));
        return "redirect:/collaborators";
    }

    //Delete
    @RequestMapping(value = "/collaborators/{collaboratorId}/delete", method = RequestMethod.POST)
    public String delete(@PathVariable Long collaboratorId, RedirectAttributes redirectAttributes){
        Collaborator collaborator = collaboratorService.findById(collaboratorId);
        List<Project> allProjects = projectService.findAll();

        for (Project project : allProjects) {
            project.removeCollaborator(collaborator);
            projectService.save(project);
        }
        collaborator.setRole(null);
        collaboratorService.save(collaborator);
        collaboratorService.delete(collaborator);
        redirectAttributes.addFlashAttribute("flash",
                new FlashMessage("Collaborator successfully deleted!", FlashMessage.Status.SUCCESS));
        return "redirect:/collaborators";
    }
}
