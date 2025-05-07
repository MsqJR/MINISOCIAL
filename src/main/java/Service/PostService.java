
package Service;

import Model.Post;

import java.awt.*;
import java.util.List;

//
public interface PostService
{
    void createPost(String username, String content, String imageUrl, String link);
    public List<Object> GetAllPoststhatUserHasPosted(String username);
    void UpdatePost(long postID, Post newPost);
    void DeletePost(long postID);
    void AddCommentTOPost(long postID, String username, String commentText);
    void likePost(long postId, String username);
}
