
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.Map;

public class MaxEmpoyee {

    public Employee highestSalary(Response response) {

        ArrayList list = (ArrayList) response.getBody().jsonPath().get();
        ArrayList<Employee> employeeList = new ArrayList();

        for (int i = 0; i < 5; i++) {

            Map<String, String> jsonData = (Map<String, String>) list.get(i);

            Employee e = new Employee();

            String employeeName = jsonData.get("employee_name");
            e.setEmployee_name(employeeName);


            String employeeSalary = jsonData.get("employee_salary");
            e.setEmployee_salary(employeeSalary);


            String employeeId = jsonData.get("id");
            e.setId(employeeId);

            employeeList.add(e);


        }

        Employee maxEmployeeObject = employeeList.get(0);

        for (int i = 0; i < 5; i++) {
            Employee eachElementEmployee = employeeList.get(i);
            long eachSal = Long.parseLong(eachElementEmployee.getEmployee_salary());

            long maxSalary = Long.parseLong(maxEmployeeObject.getEmployee_salary());


            if (maxSalary < eachSal) {
                maxEmployeeObject = eachElementEmployee;
            }

        }

        return maxEmployeeObject;
    }


}
