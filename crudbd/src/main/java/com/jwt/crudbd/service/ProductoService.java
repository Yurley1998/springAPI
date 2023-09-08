package com.jwt.crudbd.service;

import com.jwt.crudbd.entity.Producto;
import com.jwt.crudbd.repository.ProductoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductoService {
    @Autowired
    ProductoRepository productoRepository;
    public List<Producto> listAll(){

        return productoRepository.findAll();
    }
    public Optional<Producto> getOne(int id){
        return productoRepository.findById(id);
    }
    public Optional<Producto> getByNombre(String nombre){
        return productoRepository.findByNombre(nombre);
    }
    public void save(Producto producto){
        productoRepository.save(producto);
    }
    public void delete(int id){
        productoRepository.deleteById(id);
    }
    public boolean existsById(int id){
        return productoRepository.existsById(id);
    }
    public boolean existsByNombre(String nombre){
        return productoRepository.existsByNombre(nombre);
    }
}
