package com.forestplus.controller;

import com.forestplus.entity.TreeEntity;
import com.forestplus.service.TreeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trees")
public class TreeController {

    private final TreeService treeService;

    public TreeController(TreeService treeService) {
        this.treeService = treeService;
    }

    @GetMapping
    public List<TreeEntity> getAllTrees() {
        return treeService.getAllTrees();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TreeEntity> getTreeById(@PathVariable Long id) {
        return treeService.getTreeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public TreeEntity createTree(@RequestBody TreeEntity tree) {
        return treeService.createTree(tree);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TreeEntity> updateTree(@PathVariable Long id, @RequestBody TreeEntity tree) {
        try {
            return ResponseEntity.ok(treeService.updateTree(id, tree));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTree(@PathVariable Long id) {
        treeService.deleteTree(id);
        return ResponseEntity.noContent().build();
    }
}
