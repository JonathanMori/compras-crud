package com.avaliacaocomprascrud.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.avaliacaocomprascrud.models.Compra;
import com.avaliacaocomprascrud.models.Item;
import com.avaliacaocomprascrud.models.Usuario;
import com.avaliacaocomprascrud.repositories.ICompraRepository;
import com.avaliacaocomprascrud.repositories.IItemRepository;

@Controller
public class CompraController {

	@Autowired
	private ICompraRepository iCR;
	
	@Autowired
	private IItemRepository iIR;
	
	@RequestMapping(value="/listaCompras", method=RequestMethod.GET )
	public ModelAndView listaCompras(HttpSession session) {
		Usuario user = (Usuario) session.getAttribute("usuario");
		ModelAndView mv = new ModelAndView("compra/listaCompras");
		Iterable<Compra> compras = iCR.findByDono(user.getId());
		mv.addObject("compras", compras);
		return mv;
	}
	
	@RequestMapping(value="/cadastrarCompra", method=RequestMethod.GET )
	public String cadastrarCompra() {
		return "compra/formCompras";
	}
	
	@RequestMapping(value="/cadastrarCompra", method=RequestMethod.POST )
	public String cadastrarCompra(@Valid Compra compra, BindingResult result, RedirectAttributes attributes, HttpSession session) {
		if(result.hasErrors()) {
			attributes.addFlashAttribute("mensagem", "Verifique os campos!");
			return "redirect:/cadastrarCompra";
		}
		Usuario user = (Usuario) session.getAttribute("usuario");
		iCR.save(compra);
		attributes.addFlashAttribute("mensagem", "Compra adicionada com sucesso!");
		return "redirect:/listaCompras";
	}
	
	@RequestMapping("/deletarCompra")
	public String deletarCompra(Long compraId) {
		Compra compra = iCR.findByCompraId(compraId);
		Iterable<Item> itens = iIR.findByCompra(compra);
		for (Item item : itens) {
			iIR.delete(item);
		}
		iCR.delete(compra);
		return "redirect:/listaCompras";
	}
	
	@RequestMapping(value="/{compraId}", method=RequestMethod.GET)
	public ModelAndView detalhesCompra(@PathVariable("compraId") long compraId, HttpSession session) {
		Compra compra = iCR.findByCompraId(compraId);
		Usuario user = (Usuario) session.getAttribute("usuario");
		if (user.getId() == compra.getDono()) {
			ModelAndView mv = new ModelAndView("compra/detalhesCompra");
			Iterable<Item> itens = iIR.findByCompra(compra);
			mv.addObject("compra", compra);
			mv.addObject("itens", itens);
			return mv;
		}
		else
			return null;
	}
	
	@RequestMapping(value="/{compraId}", method=RequestMethod.POST)
	public String detalhesCompraPost(@PathVariable("compraId") long compraId, @Valid Item item, BindingResult result, RedirectAttributes attributes) {
		if(result.hasErrors()) {
			attributes.addFlashAttribute("mensagem", "Verifique os campos!");
			return "redirect:/{compraId}";
		}
		Compra compra = iCR.findByCompraId(compraId);
		item.setCompra(compra);
		iIR.save(item);
		Iterable<Item> itens = iIR.findByCompra(compra);
		double valorTotal = 0.0;
		for (Item itm : itens) {
			valorTotal += itm.getPreco();
		}
		compra.setTotal(valorTotal);
		iCR.save(compra);
		attributes.addFlashAttribute("mensagem", "Item adicionado com sucesso!");
		return "redirect:/{compraId}";
	}
	
	@RequestMapping("/deletarItem")
	public String deletarItem(Long itemId) {
		Item item = iIR.findByItemId(itemId);
		double valorTotal = 0.0;
		Compra compra = item.getCompra();
		valorTotal = compra.getTotal() - item.getPreco();
		compra.setTotal(valorTotal);
		iCR.save(compra);
		iIR.delete(item);
		long compraId = compra.getCompraId();
		String codigo = "" + compraId;
		return "redirect:/" + codigo;
	}
}
