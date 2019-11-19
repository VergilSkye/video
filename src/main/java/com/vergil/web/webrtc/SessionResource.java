package com.vergil.web.webrtc;

import com.vergil.domain.Consulta;
import com.vergil.domain.User;
import com.vergil.repository.ConsultaRepository;
import com.vergil.repository.UserRepository;
import com.vergil.security.AuthoritiesConstants;
import com.vergil.security.SecurityUtils;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.OpenViduRole;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.TokenOptions;

import java.util.HashMap;
import java.util.Map;



@RestController
@RequestMapping("/api")
public class SessionResource {

    OpenVidu openVidu;


    private ConsultaRepository consultaRepository;


    private UserRepository userRepository;


    private Map<Long, Session> consultaIdSession = new ConcurrentHashMap<>();
    private Map<String, Map<Long, String>> sessionIdUserIdToken = new ConcurrentHashMap<>();

    private String OPENVIDU_URL;
    private String SECRET;

    public SessionResource(
        @Value("${openvidu.secret}") String secret, @
        Value("${openvidu.url}") String openviduUrl,
        ConsultaRepository consultaRepository,
        UserRepository userRepository) {
        this.SECRET = secret;
        this.OPENVIDU_URL = openviduUrl;
        this.openVidu = new OpenVidu(OPENVIDU_URL, SECRET);
        this.consultaRepository = consultaRepository;
        this.userRepository = userRepository;
    }

    private static class ConsultaResourceException extends RuntimeException {
        private ConsultaResourceException(String message) {
            super(message);
        }
    }

    @PostMapping("/create-session/{consultaId}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<JSONObject> createSession(@PathVariable Long consultaId){
        System.out.println("AQUI");
        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ConsultaResourceException("Current user login not found"));


        long id_consulta = -1;
        try {
            id_consulta = new Long(consultaId);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Consulta c = consultaRepository.findById(id_consulta).get();
        if(!c.getPsicologo_id().getLogin().equals(userLogin)){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (this.consultaIdSession.get(id_consulta) != null) {
            // If there's already a valid Session object for this lesson,
            // it is not necessary to ask for a new one
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            try {
                Session session = this.openVidu.createSession();
                this.consultaIdSession.put(id_consulta, session);
                this.sessionIdUserIdToken.put(session.getSessionId(), new HashMap<>());
                showMap();
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                return getErrorResponse(e);
            }
        }
    }
    @PostMapping("/generate-token/{consultaId}")
    public ResponseEntity<JSONObject> generateToken(@PathVariable Long consultaId){
        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ConsultaResourceException("Current user login not found"));
        User userT = userRepository.findOneByLogin(userLogin).get();
        long id_consulta = -1;
        try {
            id_consulta = new Long(consultaId);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Consulta c = consultaRepository.findById(id_consulta).get();
        if(!c.getPaciente_id().getLogin().equals(userLogin) && !c.getPsicologo_id().getLogin().equals(userLogin)){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (this.consultaIdSession.get(id_consulta) == null) {
            System.out.println("There's no Session fot this consulta");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Session session = this.consultaIdSession.get(id_consulta);
        OpenViduRole role = OpenViduRole.PUBLISHER;
        JSONObject responseJson = new JSONObject();
        TokenOptions tokenOpts = new TokenOptions.Builder().role(role).build();
        try{
            String token = this.consultaIdSession.get(id_consulta).generateToken(tokenOpts);
            this.sessionIdUserIdToken.get(session.getSessionId()).put(userT.getId(), token);
            responseJson.put(0, token);
            showMap();

            return new ResponseEntity<>(responseJson, HttpStatus.OK);
        } catch (OpenViduJavaClientException e1) {
            // If internal error generate an error message and return it to client
            return getErrorResponse(e1);
        }catch (OpenViduHttpException e2) {
            if (404 == e2.getStatus()) {
                // Invalid sessionId (user left unexpectedly). Session object is not valid
                // anymore. Must clean invalid session and create a new one
                try {
                    this.sessionIdUserIdToken.remove(session.getSessionId());
                    session = this.openVidu.createSession();
                    this.consultaIdSession.put(id_consulta, session);
                    this.sessionIdUserIdToken.put(session.getSessionId(), new HashMap<>());
                    String token = session.generateToken(tokenOpts);
                    // END IMPORTANT STUFF

                    this.sessionIdUserIdToken.get(session.getSessionId()).put(userT.getId(), token);
                    responseJson.put(0, token);
                    showMap();

                    return new ResponseEntity<>(responseJson, HttpStatus.OK);
                } catch (OpenViduJavaClientException | OpenViduHttpException e3) {
                    return getErrorResponse(e3);
                }
            } else {
                return getErrorResponse(e2);
            }
        }
    }

    @PostMapping("/remove-user/{consultaId}")
    public ResponseEntity<JSONObject> removeUser(@PathVariable Long consultaId) throws Exception {
        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ConsultaResourceException("Current user login not found"));
        User userT = userRepository.findOneByLogin(userLogin).get();


        long id_consulta = -1;
        try {
            id_consulta = new Long(consultaId);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Consulta c = consultaRepository.findById(id_consulta).get();

        if(!c.getPaciente_id().getLogin().equals(userLogin) && !c.getPsicologo_id().getLogin().equals(userLogin)){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (this.consultaIdSession.get(id_consulta) == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String sessionId = this.consultaIdSession.get(id_consulta).getSessionId();
        if (this.sessionIdUserIdToken.get(sessionId).remove(userT.getId()) != null) {
            // This user has left the lesson
            if (this.sessionIdUserIdToken.get(sessionId).isEmpty()) {
                // The last user has left the lesson
                this.consultaIdSession.remove(id_consulta);
                this.sessionIdUserIdToken.remove(sessionId);
            }

            showMap();

            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            System.out.println("Problems in the app server: the user didn't have a valid token");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/close-session/{consultaId}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<JSONObject> closeSession(@PathVariable Long consultaId) throws Exception {

        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ConsultaResourceException("Current user login not found"));

        // User userT = userRepository.findOneByLogin(userLogin).get();

        System.out.println("Closing session | {sessionName}=" + consultaId);


        long id_consulta = -1;
        try {
            id_consulta = new Long(consultaId);
        } catch (NumberFormatException e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Consulta c = consultaRepository.findById(id_consulta).get();
        if(!c.getPsicologo_id().getLogin().equals(userLogin)){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


        if(this.consultaIdSession.get(id_consulta) != null && this.sessionIdUserIdToken.get(this.consultaIdSession.get(id_consulta).toString()) != null) {
            Session s = this.consultaIdSession.get(id_consulta);
            s.close();
            this.consultaIdSession.remove(id_consulta);
            this.sessionIdUserIdToken.remove(s.getSessionId());
            showMap();
            return new ResponseEntity<>(HttpStatus.OK);
        } else if (this.consultaIdSession.get(id_consulta) != null && this.sessionIdUserIdToken.get(id_consulta) == null){
            Session s = this.consultaIdSession.get(id_consulta);
            s.close();
            this.consultaIdSession.remove(id_consulta);
            showMap();
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            showMap();
            System.out.println("Problems in the app server: the SESSION does not exist");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private void showMap() {
        System.out.println("------------------------------");
        System.out.println(this.consultaIdSession.toString());
        System.out.println(this.sessionIdUserIdToken.toString());
        System.out.println("------------------------------");
    }

    private ResponseEntity<JSONObject> getErrorResponse(Exception e) {
        JSONObject json = new JSONObject();
        json.put("cause", e.getCause());
        json.put("error", e.getMessage());
        json.put("exception", e.getClass());
        return new ResponseEntity<>(json, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
