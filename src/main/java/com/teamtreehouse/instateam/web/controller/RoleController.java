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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class RoleController {

    @Autowired
    RoleService roleService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CollaboratorService collaboratorService;

    //Index of all roles
    @RequestMapping("/roles")
    public String listRoles(Model model){
        List<Role> roles = roleService.findAll();

        model.addAttribute("roles", roles);

        if(!model.containsAttribute("role")) {
            model.addAttribute("role", new Role());
        }
        return "role/roles";
    }

    //Add a role
    @RequestMapping(value="/roles", method = RequestMethod.POST)
    public String addRole(@Valid Role role, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()){
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.role", result);
            redirectAttributes.addFlashAttribute("role", role);
            return "redirect:/roles";
        }
        roleService.save(role);
        redirectAttributes.addFlashAttribute("flash",
                new FlashMessage("Role successfully added!", FlashMessage.Status.SUCCESS));
        return "redirect:/roles";
    }

    //Edit a role
    @RequestMapping("roles/{roleId}/edit")
    public String editRole(@PathVariable Long roleId, Model model){
        if(!model.containsAttribute("role")) {
            model.addAttribute("role",roleService.findById(roleId));
        }
        return "role/edit_role";
    }

    //Update Role
    @RequestMapping(value = "/roles/update", method = RequestMethod.POST)
    public String updateRole(@Valid Role role, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.role", result);
            redirectAttributes.addFlashAttribute("role", role);
            return String.format("redirect:/roles/%s/edit", role.getId());
        }
        roleService.save(role);
        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Role successfully updated!", FlashMessage.Status.SUCCESS));
        return "redirect:/roles";
    }

    //Delete role
    @RequestMapping(value = "/roles/{roleId}/delete", method = RequestMethod.POST)
    public String deleteRole(@PathVariable Long roleId, RedirectAttributes redirectAttributes){
        Role role = roleService.findById(roleId);
        List<Project> projects = projectService.findAll();
        List<Collaborator> collaborators = collaboratorService.findAll();

//        List<Role> rolesToDelete = new ArrayList<>();
//
//        for (Project project : projects){
//            for(Role roleNeeded : project.getRolesNeeded())
//                if(Objects.equals(roleNeeded.getId(), role.getId())){
//                    rolesToDelete.add(roleNeeded);
//                }
//        }
//        for(Project project : projects){
//            for(Role roleToDelete : rolesToDelete){
//                project.removeRole(roleToDelete);
//            }
//        }

        for (Project project : projects){
            project.removeRole(role);
            projectService.save(project);
        }

        for (Collaborator collaborator : collaborators){
            if(collaborator.getRole() != null) {
                if (Objects.equals(collaborator.getRole().getId(), role.getId())) {
                    System.out.println("here");
                    collaborator.setRole(null);
                    collaboratorService.save(collaborator);
                }
            }
        }

        role.setCollaborators(null);
        roleService.save(role);
        roleService.delete(role);
        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Role successfully deleted!", FlashMessage.Status.SUCCESS));
        return "redirect:/roles";
    }
}
