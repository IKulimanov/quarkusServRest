package ru.rsatu;

import com.amazonaws.*;
import javax.inject.Inject;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.amazonaws.auth.*;
import com.amazonaws.services.cloudfront.model.FieldLevelEncryption;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.MultipartUpload;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.Bucket;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
//import org.graalvm.compiler.lir.LIRInstruction;
//import io.vertx.reactivex.redis.client.Request;
//import org.graalvm.compiler.core.GraalCompiler;
import ru.rsatu.doAccauntReg.AccauntRegRequest;
import ru.rsatu.doAllPets.doPetsStateRequest;
import ru.rsatu.doAnimal.AnimalRequest;
import ru.rsatu.doAnimal.AnimalResponce;
import ru.rsatu.doSearchLogin.SearchLoginRequest;
import ru.rsatu.dologin.LoginRequest;
import ru.rsatu.dologin.LoginResponse;


import javax.persistence.EntityManager;

import ru.rsatu.models.PetsEntity;
import ru.rsatu.models.ReqGivePetEntity;
import ru.rsatu.models.ReqPetEntity;
import ru.rsatu.models.UsersEntity;

@Path("/rest")
public class Gate {
    @Inject
    EntityManager entityManager;
    Integer pages;
    int r = 9;
    @GET
    //@Transactional
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
       /* UsersEntity us = new UsersEntity();
        us.setLogin("admin");
        us.setRole("admin");
        us.setUserId(999);
        String z = "admin" + "admin";
        us.setHashsum(z.hashCode());
        us.persist();*/
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
            return Response.ok("login bad").build();
        } else {
            if (UE.getActive() == false) {
                return Response.ok("user lock").build();
            }
        }
        String hash = request.getLogin() + request.getPassword();
        int hashUser =  UE.getHashsum();
        if (hashUser != hash.hashCode()){
            return Response.ok("password bad").build();
        }
        else
        {
            result.setlogin(UE.getLogin());
            result.setrole(UE.getRole());
            return Response.ok(result).build();
        }
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
        UsersEntity UEe = new UsersEntity();

        UE = UE.getUser(serchrequest.getLogin());

        if (UE != null) {
            return Response.ok("login invalid").build();
        }

        UEe = UEe.getUserMail(serchrequest.getEmail());
        if (UEe != null) {
            return Response.ok("email invalid").build();
        }

        return Response.ok("OK").build();
    }


    /**
     * Регистрация пользователя
     *
     * @param ARreq
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/doAccauntReg")
    @Transactional
    public Response doAccauntReg(AccauntRegRequest ARreq) {
        UsersEntity user = new UsersEntity();

        user.setActive(true);
        user.setAge(ARreq.getAge());
        user.setEmail(ARreq.getEmail());
        user.setFirstname(ARreq.getFirstname());
        user.setLastname(ARreq.getLastname());
        user.setLogin(ARreq.getLogin());
        user.setRole("user");
        user.setTelephone(ARreq.getTelephone());
        String hash = ARreq.getLogin() + ARreq.getPassword();
        user.setHashsum(hash.hashCode());
        user.setUserId(r);
        user.persist();
        r++;
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin(ARreq.getLogin());
        loginRequest.setPassword(ARreq.getPassword());
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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/uploadImage")
    public Response uploadImage(String s) throws IOException {
        System.out.println("obj");
        //System.out.println(file.getPath());
        //System.out.println(file);
        return Response.ok("OK").build();

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
        String key = "2.jpg";

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
        Integer page_t;
        Integer off;

        pets = getPets(animReq.getType(),animReq.getGender(),animReq.getAge(),animReq.getActive());

        pages = pets.size()/8;
        pages = pages + (pets.size() - pages*8);

        page_t = animReq.getPage()*8-8;

        off = page_t + 8;
        if (off > pets.size())
        {
            off = pets.size() - (off-8);
        }
        off = off + page_t;
        ArrayList<PetsEntity> listpets = new ArrayList<PetsEntity>();
        ArrayList<AnimalResponce> listResp = new ArrayList<>();

        for (Integer i = page_t; i <= off-1; i++)
        {
            listpets.add(pets.get(i));
        }
        return Response.ok(listpets).build();

    }



    @GET
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getPage")
    public Response getPage() {
        return Response.ok(pages).build();
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
    public Response doRequestAnimalGive(AnumalGiveRequest source) {

        Integer userID = searchUser(source.getLogin());
        if (userID == 0) {
            return Response.ok("login not find").build();
        }

        ReqGivePetEntity entityReqGive = new ReqGivePetEntity();
        entityReqGive.setActive(true);
        entityReqGive.setAgepet(source.getAge());
        entityReqGive.setGenderPet(source.getGender());
        entityReqGive.setIdUser(userID);
        entityReqGive.setNamepet(source.getName());
        entityReqGive.setTypePet(source.getType());
        entityReqGive.setIdReqGp(4);
        entityReqGive.setStatus("active");
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
        if (userID == 0) {
            return Response.ok("login not find").build();
        }
        reqpetlist= (ArrayList<ReqPetEntity>) entityManager.createNamedQuery("ReqPetEntity.findreq", ReqPetEntity.class)
                .setParameter("id_user", userID).setParameter("active", true)
                .getResultList();
        if (reqpetlist == null)
        {
            return Response.ok("err").build();
        }
        petslist = (ArrayList<PetsEntity>) entityManager.createNamedQuery("PetsEntity.getPetfromID", PetsEntity.class)
                .setParameter("petId", reqpetlist.get(0).getPetId()).getResultList();

        return Response.ok(petslist).build();
    }

    /**
     * Запрос на получение всех пользователей учитывая статус
     * @param state
     * @return
     */

    /**
     * Запрос на получение всех пользователей учитывая статус
     * @param UserSearch
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/doAllUsers")
    public Response doAllUsers(doSearchStatUser UserSearch) {
        boolean active = true;
        ArrayList<UsersEntity> Users;
        List<UsersEntity> User;
        if (UserSearch.getSelected().equals("active")) {
            active = true;
        }
        else if (UserSearch.getSelected().equals("archive")) {
            active = false;
        }
        if (UserSearch.getSearch().equals("any")) {
            /**
             * получение всех польхователей с определенным статусом
             */
            Users = (ArrayList<UsersEntity>) entityManager.createNamedQuery("UsersEntity.findUsersStat", UsersEntity.class)
                    .setParameter("active", active)
                    .getResultList();
            }
        else{
            /**
            * получение пользователя с определенным статусом
            */
            doSearchResponce result = null;
            User = entityManager.createNamedQuery("UsersEntity.findloginStat", UsersEntity.class)
                    .setParameter("login", UserSearch.getSearch()).setParameter("active", active)
                    .getResultList();
            result.setEmail(User.get(0).getEmail());
            result.setFirstname(User.get(0).getFirstname());
            result.setLastname(User.get(0).getLastname());
            result.setLogin(User.get(0).getLogin());
            result.setTelphone(User.get(0).getTelephone());
            //result.setPages(1);
            return Response.ok(result).build();
        }

        Integer page_t;
        Integer off;
        Integer countP = Users.size()/5;
        countP = countP + (Users.size() - countP);
       // int j = 0;
        page_t = UserSearch.getPage()*5-5;
        off = page_t + 5;
        if (off > Users.size())
        {
            off = Users.size() - (off-5);
        }
        off = off + page_t;

        ArrayList<doSearchResponce> resultArr = new ArrayList<>();


        for (Integer i = page_t; i <= off-1; i++)
        {
            doSearchResponce temp = new doSearchResponce();
            temp.setLogin(Users.get(i).getLogin());
            temp.setLastname(Users.get(i).getLastname());
            temp.setFirstname(Users.get(i).getFirstname());
            temp.setTelphone(Users.get(i).getTelephone());
            temp.setEmail(Users.get(i).getEmail());

            resultArr.add(temp);
        }

        return Response.ok(resultArr).build();

    }

    /**
     * Запрос на поиск пользователя по логину
     * @param us
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/doFindUser")
    public Response doFindUser(UsersEntity us) {
        System.out.println(us.getLogin());
        FindUserResponce resp = new FindUserResponce();
       List<UsersEntity> en;
        en = (ArrayList<UsersEntity>) entityManager.createNamedQuery("UsersEntity.findlogin", UsersEntity.class)
                .setParameter("login", us.getLogin())
                .getResultList();
        resp.setAge(en.get(0).getAge());
        resp.setEmail(en.get(0).getEmail());
        resp.setFirstname(en.get(0).getFirstname());
        resp.setLastname(en.get(0).getLastname());
        resp.setTelephone(en.get(0).getTelephone());
        return Response.ok(resp).build();
    }

    /**
     * Запрос на получение списка животных в записимости от состояния
     * @param petsStateReq
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/doAllPets")
    @Transactional
    public Response doAllPets(doPetsStateRequest petsStateReq) {
        ArrayList<PetsEntity> pets;
        Integer page_t;
        Integer off;
        boolean isActive;
        if (petsStateReq.getSelected().equals("active")){
            isActive = true;
        } else isActive = false;
        pets =(ArrayList<PetsEntity>) entityManager.createNamedQuery("PetsEntity.getPetstate", PetsEntity.class)
                .setParameter("active", isActive).getResultList();

        pages = pets.size()/5;
        pages = pages + (pets.size() - pages*5);

        page_t = 1*5-5;

        off = page_t + 5;
        if (off > pets.size())
        {
            off = pets.size() - (off-5);
        }
        off = off + page_t;
        ArrayList<PetsEntity> listpets = new ArrayList<PetsEntity>();
        ArrayList<AnimalResponce> listResp = new ArrayList<>();

        for (Integer i = page_t; i <= off-1; i++)
        {
            listpets.add(pets.get(i));
        }
        return Response.ok(listpets).build();
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
    public Response doGiveReq(doTakeTablRequest tablReq) {
        System.out.println(tablReq.getSearch());
        ArrayList<doGiveTablResponce> doTakeTablResponces = new ArrayList<>();
        if (tablReq.getSearch().equals("any") == false ) {
            System.out.println("1");
            int IDuser = searchUser(tablReq.getSearch());
            System.out.println(IDuser);
            ArrayList<ReqGivePetEntity> ReqPetList;

            ReqPetList = (ArrayList<ReqGivePetEntity>) entityManager.createNamedQuery("ReqGivePetEntity.findreqUserId", ReqGivePetEntity.class)
                    .setParameter("userId", IDuser).setParameter("status", tablReq.getSelected())
                    .getResultList();

            for (int i = 0; i <= ReqPetList.size(); i++) {

                doGiveTablResponce temp = new doGiveTablResponce();
                UsersEntity user = new UsersEntity();
                PetsEntity pet = new PetsEntity();
                user = UsersEntity.findById(ReqPetList.get(i).getIdUser());
                temp.setLogin(user.getLogin());
                temp.setEmail(user.getEmail());
                temp.setNameuser(user.getFirstname());
                temp.setPhone(user.getTelephone());

                temp.setTypepet(ReqPetList.get(i).getTypePet());
                temp.setNamepet(ReqPetList.get(i).getNamepet());
                temp.setAgepet(ReqPetList.get(i).getAgepet());
                temp.setGenderpet(ReqPetList.get(i).getGenderPet());
                doTakeTablResponces.add(temp);
            }

        }
        else{
            if (tablReq.getSearch().equals("any") == true){
                System.out.println("2");
                ArrayList<ReqGivePetEntity> ReqPetList;
                ReqPetList = (ArrayList<ReqGivePetEntity>) entityManager.createNamedQuery("ReqGivePetEntity.findreqStatus", ReqGivePetEntity.class)
                        .setParameter("status", tablReq.getSelected())
                        .getResultList();

                for (int i = 0; i < ReqPetList.size(); i++) {
                    doGiveTablResponce temp = new doGiveTablResponce();
                    UsersEntity user = new UsersEntity();
                    PetsEntity pet = new PetsEntity();

                    user = UsersEntity.findById(ReqPetList.get(i).getIdUser());
                    temp.setLogin(user.getLogin());
                    temp.setEmail(user.getEmail());
                    temp.setNameuser(user.getFirstname());
                    temp.setPhone(user.getTelephone());

                    temp.setTypepet(ReqPetList.get(i).getTypePet());
                    temp.setNamepet(ReqPetList.get(i).getNamepet());
                    temp.setAgepet(ReqPetList.get(i).getAgepet());
                    temp.setGenderpet(ReqPetList.get(i).getGenderPet());

                    doTakeTablResponces.add(temp);
                }
            }
        }

        return Response.ok(doTakeTablResponces).build();

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
    public Response doTakeReq(doTakeTablRequest tablReq) {
        ArrayList<doTakeTablResponce> doTakeTablResponces = new ArrayList<>();

    if (tablReq.getSearch().equals("any") == false ) {
        System.out.println("1");
        int IDuser = searchUser(tablReq.getSearch());
        System.out.println();
        ArrayList<ReqPetEntity> ReqPetList;

        ReqPetList = (ArrayList<ReqPetEntity>) entityManager.createNamedQuery("ReqPetEntity.findreqUserId", ReqPetEntity.class)
                .setParameter("userId", IDuser).setParameter("status", tablReq.getSelected())
                .getResultList();
        System.out.println( "--------"+ReqPetList.size());
        for (int i = 0; i < ReqPetList.size(); i++) {

            doTakeTablResponce temp = new doTakeTablResponce();
            UsersEntity user = new UsersEntity();
            PetsEntity pet = new PetsEntity();
            user = UsersEntity.findById(ReqPetList.get(i).getUserId());
            temp.setLogin(user.getLogin());
            temp.setEmail(user.getEmail());
            temp.setNameuser(user.getFirstname());
            temp.setPhone(user.getTelephone());
            pet = PetsEntity.findById(ReqPetList.get(i).getPetId());
            temp.setTypepet(pet.getType());
            temp.setNamepet(pet.getName());
            temp.setAgepet(pet.getAge());
            temp.setGenderpet(pet.getGender());
            doTakeTablResponces.add(temp);
        }

    }
    else{
        if (tablReq.getSearch().equals("any") == true){
            System.out.println("2");
            ArrayList<ReqPetEntity> ReqPetList;
            ReqPetList = (ArrayList<ReqPetEntity>) entityManager.createNamedQuery("ReqPetEntity.findreqStatus", ReqPetEntity.class)
                    .setParameter("status", tablReq.getSelected())
                    .getResultList();

            for (int i = 0; i < ReqPetList.size(); i++) {
                doTakeTablResponce temp = new doTakeTablResponce();
                UsersEntity user = new UsersEntity();
                PetsEntity pet = new PetsEntity();
                System.out.println(ReqPetList.get(i).getPets());
                user = UsersEntity.findById(ReqPetList.get(i).getUserId());
                temp.setLogin(user.getLogin());
                temp.setEmail(user.getEmail());
                temp.setNameuser(user.getFirstname());
                temp.setPhone(user.getTelephone());
                pet = PetsEntity.findById(ReqPetList.get(i).getPetId());
                temp.setTypepet(pet.getType());
                temp.setNamepet(pet.getName());
                temp.setAgepet(pet.getAge());
                temp.setGenderpet(pet.getGender());
                temp.setIschild(ReqPetList.get(i).getChild());
                temp.setIspet(ReqPetList.get(i).getPets());
                temp.setIshouse(ReqPetList.get(i).getIsHouse());
                doTakeTablResponces.add(temp);
            }
        }
    }

        return Response.ok(doTakeTablResponces).build();

    }


    /**
     * Установка значения с активного на архивное у пользователя
     * @param login
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/setUserInArchive")
    public Response setUserInArchive(String login) {
        UsersEntity user = new UsersEntity();
        int id = searchUser(login);
        user = PetsEntity.findById(id);
        user.setActive(false);
        return Response.ok(user).build();
    }

    /**
     * Установка значения статуса у заявки на забор  take питомца
     * @param id
     * @param status
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/setReqStatus")
    public Response setReqStatus(int id, String status) {
        ReqPetEntity reqPet = new ReqPetEntity();
        reqPet = PetsEntity.findById(id);
        reqPet.setStatus(status);
        return Response.ok(reqPet).build();
    }

    /**
     * Установка значения статуса у заявки на отдачу  give питомца
     * @param id
     *  @param status
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/setReqStatusGive")
    public Response setReqStatusGive(int id, String status) {
        ReqGivePetEntity reqPet = new ReqGivePetEntity();
        reqPet = PetsEntity.findById(id);
        reqPet.setStatus(status);
        return Response.ok(reqPet).build();
    }

    /**
     * Установка значения с активного на архивное у питомца
     * @param id
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/setUserInArchive")
    public Response setUserInArchive(int id) {
        PetsEntity pet = new PetsEntity();

        pet = PetsEntity.findById(id);
        pet.setActive(false);
        return Response.ok(pet).build();
    }

    /**
     * Поиск пользователей по логину и статусу
     * @param   login
     * @param state
     * @return
     */
    public int searchUserLogState(String login, String state)
    {
        boolean isActive;
        if (state.equals("active")){
            isActive = true;
        } else isActive = false;
        ArrayList<UsersEntity> en;
        en = (ArrayList<UsersEntity>) entityManager.createNamedQuery("UsersEntity.findloginStat", UsersEntity.class)
                .setParameter("login", login).setParameter("active", isActive)
                .getResultList();
        return en.size();
    }

    /**
     * Поиск пользователя по логину
     * @param login
     * @return
     */
    public Integer searchUser(String login)
    {

        ArrayList<UsersEntity> en;
        //UsersEntity[] entitis
                en = (ArrayList<UsersEntity>) entityManager.createNamedQuery("UsersEntity.findlogin", UsersEntity.class)
                .setParameter("login", login)
                .getResultList();//.toArray(new UsersEntity[0]);
        if (en == null) {
            return 0;
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
        ArrayList<PetsEntity> en;
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
        if (gender.equals("any") && !(type.equals("any"))){
            en =(ArrayList<PetsEntity>) entityManager.createNamedQuery("PetsEntity.getPetA", PetsEntity.class)
                    .setParameter("active", active)
                    .setParameter("type", type).setParameter("ageh", ageH)
                    .setParameter("agel", ageL).getResultList();
            return en;
        }
        if (type.equals("any") && !(gender.equals("any"))){
            en =(ArrayList<PetsEntity>) entityManager.createNamedQuery("PetsEntity.getPetB", PetsEntity.class)
                    .setParameter("active", active).setParameter("ageh", ageH)
                    .setParameter("agel", ageL).setParameter("gender", gender)
                    .getResultList();
            return en;
        }
        if ((gender.equals("any")) && (type.equals("any"))){
            en =(ArrayList<PetsEntity>) entityManager.createNamedQuery("PetsEntity.getPetC", PetsEntity.class)
                    .setParameter("active", active).setParameter("ageh", ageH)
                    .setParameter("agel", ageL).getResultList();
            return en;
        }else{
            en =(ArrayList<PetsEntity>) entityManager.createNamedQuery("PetsEntity.getPetD", PetsEntity.class)
            .setParameter("active", active).setParameter("type", type)
                    .setParameter("ageh", ageH).setParameter("agel", ageL)
                    .setParameter("gender", gender).getResultList();
            return en;
        }

    }
}
