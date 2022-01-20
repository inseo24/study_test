package com.example.test.repository;

import com.example.test.model.Employee;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;


// Integration testing Repository using MySQL database -> @AutoConfigureTestDatabase
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EmployeeRepositoryIntegrationTest {

    @Autowired
    public EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeEach
    public void setup() {
        employee = Employee.builder()
                .firstName("seoin")
                .lastName("choi")
                .email("seoin@naver.com")
                .build();
    }

    // JUnit test for save employee operation
    @DisplayName("JUnit test for save employee operation")
    @Test
    public void givenEmployeeObject_whenSave_thenReturnSavedEmployee() {

        // given - precondition or setup


        // when - action or the behavior that we are going to test
        Employee savedEmployee = employeeRepository.save(employee);

        // then - verify the output
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getId()).isGreaterThan(0);

    }

    // unit test for
    @DisplayName("Junit test for get all employees operation")
    @Test
    public void givenEmployeesList_whenFindAll_thenEmployeesList() {
        // given - precondition or setup
        Employee employee1 = Employee.builder()
                .firstName("seoin")
                .lastName("choi")
                .email("seoin@naver.com")
                .build();

        Employee employee2 = Employee.builder()
                .firstName("john")
                .lastName("cena")
                .email("cena@naver.com")
                .build();

        employeeRepository.save(employee1);
        employeeRepository.save(employee2);

        // when - action or the behavior that we are going to test
        List<Employee> employeeList = employeeRepository.findAll();

        // then - verify the output
        assertThat(employeeList).isNotNull();
        assertThat(employeeList.size()).isEqualTo(2);

    }

    // unit test for get by id
    @DisplayName("Junit test for get employee by id operation")
    @Test
    public void givenEmployeeObject_whenFindById_thenReturnEmployeeObject() {
        // given - precondition or setup
        employeeRepository.save(employee);

        // when - action or the behavior that we are going to test
        Employee employeeDB = employeeRepository.findById(employee.getId()).get();

        // then - verify the output
        assertThat(employeeDB).isNotNull();
    }

    // unit test for
    @DisplayName("Junit test for find by email")
    @Test
    public void givenEmployeeEmail_whenFindByEmail_thenReturnEmployeeObject() {
        // given - precondition or setup
        employeeRepository.save(employee);

        // when - action or the behavior that we are going to test
        Employee employeeDB = employeeRepository.findByEmail(employee.getEmail()).get();

        // then - verify the output
        assertThat(employeeDB).isNotNull();
    }

    // unit test for update employee operation
    @DisplayName("Junit test for update employee operation")
    @Test
    public void givenEmployeeObject_whenUpdateEmployee_thenReturnUpdatedEmployee() {
        // given - precondition or setup
        employeeRepository.save(employee);

        // when - action or the behavior that we are going to test
        Employee savedEmployee = employeeRepository.findById(employee.getId()).get();
        savedEmployee.setEmail("inseo@naver.com");
        savedEmployee.setFirstName("inseo");
        Employee updatedEmployee = employeeRepository.save(savedEmployee);

        // then - verify the output
        assertThat(updatedEmployee.getEmail()).isEqualTo("inseo@naver.com");
        assertThat(updatedEmployee.getFirstName()).isEqualTo("inseo");
    }

    // unit test for delete operation
    @DisplayName("Junit test for delete employee operation")
    @Test
    public void givenEmployeeObject_whenDelete_thenRemoveEmployee() {
        // given - precondition or setup
        employeeRepository.save(employee);

        // when - action or the behavior that we are going to test
        employeeRepository.deleteById(employee.getId());
        Optional<Employee> employeeOptional = employeeRepository.findById(employee.getId());

        // then - verify the output
        assertThat((employeeOptional).isEmpty());
    }

    // unit test for custom query using JPQL with index params
    @DisplayName("Junit test for JPQL with index params")
    @Test
    public void givenFirstNameAndLastName_whenFindByJPQL_thenReturnEmployeeObject() {
        // given - precondition or setup
        employeeRepository.save(employee);
        String firstName = "seoin";
        String lastName = "choi";

        // when - action or the behavior that we are going to test
        Employee savedEmployee = employeeRepository.findByJPQL(firstName, lastName);

        // then - verify the output
        assertThat(savedEmployee).isNotNull();

    }

    // unit test for custom query using JPQL with named params
    @DisplayName("Junit test for JPQL with named params")
    @Test
    public void given_whenFindByJPQLNamedParams_then() {
        // given - precondition or setup
        employeeRepository.save(employee);
        String firstName = "seoin";
        String lastName = "choi";

        // when - action or the behavior that we are going to test
        Employee savedEmployee = employeeRepository.findByJPQLNamedParams(firstName, lastName);

        // then - verify the output
        assertThat(savedEmployee).isNotNull();
    }

    // unit test for custom query using native SQL with index params
    @DisplayName("Junit test for native SQL with index params")
    @Test
    public void givenFirstNameAndLastName_whenFindByNativeSQL_thenReturnEmployeeObject() {
        // given - precondition or setup
        employeeRepository.save(employee);
        String firstName = "seoin";
        String lastName = "choi";

        // when - action or the behavior that we are going to test
        Employee savedEmployee = employeeRepository.findByNativeSQL(firstName, lastName);

        // then - verify the output
        assertThat(savedEmployee).isNotNull();
    }

    // unit test for custom query using native SQL with index params
    @DisplayName("Junit test for native SQL with index params")
    @Test
    public void givenFirstNameAndLastName_whenFindByNativeSQLNamed_thenReturnEmployeeObject() {
        // given - precondition or setup
        employeeRepository.save(employee);
        String firstName = "seoin";
        String lastName = "choi";

        // when - action or the behavior that we are going to test
        Employee savedEmployee = employeeRepository.findByNativeSQLNamed(firstName, lastName);

        // then - verify the output
        assertThat(savedEmployee).isNotNull();
    }
}
