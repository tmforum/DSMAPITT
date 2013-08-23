package tmf.org.dsmapi.tt;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TroubleTicketManager extends AbstractManager<TroubleTicket> {

    @PersistenceContext(unitName = "DSTroubleTicketPU")
    private EntityManager em;

    public TroubleTicketManager() {
        super(TroubleTicket.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
