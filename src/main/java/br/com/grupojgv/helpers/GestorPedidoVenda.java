package br.com.grupojgv.helpers;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.PrePersistEntityState;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.comercial.BarramentoRegra;
import br.com.sankhya.modelcore.comercial.centrais.CACHelper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.SPBeanUtils;
import br.com.sankhya.ws.ServiceContext;
import com.sankhya.util.TimeUtils;
import com.sankhya.util.XMLUtils;
import org.jdom.Element;
import java.math.BigDecimal;

public class GestorPedidoVenda {
    /**
     * Cria apenas o cabeçalho da Nota/Pedido (TGFCAB) contendo 1 item.
     *
     * @param cabecalhoXml Cabeçalho XML (TGFCAB)
     * @return O NUNOTA gerado.
     * @throws Exception Em caso de erro nas regras de negócio.
     */
    public BigDecimal gerarPedido(Element cabecalhoXml) throws Exception {
        JapeSession.SessionHandle hnd = null;
        BigDecimal nuNotaGerada = null;

        try {
            hnd = JapeSession.open();

            // 1. Preparação do Contexto (Necessário para simular uma requisição da tela)
            ServiceContext sctx = ServiceContext.getCurrent(); // Se estiver rodando numa ação
            if (sctx == null) {
                // Se estiver num Job sem contexto HTTP, é necessário criar um contexto fictício ou garantir AuthInfo
                sctx = new ServiceContext(null);
                sctx.setAutentication(AuthenticationInfo.getCurrent());
            }

            // Configura propriedades de sessão necessárias para a Central de Notas
            SPBeanUtils.setupContext(sctx);
            CACHelper.setupContext(sctx);

            // 2. Instancia o Helper da Central (O motor de regras)
            CACHelper cacHelper = new CACHelper();


            // O NUNOTA deve ser nulo para inclusão, ou passado para alteração
            // O CACHelper vai calcular impostos iniciais, numeração, etc.
            BarramentoRegra regraCab = cacHelper.incluirAlterarCabecalho(sctx, cabecalhoXml);

            // Recupera o NUNOTA gerado pelo sistema
            // O BarramentoRegra contém as PKs envolvidas na transação
            nuNotaGerada = new BigDecimal(regraCab.getDadosBarramento().getPksEnvolvidas().iterator().next().getValues()[0].toString());

            System.out.println("Cabeçalho criado. NUNOTA: " + nuNotaGerada);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Erro ao gerar pedido: " + e.getMessage());
        } finally {
            JapeSession.close(hnd);
        }

        return nuNotaGerada;
    }

