package com.cfp.controlfinanciero.service;

import com.cfp.controlfinanciero.domain.Categoria;
import com.cfp.controlfinanciero.domain.Gasto;
import com.cfp.controlfinanciero.domain.Usuario;
import com.cfp.controlfinanciero.dto.GastoMesResponse;
import com.cfp.controlfinanciero.dto.GastoRequest;
import com.cfp.controlfinanciero.dto.GastoResponse;
import com.cfp.controlfinanciero.exception.BadRequestException;
import com.cfp.controlfinanciero.exception.ForbiddenException;
import com.cfp.controlfinanciero.exception.NotFoundException;
import com.cfp.controlfinanciero.exception.UnprocessableException;
import com.cfp.controlfinanciero.repository.CategoriaRepository;
import com.cfp.controlfinanciero.repository.GastoRepository;
import com.cfp.controlfinanciero.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GastoService {
    private final GastoRepository gastoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public GastoService(GastoRepository gastoRepository, CategoriaRepository categoriaRepository, UsuarioRepository usuarioRepository) {
        this.gastoRepository = gastoRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new BadRequestException("Debe iniciar sesión para registrar gastos");
        }
        return usuarioRepository.findByCorreo(auth.getName())
                .orElseThrow(() -> new BadRequestException("Debe iniciar sesión para registrar gastos"));
    }

    public GastoResponse registrar(GastoRequest req) {
        Usuario usuario = usuarioActual();

        // monto
        if (req.getMonto() == null) {
            throw new BadRequestException("El monto debe ser un número mayor que cero");
        }
        long monto;
        if (req.getMonto() instanceof Number n) {
            monto = n.longValue();
        } else {
            try {
                monto = Long.parseLong(String.valueOf(req.getMonto()).trim());
            } catch (Exception e) {
                throw new BadRequestException("El monto debe ser un número mayor que cero");
            }
        }
        if (monto <= 0) {
            throw new BadRequestException("El monto debe ser un número mayor que cero");
        }

        // categoria
        if (req.getCategoria() == null || req.getCategoria().trim().isEmpty()) {
            throw new BadRequestException("Debe seleccionar una categoría");
        }
        String codigo = req.getCategoria().trim().toUpperCase();
        Categoria categoria = categoriaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new UnprocessableException("La categoría no es válida. Use una de las cinco predeterminadas"));

        // fecha
        if (req.getFecha() == null || req.getFecha().trim().isEmpty()) {
            throw new BadRequestException("La fecha es obligatoria y debe ser válida");
        }
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(req.getFecha().trim());
        } catch (Exception e) {
            throw new BadRequestException("La fecha es obligatoria y debe ser válida");
        }

        // nota
        String nota = req.getNota();
        if (nota != null && nota.length() > 200) {
            throw new BadRequestException("La nota no puede superar 200 caracteres");
        }

        Gasto gasto = new Gasto();
        gasto.setUsuario(usuario);
        gasto.setCategoria(categoria);
        gasto.setMonto(monto);
        gasto.setFecha(fecha);
        gasto.setNota(nota);
        gasto = gastoRepository.save(gasto);

        YearMonth ym = YearMonth.from(fecha);
        LocalDate inicio = ym.atDay(1);
        LocalDate fin = ym.atEndOfMonth();
        Long totalMes = gastoRepository.sumMontoByUsuarioAndFechaBetween(usuario, inicio, fin);
        Long totalCategoria = gastoRepository.sumMontoByUsuarioAndCategoriaAndFechaBetween(usuario, categoria, inicio, fin);

        GastoResponse res = new GastoResponse();
        res.setMensaje("Gasto registrado");
        res.setId(gasto.getId());
        res.setMonto(gasto.getMonto());
        res.setCategoria(categoria.getCodigo());
        res.setFecha(gasto.getFecha());
        res.setNota(gasto.getNota());
        res.setTotalMes(totalMes);
        res.setTotalCategoria(totalCategoria);
        res.setFechaRegistro(gasto.getFechaRegistro());
        return res;
    }

    public GastoMesResponse listarPorMes(String mesParam) {
        Usuario usuario = usuarioActual();
        YearMonth ym;
        if (mesParam == null || mesParam.trim().isEmpty()) {
            ym = YearMonth.now();
        } else {
            String v = mesParam.trim();
            if (!v.matches("\\d{4}-\\d{2}")) {
                throw new BadRequestException("El mes debe tener el formato AAAA-MM");
            }
            try {
                ym = YearMonth.parse(v);
            } catch (Exception e) {
                throw new BadRequestException("El mes debe tener el formato AAAA-MM");
            }
        }
        LocalDate inicio = ym.atDay(1);
        LocalDate fin = ym.atEndOfMonth();
        List<Gasto> gastos = gastoRepository.findByUsuarioAndFechaBetween(usuario, inicio, fin);
        Long totalMes = gastoRepository.sumMontoByUsuarioAndFechaBetween(usuario, inicio, fin);

        List<GastoResponse> lista = gastos.stream().map(g -> {
            GastoResponse r = new GastoResponse();
            r.setId(g.getId());
            r.setMonto(g.getMonto());
            r.setCategoria(g.getCategoria().getCodigo());
            r.setFecha(g.getFecha());
            r.setNota(g.getNota());
            r.setFechaRegistro(g.getFechaRegistro());
            return r;
        }).collect(Collectors.toList());

        GastoMesResponse res = new GastoMesResponse();
        res.setGastos(lista);
        res.setTotalMes(totalMes);
        return res;
    }

    public GastoResponse obtenerPorId(Long id) {
        Usuario usuario = usuarioActual();
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El gasto no existe"));
        if (!gasto.getUsuario().getId().equals(usuario.getId())) {
            throw new ForbiddenException("No tiene permiso para ver este gasto");
        }
        GastoResponse res = new GastoResponse();
        res.setId(gasto.getId());
        res.setMonto(gasto.getMonto());
        res.setCategoria(gasto.getCategoria().getCodigo());
        res.setFecha(gasto.getFecha());
        res.setNota(gasto.getNota());
        res.setFechaRegistro(gasto.getFechaRegistro());
        return res;
    }
}
