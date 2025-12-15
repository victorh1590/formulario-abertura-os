package br.com.grupojgv.routine.task;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.lang.reflect.Field;

public class TypedVOUtils {
    public static DynamicVO vo(TypedVO cab, String instanceName) throws Exception {
        EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
        DynamicVO cabVO = (DynamicVO) dwfFacade.getDAOInstance(instanceName).getDefaultValueObjectInstance();
        for(Field f: cab.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            String key = f.getName();
            Object value = f.get(cab);
            cabVO.setProperty(key, value);
        }
        return cabVO;
    }
}