    /**
     * Cria apenas o cabeçalho da Nota/Pedido (TGFCAB) contendo 1 item.
     *
     * @param codTop Código do Tipo de Operação (TGFTOP)
     * @param codParc Código do Parceiro (TGFPAR)
     * @param codEmp Código da Empresa (TSIEMP)
     * @param codProd Código do Produto (TGFPRO)
     * @param qtd Quantidade do Produto
     * @param vlrUnit Valor do produto
     * @return O NUNOTA gerado.
     * @throws Exception Em caso de erro nas regras de negócio.
     */
    public BigDecimal gerarPedido(
        BigDecimal codTop,
        BigDecimal codParc,
        BigDecimal codEmp,
        BigDecimal codProd,
        BigDecimal qtd,
        BigDecimal vlrUnit
    ) throws Exception {
        JapeSession.SessionHandle hnd = null;
        BigDecimal nuNotaGerada = null;

        try {
            hnd = JapeSession.open();

            // 1. Preparação do Contexto (Necessário para simular uma requisição da tela)
            ServiceContext sctx = ServiceContext.getCurrent(); // Se estiver rodando numa ação
            if (sctx == null) {
                // Se estiver num Job sem contexto HTTP, é necessário criar um contexto fictício ou garantir AuthInfo
                sctx = new ServiceContext(null);
                sctx.setAutentication(AuthenticationInfo.getCurrent());
            }

            // Configura propriedades de sessão necessárias para a Central de Notas
            SPBeanUtils.setupContext(sctx);
            CACHelper.setupContext(sctx);

            // 2. Instancia o Helper da Central (O motor de regras)
            CACHelper cacHelper = new CACHelper();

            // -----------------------------------------------------------
            // PASSO A: Criar o Cabeçalho (TGFCAB)
            // -----------------------------------------------------------
            Element cabecalhoXml = new Element("Cabecalho");

            // Campos obrigatórios mínimos
            XMLUtils.addContentElement(cabecalhoXml, "NUNOTA", 0); // NUNOTA
            XMLUtils.addContentElement(cabecalhoXml, "CODTIPOPER", codTop); // TOP
            XMLUtils.addContentElement(cabecalhoXml, "CODPARC", codParc);   // Parceiro
            XMLUtils.addContentElement(cabecalhoXml, "CODEMP", codEmp);     // Empresa
            XMLUtils.addContentElement(cabecalhoXml, "DTNEG", TimeUtils.formataDDMMYYYY(TimeUtils.getNow())); // Data
            XMLUtils.addContentElement(cabecalhoXml, "TIPMOV", "P");        // P = Pedido de Venda

            // O NUNOTA deve ser nulo para inclusão, ou passado para alteração
            // O CACHelper vai calcular impostos iniciais, numeração, etc.
            BarramentoRegra regraCab = cacHelper.incluirAlterarCabecalho(sctx, cabecalhoXml);

            // Recupera o NUNOTA gerado pelo sistema
            // O BarramentoRegra contém as PKs envolvidas na transação
            nuNotaGerada = new BigDecimal(regraCab.getDadosBarramento().getPksEnvolvidas().iterator().next().getValues()[0].toString());

            System.out.println("Cabeçalho criado. NUNOTA: " + nuNotaGerada);

            // -----------------------------------------------------------
            // PASSO B: Inserir Itens (TGFITE)
            // -----------------------------------------------------------

            // Elemento XML raiz que conterá a lista de itens
            Element itensElem = new Element("itens");
            // Parâmetros de controle do Helper
            itensElem.setAttribute("INFORMARPRECO", "true"); // Força o uso do preço informado no XML
            itensElem.setAttribute("recalcularValoresMoeda", "false"); // Otimização

            // Criação do Item individual
            Element itemElem = new Element("item");
            XMLUtils.addContentElement(itemElem, "NUNOTA", nuNotaGerada);
            XMLUtils.addContentElement(itemElem, "CODPROD", codProd);
            XMLUtils.addContentElement(itemElem, "QTDNEG", qtd);
            XMLUtils.addContentElement(itemElem, "VLRUNIT", vlrUnit);
            XMLUtils.addContentElement(itemElem, "CODVOL", getVolumePadrao(codProd)); // Helper auxiliar abaixo
            // Outros campos opcionais: CODLOCALORIG, CONTROLE, etc.

            // Adiciona o item à lista
            itensElem.addContent(itemElem);

            // Executa a inserção do item através do Helper
            // O último parâmetro 'true' indica "inicializaProdutos" (busca impostos, custos, etc.)
            cacHelper.incluirAlterarItem(nuNotaGerada, sctx, itensElem, true);

            // -----------------------------------------------------------
            // PASSO C: Confirmar (Opcional)
            // Se desejar já confirmar o pedido (gerar financeiro/estoque se a TOP mandar)
            // -----------------------------------------------------------
            // cacHelper.processarConfirmacao(nuNotaGerada);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Erro ao gerar pedido: " + e.getMessage());
        } finally {
            JapeSession.close(hnd);
        }

        return nuNotaGerada;
    }

