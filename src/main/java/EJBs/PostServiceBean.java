package EJBs;

import Service.PostService;
import Service.UserService;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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

    @EJB
    private UserService userService;

    /****************************************************************************/
    @Override
    public void createPost(String username, String content, String imageUrl, String link) {
        // Use the userService to find the user by name
        User user = userService.findUserByName(username);
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
    /***************************************************************************************/

    @Override
    public void DeletePost(long postID, String username)
    {
        Post post = em.find(Post.class, postID);
        // Use userService to find the user
        User user = userService.findUserByName(username);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }
        if (post == null) {
            throw new RuntimeException("Post not found with ID: " + postID);
        }
        if (!post.getUser().getName().equals(username)) {
            throw new RuntimeException("User is not the owner of the post");
        }
        em.remove(post);
        System.out.println("Successfully deleted post with ID: " + postID);
    }

    @Override
    public void UpdatePost(long postID, Post newPost, String username)
    {
        Post existingPost = em.find(Post.class, postID);
        // Use userService to find the user
        User user = userService.findUserByName(username);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }
        if (existingPost == null) {
            throw new RuntimeException("Post not found with ID: " + postID);
        }
        if (!existingPost.getUser().getName().equals(username)) {
            throw new RuntimeException("User is not the owner of the post");
        }
        if (newPost.getContent() != null) {
            existingPost.setContent(newPost.getContent());
        }
        if (newPost.getMedia() != null) {
            existingPost.setMedia(newPost.getMedia());
        }
        em.merge(existingPost);
        System.out.println("Successfully updated the post");
    }

    @Override
    public void likePost(long postID, String username, String friendName)
    {
        Post existingPost = em.find(Post.class, postID);
        // Use userService to find the users
        User user = userService.findUserByName(username);
        User friend = userService.findUserByName(friendName);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }
        if (existingPost == null) {
            throw new RuntimeException("Post not found with ID: " + postID);
        }
        if (friend == null) {
            throw new RuntimeException("Friend not found: " + friendName);
        }
        if (!friend.getPosts().contains(existingPost)) {
            throw new RuntimeException("Friend does not have this post");
        }
        existingPost.getLikes().add(user);
        em.merge(existingPost);
        System.out.println("Successfully liked " + friendName + "'s post");
    }

    @Override
    public void removelikefromPost(long postID, String username, String friendName)
    {
        Post existingPost = em.find(Post.class, postID);
        // Use userService to find the users
        User user = userService.findUserByName(username);
        User friend = userService.findUserByName(friendName);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }
        if (existingPost == null) {
            throw new RuntimeException("Post not found with ID: " + postID);
        }
        if (!friend.getPosts().contains(existingPost)) {
            throw new RuntimeException("Friend does not have this post");
        }

        existingPost.getLikes().remove(user);
        em.merge(existingPost);
        System.out.println("Successfully removed like from " + friendName + "'s post");
    }

    @Override
    public void commentPost(long postID, String comment, String username, String friendName)
    {
        try {
            Post existingPost = em.find(Post.class, postID);
            // Use userService to find the users
            User user = userService.findUserByName(username);
            User friend = userService.findUserByName(friendName);
            if (user == null) {
                throw new RuntimeException("User not found: " + username);
            }
            if (existingPost == null) {
                throw new RuntimeException("Post not found with ID: " + postID);
            }
            if (!friend.getPosts().contains(existingPost)) {
                throw new RuntimeException("Friend does not have this post");
            }
            if (comment == null || comment.trim().isEmpty()) {
                existingPost.getComments().add(user);
                em.merge(existingPost);
                System.out.println("Successfully commented on " + friendName + "'s post");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void removeComment(long postID, long commentID, String username, String friendName)
    {
        Post existingPost = em.find(Post.class, postID);
        Post existingComment = em.find(Post.class, commentID);
        // Use userService to find the user
        User user = userService.findUserByName(username);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }
        if (existingPost == null) {
            throw new RuntimeException("Post not found with ID: " + postID);
        }
        if (existingComment == null) {
            throw new RuntimeException("Comment not found with ID: " + commentID);
        }
        existingPost.getComments().remove(user);
        em.merge(existingPost);
        System.out.println("Successfully removed comment from " + friendName + "'s post");
    }

    @Override
    public void updateComment(long postID, long commentID, String newComment, String username) {
        Post existingPost = em.find(Post.class, postID);
        Post existingComment = em.find(Post.class, commentID);
        // Use userService to find the user
        User user = userService.findUserByName(username);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }
        if (existingPost == null) {
            throw new RuntimeException("Post not found with ID: " + postID);
        }
        if (existingComment == null) {
            throw new RuntimeException("Comment not found with ID: " + commentID);
        }
        existingComment.setContent(newComment);
        em.merge(existingComment);
        System.out.println("Successfully updated comment from " + username + "'s post");
    }

    @Override
   public List<Post> Getallposts()
    {
        try {
            TypedQuery<Post> q = em.createQuery("SELECT p FROM Post p", Post.class);
            return q.getResultList();
        } catch (NoResultException e) {
            return new java.util.ArrayList<>();
        }

    }

}