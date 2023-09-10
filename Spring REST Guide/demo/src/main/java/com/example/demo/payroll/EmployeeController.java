package com.example.demo.payroll;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class EmployeeController {

    private final EmployeeRepository repository;
    private final EmployeeModelAssembler assembler;

    EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

//    BASIC

//    @GetMapping("/employees")
//    List<Employee> all() {
//        return repository.findAll();
//    }


//    USING HATEOAS TO ENCAPSULATE LINKS

//    @GetMapping("/employees")
//    CollectionModel<EntityModel<Employee>> all() {
//
//        List<EntityModel<Employee>> employees = repository.findAll().stream()
//                .map(employee -> EntityModel.of(employee,
//                        linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
//                        linkTo(methodOn(EmployeeController.class).all()).withRel("employees")))
//                .collect(Collectors.toList());
//
//        return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
//
//    }

    @GetMapping("/employees")
    CollectionModel<EntityModel<Employee>> all() {

        List<EntityModel<Employee>> employees = repository.findAll().stream().map(assembler::toModel).collect(Collectors.toList());

        return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());

    }

    /* BASIC */

//    @PostMapping("/employees")
//    Employee newEmployee(@RequestBody Employee newEmployee) {
//        return repository.save(newEmployee);
//    }

    /* ADDING TWEAK OF HATEOAS HERE TOO */
    @PostMapping("/employees")
    ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) {
        EntityModel<Employee> entityModel = assembler.toModel(repository.save(newEmployee));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }


//    BASIC

//    @GetMapping("/employees/{id}")
//    Employee one(@PathVariable Long id) {
//        return repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
//    }


//    USING HATEOAS TO ENCAPSULATE LINKS

//    @GetMapping("/employees/{id}")
//    EntityModel<Employee> one(@PathVariable Long id) {
//
//        Employee employee = repository.findById(id) //
//                .orElseThrow(() -> new EmployeeNotFoundException(id));
//
//        return EntityModel.of(employee, //
//                linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(),
//                linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
//    }


//    INJECTING ASSEMBLER TO SIMPLIFY LINK CREATION

    @GetMapping("/employees/{id}")
    EntityModel<Employee> one(@PathVariable Long id) {

        Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));

        return assembler.toModel(employee);

    }

    //BASIC
//    @PutMapping("/employees/{id}")
//    Employee replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
//        return (repository.findById(id).map(employee -> {
//            employee.setName(newEmployee.getName());
//            employee.setRole(newEmployee.getRole());
//            return repository.save(employee);
//        }).orElseGet(() -> {
//            newEmployee.setId(id);
//            return repository.save(newEmployee);
//        }));
//    }

    @PutMapping("/employees/{id}")
    ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
        Employee updatedEmployee = repository.findById(id).map(employee -> {
            employee.setName(newEmployee.getName());
            employee.setRole(newEmployee.getRole());
            return repository.save(employee);
        }).orElseGet(() -> {
            newEmployee.setId(id);
            return repository.save(newEmployee);
        });

        EntityModel<Employee> entityModel = assembler.toModel(updatedEmployee);

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);

    }

//    BASIC
//    @DeleteMapping("/employees/{id}")
//    void deleteEmployee(@PathVariable Long id) {
//        repository.deleteById(id);
//    }

//    ENHANCED DELETE
    @DeleteMapping("employees/{id}")
    ResponseEntity<?> deleteEmployee(@PathVariable Long id) {

        repository.deleteById(id);

        return ResponseEntity.noContent().build();

    }

}
