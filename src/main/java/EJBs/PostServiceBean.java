package EJBs;

import Messaging.NotificationService;
import Model.*;
import Service.PostService;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
public class PostServiceBean implements PostService
{

    @PersistenceContext
    private EntityManager em;

    @EJB(beanName = "NotificationServiceBean" )
    private NotificationService nss;



    @Override
    public void deletePost(long postId) {
        Post post = em.find(Post.class, postId);
        if (post != null) {
            em.remove(post);
        }
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
    public Post findPostById(long id) {
        try {
            TypedQuery<Post> query = em.createQuery(
                    "SELECT p FROM Post p WHERE p.id = :id", Post.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        }
        catch (Exception e) {
            return null;
        }
    }

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
    @Override
    public List<Object> getUserFeed(String username) {
        User user = findUserByName(username);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }

        TypedQuery<Post> query = em.createQuery(
                "SELECT p FROM Post p WHERE p.user = :user OR p.user IN " +
                        "(SELECT f FROM User u JOIN u.friends f WHERE u = :user) " +
                        "ORDER BY p.createdAt DESC", Post.class);
        query.setParameter("user", user);
        List<Post> posts = query.getResultList();

        List<Object> result = new ArrayList<>();

        for (Post post : posts) {
            List<Comment> comments = em.createQuery(
                            "SELECT c FROM Comment c WHERE c.post = :post", Comment.class)
                    .setParameter("post", post)
                    .getResultList();

            List<Like> likes = em.createQuery(
                            "SELECT l FROM Like l WHERE l.post = :post", Like.class)
                    .setParameter("post", post)
                    .getResultList();

            List<String> likeUsernames = likes.stream()
                    .map(like -> like.getUser().getName())
                    .collect(Collectors.toList());

            List<String> commentUsernames = comments.stream()
                    .map(comment -> comment.getText()+"/"+comment.getUser().getName() )
                    .collect(Collectors.toList());

            Map<String, Object> postMap = new HashMap<>();
            postMap.put("user name", post.getUser().getName());
            postMap.put("postId", post.getPostId());
            postMap.put("content", post.getContent());
            postMap.put("likes", likeUsernames.isEmpty() ? null : likeUsernames);
            postMap.put("comments", commentUsernames.isEmpty() ? null : commentUsernames);

            result.add(postMap);
        }

        return result;
    }


    @Override
    public void UpdatePost(long postID, Post newPost) {
        Post existingPost = em.find(Post.class, postID);
        if (existingPost == null) {
            throw new RuntimeException("Post not found with ID: " + postID);
        }
        existingPost.setContent(newPost.getContent());
        em.merge(existingPost);
        System.out.println("Successfully updated post");
    }

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

    @Override
    public void AddCommentTOPost(long postID, String username, String commentText) {
        Post post = findPostById(postID);
        User user = findUserByName(username);

        if (post == null || user == null) {
            throw new RuntimeException("Post or User not found.");
        }

        if (!user.getFriends().contains(post.getUser()) && !post.getUser().equals(user)) {
            throw new RuntimeException("You can only comment on your own or your friend's post.");
        }

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setText(commentText);
        comment.setCommentedAt(LocalDateTime.now());
        em.persist(comment);

        post.getComments().add(comment);
        em.merge(post);

        nss.sendPostCommentedNotification(
                username,
                post.getUser().getName() + " commented on your post.",
                postID,
                commentText
        );
    }

    @Override
    public void likePost(long postId, String username)
    {
        Post post = findPostById(postId);
        User user = findUserByName(username);
        if (post == null || user == null) {
            throw new RuntimeException("Post or User not found.");
        }

        Like like = new Like();
        like.setPost(post);
        like.setUser(user);
        em.persist(like);

        post.getLikes().add(like);
        em.merge(post);

        nss.sendPostLikedNotification(
                username,
                post.getUser().getName() + " liked your post.",
                postId
        );
        System.out.println("Successfully liked post");
    }


    @Override
    public void createGroupPost(User user, Group group, String content, String imageUrl, String link) {
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("Post content cannot be empty");
        }

        Post post = new Post();
        post.setUser(user);
        post.setGroup(group);
        post.setContent(content);

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

}