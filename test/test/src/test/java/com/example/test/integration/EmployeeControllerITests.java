package com.example.test.integration;

import com.example.test.model.Employee;
import com.example.test.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers // test container 
public class EmployeeControllerITests {

	// adding MySQL container
	// before testing, should check -> Is docker running
	@Container
	private static MySQLContainer mySQLContainer = new MySQLContainer("mysql:latest")  // params -> docker image name
					.withUsername("username")
					.withPassword("password") // <- can assign values
					.withDatabaseName("ems");
	// 1) test container pull mysql docker image from the docker hub
	// 2) Deploy MySQL in a docker container
	// 3) Run the Integration tests with MySQL database(deployed in docker container)
	
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        employeeRepository.deleteAll();
    }

    @Test
    public void givenEmployeeObject_whenCreateEmployee_thenReturnSavedEmployee() throws Exception {
        
    	System.out.println("mysql container username : " + mySQLContainer.getUsername()); // username : test(default)
    	System.out.println("mysql container password : " + mySQLContainer.getPassword());  // password : test(default)
    	System.out.println("mysql container database name : " + mySQLContainer.getDatabaseName());  // database name :  test(default)
    	System.out.println("mysql container JdbcUrl : " + mySQLContainer.getJdbcUrl()); // jdbc:mysql://localhost:[port]/test(default)
    	
    	
    	// given - precondition or setup
        Employee employee = Employee.builder()
                .firstName("seoin")
                .lastName("choi")
                .email("jnh@naver.com")
                .build();

        // when - action or behavior that we are going test
        ResultActions response = mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)));

        // then - verify the result or output using assert statements
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName",
                        is(employee.getFirstName())))
                .andExpect(jsonPath("$.lastName",
                        is(employee.getLastName())))
                .andExpect(jsonPath("$.email",
                        is(employee.getEmail())));
    }

    @DisplayName("Junit test for get All employees")
    @Test
    public void givenListOfEmployees_whenGetAllEmployees_thenReturnEmployeeList() throws Exception {
        // given - precondition or setup
        List<Employee> listOfEmployees = new ArrayList<>();
        listOfEmployees.add(Employee.builder().firstName("seoin").lastName("choi").email("jnh@naver.com").build());
        listOfEmployees.add(Employee.builder().firstName("inseo").lastName("choi").email("hnj@naver.com").build());
        employeeRepository.saveAll(listOfEmployees);

        // when - action or the behavior that we are going to test
        ResultActions response = mockMvc.perform(get("/api/employees"));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()",
                        is(listOfEmployees.size())));
    }

    // positive scenario - valid employee id
    // JUnit test for GET employee by id REST API
    @Test
    public void givenEmployeeId_whenGetEmployeeById_thenReturnEmployeeObject() throws Exception {
        //given
        Employee employee = Employee.builder()
                .firstName("seoin")
                .lastName("choi")
                .email("jnh@naver.com")
                .build();
        employeeRepository.save(employee);

        // when
        ResultActions response = mockMvc.perform(get("/api/employees/{id}", employee.getId()));

        // then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.firstName", is(employee.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(employee.getLastName())))
                .andExpect(jsonPath("$.email", is(employee.getEmail())));
    }

    // negative scenario - valid employee id
    // JUnit test for GET employee by id REST API
    @Test
    public void givenEmployeeId_whenGetEmployeeById_thenReturnEmpty() throws Exception {
        //given
        long employeeId = 1L;

        // when
        ResultActions response = mockMvc.perform(get("/api/employees/{id}", employeeId));

        // then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    // positive scenario
    @DisplayName("Junit test for update employee")
    @Test
    public void givenUpdatedEmployee_whenUpdateEmployee_thenReturnEmployeeObject() throws Exception {
        // given - precondition or setup
        Employee savedEmployee = Employee.builder()
                .firstName("seoin")
                .lastName("choi")
                .email("jnh@naver.com")
                .build();
        employeeRepository.save(savedEmployee);

        Employee updatedEmployee = Employee.builder()
                .firstName("inseo")
                .lastName("choi")
                .email("hnj@naver.com")
                .build();

        // when - action or the behavior that we are going to test
        ResultActions response = mockMvc.perform(put("/api/employees/{id}", savedEmployee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.firstName", is(updatedEmployee.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(updatedEmployee.getLastName())))
                .andExpect(jsonPath("$.email", is(updatedEmployee.getEmail())));
    }

    // negative scenario
    @DisplayName("Junit test for update employee")
    @Test
    public void givenUpdatedEmployee_whenUpdateEmployee_thenReturn404Error() throws Exception {
        // given - precondition or setup
        long employeeId = 1L;
        Employee updatedEmployee = Employee.builder()
                .firstName("inseo")
                .lastName("choi")
                .email("hnj@naver.com")
                .build();

        // when - action or the behavior that we are going to test
        ResultActions response = mockMvc.perform(put("/api/employees/{id}", employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        // then - verify the output
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @DisplayName("Junit test for delete employee")
    @Test
    public void givenEmployeeId_whenDeleteByEmployeeId_thenReturn200() throws Exception {
        // given - precondition or setup
        long employeeId = 1L;
        Employee savedEmployee = Employee.builder()
                .firstName("seoin")
                .lastName("choi")
                .email("jnh@naver.com")
                .build();
        employeeRepository.save(savedEmployee);

        // when - action or the behavior that we are going to test
        ResultActions response = mockMvc.perform(delete("/api/employees/{id}", employeeId));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print());
    }
}