    /**
     * Cria apenas o cabeçalho da Nota/Pedido (TGFCAB).
     *
     * @param codTipoOper Código do Tipo de Operação (TGFTOP)
     * @param codParc Código do Parceiro (TGFPAR)
     * @param codEmp Código da Empresa (TSIEMP)
     * @param codNat Código da Natureza (TGFNAT) - Opcional, pode passar null se a TOP preencher
     * @param codCenCus Código do Centro de Resultado (TSICUS) - Opcional
     * @return O NUNOTA gerado.
     * @throws Exception Em caso de erro nas regras de negócio.
     */
    public BigDecimal criarCabecalho(
        BigDecimal codTipoOper,
        BigDecimal codParc,
        BigDecimal codEmp,
        BigDecimal codNat,
        BigDecimal codCenCus,
        BigDecimal codTipVenda,
        BigDecimal adCodTipoDeOs
    ) throws Exception {
        JapeSession.SessionHandle hnd = null;
        BigDecimal nuNotaGerada = null;

        try {
            hnd = JapeSession.open();
            prepararContexto();
            CACHelper cacHelper = new CACHelper();
            EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
            DynamicVO cabVO = (DynamicVO) dwfFacade.getDAOInstance("CabecalhoNota").getDefaultValueObjectInstance();
            cabVO.setProperty("CODTIPOPER", codTipoOper);
            cabVO.setProperty("CODPARC", codParc);
            cabVO.setProperty("CODEMP", codEmp);
            cabVO.setProperty("TIPMOV", "P");
            cabVO.setProperty("DTNEG", new java.sql.Timestamp(System.currentTimeMillis()));
            cabVO.setProperty("NUMNOTA", BigDecimal.ZERO);
            cabVO.setProperty("CODTIPVENDA", codTipVenda);
            cabVO.setProperty("AD_CODTIPODEOS", adCodTipoDeOs);

            if (codNat != null) {
                cabVO.setProperty("CODNAT", codNat);
            }
            if (codCenCus != null) {
                cabVO.setProperty("CODCENCUS", codCenCus);
            }
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

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Erro ao criar cabeçalho do pedido: " + e.getMessage());
        } finally {
            JapeSession.close(hnd);
        }

        return nuNotaGerada;
    }

    /**
     * Adiciona um item a uma nota já existente (TGFITE).
     *
     * @param nuNota Número Único da Nota (Gerado pelo metodo criarCabecalho)
     * @param codProd Código do Produto
     * @param qtd Quantidade negociada
     * @param vlrUnit Valor unitário
     * @throws Exception em caso de falha na inclusão
     */
    public void adicionarItem(BigDecimal nuNota, BigDecimal codProd, BigDecimal qtd, BigDecimal vlrUnit) throws Exception {
        JapeSession.SessionHandle hnd = null;

        try {
            hnd = JapeSession.open();

            // Prepara o contexto novamente (necessário pois é uma nova chamada ao Helper)
            prepararContexto();

            CACHelper cacHelper = new CACHelper();

            // Elemento raiz da lista de itens
            Element itensElem = new Element("itens");
            itensElem.setAttribute("INFORMARPRECO", "true"); // Define que vamos respeitar o preço passado

            // Criação do Item individual
            Element itemElem = new Element("item");
            XMLUtils.addContentElement(itemElem, "NUNOTA", nuNota);
            XMLUtils.addContentElement(itemElem, "CODPROD", codProd);
            XMLUtils.addContentElement(itemElem, "QTDNEG", qtd);
            XMLUtils.addContentElement(itemElem, "VLRUNIT", vlrUnit);
            XMLUtils.addContentElement(itemElem, "CODVOL", getVolumePadrao(codProd));
            // Caso queira forçar local ou controle:
            // XMLUtils.addContentElement(itemElem, "CODLOCALORIG", new BigDecimal(1000));

            // Adiciona o item à lista
            itensElem.addContent(itemElem);

            // Chama o serviço para incluir o item
            // O último parâmetro 'true' força a inicialização dos dados do produto (impostos, custos, etc)
            cacHelper.incluirAlterarItem(nuNota, ServiceContext.getCurrent(), itensElem, true);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Erro ao adicionar item ao pedido " + nuNota + ": " + e.getMessage());
        } finally {
            JapeSession.close(hnd);
        }
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

    /**
     * Busca o volume padrão do produto para preencher o XML.
     */
    private String getVolumePadrao(BigDecimal codProd) throws Exception {
        DynamicVO prod = JapeFactory.dao("Produto").findByPK(codProd);
        if (prod == null) throw new Exception("Produto " + codProd + " não encontrado.");
        return prod.asString("CODVOL");
    }
}