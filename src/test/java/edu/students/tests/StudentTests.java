package edu.students.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.students.models.Student;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.HashMap;

import static org.hamcrest.Matchers.*;

public class StudentTests {

    @BeforeAll
    public static void test_setUps() {
        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @ParameterizedTest
    @MethodSource("edu.students.genetators.Generator#grades")
    public void test_createNewStudent(int x) {

        Student student = new Student(1, "Kate", new int[]{x});

        RestAssured.given()
                .body(student)
                .contentType(ContentType.JSON)
                .post("/student")
                .then()
                .statusCode(201);
    }

    @Test
    public void test_createNewStudent() {

        Student student = new Student(2, "Kate", new int[]{2, 3, 4, 5});

        RestAssured.given()
                .body(student)
                .contentType(ContentType.JSON)
                .post("/student")
                .then()
                .statusCode(201)
                .extract().response();
    }

    @Test
    public void test_updateStudent() {

        Student student = new Student(2, "Kate", new int[]{2, 3, 4, 5});

        RestAssured.given()
                .body(student)
                .contentType(ContentType.JSON)
                .when()
                .post("/student");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new HashMap<Object, Object>() {{
                    put("id", student.getId());
                    put("name", "Olga");
                    put("marks", new int[]{2, 3, 4, 5});
                }})
                .when()
                .post("/student")
                .then()
                .statusCode(201)
                //.body("id", equalTo(student.getId()))
                .extract().response();
    }

    @Test
    public void test_createNewStudentWithNullId() {

        Student student = new Student(null, "Kate", new int[]{2, 3, 4, 5});

        RestAssured.given()
                .body(student)
                .contentType(ContentType.JSON)
                .post("/student")
                .then()
                .statusCode(201)
                .extract().response();
                //.extract().as(Response.class)
                //.extract().jsonPath().getObject("id", Response.class)
        //System.out.println(response);

    }

    @Test
    public void test_createNewStudentWithoutName() {

        RestAssured.given()
                .body(new HashMap<Object, Object>() {{
                    put("Id", 11);
                    put("marks", new int[]{2, 3, 4, 5});
                }})
                .contentType(ContentType.JSON)
                .post("/student")
                .then()
                .statusCode(400);
    }

    @Test
    public void test_getCreatedStudent() {

        int studentId = 3;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new HashMap<Object, Object>() {{
                    put("id", studentId);
                    put("name", "Kate");
                    put("marks", new int[]{2, 3, 4, 5});
                }})
                .when()
                .post("/student");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("id", studentId)
                .when()
                .get("/student/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(studentId));
    }

    @Test
    public void test_getNotCreatedStudent() {

        int studentId = 3;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new HashMap<Object, Object>() {{
                    put("id", studentId);
                    put("name", "Kate");
                    put("marks", new int[]{2, 3, 4, 5});
                }})
                .when()
                .post("/student");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("id", studentId + 1)
                .when()
                .get("/student/{id}")
                .then()
                .statusCode(404)
                .body(isEmptyOrNullString());
    }

    @Test
    public void test_deleteCreatedStudent() {

        int studentId = 1;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new HashMap<Object, Object>() {{
                    put("id", studentId);
                    put("name", "Kate");
                    put("marks", new int[]{2, 3, 4, 5});
                }})
                .when()
                .post("/student");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("id", studentId)
                .when()
                .delete("/student/{id}")
                .then()
                .statusCode(200);
    }

    @Test
    public void test_deleteNotCreatedStudent() {

        int studentId = 999;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new HashMap<Object, Object>() {{
                    put("id", studentId);
                    put("name", "Kate");
                    put("marks", new int[]{2, 3, 4, 5});
                }})
                .when()
                .post("/student");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("id", studentId + 1)
                .delete("/student/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    public void test_getTopEmptyStudentsList() {;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .get("/topStudent")
                .then()
                .statusCode(200)
                .body(isEmptyOrNullString());

    }

    @Test
    public void test_getTopStudentsListWithoutMarks() {

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new HashMap<Object, Object>() {{
                    put("id", 20);
                    put("name", "Kate");
                    put("marks", new int[]{2, 3, 4, 5});
                }})
                .when()
                .post("/student");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/topStudent")
                .then()
                .statusCode(200)
                .body("marks", is(empty()));
                //.body("marks", is(not(empty())));
    }

}
