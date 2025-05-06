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

import java.util.List;

@Stateless
public class PostServiceBean implements PostService
{

    @PersistenceContext
    private EntityManager em;


    /****************************************************************************************/
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
    private Post findPostById(long id) {
        try {
            TypedQuery<Post> query = em.createQuery(
                    "SELECT p FROM Post p WHERE p.ID = :id", Post.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        }
        catch (Exception e) {
            return null;
        }
    }
/****************************************************************************************/
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
 /*****************************************************************************************/
 @Override
 public List<Post> GetAllPoststhatUserHasPosted(String username)
 {
     User user = findUserByName(username);
     if (user == null) {
         throw new RuntimeException("User not found: " + username);
     }
     TypedQuery<Post> query = em.createQuery(
             "SELECT p FROM Post p WHERE p.user = :user", Post.class);
     query.setParameter("user", user);
     return query.getResultList();
 }
 /***********************************************************************************/
 @Override
 public void UpdateProfile(long postID, Post newPost) {
     Post existingPost = em.find(Post.class, postID);
     if (existingPost == null) {
         throw new RuntimeException("Post not found with ID: " + postID);
     }
     existingPost.setContent(newPost.getContent());
     em.merge(existingPost);
     System.out.println("Successfully updated post");
 }
 /**********************************************************************************/
 @Override
 public void DeletePost(long postID)
 {
     Post post = findPostById(postID);
     if (post == null) {
         throw new RuntimeException("Post not found with ID: " + postID);
     }
     em.remove(post);
     System.out.println("Successfully deleted post");
 }

}


