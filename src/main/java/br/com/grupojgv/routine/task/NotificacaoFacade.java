package br.com.grupojgv.routine.task;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.jape.EntityFacade;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class NotificacaoFacade {
    /**
     * Envia um aviso do sistema para um usuário específico.
     *
     * @param codUsuarioDestino BigDecimal com o CODUSU do destinatário.
     * @param titulo String com o título do aviso.
     * @param descricao String com o corpo da mensagem.
     * @param importancia Inteiro (1=Alta, 2=Média, 3=Baixa).
     * @throws Exception
     */
    public void enviarAvisoUsuario(BigDecimal codUsuarioDestino, String titulo, String descricao, int importancia) throws Exception {
        EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();

        DynamicVO avisoVO = (DynamicVO) dwfFacade.getDefaultValueObjectInstance("AvisoSistema");
        avisoVO.setProperty("TITULO", titulo);
        avisoVO.setProperty("DESCRICAO", descricao);
        avisoVO.setProperty("CODUSU", codUsuarioDestino); // Destinatário
        avisoVO.setProperty("IMPORTANCIA", BigDecimal.valueOf(importancia));
        avisoVO.setProperty("IDENTIFICADOR", "PERSONALIZADO");
        avisoVO.setProperty("TIPO", "P"); // P = Personalizado
        avisoVO.setProperty("DHCRIACAO", new Timestamp(System.currentTimeMillis()));
        // Opcional: Definir quem enviou (pode ser o usuário logado ou null para 'Sistema')
        // avisoVO.setProperty("CODUSUREMETENTE", AuthenticationInfo.getCurrent().getUserID());
        dwfFacade.createEntity("AvisoSistema", (EntityVO) avisoVO);
    }

    public void enviarAvisoGrupo(BigDecimal codGrupoDestino, String titulo, String descricao, int importancia) throws Exception {
        EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
        DynamicVO avisoVO = (DynamicVO) dwfFacade.getDefaultValueObjectInstance("AvisoSistema");
        avisoVO.setProperty("TITULO", titulo);
        avisoVO.setProperty("DESCRICAO", descricao);
        avisoVO.setProperty("CODGRUPO", codGrupoDestino); // Destinatários
        avisoVO.setProperty("IMPORTANCIA", BigDecimal.valueOf(importancia));
        avisoVO.setProperty("IDENTIFICADOR", "PERSONALIZADO");
        avisoVO.setProperty("TIPO", "P");
        avisoVO.setProperty("DHCRIACAO", new Timestamp(System.currentTimeMillis()));
        dwfFacade.createEntity("AvisoSistema", (EntityVO) avisoVO);
    }
}
