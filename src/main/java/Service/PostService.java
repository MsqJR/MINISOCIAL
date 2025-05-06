
package Service;

import Model.Post;

import java.awt.*;
import java.util.List;

//
public interface PostService
{
    void createPost(String username, String content, String imageUrl, String link);
    List<Post> GetAllPoststhatUserHasPosted(String username);
    void UpdateProfile(long postID, Post newPost);
    void DeletePost(long postID);
}
