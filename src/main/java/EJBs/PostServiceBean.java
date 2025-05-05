package EJBs;

import Service.PostService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import Model.ImageAttachement;
import Model.LinkAttachement;
import Model.Post;
import Model.User;

@Stateless
public class PostServiceBean implements PostService {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void createPost(String username, String content, String imageUrl, String link) {
        User user = findUserByName(username);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }

        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("Post content cannot be empty");
        }

        Post post = new Post();
        post.setContent(content);
        post.setUser(user);

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            ImageAttachement media = new ImageAttachement();
            media.setImage_url(imageUrl);
            em.persist(media);
            post.setMedia(media);
        } else if (link != null && !link.trim().isEmpty()) {
            LinkAttachement media = new LinkAttachement();
            media.setLink(link);
            em.persist(media);
            post.setMedia(media);
        }

        em.persist(post);
    }

    private User findUserByName(String name) {
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.Name = :name", User.class);
            query.setParameter("name", name);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}