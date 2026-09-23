package com.uade.tpo.marketplace.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uade.tpo.marketplace.entity.Categoria;
import com.uade.tpo.marketplace.entity.Producto;
import com.uade.tpo.marketplace.entity.Usuario;
import com.uade.tpo.marketplace.repository.CategoriaRepository;
import com.uade.tpo.marketplace.repository.ProductoRepository;
import com.uade.tpo.marketplace.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
@Transactional
public class CatalogoControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private CatalogoController catalogoController;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Categoria catElectronica;
    private Categoria catHogar;

    @BeforeEach
    void setUp() {
        // Use existing categories and users from data.sql
        Usuario vendedor = usuarioRepository.findByUsername("jperez").orElseGet(() -> {
            Usuario u = new Usuario();
            u.setUsername("vendedor1");
            u.setNombre("Juan");
            u.setApellido("Perez");
            u.setEmail("vendedor@test.com");
            u.setPassword("123456");
            return usuarioRepository.save(u);
        });

        catElectronica = categoriaRepository.findAll().stream()
                .filter(c -> c.getNombre().equals("Electronica"))
                .findFirst()
                .orElseGet(() -> categoriaRepository.save(new Categoria("Electronica")));
                
        catHogar = categoriaRepository.findAll().stream()
                .filter(c -> c.getNombre().equals("Hogar"))
                .findFirst()
                .orElseGet(() -> categoriaRepository.save(new Categoria("Hogar")));

        this.mockMvc = MockMvcBuilders.standaloneSetup(catalogoController).build();

        productoRepository.deleteAll();
        productoRepository.flush();

        // Producto A (Electronica, ZZZ nombre para testear orden)
        Producto p1 = new Producto();
        p1.setNombre("Zapatilla Electrica");
        p1.setDescripcion("Zapatilla con multiples tomas");
        p1.setPrecio(new BigDecimal("1500.00"));
        p1.setStock(10);
        p1.setActivo(true);
        p1.setVendedor(vendedor);
        p1.setCategorias(Set.of(catElectronica));
        productoRepository.save(p1);

        // Producto B (Electronica, AAA nombre)
        Producto p2 = new Producto();
        p2.setNombre("Auriculares Bluetooth");
        p2.setDescripcion("Auriculares inalambricos");
        p2.setPrecio(new BigDecimal("5000.00"));
        p2.setStock(5);
        p2.setActivo(true);
        p2.setVendedor(vendedor);
        p2.setCategorias(Set.of(catElectronica));
        productoRepository.save(p2);

        // Producto C (Hogar, AAA nombre)
        Producto p3 = new Producto();
        p3.setNombre("Almohadon");
        p3.setDescripcion("Almohadon de plumas");
        p3.setPrecio(new BigDecimal("2000.00"));
        p3.setStock(20);
        p3.setActivo(true);
        p3.setVendedor(vendedor);
        p3.setCategorias(Set.of(catHogar));
        productoRepository.save(p3);

        // Producto D (Inactivo, no deberia verse)
        Producto p4 = new Producto();
        p4.setNombre("Mouse Inactivo");
        p4.setDescripcion("No deberia verse");
        p4.setPrecio(new BigDecimal("1000.00"));
        p4.setStock(0);
        p4.setActivo(false);
        p4.setVendedor(vendedor);
        p4.setCategorias(Set.of(catElectronica));
        productoRepository.save(p4);
        
        // Producto E (Electronica, nombre "Auriculares con cable")
        Producto p5 = new Producto();
        p5.setNombre("Auriculares con cable");
        p5.setDescripcion("Auriculares simples");
        p5.setPrecio(new BigDecimal("1000.00"));
        p5.setStock(50);
        p5.setActivo(true);
        p5.setVendedor(vendedor);
        p5.setCategorias(Set.of(catElectronica));
        productoRepository.save(p5);
        
        productoRepository.flush();
    }

    @Test
    void getCatalogo_FiltroNombreCategoriaOrdenYPagina_DevuelveCorrectamente() throws Exception {
        // Queremos buscar nombre = "auriculares", categoriaId = catElectronica.getId()
        // Esperamos "Auriculares Bluetooth" y "Auriculares con cable" en orden alfabetico
        // Con page=0 y size=1 deberia devolver solo el primero ("Auriculares Bluetooth")
        
        mockMvc.perform(get("/api/productos")
                .param("nombre", "auric")
                .param("categoriaId", catElectronica.getId().toString())
                .param("page", "0")
                .param("size", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nombre", is("Auriculares Bluetooth")))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(2)));
                
        // Pagina 2
        mockMvc.perform(get("/api/productos")
                .param("nombre", "auric")
                .param("categoriaId", catElectronica.getId().toString())
                .param("page", "1")
                .param("size", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nombre", is("Auriculares con cable")));
    }
    
    @Test
    void getCatalogo_SinFiltros_DevuelveTodosLosActivosEnOrdenAlfabetico() throws Exception {
        // Almohadon, Auriculares Bluetooth, Auriculares con cable, Zapatilla Electrica (4 activos)
        mockMvc.perform(get("/api/productos")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(4)))
                .andExpect(jsonPath("$.content[0].nombre", is("Almohadon")))
                .andExpect(jsonPath("$.content[1].nombre", is("Auriculares Bluetooth")))
                .andExpect(jsonPath("$.content[2].nombre", is("Auriculares con cable")))
                .andExpect(jsonPath("$.content[3].nombre", is("Zapatilla Electrica")));
    }

    @Test
    void getCatalogo_NoDevuelveProductosInactivos() throws Exception {
        // "Mouse Inactivo" is active=false, should not be in the catalog
        mockMvc.perform(get("/api/productos")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].nombre", not(hasItem("Mouse Inactivo"))));
    }
    @Test
    void getCatalogo_FiltroRangoDePrecio() throws Exception {
        // precioMin = 1500, precioMax = 3000
        // Esperamos "Almohadon" (2000) y "Zapatilla Electrica" (1500)
        mockMvc.perform(get("/api/productos")
                .param("precioMin", "1500.00")
                .param("precioMax", "3000.00")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].nombre", is("Almohadon")))
                .andExpect(jsonPath("$.content[1].nombre", is("Zapatilla Electrica")));
    }
}
