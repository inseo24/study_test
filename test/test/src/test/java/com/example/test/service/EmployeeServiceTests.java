package com.example.test.service;

import com.example.test.exception.ResourceNotFoundException;
import com.example.test.model.Employee;
import com.example.test.repository.EmployeeRepository;
import com.example.test.service.impl.EmployeeServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTests {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;

    @BeforeEach
    public void setup() {
        // employeeRepository = Mockito.mock(EmployeeRepository.class);
        // employeeService = new EmployeeServiceImpl(employeeRepository);
        employee = Employee.builder()
                .id(1L)
                .firstName("seoin")
                .lastName("choi")
                .email("seoin@naver.com")
                .build();
    }

    // unit test for saveEmployee method
    @DisplayName("Junit test for saveEmployee method")
    @Test
    public void givenEmployeeObject_whenSaveEmployee_thenReturnEmployeeObject() {
        // 레포지터리 관련된 것들에 stubbing 이 필요하다고 함 -> return 해줄 것
        // given - precondition or setup
        given(employeeRepository.findByEmail(employee.getEmail()))
                .willReturn(Optional.empty());
        given(employeeRepository.save(employee)).willReturn(employee);

        // when - action or the behavior that we are going to test
        Employee savedEmployee = employeeService.saveEmployee(employee);

        // then - verify the output
        assertThat(savedEmployee).isNotNull();
    }

    @DisplayName("Junit test for saveEmployee method throw exception")
    @Test
    public void givenExistingEmail_whenSaveEmployee_thenThrowsException() {
        // 레포지터리 관련된 것들에 stubbing 이 필요하다고 함 -> return 해줄 것
        // given - precondition or setup
        given(employeeRepository.findByEmail(employee.getEmail()))
                .willReturn(Optional.of(employee));

        // when - action or the behavior that we are going to test
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.saveEmployee(employee);
        });

        // then - verify the output
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    // unit test for get all employees - positive scenario
    @DisplayName("Junit test for get all employees method - positive")
    @Test
    public void givenEmployeesList_whenGetAllEmployees_thenReturnEmployeesList() {
        // given - precondition or setup
        Employee employee1 = Employee.builder()
                .id(2L)
                .firstName("inseo")
                .lastName("choi")
                .email("inseo@naver.com")
                .build();

        given(employeeRepository.findAll())
                .willReturn(List.of(employee, employee1));

        // when - action or the behavior that we are going to test
        List<Employee> employeeList = employeeService.getAllEmployees();

        // then - verify the output
        assertThat(employeeList).isNotNull();
        assertThat(employeeList.size()).isEqualTo(2);
    }

    // unit test for get all employees - negative scenario
    @DisplayName("Junit test for get all employees method - negative")
    @Test
    public void givenEmptyEmployeesList_whenGetAllEmployees_thenReturnEmptyList() {
        // given - precondition or setup
        given(employeeRepository.findAll())
                .willReturn(Collections.emptyList());

        // when - action or the behavior that we are going to test
        List<Employee> employeeList = employeeService.getAllEmployees();

        // then - verify the output
        assertThat(employeeList).isEmpty();
        assertThat(employeeList.size()).isEqualTo(0);
    }

    // unit test for get employee by id
    @DisplayName("Junit test for get employee by id method")
    @Test
    public void givenEmployeeId_whenGetEmployeeById_thenReturnEmployeeObject() {
        // given - precondition or setup
        given(employeeRepository.findById(1L))
                .willReturn(Optional.of(employee));

        // when - action or the behavior that we are going to test
        Employee foundEmployee = employeeService.getEmployeeById(employee.getId()).get();

        // then - verify the output
        assertThat(foundEmployee).isNotNull();
    }

    // unit test for update method
    @DisplayName("Junit test for update method")
    @Test
    public void givenEmployeeObject_whenUpdateEmployee_thenReturnUpdatedEmployee() {
        // given - precondition or setup
        given(employeeRepository.save(employee))
                .willReturn(employee);
        employee.setEmail("abc@gmail.com");
        employee.setFirstName("namu");

        // when - action or the behavior that we are going to test
        Employee updatedEmployee = employeeService.updateEmployee(employee);

        // then - verify the output
        assertThat(updatedEmployee.getEmail()).isEqualTo("abc@gmail.com");
        assertThat(updatedEmployee.getFirstName()).isEqualTo("namu");
    }

    // unit test for delete method
    @DisplayName("Junit test for delete method")
    @Test
    public void givenId_whenDeleteById_thenReturnNothing() {
        // given - precondition or setup
        willDoNothing().given(employeeRepository).deleteById(1L);
        long employeeId = 1L;
        // when - action or the behavior that we are going to test
        employeeService.deleteEmployee(employeeId);

        // then - verify the output
        verify(employeeRepository, times(1)).deleteById(employeeId);
    }
}
