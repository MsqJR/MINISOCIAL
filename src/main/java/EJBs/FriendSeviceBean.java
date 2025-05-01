package EJBs;

import Model.Friend;
import Model.User;
import Service.FriendService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

public class FriendSeviceBean implements FriendService
{
    @PersistenceContext
    private EntityManager em;

    @Override
    public Friend findFreindByName(String FNAME) {
        try {
            TypedQuery<Friend> query = em.createQuery(
                    "SELECT F FROM Friend F WHERE F.FriendName = :FNAME", Friend.class);
            query.setParameter("FNAME", FNAME);
            return query.getSingleResult();
        } catch (
                NoResultException e) {
            return null;
        }
    }
 /*********************************************************************************************/
}
