package br.com.grupojgv.utils;

import br.com.grupojgv.annotations.PkMember;
import br.com.grupojgv.model.FormularioFormatado;
import br.com.grupojgv.model.OrcamentoOS;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import lombok.extern.jbosslog.JBossLog;

import java.lang.reflect.Field;

@JBossLog
public class CommandUtils {
    public static void insertFormKvps(FluidUpdateVO fluidUpdateVO, FormularioFormatado form) throws IllegalAccessException {
        for(Field f: OrcamentoOS.class.getDeclaredFields()) {
            f.setAccessible(true);
            String key = f.getName();
            Object value = f.get(form);
            log.info("Inserting field " + key);
            fluidUpdateVO.set(key, value);
        }
    }

    public static void insertFormKvps(FluidCreateVO fluidVO, FormularioFormatado form) throws  IllegalAccessException {
        for(Field f: OrcamentoOS.class.getDeclaredFields()) {
            f.setAccessible(true);
            String key = f.getName();
            Object value = f.get(form);
            log.info("Inserting field " + key);
            fluidVO.set(key, value);
        }
    }

    public static void setFieldsFromKvps(Registro registro, FormularioFormatado form) throws Exception {
        for(Field f: OrcamentoOS.class.getDeclaredFields()) {
            f.setAccessible(true);
            String key = f.getName();
            Object value = f.get(form);
            log.info("Inserting field " + key);
            if(!f.isAnnotationPresent(PkMember.class)) {
                registro.setCampo(key, value);
            }
        }
    }
}
