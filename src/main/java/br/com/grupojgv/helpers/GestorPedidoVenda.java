package br.com.grupojgv.helpers;

import br.com.grupojgv.routine.task.CabecalhoNota;
import br.com.grupojgv.routine.task.TypedVOUtils;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.PrePersistEntityState;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.comercial.BarramentoRegra;
import br.com.sankhya.modelcore.comercial.centrais.CACHelper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.SPBeanUtils;
import br.com.sankhya.ws.ServiceContext;
import lombok.extern.jbosslog.JBossLog;

import java.math.BigDecimal;

@JBossLog
public class GestorPedidoVenda {
    /**
     * Cria apenas o cabeçalho da Nota/Pedido (TGFCAB).
     *
     * @param cab Objeto representando o CabecalhoNota
     * @return O NUNOTA gerado.
     * @throws Exception Em caso de erro nas regras de negócio.
     */
    public BigDecimal criarCabecalho(CabecalhoNota cab) throws Exception {
        JapeSession.SessionHandle hnd = null;
        BigDecimal nuNotaGerada = null;

        try {
            hnd = JapeSession.open();
            prepararContexto();
            CACHelper cacHelper = new CACHelper();
            EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
            DynamicVO cabVO = TypedVOUtils.vo(cab, "CabecalhoNota");
            PrePersistEntityState cabState = PrePersistEntityState.build(
                dwfFacade,
                "CabecalhoNota",
                cabVO
            );
            BarramentoRegra regraCab = cacHelper.incluirAlterarCabecalho(
                AuthenticationInfo.getCurrent(),
                cabState
            );
            nuNotaGerada = (BigDecimal) cabState.getNewVO().getProperty("NUNOTA");
            if (nuNotaGerada == null && !regraCab.getDadosBarramento().getPksEnvolvidas().isEmpty()) {
                nuNotaGerada = new BigDecimal(regraCab.getDadosBarramento().getPksEnvolvidas().iterator().next().getValues()[0].toString());
            }
            log.info("PK Nota gerada: " + nuNotaGerada);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Erro ao criar cabeçalho do pedido: " + e.getMessage());
        } finally {
            JapeSession.close(hnd);
        }

        return nuNotaGerada;
    }

    /**
     * Configura o ServiceContext para simular uma ação da Central de Notas.
     * Essencial para que o CACHelper funcione fora da tela (Jobs, Ações Agendadas).
     */
    private void prepararContexto() throws Exception {
        ServiceContext sctx = ServiceContext.getCurrent();
        if (sctx == null) {
            sctx = new ServiceContext(null);
            sctx.setAutentication(AuthenticationInfo.getCurrent());
            ServiceContext.makeCurrent(sctx);
        }
        SPBeanUtils.setupContext(sctx);
        CACHelper.setupContext(sctx);
    }
}