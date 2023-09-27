package api.reqres;

import api.reqres.colors.Data;
import api.reqres.registration.Register;
import api.reqres.registration.SuccesReg;
import api.reqres.registration.UnSuccesReg;
import api.reqres.spec.Specification;
import api.reqres.users.UserData;
import api.reqres.users.UserTime;
import api.reqres.users.UserTimeResponse;
import org.junit.Assert;
import org.junit.Test;

import java.time.Clock;
import java.util.List;

import static io.restassured.RestAssured.given;

public class ReqresTest {
    private final static String URL = "https://reqres.in/";

    @Test
    public void checkAvatarAndIdTest(){
        Specification.installSpecs(Specification.requestSpe(URL), Specification.requestSpecOK200());
        List<UserData> users = given()
                .when()
                .get("api/users?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("data",UserData.class);

     //   users.forEach(x-> Assert.assertTrue(x.getAvatar().contains(x.getId().toString())));
     //   Assert.assertTrue(users.stream().allMatch(x->x.getEmail().endsWith("@reqres.in")));

        List<String> avatars = users.stream().map(UserData::getAvatar).toList();
        List<String> ids = users.stream().map(x->x.getId().toString()).toList();

        for(int i = 0; i < avatars.size(); i++){
            Assert.assertTrue(avatars.get(i).contains(ids.get(i)));
        }
    }

    @Test
    public void successReqTest(){
        Specification.installSpecs(Specification.requestSpe(URL), Specification.requestSpecOK200());
        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";
        Register user = new Register("eve.holt@reqres.in","pistol");
        SuccesReg succesReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(SuccesReg.class);
        Assert.assertNotNull(succesReg.getId());
        Assert.assertNotNull(succesReg.getToken());
        Assert.assertEquals(id, succesReg.getId());
        Assert.assertEquals(token, succesReg.getToken());
    }

    @Test
    public void unSuccessReqTest() {
        Specification.installSpecs(Specification.requestSpe(URL), Specification.requestError400());
        Register user = new Register("sydney@fife", "");
        UnSuccesReg unSuccesReg = given()
                .body(user)
                .post("api/register")
                .then().log().all()
                .extract().as(UnSuccesReg.class);
        Assert.assertEquals("Missing password", unSuccesReg.getError());
    }

    @Test
    public void sortedYearsTest(){
        Specification.installSpecs(Specification.requestSpe(URL), Specification.requestSpecOK200());
        List<Data> colors = given()
                .when()
                .get("api/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("data", Data.class);
        List<Integer> years = colors.stream().map(Data::getYear).toList();
        List<Integer> sortedYears = years.stream().sorted().toList();
        Assert.assertEquals(sortedYears, years);
    }

    @Test
    public void deleteUserTest(){
        Specification.installSpecs(Specification.requestSpe(URL), Specification.requestUnique(204));
        given()
                .when()
                .delete("api/users/2")
                .then().log().all();
    }

    @Test
    public void timeTest(){
        Specification.installSpecs(Specification.requestSpe(URL), Specification.requestSpecOK200());
        UserTime user = new UserTime("morpheus", "zion resident");
        UserTimeResponse response = given()
                .body(user)
                .when()
                .put("api/users/2")
                .then().log().all()
                .extract().as(UserTimeResponse.class);
        String regex = "(.{3}\\..*)$";
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex, "");
        Assert.assertEquals(currentTime, response.getUpdatedAt().replaceAll(regex, ""));
    }
}
