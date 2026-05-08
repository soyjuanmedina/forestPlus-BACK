package com.forestplus.security;

import org.springframework.stereotype.Component;
import com.forestplus.entity.TreeEntity;
import com.forestplus.entity.UserEntity;
import com.forestplus.repository.TreeRepository;
import lombok.RequiredArgsConstructor;

@Component("treeSecurity")
@RequiredArgsConstructor
public class TreeSecurity {

    private final TreeRepository treeRepository;
    private final CurrentUserService currentUserService;

    /**
     * Comprueba si el usuario actual puede editar el árbol completo.
     */
    public boolean canEdit(Long treeId) {
        TreeEntity tree = treeRepository.findByIdWithOwners(treeId).orElse(null);
        if (tree == null) return false;

        Long currentUserId = currentUserService.getCurrentUserId();
        String currentUserRole = currentUserService.getCurrentUserRole();
        Long currentUserCompanyId = currentUserService.getCurrentUserCompanyId();

        // Admin global
        if ("ADMIN".equals(currentUserRole)) return true;

        // Árbol de usuario: propietario
        if (tree.getOwnerUser() != null && tree.getOwnerUser().getId() != null &&
            tree.getOwnerUser().getId().equals(currentUserId)) {
            return true;
        }

        // Árbol de compañía: solo company_admin de la compañía
        if (tree.getOwnerCompany() != null && "COMPANY_ADMIN".equals(currentUserRole) &&
            currentUserCompanyId != null && currentUserCompanyId.equals(tree.getOwnerCompany().getId())) {
            return true;
        }

        // Árbol de un usuario que pertenece a la misma compañía que el COMPANY_ADMIN
        if (tree.getOwnerUser() != null && "COMPANY_ADMIN".equals(currentUserRole) && currentUserCompanyId != null) {
            UserEntity owner = tree.getOwnerUser();
            if (owner.getCompany() != null && currentUserCompanyId.equals(owner.getCompany().getId())) {
                return true;
            }
        }

        // No cumple ninguna condición
        return false;
    }

    /**
     * Comprueba si el usuario actual puede editar únicamente el nombre del árbol.
     * Se aplica a company_admin de un árbol de compañía.
     */
    public boolean canEditNameOnly(Long treeId) {
        TreeEntity tree = treeRepository.findByIdWithOwners(treeId).orElse(null);
        if (tree == null) return false;

        Long currentUserId = currentUserService.getCurrentUserId();
        String currentUserRole = currentUserService.getCurrentUserRole();
        Long currentUserCompanyId = currentUserService.getCurrentUserCompanyId();

        // Admin global y propietario de usuario pueden editar todo
        if ("ADMIN".equals(currentUserRole) || 
            (tree.getOwnerUser() != null && tree.getOwnerUser().getId() != null &&
             tree.getOwnerUser().getId().equals(currentUserId))) {
            return true;
        }

        // Company admin: solo puede editar nombre si es árbol de su compañía
        if (tree.getOwnerCompany() != null && "COMPANY_ADMIN".equals(currentUserRole) &&
            currentUserCompanyId != null && currentUserCompanyId.equals(tree.getOwnerCompany().getId())) {
            return true;
        }

        return false;
    }
    /**
     * Comprueba si el usuario actual puede eliminar el árbol.
     */
    public boolean canDelete(Long treeId) {
        // Por ahora usamos la misma lógica que canEdit
        return canEdit(treeId);
    }
}
