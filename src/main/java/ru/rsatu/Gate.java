package ru.rsatu;

import java.util.Base64;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import ru.rsatu.dologin.LoginRequest;
import ru.rsatu.dologin.LoginResponse;
import ru.rsatu.getSum.getSumRequest;
import ru.rsatu.getSum.getSumResponse;
import ru.rsatu.models.PetsEntity;
import ru.rsatu.models.UsersEntity;

@Path("/rest")
public class Gate {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/doLogin")
    public Response doLogin(LoginRequest request) {
        LoginResponse result = new LoginResponse();
        String login = request.getLogin();
        String password = request.getPassword();

        // логика проверки логина, формирование токена
        String token = login + "@@@" + password;
        String userName = "Test Test";
        // формирование ответа
        result.setToken(token);
        result.setUserName(userName);
        return Response.ok(result).build();
    }
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/test")
    public String test() {

        return "hello";

    }
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getSum")
    public Response getSum(getSumRequest request) {
        getSumResponse result = new getSumResponse();
        String numberOne = request.getNumberOne();
        String numberTwo = request.getNumberTwo();
        // логика проверки логина, формирование токена
        String summa = String.valueOf(Integer.parseInt(numberOne) + Integer.parseInt(numberTwo));
        //String userName = "Test Test";
        // формирование ответа
        result.setSumma(summa);
        //result.setUserName(userName);
        return Response.ok(result).build();

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/add")
    @Transactional
    public Response add(PetsEntity item){
        item.persist();
        return Response.ok(item).status(201).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/get")
    @Transactional
    public List<PetsEntity> get(){
        return PetsEntity.listAll();
    }

    }
