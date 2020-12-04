package com.avaliacaocomprascrud.repositories;

import org.springframework.data.repository.CrudRepository;

import com.avaliacaocomprascrud.models.Compra;
import com.avaliacaocomprascrud.models.Item;

public interface IItemRepository extends CrudRepository<Item, String> {
	Item findByItemId(long itemId);
	Iterable<Item> findByCompra(Compra compra);
}
