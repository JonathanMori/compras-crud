package com.avaliacaocomprascrud.repositories;

import org.springframework.data.repository.CrudRepository;

import com.avaliacaocomprascrud.models.Compra;

public interface ICompraRepository extends CrudRepository<Compra, String> {
	Compra findByCompraId(long compraId);
	Iterable<Compra> findByDono(long dono);
}
