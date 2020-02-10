package ru.rsatu;

import com.amazonaws.*;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Driver.*;

import axle.logic.SamplePredicates;
import com.amazonaws.auth.*;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.Bucket;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
//import org.graalvm.compiler.lir.LIRInstruction;
import ru.rsatu.doAnimal.AnimalRequest;
import ru.rsatu.doAnimal.AnimalResponce;
import ru.rsatu.doSearchLogin.SearchLoginRequest;
import ru.rsatu.dologin.LoginRequest;
import ru.rsatu.dologin.LoginResponse;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.json.Json;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

import io.quarkus.runtime.StartupEvent;
import ru.rsatu.models.PetsEntity;
import ru.rsatu.models.ReqGivePetEntity;
import ru.rsatu.models.ReqPetEntity;
import ru.rsatu.models.UsersEntity;

@Path("/rest")
public class Gate {
    @Inject
    EntityManager entityManager;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {


        return "hello";
    }

    /**
     * Запрос на выход в личный кабинет
     *
     * @param request
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/doLogin")
    public Response doLogin(LoginRequest request) {
        UsersEntity UE = new UsersEntity();
        LoginResponse result = new LoginResponse();
        String login = request.getLogin();
        String password = request.getPassword();
        UE = UE.getUser(login);
        if (UE == null) {
            return Response.ok("login or password bad").build();
        } else {
            if (UE.getActive() == false) {
                return Response.ok("user lock").build();
            }
            result.setlogin(UE.getLogin());
            result.setrole(UE.getRole());
            return Response.ok(result).build();
        }
        // логика проверки логина, формирование токена
        /*String token = login + password;
        // String userName = "Test Test";
        int hash = token.hashCode();
        // формирование ответа
        result.setToken(hash);
        //result.setUserName(userName);

        UsersEntity[] entitis = entityManager.createNamedQuery("UsersEntity.findHash", UsersEntity.class)
                .setParameter("hashsum", hash)
                .getResultList().toArray(new UsersEntity[0]);
        if (entitis != null) {
            return Response.ok(result).build();
        }
        return Response.ok("login or password bad").build();*/
    }

    /**
     * Поиск логина и эмейла в бд
     *
     * @param serchrequest
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/doSearchLogin")
    @Transactional
    public Response doSerchLog(SearchLoginRequest serchrequest) {
        UsersEntity UE = new UsersEntity();
        UE = UE.getUser(serchrequest.getLogin());
        if (UE != null) {
            return Response.ok("login invalid").build();
        }
        UE = UE.getUserMail(serchrequest.getEmail());
        if (UE != null) {
            return Response.ok("email invalid").build();
        }
        return Response.ok("OK").build();
    }


    /**
     * Регистрация пользователя
     *
     * @param usr
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/doAccauntReg")
    @Transactional
    public Response doAccauntReg(UsersEntity usr) {
        usr.persist();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin(usr.getLogin());
        loginRequest.setPassword(usr.getHashsum());
        return doLogin(loginRequest);
    }

    /**
     * Добавление животного в бд
     *
     * @param item
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addPets")
    @Transactional
    public Response addPets(PetsEntity item) {
        item.persist();
        return Response.ok(item).status(201).build();
    }

    /**
     * Запрос на получение списка из 8 животных с учётом фильтров
     * @param animReq
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getAnimal")
    public Response getAnimal(AnimalRequest animReq) throws IOException {
        /**
         * AMAZON
         */

        AWSCredentials credentials = null;
        String s3_accessKey = "F9vj-60KVf2jnCycxiyL";
        String s3_secretKey = "EtVde7J0tqLm572I8mCvH0HWssdG10uwm-MJetD4";
        BasicAWSCredentials creds = new BasicAWSCredentials(s3_accessKey, s3_secretKey);
        S3Object object = null;

      /*  try{
            credentials = new ProfileCredentialsProvider().getCredentials();

        }catch (Exception e){
            throw new AmazonClientException(
                    "asdasdas", e
            );
        }*/

        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(
                                "storage.yandexcloud.net","ru-central1-c"
                        )
                )
                .build();

        String bucketName = "animals-photo";// + UUID.randomUUID();
        String key = "1.jpg";

        System.out.println("Start Amazon S3");

        try{

            System.out.println("Listing buckets");
            for (Bucket bucket : s3.listBuckets()) {
                System.out.println(" - " + bucket.getName());
            }
            System.out.println();


            System.out.println("Downaloading an object");
            object = s3.getObject(new GetObjectRequest(bucketName,key));
            System.out.println("Content-Type: "); //+ object.getObjectMetadata().getContentType());
            System.out.println(object.getObjectContent());
            //displayTextInputStream(object.getObjectContent());
            /*
            ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
            .withBucketName(bucketName)
            .withPrefix("My"));
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                System.out.println(" - " + objectSummary.getKey() + "  " +
                        "(size = " + objectSummary.getSize() + ")");
            }
            System.out.println("=---------=");
*/
        } catch (AmazonServiceException ase){
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace){
            System.out.println("=----2-----=");
        }


        /**
         *
         */
        ArrayList<PetsEntity> pets;

        System.out.println("tyt");

        pets = getPets(animReq.getType(),animReq.getGender(),animReq.getAge(),animReq.getActive());
        System.out.println("tyts");
        if (pets.size() < 8)
        {
            System.out.println("tyt0");
            return Response.ok(pets).build();
        }

        Integer countP = pets.size()/8;
        Integer page_t = animReq.getPage()*8-8;
        Integer off = pets.size() - page_t - 1;

        ArrayList<PetsEntity> listpets = new ArrayList<PetsEntity>();
        ArrayList<AnimalResponce> listResp = new ArrayList<>();
       // Integer j = 0;
        System.out.println("tyt1");
        //AnimalResponce anResp = new AnimalResponce();
        for (Integer i = page_t; i <= page_t+off-1; i++)
        {
            //anResp.setName(pets.get(i).getName());
            //anResp.setAge(pets.get(i).getAge());
            //anResp.setType(pets.get(i).getType());
            //anResp.setGender(pets.get(i).getGender());
            //anResp.setPhoto(object.getObjectContent());
            //listResp.add(i, anResp);
            //listpets.get(i).setPhoto(object.getObjectContent());
            listpets.add(pets.get(i));
           // j++;
           // System.out.println(anResp.getName());
        }
        return Response.ok(listpets).build();

    }

    /**
     * Displays the contents of the specified input stream as text.
     *
     * @param input
     *            The input stream to display as text.
     *
     * @throws IOException
     */
    private static void displayTextInputStream(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;

            System.out.println("    " + line);
        }
        System.out.println();
    }

    /**
     * Запрос на оставление заявки на забор питомца
     *
     * @param idAnimal
     * @param login
     * @param childs
     * @param pets
     * @param house
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/doReqAnimalTake")
    public Response doRequestAnimalTake(String login, Integer idAnimal,
                                     boolean childs, boolean pets, boolean house) {

        Integer userID = searchUser(login);
        if (userID == -1) {
            return Response.ok("login not find").build();
        }

        ReqPetEntity entityReq = new ReqPetEntity();
        entityReq.setActive(true);
        entityReq.setChild(childs);
        entityReq.setIsHouse(house);
        entityReq.setPets(pets);
        entityReq.setUserId(userID);
        entityReq.setPetId(idAnimal);
        entityReq.persist();
        return Response.ok(entityReq).build();
    }

    /**
     * Запрос на добавление заявки на оставление питомца
     * @param login
     * @param name
     * @param age
     * @param type
     * @param gender
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/doReqAnimalGive")
    public Response doRequestAnimalGive(String login, String name, Integer age, String type, String gender) {

        Integer userID = searchUser(login);
        if (userID == -1) {
            return Response.ok("login not find").build();
        }

        ReqGivePetEntity entityReqGive = new ReqGivePetEntity();
        entityReqGive.setActive(true);
        entityReqGive.setAgepet(age);
        entityReqGive.setGenderPet(gender);
        entityReqGive.setIdUser(userID);
        entityReqGive.setNamepet(name);
        entityReqGive.setTypePet(type);
        entityReqGive.persist();
        return Response.ok(entityReqGive).build();
    }

    /**
     * Запрос на получени всех активных запросов на оставление питомца
     * @param login
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/doUserReqGive")
    public Response doUserReqGive(String login) {
        Integer userID = searchUser(login);
        ArrayList<ReqGivePetEntity> reqGivePet;
        if (userID == -1) {
            return Response.ok("login not find").build();
        }
        reqGivePet = (ArrayList<ReqGivePetEntity>) entityManager.createNamedQuery("ReqGivePetEntity.findreq", ReqGivePetEntity.class)
                .setParameter("id_user", userID).setParameter("active", true)
                .getResultList();

        return Response.ok(reqGivePet).build();
    }
    /**
     * Запрос на получени всех активных запросов на забор питомца
     * @param login
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/doUserReqTake")
    public Response doUserReqTake(String login) {
        ArrayList<ReqPetEntity> reqpetlist;
        ArrayList<PetsEntity> petslist;
        Integer userID = searchUser(login);
        if (userID == -1) {
            return Response.ok("login not find").build();
        }
        reqpetlist= (ArrayList<ReqPetEntity>) entityManager.createNamedQuery("ReqPetEntity.findreq", ReqPetEntity.class)
                .setParameter("id_user", userID).setParameter("active", true)
                .getResultList();

        petslist = (ArrayList<PetsEntity>) entityManager.createNamedQuery("PetsEntity.getPetfromID", PetsEntity.class)
                .setParameter("petId", reqpetlist.get(0).getPetId()).getResultList();

        return Response.ok(petslist).build();
    }

    /**
     * Запрос на получение всех пользователей учитывая статус
     * @param state
     * @return
     */

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/doAllUsers")
    public Response doAllUsers(Integer state) {
        List<PanacheEntityBase> usersList;
        switch (state){
            case 0:
                usersList = UsersEntity.listAll();
                break;
            case 1:
                usersList = UsersEntity.list("active", true);
                break;
            case 2:
                usersList = UsersEntity.list("active", false);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }


        return Response.ok(usersList).build();
    }

    /**
     * Запрос на поиск пользователя по логину
     * @param login
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/doFindUser")
    public Response doFindUser(String login) {
        return Response.ok(UsersEntity.find("login", login)).build();
    }

    /**
     * Запрос на получение списка животных в записимости от состояния
     * @param state
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/doAllPets")
    public Response doAllPets(Integer state) {
        List<PanacheEntityBase> petsList;
        switch (state){
            case 0:
                petsList = PetsEntity.listAll();
                break;
            case 1:
                petsList = PetsEntity.list("active", true);
                break;
            case 2:
                petsList = PetsEntity.list("active", false);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
        return Response.ok(petsList).build();
    }

    /**
     * Запрос на получение всех заявок на оставление питомцев
     *
     * @param state
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/doGiveReq")
    public Response doGiveReq(Integer state) {
        List<PanacheEntityBase> reqGiveList;
        switch (state){
            case 0:
                reqGiveList = ReqGivePetEntity.listAll();
                break;
            case 1:
                reqGiveList = ReqGivePetEntity.list("active", true);
                break;
            case 2:
                reqGiveList = ReqGivePetEntity.list("active", false);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
        return Response.ok(reqGiveList).build();
    }

    /**
     * Запрос на получение всех заявок на забор питомца
     * @param state
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/doTakeReq")
    public Response doTakeReq(Integer state) {
        List<PanacheEntityBase> reqTakeList;
        switch (state){
            case 0:
                reqTakeList = ReqPetEntity.listAll();
                break;
            case 1:
                reqTakeList = ReqPetEntity.list("active", true);
                break;
            case 2:
                reqTakeList = ReqPetEntity.list("active", false);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
        return Response.ok(reqTakeList).build();
    }


/*
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/get")
    @Transactional
    public List<PetsEntity> get() {
        return PetsEntity.listAll();
    }*/

    public Integer searchUser(String login)
    {

        ArrayList<UsersEntity> en;
        //UsersEntity[] entitis
                en = (ArrayList<UsersEntity>) entityManager.createNamedQuery("UsersEntity.findlogin", UsersEntity.class)
                .setParameter("login", login)
                .getResultList();//.toArray(new UsersEntity[0]);
        if (en == null) {
            return -1;
        }

        return en.get(0).getUserId();
    }

    /**
     * Получение списка животных по заданным фильтрам
     * @param type
     * @param gender
     * @param age
     * @param active
     * @return
     */
    public ArrayList<PetsEntity> getPets(String type, String gender, Integer age, Boolean active){
        System.out.println("tesgdg");
        Integer ageL = 0;
        Integer ageH = 100;
        switch (age){
            case 0:
                ageL = 0;
                ageH = 100;
                break;
            case 1:
                ageL = 0;
                ageH = 1;
                break;
            case 2:
                ageL = 1;
                ageH = 3;
                break;
            case 3:
                ageL = 3;
                ageH = 6;
                break;
            case 4:
                ageL = 6;
                ageH = 100;
                break;
        }
        System.out.println("aaa");

        ArrayList<PetsEntity> en;
                en =(ArrayList<PetsEntity>) entityManager.createNamedQuery("PetsEntity.getPet", PetsEntity.class)
                .setParameter("active", active).setParameter("type", type).setParameter("ageh", ageH)
                .setParameter("agel", ageL).setParameter("gender", gender)
                .getResultList();
        System.out.println(en.size());
        return en;
    }
}
