package com.javamentor.developer.social.platform.userControllerTests;

import com.github.database.rider.core.api.dataset.DataSet;
import com.javamentor.developer.social.platform.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DataSet(value = {
        "datasets/user/active.yml",
        "datasets/user/role.yml",
        "datasets/user/status.yml",
        "datasets/user/userFriends.yml",
        "datasets/user/user.yml"
}, cleanBefore = true, cleanAfter = true)
public class UserControllerTests extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createUser() throws Exception {
        mockMvc.perform(post("/api/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"firstName\": \"Админ\"," +
                        "\"lastName\": \"LastName\"," +
                        "\"dateOfBirth\": \"1994-05-30\"," +
                        "\"aboutMe\": \"Some information\"," +
                        "\"avatar\": \"myImage\"," +
                        "\"education\": \"PTU\"," +
                        "\"statusName\": \"Learning java\"," +
                        "\"activeName\": \"Online\"," +
                        "\"email\": \"admin@admin.ru\"," +
                        "\"password\": \"adminpass\"," +
                        "\"roleName\": \"User\"," +
                        "\"city\": \"Msc\"," +
                        "\"linkSite\": \"mysite.ru\"" +
                        "}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Админ"))
                .andExpect(jsonPath("$.email").value("admin@admin.ru"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void findUserById() throws Exception {
        mockMvc.perform(get("/api/user/{id}", 4L))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(4))
                .andExpect(jsonPath("$.firstName").value("Admin3"))
                .andExpect(jsonPath("$.email").value("admin0@user.ru"))
                .andExpect(jsonPath("$.roleName").value("User"))
                .andExpect(jsonPath("$.aboutMe").value("My description about life - Admin3"))
                .andExpect(jsonPath("$.city").value("SPb"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void findUserInvalidId() throws Exception {
        this.mockMvc.perform(get("/api/user/{id}", 115L))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("User with ID: 115 does not exist."));
    }

    @Test
    void findAllUsers() throws Exception {
        mockMvc.perform(get("/api/user/all"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void updateUser() throws Exception {
        this.mockMvc.perform(put("/api/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"userId\": \"5\"," +
                        "\"firstName\": \"Update\"," +
                        "\"email\": \"Update@email.com\"," +
                        "\"password\": \"Qwerty123\"," +
                        "\"lastName\": \"LastName\"," +
                        "\"dateOfBirth\": \"1994-05-30\"," +
                        "\"aboutMe\": \"Some new information\"," +
                        "\"education\": \"PTU\"," +
                        "\"statusName\": \"Pureness and perfection\"," +
                        "\"activeName\": \"Online\"," +
                        "\"avatar\": \"www.newAvatar.ru/9090\"," +
                        "\"roleName\": \"User\"," +
                        "\"city\": \"Msc\"," +
                        "\"linkSite\": \"myNewSite.ru\"" +
                        "}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("5"))
                .andExpect(jsonPath("$.firstName").value("Update"))
                .andExpect(jsonPath("$.email").value("Update@email.com"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void findUserFriends() throws Exception {
        mockMvc.perform(get("/api/user/getFriends/2"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    public void deleteUserInvalidId() throws Exception {
        mockMvc.perform(delete("/api/user/delete/555")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("User with ID: 555 does not exist."));
    }

//    @Test
//    public void deleteUserOK() throws Exception {
//        mockMvc.perform(delete("/api/user/delete/2")
//                .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().string("Пользователь с ID: 2 удалён успешно."));
//    }

}
