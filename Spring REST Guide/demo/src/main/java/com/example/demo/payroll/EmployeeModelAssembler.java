package com.example.demo.payroll;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EmployeeModelAssembler implements RepresentationModelAssembler<Employee, EntityModel<Employee>> {

    @Override
    public EntityModel<Employee> toModel(Employee employee) {
        // Create an EntityModel for the employee
        EntityModel<Employee> employeeModel = EntityModel.of(employee);

        // Add a self-link to the employee
        employeeModel.add(linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel());

        // Add a link to the collection of all employees
        employeeModel.add(linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));

        return employeeModel;
    }

}
