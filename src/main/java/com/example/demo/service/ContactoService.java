package com.example.demo.service;

import com.example.demo.model.Contacto;
import com.example.demo.repository.ContactoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContactoService {
    private final ContactoRepository contactoRepository;

    public ContactoService(ContactoRepository contactoRepository) {
        this.contactoRepository = contactoRepository;
    }

    public List<Contacto> obtenerTodos() {
        return contactoRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaEnvio"));
    }

    @Transactional
    public Contacto guardar(Contacto contacto) {
        contacto.setIdContacto(null);
        contacto.setLeido(false);
        return contactoRepository.save(contacto);
    }

    @Transactional
    public Contacto marcarLeido(Integer id) {
        Contacto c = contactoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
        c.setLeido(true);
        return contactoRepository.save(c);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!contactoRepository.existsById(id)) throw new RuntimeException("Mensaje no encontrado");
        contactoRepository.deleteById(id);
    }
}
