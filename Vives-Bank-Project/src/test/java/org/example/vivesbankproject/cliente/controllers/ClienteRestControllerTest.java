package org.example.vivesbankproject.cliente.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vivesbankproject.rest.cliente.dto.ClienteProducto;
import org.example.vivesbankproject.rest.cliente.dto.ClienteRequestSave;
import org.example.vivesbankproject.rest.cliente.dto.ClienteRequestUpdate;
import org.example.vivesbankproject.rest.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.rest.cliente.exceptions.ClienteNotFoundByDni;
import org.example.vivesbankproject.rest.cliente.service.ClienteService;
import org.example.vivesbankproject.rest.cuenta.dto.tipoCuenta.TipoCuentaResponseCatalogo;
import org.example.vivesbankproject.rest.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.rest.users.models.User;
import org.example.vivesbankproject.utils.pagination.PaginationLinksUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "adminPassword123", roles = {"ADMIN", "USER"})
class ClienteRestControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String myEndpoint = "/v1/clientes";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;


    @MockBean
    private PaginationLinksUtils paginationLinksUtils;


    @Test
    void getAll() throws Exception {
        ClienteResponse clienteResponse = ClienteResponse.builder()
                .guid("unique-guid")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .calle("Calle Falsa 123")
                .numero("123")
                .codigoPostal("28001")
                .piso("1")
                .letra("A")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .createdAt("2024-11-26T15:23:45.123")
                .updatedAt("2024-11-27T15:23:45.123")
                .isDeleted(false)
                .build();

        Page<ClienteResponse> page = new PageImpl<>(List.of(clienteResponse), PageRequest.of(0, 10), 1);
        when(clienteService.getAll(any(), any(), any(), any(), any(), any())).thenReturn(page);
        when(paginationLinksUtils.createLinkHeader(eq(page), any())).thenReturn("");

        mockMvc.perform(get("/v1/clientes")
                        .param("dni", "12345678A")
                        .param("nombre", "Juan")
                        .param("apellido", "Perez")
                        .param("email", "juan.perez@example.com")
                        .param("telefono", "123456789")
                        .param("fotoPerfil", "fotoprfil.jpg")
                        .param("fotoDni", "fotodni.jpg")
                        .param("userId", "user-guid")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].dni").value("12345678A"))
                .andExpect(jsonPath("$.content[0].nombre").value("Juan"))
                .andExpect(jsonPath("$.content[0].apellidos").value("Perez"))
                .andExpect(jsonPath("$.content[0].calle").value("Calle Falsa 123"))
                .andExpect(jsonPath("$.content[0].numero").value("123"))
                .andExpect(jsonPath("$.content[0].codigoPostal").value("28001"))
                .andExpect(jsonPath("$.content[0].piso").value("1"))
                .andExpect(jsonPath("$.content[0].letra").value("A"))
                .andExpect(jsonPath("$.content[0].email").value("juan.perez@example.com"))
                .andExpect(jsonPath("$.content[0].telefono").value("123456789"))
                .andExpect(jsonPath("$.content[0].fotoPerfil").value("fotoprfil.jpg"))
                .andExpect(jsonPath("$.content[0].fotoDni").value("fotodni.jpg"))
                .andExpect(jsonPath("$.content[0].userId").value("user-guid"))
                .andExpect(jsonPath("$.content[0].createdAt").value("2024-11-26T15:23:45.123"))
                .andExpect(jsonPath("$.content[0].updatedAt").value("2024-11-27T15:23:45.123"))
                .andExpect(jsonPath("$.content[0].isDeleted").value(false));
    }

    @Test
    void GetById() throws Exception {
        ClienteResponse clienteResponse = ClienteResponse.builder()
                .guid("unique-guid")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .calle("Calle Falsa 123")
                .numero("123")
                .codigoPostal("28001")
                .piso("1")
                .letra("A")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .createdAt("2024-11-26T15:23:45.123")
                .updatedAt("2024-11-27T15:23:45.123")
                .isDeleted(false)
                .build();

        when(clienteService.getById("unique-guid")).thenReturn(clienteResponse);

        mockMvc.perform(get("/v1/clientes/unique-guid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value("12345678A"))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellidos").value("Perez"))
                .andExpect(jsonPath("$.calle").value("Calle Falsa 123"))
                .andExpect(jsonPath("$.numero").value("123"))
                .andExpect(jsonPath("$.codigoPostal").value("28001"))
                .andExpect(jsonPath("$.piso").value("1"))
                .andExpect(jsonPath("$.letra").value("A"))
                .andExpect(jsonPath("$.email").value("juan.perez@example.com"))
                .andExpect(jsonPath("$.telefono").value("123456789"))
                .andExpect(jsonPath("$.fotoPerfil").value("fotoprfil.jpg"))
                .andExpect(jsonPath("$.fotoDni").value("fotodni.jpg"))
                .andExpect(jsonPath("$.userId").value("user-guid"))
                .andExpect(jsonPath("$.createdAt").value("2024-11-26T15:23:45.123"))
                .andExpect(jsonPath("$.updatedAt").value("2024-11-27T15:23:45.123"))
                .andExpect(jsonPath("$.isDeleted").value(false));
    }

    @Test
    void GetByDni() throws Exception {
        String dni = "12345678A";
        ClienteResponse clienteResponse = ClienteResponse.builder()
                .nombre("Juan Pérez")
                .dni(dni)
                .email("juan.perez@email.com")
                .telefono("123456789")
                .build();

        when(clienteService.getByDni(dni)).thenReturn(clienteResponse);

        mockMvc.perform(get("/v1/clientes/dni/{dni}", dni)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value(dni))
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$.email").value("juan.perez@email.com"))
                .andExpect(jsonPath("$.telefono").value("123456789"));

        verify(clienteService).getByDni(dni);
    }

    @Test
    void GetByDniNotFound() throws Exception {
        String dni = "99999999X";

        when(clienteService.getByDni(dni)).thenThrow(new ClienteNotFoundByDni(dni));

        mockMvc.perform(get("/v1/clientes/dni/{dni}", dni)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(clienteService).getByDni(dni);
    }



    @Test
    void Save() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .calle("Calle Falsa")
                .numero("123")
                .codigoPostal("28080")
                .piso("3")
                .letra("A")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("123456789")
                .isDeleted(false)
                .build();

        ClienteResponse clienteResponse = ClienteResponse.builder()
                .guid("unique-guid")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .calle("Calle Falsa")
                .numero("123")
                .codigoPostal("28080")
                .piso("3")
                .letra("A")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("123456789")
                .createdAt("2024-11-28T10:00:00Z")
                .updatedAt("2024-11-28T10:00:00Z")
                .isDeleted(false)
                .build();

        when(clienteService.save(any(ClienteRequestSave.class))).thenReturn(clienteResponse);

        mockMvc.perform(post("/v1/clientes")
                        .contentType("application/json")
                        .content("{"
                                + "\"dni\": \"12345678A\","
                                + "\"nombre\": \"Juan\","
                                + "\"apellidos\": \"Perez\","
                                + "\"calle\": \"Calle Falsa\","
                                + "\"numero\": \"123\","
                                + "\"codigoPostal\": \"28080\","
                                + "\"piso\": \"3\","
                                + "\"letra\": \"A\","
                                + "\"email\": \"juan.perez@example.com\","
                                + "\"telefono\": \"123456789\","
                                + "\"fotoPerfil\": \"fotoprfil.jpg\","
                                + "\"fotoDni\": \"fotodni.jpg\","
                                + "\"userId\": \"123456789\","
                                + "\"isDeleted\": false"
                                + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.guid").value("unique-guid"))
                .andExpect(jsonPath("$.dni").value("12345678A"))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellidos").value("Perez"))
                .andExpect(jsonPath("$.calle").value("Calle Falsa"))
                .andExpect(jsonPath("$.numero").value("123"))
                .andExpect(jsonPath("$.codigoPostal").value("28080"))
                .andExpect(jsonPath("$.piso").value("3"))
                .andExpect(jsonPath("$.letra").value("A"))
                .andExpect(jsonPath("$.email").value("juan.perez@example.com"))
                .andExpect(jsonPath("$.telefono").value("123456789"))
                .andExpect(jsonPath("$.fotoPerfil").value("fotoprfil.jpg"))
                .andExpect(jsonPath("$.fotoDni").value("fotodni.jpg"))
                .andExpect(jsonPath("$.userId").value("123456789"))
                .andExpect(jsonPath("$.createdAt").value("2024-11-28T10:00:00Z"))
                .andExpect(jsonPath("$.updatedAt").value("2024-11-28T10:00:00Z"))
                .andExpect(jsonPath("$.isDeleted").value(false));
    }


    @Test
    void InvalidDni() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .dni("1234567A")
                .nombre("Juan")
                .apellidos("Perez")
                .calle("Calle Falsa")
                .numero("123")
                .codigoPostal("28080")
                .piso("3")
                .letra("A")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("123456789")
                .isDeleted(false)
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El DNI debe tener 8 numeros seguidos de una letra"))
        );
    }

    @Test
    void EmptyNombre() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .dni("12345678A")
                .nombre("")
                .apellidos("Perez")
                .calle("Calle Falsa")
                .numero("123")
                .codigoPostal("28080")
                .piso("3")
                .letra("A")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("123456789")
                .isDeleted(false)
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El nombre no puede estar vacio"))
        );
    }

    @Test
    void EmptyApellidos() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("")
                .calle("Calle Falsa")
                .numero("123")
                .codigoPostal("28080")
                .piso("3")
                .letra("A")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("123456789")
                .isDeleted(false)
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("Los apellidos no pueden estar vacio"))
        );
    }

    @Test
    void InvalidEmail() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .calle("Calle Falsa")
                .numero("123")
                .codigoPostal("28080")
                .piso("3")
                .letra("A")
                .email("wd")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("123456789")
                .isDeleted(false)
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El email debe ser valido"))
        );
    }

    @Test
    void nullEmail() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .calle("Calle Falsa")
                .numero("123")
                .codigoPostal("28080")
                .piso("3")
                .letra("A")
                .email(null)
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("123456789")
                .isDeleted(false)
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        Map<String, String> errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        assertEquals("El email no puede ser nulo", errorResponse.get("email"));
    }

    @Test
    void InvalidTelefono() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .calle("Calle Falsa")
                .numero("123")
                .codigoPostal("28080")
                .piso("3")
                .letra("A")
                .email("juan.perez@example.com")
                .telefono("123489")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("123456789")
                .isDeleted(false)
                .build();

        MockHttpServletResponse result = mockMvc.perform(
                        post("/v1/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatus()),
                () -> assertTrue(result.getContentAsString().contains("El telefono debe tener 9 numeros"))
        );
    }

    @Test
    void EmptyTelefono() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .calle("Calle Falsa")
                .numero("123")
                .codigoPostal("28080")
                .piso("3")
                .letra("A")
                .email("juan.perez@example.com")
                .telefono("")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("123456789")
                .isDeleted(false)
                .build();

        MockHttpServletResponse result = mockMvc.perform(
                        post("/v1/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatus());

        Map<String, String> errorResponse = objectMapper.readValue(result.getContentAsString(), Map.class);

        assertEquals("El telefono debe tener 9 numeros", errorResponse.get("telefono"));
    }

    @Test
    void emptyCalleSave() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .calle("")
                .numero("1")
                .codigoPostal("28001")
                .piso("1")
                .letra("A")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("La calle no puede estar vacia"))
        );
    }

    @Test
    void emptyNumeroSave() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .calle("Calle Falsa")
                .numero("")
                .codigoPostal("28001")
                .piso("1")
                .letra("A")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        Map<String, String> errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        assertEquals("El numero no puede estar vacio", errorResponse.get("numero"));
    }

    @Test
    void nullCodigoPostalSave() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .calle("Calle Falsa")
                .numero("1")
                .codigoPostal(null)
                .piso("1")
                .letra("A")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        Map<String, String> errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        assertEquals("El codigo postal no puede ser nulo", errorResponse.get("codigoPostal"));
    }

    @Test
    void invalidCodigoPostalSave() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .calle("Calle Falsa")
                .numero("1")
                .codigoPostal("1234")
                .piso("1")
                .letra("A")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El codigo postal debe tener 5 numeros"))
        );
    }

    @Test
    void emptyPisoSave() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .calle("Calle Falsa")
                .numero("1")
                .codigoPostal("28001")
                .piso("")
                .letra("A")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El piso no puede estar vacio"))
        );
    }

    @Test
    void emptyLetraSave() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .calle("Calle Falsa")
                .numero("1")
                .codigoPostal("28001")
                .piso("1")
                .letra("")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("La letra no puede estar vacia"))
        );
    }


    @Test
    void Update() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .calle("Calle Falsa")
                .numero("123")
                .codigoPostal("28080")
                .piso("3")
                .letra("A")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .build();

        ClienteResponse clienteResponse = ClienteResponse.builder()
                .guid("unique-guid")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .calle("Calle Falsa")
                .numero("123")
                .codigoPostal("28080")
                .piso("3")
                .letra("A")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("123456789")
                .createdAt("2024-11-28T10:00:00Z")
                .updatedAt("2024-11-28T10:00:00Z")
                .isDeleted(false)
                .build();

        when(clienteService.update(eq("unique-guid"), any(ClienteRequestUpdate.class)))
                .thenReturn(clienteResponse);

        mockMvc.perform(put("/v1/clientes/unique-guid")
                        .contentType("application/json")
                        .content("{"
                                + "\"nombre\": \"Juan\","
                                + "\"apellidos\": \"Perez\","
                                + "\"calle\": \"Calle Falsa\","
                                + "\"numero\": \"123\","
                                + "\"codigoPostal\": \"28080\","
                                + "\"piso\": \"3\","
                                + "\"letra\": \"A\","
                                + "\"email\": \"juan.perez@example.com\","
                                + "\"telefono\": \"123456789\","
                                + "\"fotoPerfil\": \"fotoprfil.jpg\","
                                + "\"fotoDni\": \"fotodni.jpg\","
                                + "\"userId\": \"123456789\""
                                + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").value("unique-guid"))
                .andExpect(jsonPath("$.dni").value("12345678A"))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellidos").value("Perez"))
                .andExpect(jsonPath("$.calle").value("Calle Falsa"))
                .andExpect(jsonPath("$.numero").value("123"))
                .andExpect(jsonPath("$.codigoPostal").value("28080"))
                .andExpect(jsonPath("$.piso").value("3"))
                .andExpect(jsonPath("$.letra").value("A"))
                .andExpect(jsonPath("$.email").value("juan.perez@example.com"))
                .andExpect(jsonPath("$.telefono").value("123456789"))
                .andExpect(jsonPath("$.fotoPerfil").value("fotoprfil.jpg"))
                .andExpect(jsonPath("$.fotoDni").value("fotodni.jpg"))
                .andExpect(jsonPath("$.userId").value("123456789"))
                .andExpect(jsonPath("$.createdAt").value("2024-11-28T10:00:00Z"))
                .andExpect(jsonPath("$.updatedAt").value("2024-11-28T10:00:00Z"))
                .andExpect(jsonPath("$.isDeleted").value(false));
    }

    @Test
    void emptyNombreUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("")
                .apellidos("Perez")
                .calle("Calle Falsa")
                .numero("123")
                .codigoPostal("28080")
                .piso("3")
                .letra("A")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .build();

        MvcResult result = mockMvc.perform(
                        put("/v1/clientes/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El nombre no puede estar vacio"))
        );
    }

    @Test
    void emptyApellidosUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos(" ")
                .calle("Calle Falsa")
                .numero("123")
                .codigoPostal("28080")
                .piso("3")
                .letra("A")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .build();

        MvcResult result = mockMvc.perform(
                        put("/v1/clientes/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("Los apellidos no pueden estar vacio"))
        );
    }

    @Test
    void invalidEmailUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .calle("Calle Falsa")
                .numero("123")
                .codigoPostal("28080")
                .piso("3")
                .letra("A")
                .email("a")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .build();

        MvcResult result = mockMvc.perform(
                        put("/v1/clientes/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El email debe ser valido"))
        );
    }

    @Test
    void nullEmailUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .calle("Calle Falsa")
                .numero("123")
                .codigoPostal("28080")
                .piso("3")
                .letra("A")
                .email(null)
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .build();

        MvcResult result = mockMvc.perform(
                        put("/v1/clientes/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        Map<String, String> errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        assertEquals("El email no puede ser un campo nulo", errorResponse.get("email"));
    }
    @Test
    void invalidTelefonoUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .calle("Calle Falsa")
                .numero("123")
                .codigoPostal("28080")
                .piso("3")
                .letra("A")
                .email("juan.perez@example.com")
                .telefono("13214")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .build();
        MvcResult result = mockMvc.perform(
                        put("/v1/clientes/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(responseContent.contains("El telefono debe tener 9 numeros"))
        );
    }

    @Test
    void emptyTelefonoUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .calle("Calle Falsa")
                .numero("123")
                .codigoPostal("28080")
                .piso("3")
                .letra("A")
                .email("juan.perez@example.com")
                .telefono("")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .build();

        MvcResult result = mockMvc.perform(
                        put("/v1/clientes/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        Map<String, String> errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        assertEquals("El telefono debe tener 9 numeros", errorResponse.get("telefono"));
    }

    @Test
    void emptyFotoPerfilUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .calle("Calle Falsa")
                .numero("123")
                .codigoPostal("28080")
                .piso("3")
                .letra("A")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil(" ")
                .fotoDni("fotodni.jpg")
                .build();

        MvcResult result = mockMvc.perform(
                        put("/v1/clientes/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("La foto de perfil no puede estar vacia"))
        );
    }

    @Test
    void emptyFotoDniUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .calle("Calle Falsa")
                .numero("123")
                .codigoPostal("28080")
                .piso("3")
                .letra("A")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni(" ")
                .build();
        MvcResult result = mockMvc.perform(
                        put("/v1/clientes/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("La foto del DNI no puede estar vacia"))
        );
    }

    @Test
    void emptyCalleUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .calle("")
                .numero("1")
                .codigoPostal("28001")
                .piso("1")
                .letra("A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .build();

        MvcResult result = mockMvc.perform(
                        put("/v1/clientes/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("La calle no puede estar vacia"))
        );
    }

    @Test
    void emptyNumeroUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .calle("Calle Falsa")
                .numero("")
                .codigoPostal("28001")
                .piso("1")
                .letra("A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .build();

        MvcResult result = mockMvc.perform(
                        put("/v1/clientes/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        Map<String, String> errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        assertEquals("El numero no puede estar vacio", errorResponse.get("numero"));
    }

    @Test
    void nullCodigoPostalUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .calle("Calle Falsa")
                .numero("1")
                .codigoPostal(null)
                .piso("1")
                .letra("A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .build();

        MvcResult result = mockMvc.perform(
                        put("/v1/clientes/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        Map<String, String> errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        assertEquals("El codigo postal no puede ser nulo", errorResponse.get("codigoPostal"));
    }

    @Test
    void invalidCodigoPostalUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .calle("Calle Falsa")
                .numero("1")
                .codigoPostal("1234")
                .piso("1")
                .letra("A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .build();

        MvcResult result = mockMvc.perform(
                        put("/v1/clientes/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El codigo postal debe tener 5 numeros"))
        );
    }

    @Test
    void emptyPisoUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .calle("Calle Falsa")
                .numero("1")
                .codigoPostal("28001")
                .piso("")
                .letra("A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .build();

        MvcResult result = mockMvc.perform(
                        put("/v1/clientes/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El piso no puede estar vacio"))
        );
    }

    @Test
    void emptyLetraUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .calle("Calle Falsa")
                .numero("1")
                .codigoPostal("28001")
                .piso("1")
                .letra("")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .build();

        MvcResult result = mockMvc.perform(
                        put("/v1/clientes/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("La letra no puede estar vacia"))
        );
    }


    @Test
    void Delete() throws Exception {
        Mockito.doNothing().when(clienteService).deleteById("unique-guid");

        mockMvc.perform(delete("/v1/clientes/unique-guid"))
                .andExpect(status().isNoContent());
    }

    /*@Test
    public void GetUserProfile() throws Exception {
        String token = jwtTokenUtil.generateToken("testUser", "userId-123", "USER");

        ClienteResponse clienteResponse = ClienteResponse.builder()
                .guid("user-guid-123")
                .dni("12345678A")
                .nombre("Test")
                .apellidos("User")
                .email("test@example.com")
                .telefono("123456789")
                .fotoPerfil("http://example.com/foto.jpg")
                .fotoDni("http://example.com/fotoDni.jpg")
                .userId("userId-123")
                .createdAt("2024-11-27T00:00:00")
                .updatedAt("2024-11-27T00:00:00")
                .isDeleted(false)
                .build();

        when(clienteService.getUserByGuid("user-guid-123")).thenReturn(clienteResponse);

        mockMvc.perform(get("/api/v1/clientes/me/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").value("user-guid-123"))
                .andExpect(jsonPath("$.dni").value("12345678A"))
                .andExpect(jsonPath("$.nombre").value("Test"))
                .andExpect(jsonPath("$.apellidos").value("User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.telefono").value("123456789"))
                .andExpect(jsonPath("$.fotoPerfil").value("http://example.com/foto.jpg"))
                .andDo(print());
    }*/


    
    @Test
    void handleValidationExceptionUpdateError() throws Exception {
        var result = mockMvc.perform(put("/v1/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nombre\": \"\", \"apellidos\": \"\", \"email\": \"\", \"telefono\": \"\" }")) // Campos vacíos
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").value("El nombre no puede estar vacio"))
                .andExpect(jsonPath("$.apellidos").value("Los apellidos no pueden estar vacio"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println(responseContent);

        assertAll(
                () -> assertTrue(responseContent.contains("\"email\":\"El email no puede estar vacio\"")
                        || responseContent.contains("\"email\":\"El email debe ser valido\"")),
                () -> assertTrue(responseContent.contains("\"telefono\":\"El telefono no puede estar vacio\"")
                        || responseContent.contains("\"telefono\":\"El telefono debe tener 9 numeros\""))
        );
    }

    @Test
    void GetCatalogo() throws Exception {

        List<TipoCuentaResponseCatalogo> tiposCuentasResponse = List.of(
                TipoCuentaResponseCatalogo.builder()
                        .nombre("Cuenta Corriente")
                        .interes("1.5")
                        .build(),
                TipoCuentaResponseCatalogo.builder()
                        .nombre("Cuenta de Ahorros")
                        .interes("2.0")
                        .build()
        );

        List<TipoTarjeta> tiposTarjetas = List.of(TipoTarjeta.DEBITO, TipoTarjeta.CREDITO);

        ClienteProducto clienteProducto = ClienteProducto.builder()
                .tiposCuentas(tiposCuentasResponse)
                .tiposTarjetas(tiposTarjetas)
                .build();

        when(clienteService.getCatalogue()).thenReturn(clienteProducto);

        mockMvc.perform(get("/v1/clientes/catalogo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tiposCuentas").isArray())
                .andExpect(jsonPath("$.tiposCuentas[0].nombre").value("Cuenta Corriente"))
                .andExpect(jsonPath("$.tiposCuentas[0].interes").value("1.5"))
                .andExpect(jsonPath("$.tiposCuentas[1].nombre").value("Cuenta de Ahorros"))
                .andExpect(jsonPath("$.tiposCuentas[1].interes").value("2.0"))
                .andExpect(jsonPath("$.tiposTarjetas").isArray())
                .andExpect(jsonPath("$.tiposTarjetas[0]").value("DEBITO"))
                .andExpect(jsonPath("$.tiposTarjetas[1]").value("CREDITO"));

        verify(clienteService).getCatalogue();
    }

    @Test
    void GetCatalogoEmpty() throws Exception {

        ClienteProducto clienteProducto = ClienteProducto.builder()
                .tiposCuentas(Collections.emptyList())
                .tiposTarjetas(Collections.emptyList())
                .build();

        when(clienteService.getCatalogue()).thenReturn(clienteProducto);

        mockMvc.perform(get("/v1/clientes/catalogo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tiposCuentas").isEmpty())
                .andExpect(jsonPath("$.tiposTarjetas").isEmpty());

        verify(clienteService).getCatalogue();
    }
}
